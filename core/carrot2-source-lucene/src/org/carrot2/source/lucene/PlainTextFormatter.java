
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

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

/**
 * An implementation of the Lucene highlighter's {@link Formatter} interface that doesn't
 * do any highlighting and just returns the original text.
 */
public final class PlainTextFormatter implements Formatter
{
    public String highlightTerm(String originalText, TokenGroup tokengroup)
    {
        return originalText;
    }
}
