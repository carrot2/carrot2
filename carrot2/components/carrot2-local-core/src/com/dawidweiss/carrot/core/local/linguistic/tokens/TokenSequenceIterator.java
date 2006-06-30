
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

package com.dawidweiss.carrot.core.local.linguistic.tokens;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over several other sequences.
 * 
 * @author Dawid Weiss
 */
public class TokenSequenceIterator implements Iterator {

    /** An array of input token sequences. */
    private TokenSequence[] sequences;
    
    /** Current token sequence in {@link #sequences} */
    private int currentSequence;
    
    /** Total length of sequences */
    private int totalLength;

    /** Position within the current sequence */
    private int currentSequenceIndex;

    /** Number of tokens in the current sequence */
    private int currentSequenceLength;

    /**
     * Creates a new compound iterator.
     * 
     * @param sequences An array of sequences from which tokens
     * are to be retrieved. This array must not be changed as it used
     * internally.
     */
    public TokenSequenceIterator(final TokenSequence [] sequences) {
        this.sequences = sequences;

        // Calculate total length
        totalLength = 0;
        for (int i = 0; i < sequences.length; i++) {
            totalLength += sequences[i].getLength();
        }

        // Advance to the first sequence
        this.currentSequence = -1;
        nextSequence();
    }

    /**
     * Advance to the next sequence in the compound stream.
     */
    private void nextSequence() {
        currentSequence++;
        if (currentSequence == sequences.length) {
            return;
        }
        this.currentSequenceIndex = 0;
        this.currentSequenceLength = sequences[currentSequence].getLength();
        if (currentSequenceLength == 0) {
            // If this sequence has no elements, advance to the next
            // one immediately.
            nextSequence();
        }
    }

    public boolean hasNext() {
        if (this.currentSequence == this.sequences.length) {
            return false;
        }
        
        if (this.currentSequenceIndex < this.currentSequenceLength) {
            return true;
        }

        return false;
    }

    public final Object next() {
        return nextToken();
    }

    public Token nextToken() {
        if (this.currentSequence == this.sequences.length) {
            throw new NoSuchElementException();
        }

        final Token token = this.sequences[currentSequence].getTokenAt(currentSequenceIndex);
        currentSequenceIndex++;
        if (currentSequenceIndex == currentSequenceLength) {
            nextSequence();
        }
        
        return token;
    }

    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
