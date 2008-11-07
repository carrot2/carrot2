
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

import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.attribute.Bindable;

/**
 * Marks stop words based on the current language model.
 * <p>
 * This class saves the following results to the {@link PreprocessingContext}:
 * <ul>
 * <li>{@link AllWords#commonTermFlag}</li>
 * </ul>
 * <p>
 * This class requires that {@link Tokenizer} and {@link CaseNormalizer} be invoked first.
 */
@Bindable(prefix = "StopListMarker")
public final class StopListMarker
{
    /**
     * Marks stop words and saves the results to the <code>context</code>.
     */
    public void mark(PreprocessingContext context)
    {
        final char [][] wordImages = context.allWords.image;
        final boolean [] commonTermFlags = new boolean [wordImages.length];
        final MutableCharArray current = new MutableCharArray("");

        for (int i = 0; i < commonTermFlags.length; i++)
        {
            final char [] imageLowerCase = CharArrayUtils.toLowerCase(wordImages[i]);
            current.reset(imageLowerCase);
            commonTermFlags[i] = context.language.isCommonWord(current);
        }

        context.allWords.commonTermFlag = commonTermFlags;
    }
}
