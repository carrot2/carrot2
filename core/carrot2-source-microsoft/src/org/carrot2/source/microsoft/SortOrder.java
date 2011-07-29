package org.carrot2.source.microsoft;

import org.carrot2.util.StringUtils;

/**
 * Sort order for {@link Bing2NewsDocumentSource}.
 */
public enum SortOrder
{
    DATE, RELEVANCE;

    @Override
    public String toString()
    {
        return StringUtils.identifierToHumanReadable(name());
    }
}
