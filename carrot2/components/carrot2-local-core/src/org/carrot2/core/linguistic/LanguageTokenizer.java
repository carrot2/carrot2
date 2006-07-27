
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

package org.carrot2.core.linguistic;

import org.carrot2.core.linguistic.tokens.Token;

import java.io.Reader;


/**
 * A tokenizer for a specific language. Tokenizers <b>need not be  thread
 * safe</b>. Every call to <code>restartTokenizationOn</code> effectively
 * purges any previous tokenization data.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface LanguageTokenizer {
    /**
     * Restarts tokenization for a given input stream
     */
    public void restartTokenizationOn(Reader stream);

    /**
     * Allows the implementation to reuse resources. All Tokens returned from
     * the tokenizer become invalid after this is called.
     */
    public void reuse();

    /**
     * Fills the array with tokens (valid until the next call to
     * <code>restartTokenizationOn()</code>. The array is filled from
     * <code>startAt</code> until the end of the array or the end of the
     * tokenized stream.
     * 
     * @return The number of parsed tokens placed in the array, or 0 if no more
     *         tokens are available.
     */
    public int getNextTokens(Token [] array, int startAt);
}
