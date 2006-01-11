
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

package carrot2.demo.index;

import java.io.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;

/**
 * @author Stanislaw Osinski
 */
public class RawDocumentsLuceneIndexBase
{
    protected static final String [] SEARCH_FIELDS = new String []
    {
        "title", "body"
    };
    
    protected static Analyzer createPorterAnalyzer()
    {
        return new StandardAnalyzer()
        {
            public TokenStream tokenStream(String fieldName, Reader reader)
            {
                return new PorterStemFilter(super
                    .tokenStream(fieldName, reader));
            }
        };
    }
}
