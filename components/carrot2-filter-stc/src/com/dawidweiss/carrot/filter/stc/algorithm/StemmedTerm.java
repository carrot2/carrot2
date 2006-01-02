
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.filter.stc.algorithm;


/**
 * Class representing a word after being stemmed.
 */
public interface StemmedTerm
{
    /** unsupported operation exception if stemming is not available. */
    public static final UnsupportedOperationException STEM_NOT_AVAILABLE = new UnsupportedOperationException(
            "Stems are not available for the called stemmer."
        );

    /**
     * Indicates whether this term exists in the stop-list
     */
    public boolean isStopWord();


    /**
     * Sets the stop-word flag to true or false. The result obtained from isStopWord must be
     * consistent with this method.
     */
    public void setStopWord(boolean value);


    /**
     * Returns the original form of this term.
     */
    public String getTerm();


    /**
     * Returns the stemmed form of original term.
     *
     * @throws UnsupportedOperationException when the object doesn't implement this interface
     *         (stemmed form doesn't exist - only equals method is implemented).
     */
    public String getStemmed()
        throws UnsupportedOperationException;
}
