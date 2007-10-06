
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.linguistic.tokens;

/**
 * A read-only sequence of {@link Token} objects. Tokens from the sequence can
 * be retrieved individually, or in bulk by copying to another array.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface TokenSequence {
    /**
     * @return Returns the length of this sequence of tokens.
     */
    public int getLength();

    /**
     * @param index The index of the token to be returned.
     *
     * @return An instance of {@link Token}, or its specialized subclass.
     */
    public Token getTokenAt(int index);

    /**
     * Copies a subsequence of tokens from this token sequence to another
     * array.
     *
     * @param destination The destination array.
     * @param startAt Start copying from this index.
     * @param destinationStartAt Starting index in the destination array.
     * @param maxLength Maximum number of tokens to copy.
     *
     * @return Returns the number of actually copied tokens.
     */
    public int copyTo(Token[] destination, int startAt, int destinationStartAt,
        int maxLength);
}
