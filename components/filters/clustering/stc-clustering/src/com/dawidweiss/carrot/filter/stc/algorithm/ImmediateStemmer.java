

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


package com.dawidweiss.carrot.filter.stc.algorithm;


/**
 * A stemmer interface for immediate stemmers (those capable of returning the stemmed form of a
 * word immediately).
 */
public interface ImmediateStemmer
{
    /**
     * Stems a single word and returns the result immediately. Notice that the classes implementing
     * this method may convert the string to lowercase.
     */
    public String stemWord(String word);
}
