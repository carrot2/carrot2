/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.suffixarrays.wrapper;

/**
 * An integer wrapper that has access to information on tokens' types and can
 * thus provide some additional information, such as whether the token
 * corresponding to an integer code is a stop word.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TypeAwareIntWrapper extends IntWrapper
{
    /**
     * Checs if the token with given code is a stop word.
     * 
     * @param token integer code
     * @return true if token corresponding to the given code is a stop word
     * @throws NullPointerException when token with the given code does not
     *             exist
     */
    public boolean isStopWord(int code);
}