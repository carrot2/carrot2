package org.carrot2.source.lucene;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

/**
 * An implementation of the Lucene highlighter's {@link Formatter} interface that doesn't
 * do any highlighting and just returns the original text.
 */
final class PlainTextFormatter implements Formatter
{
    public String highlightTerm(String originalText, TokenGroup tokengroup)
    {
        return originalText;
    }
}
