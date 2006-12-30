
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.index;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

/**
 * @author Stanislaw Osinski
 */
public class RawDocumentsLuceneIndexSearcher extends
    RawDocumentsLuceneIndexBase
{
    private final static Logger logger = Logger
        .getLogger(RawDocumentsLuceneIndexSearcher.class);

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
            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query queryComponent = null;
            try
            {
                queryComponent = queryParser.parse(query);
            }
            catch (ParseException e)
            {
                throw new RuntimeException("Lucene query parse exception", e);
            }
            booleanQuery.add(queryComponent, BooleanClause.Occur.MUST);
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
