
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

package org.carrot2.text.preprocessing;

import org.carrot2.text.analysis.TokenTypeUtils;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.attribute.Bindable;

/**
 * Formats cluster labels for final rendering.
 */
@Bindable
public class LabelFormatter
{
    /**
     * Formats a cluster label for final rendering.
     */
    public String format(PreprocessingContext context, int featureIndex)
    {
        final char [][] wordsImage = context.allWords.image;
        final int [][] phrasesWordIndices = context.allPhrases.wordIndices;
        final int wordCount = wordsImage.length;

        final StringBuilder label = new StringBuilder();
        if (featureIndex < wordCount)
        {
            final char [] image = wordsImage[featureIndex];
            appendFormatted(label, image, true, false);
        }
        else
        {
            final boolean insertSpace = context.language.getLanguageCode().usesSpaceDelimiters();
            final int [] wordIndices = phrasesWordIndices[featureIndex - wordCount];
            final short [] termTypes = context.allWords.type;
            for (int i = 0; i < wordIndices.length; i++)
            {
                if (insertSpace && i > 0) label.append(' ');

                final int wordIndex = wordIndices[i];
                appendFormatted(label, wordsImage[wordIndex], i == 0,
                    TokenTypeUtils.isCommon(termTypes[wordIndex]));
            }
        }

        return label.toString();
    }

    /**
     * A method for formatting cluster labels that is not coupled to
     * {@link PreprocessingContext#allLabels} and can be used in algorithms that do not
     * use the full preprocessing pipeline.
     * 
     * @param image images of the words making the label.
     * @param stopWord determines whether the corresponding word of the label is a stop
     *            word
     * @param joinWithSpace if <code>true</code>, label tokens will be joined with a space
     *            character, if <code>false</code>, no extra characters will be inserted
     *            between label tokens.
     */
    public static String format(char [][] image, boolean [] stopWord,
        boolean joinWithSpace)
    {
        final StringBuilder label = new StringBuilder();
        if (image.length == 1)
        {
            appendFormatted(label, image[0], true, stopWord[0]);
        }
        else
        {
            for (int i = 0; i < image.length; i++)
            {
                appendFormatted(label, image[i], i == 0, stopWord[i]);
                if (joinWithSpace && i < image.length - 1)
                {
                    label.append(' ');
                }
            }
        }

        return label.toString();
    }

    /**
     * Appends a segment of the label to the buffer, capitalized or lower-cased depending
     * on the position and content.
     */
    private static void appendFormatted(final StringBuilder label, final char [] image,
        boolean isFirst, boolean isCommon)
    {
        if (CharArrayUtils.hasCapitalizedLetters(image))
        {
            label.append(image);
        }
        else if (isFirst)
        {
            label.append(CharArrayUtils.toCapitalizedCopy(image));
        }
        else
        {
            if (isCommon)
            {
                label.append(CharArrayUtils.toLowerCaseCopy(image));
            }
            else
            {
                label.append(CharArrayUtils.toCapitalizedCopy(image));
            }
        }
    }
}
