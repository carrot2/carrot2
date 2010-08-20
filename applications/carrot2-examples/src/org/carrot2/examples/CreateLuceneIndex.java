
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
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
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
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

        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
        IndexWriter writer = new IndexWriter(FSDirectory.open(indexDir), analyzer, true, MaxFieldLength.UNLIMITED);
        
        for (Document d : SampleDocumentData.DOCUMENTS_DATA_MINING)
        {
            final org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            /*
             * We will create Lucene documents with searchable "fullContent" field and "title", 
             * "url" and "snippet" fields for clustering.
             */
            doc.add(new Field("fullContent", d.getSummary(), Store.NO, Index.ANALYZED));

            doc.add(new Field("title", d.getTitle(), Store.YES, Index.NO));
            doc.add(new Field("snippet", d.getSummary(), Store.YES, Index.NO));
            doc.add(new Field("url", d.getContentUrl(), Store.YES, Index.NO));
            writer.addDocument(doc);
        }

        writer.close();
    }
}
