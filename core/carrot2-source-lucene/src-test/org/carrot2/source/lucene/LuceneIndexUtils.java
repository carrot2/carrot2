
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

package org.carrot2.source.lucene;

import static org.carrot2.core.test.SampleDocumentData.DOCUMENTS_DATA_MINING;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
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
        final IndexWriter w = new IndexWriter(directory, analyzer, true,
            IndexWriter.MaxFieldLength.UNLIMITED);
        for (Document d : DOCUMENTS_DATA_MINING)
        {
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();

            doc.add(new Field("title", (String) d.getField(Document.TITLE), Store.YES,
                Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));

            doc.add(new Field("snippet", (String) d.getField(Document.SUMMARY),
                Store.YES, Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));

            doc.add(new Field("url", (String) d.getField(Document.CONTENT_URL),
                Store.YES, Field.Index.NO));

            w.addDocument(doc);
        }
        
        /*
         * Add a test document with snippet (content) field with multiple values.  
         */
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
        doc.add(new Field("snippet", "terma",
            Store.YES, Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));
        doc.add(new Field("snippet", "termb",
            Store.YES, Field.Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));
        w.addDocument(doc);

        w.close();
    }
}
