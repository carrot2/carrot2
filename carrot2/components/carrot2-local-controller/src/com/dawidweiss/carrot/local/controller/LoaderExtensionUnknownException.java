
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
 * Thrown from component and process loader utility classes when a particular
 * loader for the given extension is not known.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LoaderExtensionUnknownException extends Exception {
    /**
     * Creates a new exception.
     */
    public LoaderExtensionUnknownException() {
        super();
    }

    /**
     * Creates a new exception with an information about the cause of the
     * error.
     *
     * @param message
     */
    public LoaderExtensionUnknownException(String message) {
        super(message);
    }
}
