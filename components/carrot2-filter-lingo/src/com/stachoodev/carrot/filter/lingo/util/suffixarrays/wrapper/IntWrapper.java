
/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.stachoodev.carrot.filter.lingo.util.suffixarrays.wrapper;


/**
 *
 */
public interface IntWrapper {
    /**
     *
     */
    public int[] asIntArray();

    /**
     *
     */
    public int length();

    /**
     *
     */
    public void reverse();

    /**
     * Method getStringRepresentation.
     *
     * @param substring
     *
     * @return String
     */
    public String getStringRepresentation(Substring substring);
}
