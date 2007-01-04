
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.index;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import org.carrot2.core.clustering.RawDocument;

/**
 * @author Stanislaw Osinski
 */
public class RawDocumentsLuceneIndexBuilder extends RawDocumentsLuceneIndexBase
{
    /** */
    private final static Logger logger = Logger
        .getLogger(RawDocumentsLuceneIndexBuilder.class);

    /**
     * @param rawDocuments
     * @throws IOException
     */
    public static Directory index(List rawDocuments) throws IOException
    {
        Analyzer porterAnalyzer = createPorterAnalyzer();
        Directory indexDirectory = new RAMDirectory();

        IndexWriter indexWriter = new IndexWriter(indexDirectory,
            porterAnalyzer, true);
        indexWriter.setMergeFactor(100);

        long start = System.currentTimeMillis();
        for (Iterator iter = rawDocuments.iterator(); iter.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) iter.next();
            indexWriter.addDocument(createDocument(rawDocument));
        }
        long stop = System.currentTimeMillis();
        logger.info("Lucene index built in " + (stop - start) + " ms");

        indexWriter.optimize();
        indexWriter.close();

        return indexDirectory;
    }

    /**
     * @param rawDocument
     */
    private static Document createDocument(RawDocument rawDocument)
    {
        Document document = new Document();

        if (rawDocument.getId() == null)
        {
            throw new RuntimeException(
                "RawDocuments must have a non-null id for ");
        }

        // Id: store, don't index
        document.add(
                new Field("id", rawDocument.getId().toString(), Field.Store.YES, Field.Index.NO));

        // Title:
        document.add(
                new Field(SEARCH_FIELDS[0], (rawDocument.getTitle() != null ? rawDocument.getTitle() : ""),
                        Field.Store.YES, Field.Index.TOKENIZED));

        // Description:
        document.add(
                new Field(SEARCH_FIELDS[1], (rawDocument.getSnippet() != null ? rawDocument.getSnippet() : ""),
                        Field.Store.YES, Field.Index.TOKENIZED));

        return document;
    }
}
