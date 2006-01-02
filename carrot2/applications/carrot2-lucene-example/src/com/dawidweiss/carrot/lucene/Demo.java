
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.lucene;

import java.text.MessageFormat;
import java.io.*;
import java.util.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.demo.*;
import org.apache.lucene.demo.html.Entities;
import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;



/** 
 * An example of Carrot2 integration with Lucene.
 *
 * <p>This is a very simple use-case scenario, actually.
 * I just took Lucene's demo code and added a clustering
 * process on top of it. You can play with various clustering
 * and pre-filtering components and build whatever suits your
 * data best.
 *
 * <p>Please note that the quality of 'summaries' of documents
 * is crucial for good clustering results. By quality I understand
 * relevance to the actual document, but also length of the summary,
 * its syntactical correctness etc.
 *
 * @author Dawid Weiss
 */
public class Demo {
	
	public static void main(String [] args) throws Exception {

		if (args.length < 2) {
			System.out.println("Use arguments: index_path query");
			return;
		}

		String indexName = args[0];
		String queryString = args[1];

		long startTime = System.currentTimeMillis();

		// used to tokenize queries
		Analyzer analyzer = new StopAnalyzer();

		// open reader
		IndexReader reader = IndexReader.open(indexName);
		
		long openingIndexEndTime = System.currentTimeMillis();

		// make searcher
		Searcher searcher = new IndexSearcher(reader);

		Query query = null;
		try {
			query = QueryParser.parse(queryString, "contents", analyzer);
		} catch (ParseException e) {
			System.err.println("Problems parsing query: " + e.toString());
			return;
		}
		
		int start = 0;
		int requiredHits = 100;
		Hits hits = searcher.search(query);
		int end = Math.min(hits.length(), start + requiredHits);
		long searchEndTime = System.currentTimeMillis();

        // prepare an array for documents to be clustered.
        ArrayList docs = new ArrayList(end-start);
        
        int j = 0;
		for (int i = start; i < end; i++, j++) {
			Document doc = hits.doc(i);

			// retrieve the attributes of a document.
			String url = doc.get("url");
			String title = doc.get("title");
			if (title.equals(""))
				title = url;

			String summary = doc.get("summary");

            docs.add( new DocumentAdapter(j, url, title, summary)); 
    	}

        // cluster the documents.
        Clusterer clusterer = new Clusterer();

        // warm-up round (stemmer tables must be read etc).
        List clusters = clusterer.clusterHits(docs);

        long clusteringStartTime = System.currentTimeMillis();
        clusters = clusterer.clusterHits(docs);
        long clusteringEndTime = System.currentTimeMillis();

        System.out.println("Results for: " + queryString);
        
        MessageFormat mf = new MessageFormat("Timings: index opened in: {0,number,#.###}s, "
            + "search: {1,number,#.###}s, clustering: {2,number,#.###}s");
        System.out.println(
            mf.format(new Object [] {
                new Double((openingIndexEndTime-startTime)/1000d),
                new Double((searchEndTime-openingIndexEndTime)/1000d),
                new Double((clusteringEndTime-clusteringStartTime)/1000d)
            })
        );

        // dump the clusters info
        for (Iterator i = clusters.iterator(); i.hasNext();) {
            RawCluster rawCluster = (RawCluster) i.next();
            // is it a 'junk' cluster? Junk clusters group 'other' documents for which the
            // only similarity is the lack of any similarity.
            System.out.print(" :> ");
            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null) {
                System.out.print("(JUNK) ");
            }

            // get description phrases for this cluster (String)
            List phrases = rawCluster.getClusterDescription();
            for (j=0;j<Math.min(2,phrases.size());j++) {
                if (j>0) System.out.print("; ");
                System.out.print((String) phrases.get(j));
            }
            System.out.println();
            
            // get documents in this cluster. These might _not_ be the same
            // documents as returned from the input component (in this case, Lucene).
            // but they will have identical identifiers. We simply map them
            // back to the original docs.
			List rawDocuments = rawCluster.getDocuments();
            int clusterDocumentsNumber = rawDocuments.size();
            int maxDocs = Math.min(3, clusterDocumentsNumber);
			for (Iterator k = rawDocuments.iterator(); k.hasNext() && maxDocs>0; 
                    maxDocs--, clusterDocumentsNumber--) {
				RawDocument doc = (RawDocument) k.next();
				Integer offset = (Integer) doc.getId();

                DocumentAdapter docAdapter = (DocumentAdapter) docs.get(offset.intValue());
                System.out.println("    - " + docAdapter.getUrl());
                System.out.println("      " + docAdapter.getTitle());
			}
            if (clusterDocumentsNumber > 0) {
                System.out.println("      (and " + clusterDocumentsNumber + " more)");
            }
            System.out.println();
        }

  }
}	
