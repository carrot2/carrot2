
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
