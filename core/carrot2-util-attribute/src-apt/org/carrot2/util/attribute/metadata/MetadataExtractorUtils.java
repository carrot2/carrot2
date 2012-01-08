
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.metadata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A number of utility methods for working with JavaDoc comments.
 */
final class MetadataExtractorUtils
{
    /**
     * Extracts the first sentence of a JavaDoc comment.
     */
    private static final Pattern FIRST_SENTENCE_PATTERN = Pattern
        .compile("\\.(?<!((\\w\\.){2,5}+))(\\s|\\z)");

    /**
     * Converts a JavaDoc link to text.
     */
    private static final Pattern LINK_TO_TEXT_PATTERN = Pattern
        .compile("\\{@link\\s(.+?)\\}");

    /**
     * Matching of '#' characters in links.
     */
    private static final Pattern SPACE_HASH_PATTERN = Pattern.compile(">#");
    private static final Pattern TYPE_HASH_PATTERN = Pattern.compile("([^>&])#");

    private MetadataExtractorUtils()
    {
        // No instantiation
    }

    /**
     * Returns the index of the last character of the first JavaDoc sentence.
     */
    static int getEndOfFirstSentenceCharIndex(String text)
    {
        final Matcher matcher = FIRST_SENTENCE_PATTERN.matcher(text);
        if (matcher.find())
        {
            return matcher.start();
        }
        else
        {
            return -1;
        }
    }

    /**
     * Converts multiple space, tab, return characters into one space.
     */
    static String normalizeSpaces(String string)
    {
        if (string == null)
        {
            return null;
        }
        return string.replaceAll("[\\s]+", " ");
    }

    /**
     * Converts in-line link tag to text.
     */
    static String renderInlineTags(String comment)
    {
        String content = comment;
        content = LINK_TO_TEXT_PATTERN.matcher(comment).replaceAll("<code>$1</code>");
        content = SPACE_HASH_PATTERN.matcher(content).replaceAll(">");
        content = TYPE_HASH_PATTERN.matcher(content).replaceAll("$1.");
        return content;
    }

    /**
     * Converts JavaDoc comment body to plain text. Currently only normalizes space and
     * renders links. In the future, we might think of dealing with HTML properly.
     */
    static String toPlainText(String comment)
    {
        if (comment == null)
        {
            return null;
        }
        final String normalizedSpace = normalizeSpaces(comment);
        final String linksRendered = renderInlineTags(normalizedSpace);
        final String trimmed = linksRendered.trim();
        if (trimmed.length() == 0)
        {
            return null;
        }

        return trimmed;
    }
}
