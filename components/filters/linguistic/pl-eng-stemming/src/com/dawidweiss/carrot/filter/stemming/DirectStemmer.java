

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.filter.stemming;


/**
 * This is the generic interface for direct stemmers (converting a word form to its stemmed
 * counterpart).
 */
public interface DirectStemmer
{
    /**
     * Stems a term in characters array (a performance consideration) starting at index
     * <code>beginCharacter</code> (inclusive) and ending at <code>endCharacter</code>
     * (exclusive). This follows the pattern of <code>String.substring</code>
     */
    public String getStem(char [] charArray, int beginCharacter, int endCharacter);
}
