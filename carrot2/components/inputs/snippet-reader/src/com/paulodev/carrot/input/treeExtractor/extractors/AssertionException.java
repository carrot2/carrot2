

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


package com.paulodev.carrot.input.treeExtractor.extractors;


/**
 * Thrown to indicate that an assertion has failed.
 */
public class AssertionException
    extends RuntimeException
{
    /**
     * Constructs an AssertionError with no detail message.
     */
    public AssertionException()
    {
    }


    /**
     * This internal constructor does no processing on its string argument, even if it is a null
     * reference.  The public constructors will never call this constructor with a null argument.
     */
    public AssertionException(String detailMessage)
    {
        super(detailMessage);
    }
}
