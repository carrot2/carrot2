
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

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
            final int [] wordIndices = phrasesWordIndices[featureIndex - wordCount];
            final boolean [] commonTermFlag = context.allWords.commonTermFlag;
            for (int i = 0; i < wordIndices.length; i++)
            {
                final int wordIndex = wordIndices[i];
                appendFormatted(label, wordsImage[wordIndex], i == 0,
                    commonTermFlag[wordIndex]);
                if (i < wordIndices.length - 1)
                {
                    label.append(' ');
                }
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
     */
    public static String format(char [][] image, boolean [] stopWord)
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
                if (i < image.length - 1)
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
        if (CharArrayUtils.capitalizedRatio(image) > 0.0)
        {
            label.append(image);
        }
        else if (isFirst)
        {
            label.append(CharArrayUtils.capitalize(image));
        }
        else
        {
            if (isCommon)
            {
                label.append(CharArrayUtils.toLowerCase(image));
            }
            else
            {
                label.append(CharArrayUtils.capitalize(image));
            }
        }
    }
}
