
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
