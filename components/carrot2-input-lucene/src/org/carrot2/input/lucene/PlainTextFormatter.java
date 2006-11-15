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

package org.carrot2.input.lucene;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

/**
 * An implementation of the Lucene highlighter's {@link Formatter} interface
 * that doesn't do any highlighting and just returns the original text.
 * 
 * @author Stanislaw Osinski
 */
public class PlainTextFormatter implements Formatter
{
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.search.highlight.Formatter#highlightTerm(java.lang.String,
     *      org.apache.lucene.search.highlight.TokenGroup)
     */
    public String highlightTerm(String originalText, TokenGroup tokengroup)
    {
        return originalText;
    }
}
