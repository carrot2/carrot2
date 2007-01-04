
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

package org.carrot2.filter.lingo.util.suffixarrays.wrapper;


/**
 *
 */
public class StringIntWrapper extends AbstractIntWrapper {
    /** */

    /** DOCUMENT ME! */
    private String stringData;

    /**
     *
     */
    public StringIntWrapper(String stringData) {
        super();

        this.stringData = stringData;

        createIntData();
    }

    /**
     * @see org.carrot2.filter.lingo.util.suffixarrays.wrapper.IntWrapper#getStringRepresentation(org.carrot2.filter.lingo.util.suffixarrays.wrapper.Substring)
     */
    public String getStringRepresentation(Substring substring) {
        return stringData.substring(substring.getFrom(), substring.getTo());
    }

    /* ------------------------------------------------------------------ protected section */
    /* ------------------------------------------------------------------ private methods */
    /**
     *
     */
    private void createIntData() {
        intData = new int[stringData.length() + 1];

        // Any better way to do this ?
        byte[] stringAsBytes = stringData.getBytes();

        for (int i = 0; i < stringData.length(); i++) {
            intData[i] = stringAsBytes[i];
        }

        // This is to facilitate suffix comparisons
        intData[stringData.length()] = -1;
    }
}
