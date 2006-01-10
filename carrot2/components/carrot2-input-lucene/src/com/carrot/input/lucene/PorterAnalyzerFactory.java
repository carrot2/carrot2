
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

package com.carrot.input.lucene;

import java.io.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PorterAnalyzerFactory implements AnalyzerFactory
{
    /** Public instance */
    public final static AnalyzerFactory INSTANCE = new PorterAnalyzerFactory();
    
    /* (non-Javadoc)
     * @see com.carrot.input.lucene.AnalyzerFactory#getInstance()
     */
    public Analyzer getInstance()
    {
        return new StandardAnalyzer()
        {
            /* (non-Javadoc)
             * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String, java.io.Reader)
             */
            public TokenStream tokenStream(String fieldName, Reader reader)
            {
                return new PorterStemFilter(super.tokenStream(fieldName,
                    reader));
            }
        };
    }
}
