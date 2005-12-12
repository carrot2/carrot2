/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Stanislaw Osinski, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package carrot2.demo.index;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import com.dawidweiss.carrot.core.local.clustering.*;

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
        indexWriter.mergeFactor = 100;

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
     * @return
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
        document.add(Field.UnIndexed("id", (rawDocument.getId().toString())));

        // Title:
        document.add(Field.Text(SEARCH_FIELDS[0],
            (rawDocument.getTitle() != null ? rawDocument.getTitle() : "")));

        // Description:
        document
            .add(Field.Text(SEARCH_FIELDS[1],
                (rawDocument.getSnippet() != null ? rawDocument.getSnippet()
                    : "")));

        return document;
    }
}
