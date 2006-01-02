
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
package com.dawidweiss.carrot.core.local;

/**
 * An exception thrown if an operation has been attempted on a component
 * identifier that is not associated with any factory.
 *
 * @author Dawid Weiss
 *
 * @see LocalComponentFactory
 * @see LocalComponent
 */
public class MissingComponentException extends Exception {
    /**
     * Creates a new exception object with no explanation.
     */
    public MissingComponentException() {
        super();
    }

    /**
     * Creates a new exception object with  an associated message explaining
     * the cause of the error.
     *
     * @param message A message to associate with the exception.
     */
    public MissingComponentException(String message) {
        super(message);
    }

    /**
     * Creates a new exception object with  an associated root cause -- a
     * linked {@link Throwable} object.
     *
     * @param t A {@link Throwable} object to link as the root cause of the
     *        exception.
     */
    public MissingComponentException(Throwable t) {
        super(t);
    }

    /**
     * Creates a new exception object with  an associated explanation message
     * and a root cause of the exception -- a  linked {@link Throwable}
     * object.
     *
     * @param message A message to associate with the exception.
     * @param t A {@link Throwable} object to link as the root cause of the
     *        exception.
     */
    public MissingComponentException(String message, Throwable t) {
        super(message, t);
    }
}
