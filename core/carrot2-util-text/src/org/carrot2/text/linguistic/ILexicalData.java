
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

package org.carrot2.text.linguistic;

import org.carrot2.text.util.MutableCharArray;

/**
 * Additional lexical information for a given language.
 */
public interface ILexicalData
{
    /**
     * @return Returns <code>true</code> if <code>word</code> is common (meaningless) in
     *         this language. Such words are referred to as "stop words" and are usually
     *         ignored in information retrieval tasks. Depending on the implementation,
     *         <code>word</code> may be lower-cased internally.
     */
    public boolean isCommonWord(MutableCharArray word);

    /**
     * @return Returns <code>true</code> if the <code>formattedLabel</code> has been
     *         declared a stop label (meaningless) in this language. This is a very
     *         low-level tuning method.
     */
    public boolean isStopLabel(CharSequence formattedLabel);
}
