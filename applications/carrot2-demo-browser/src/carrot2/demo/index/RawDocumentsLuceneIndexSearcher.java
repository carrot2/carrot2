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

import org.apache.log4j.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

/**
 * @author Stanislaw Osinski
 */
public class RawDocumentsLuceneIndexSearcher extends
    RawDocumentsLuceneIndexBase
{
    /** */
    private final static Logger logger = Logger
        .getLogger(RawDocumentsLuceneIndexSearcher.class);

    /**
     * @param rawDocuments
     * @throws IOException
     */
    public static String [] search(Directory indexDirectory, String query)
        throws IOException
    {
        long start = System.currentTimeMillis();

        final IndexReader indexReader = IndexReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Analyzer porterAnalyzer = createPorterAnalyzer();

        // Create a boolean query that combines all fields
        BooleanQuery booleanQuery = new BooleanQuery();
        for (int i = 0; i < SEARCH_FIELDS.length; i++)
        {
            QueryParser queryParser = new QueryParser(SEARCH_FIELDS[i],
                porterAnalyzer);
            queryParser.setOperator(QueryParser.DEFAULT_OPERATOR_AND);
            Query queryComponent = null;
            try
            {
                queryComponent = queryParser.parse(query);
            }
            catch (ParseException e)
            {
                new RuntimeException("Lucene query parse exception", e);
            }
            booleanQuery.add(queryComponent, false, false);
        }

        // Perform query
        Hits hits = indexSearcher.search(booleanQuery);
        String [] documentIds = new String [hits.length()];
        for (int i = 0; i < documentIds.length; i++)
        {
            documentIds[i] = hits.doc(i).getField("id").stringValue();
        }

        indexSearcher.close();
        indexReader.close();

        long stop = System.currentTimeMillis();
        logger.info("Lucene index searched for '" + query + "' in "
            + (stop - start) + " ms");

        return documentIds;
    }
}
