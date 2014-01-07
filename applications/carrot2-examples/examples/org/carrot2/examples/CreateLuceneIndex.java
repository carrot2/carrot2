
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.carrot2.core.Document;
import org.carrot2.examples.clustering.ClusteringDataFromLucene;

/**
 * Create a Lucene index on disk based on {@link SampleDocumentData}.
 * 
 * @see ClusteringDataFromLucene
 */
public class CreateLuceneIndex
{
    public static void main(String [] args)
        throws Exception
    {
        if (args.length != 1)
        {
            System.out.println("Args: index-dir");
            System.exit(-1);
        }

        File indexDir = new File(args[0]);
        if (indexDir.exists())
        {
            System.out.println("Index directory already exists: " + indexDir.getAbsolutePath());
            System.exit(-2);
        }

        @SuppressWarnings("deprecation")
        Version luceneVersion = Version.LUCENE_CURRENT;

        Analyzer analyzer = new StandardAnalyzer(luceneVersion);
        IndexWriterConfig config = new IndexWriterConfig(luceneVersion, analyzer);
        IndexWriter writer = new IndexWriter(FSDirectory.open(indexDir), config);
        
        for (Document d : SampleDocumentData.DOCUMENTS_DATA_MINING)
        {
            final org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            /*
             * We will create Lucene documents with searchable "fullContent" field and "title", 
             * "url" and "snippet" fields for clustering.
             */
            doc.add(new TextField("fullContent", d.getSummary(), Store.NO));

            doc.add(new TextField("title", d.getTitle(), Store.YES));
            doc.add(new TextField("snippet", d.getSummary(), Store.YES));
            doc.add(new StringField("url", d.getContentUrl(), Store.YES));
            writer.addDocument(doc);
        }

        writer.close();
    }
}
