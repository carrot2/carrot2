
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.lucene;

import static org.carrot2.core.test.SampleDocumentData.DOCUMENTS_DATA_MINING;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.carrot2.core.Document;

/**
 * Utilities for creating Lucene indices.
 */
class LuceneIndexUtils
{
    static void createAndPopulateIndex(Directory directory, Analyzer analyzer)
        throws Exception
    {
        final IndexWriterConfig config = new IndexWriterConfig(analyzer);
        final IndexWriter w = new IndexWriter(directory, config);
        for (Document d : DOCUMENTS_DATA_MINING)
        {
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();

            doc.add(new TextField("title", (String) d.getField(Document.TITLE), Store.YES));
            doc.add(new TextField("snippet", (String) d.getField(Document.SUMMARY), Store.YES));
            doc.add(new StringField("url", (String) d.getField(Document.CONTENT_URL), Store.YES));

            w.addDocument(doc);
        }
        
        /*
         * Add a test document with snippet (content) field with multiple values.  
         */
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
        doc.add(new TextField("snippet", "terma", Store.YES));
        doc.add(new TextField("snippet", "termb", Store.YES));
        w.addDocument(doc);

        w.close();
    }
}
