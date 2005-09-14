
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.local.controller;

/**
 * Thrown from the controller if there is no local process with the provided
 * identifier.
 */
public class MissingProcessException extends Exception {
    /**
     * Creates a new exception object.
     */
    public MissingProcessException() {
        super();
    }

    /**
     * Creates a new exception object with a detailed message.
     */
    public MissingProcessException(String arg0) {
        super(arg0);
    }
}
