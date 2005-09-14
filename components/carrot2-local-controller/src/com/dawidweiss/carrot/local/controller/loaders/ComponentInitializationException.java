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
package com.dawidweiss.carrot.local.controller.loaders;


/**
 * Thrown from a component could not be instantiated because
 * of some error. 
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class ComponentInitializationException extends Exception {

    /**
     * Creates a new exception object. 
     * 
     * @param message The cause of the exception.
     */
    public ComponentInitializationException(String message) {
        super(message);
    }
    
    /**
     * Creates a new exception wrapper.
     * 
     * @param message The cause of the exception.
     * @param cause The wrapped exception.
     */
    public ComponentInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
