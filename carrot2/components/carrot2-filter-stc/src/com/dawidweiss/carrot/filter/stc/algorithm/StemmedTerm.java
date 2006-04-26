
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

package com.dawidweiss.carrot.filter.stc.algorithm;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

/**
 * Class representing a word after being stemmed.
 */
public class StemmedTerm {
    /** original word */
    private final String word;

    /** Stemmed form */
    private final String stemmed;

    /** A flag indicating whether this term is on stop-list */
    private final boolean stopword;

    /**
     * Public constructor wrapping a {@link Token}.
     */
    public StemmedTerm(Token token)
    {
        final TypedToken typedToken = (TypedToken) token;
        final StemmedToken stemmedToken = (StemmedToken) token;
        this.word = token.getImage();
        this.stemmed = (stemmedToken.getStem() != null ? stemmedToken.getStem() : word);

        final int tokenType = typedToken.getType();
        if ((tokenType & TypedToken.TOKEN_FLAG_STOPWORD) != 0
                || ((tokenType & TypedToken.TOKEN_FLAG_SENTENCE_DELIM) != 0)
                || ((tokenType & TypedToken.MASK_TOKEN_TYPE) == TypedToken.TOKEN_TYPE_PUNCTUATION)) {
            this.stopword = true;
        } else {
            this.stopword = false;
        }
    }


    /**
     * Indicates whether this term exists in the stop-list
     */
    public boolean isStopWord()
    {
        return stopword;
    }


    /**
     * Overrides hashCode() method of Object class
     */
    public int hashCode()
    {
        return stemmed.hashCode();
    }


    /**
     * Overrides equals() method of Object class
     */
    public boolean equals(Object other)
    {
        if (other instanceof StemmedTerm) {
            // possibility of optimization.
            return stemmed.equals(((StemmedTerm) other).stemmed);
        } else {
            return false;
        }
    }


    /**
     * Implementation of Comparable interface
     */
    public int compareTo(Object p)
    {
        return stemmed.compareTo(((StemmedTerm) p).stemmed);
    }


    /**
     * Returns string representation of this object
     */
    public String toString()
    {
        return "{" + (this.stopword ? "!"
                                    : "") + this.word + "->" + stemmed + "}";
    }


    /**
     * Implementation of StemmedTerm interface
     */
    public String getTerm()
    {
        return this.word;
    }
    
    /**
     * Returns steemed form of oryginal word
     */
    public String getStemmed()
    {
    	return this.stemmed;
    }
}
