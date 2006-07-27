
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

package org.carrot2.core;

/**
 * An exception that may be thrown during processing of a query.
 *
 * @author Dawid Weiss
 */
public class ProcessingException extends Exception {
    /**
     * Creates a new processing exception object with some explanation of the
     * cause of the error.
     *
     * @param message A message to associate with the exception.
     */
    public ProcessingException(String message) {
        super(message);
    }

    /**
     * Creates a new exception object with  an associated root cause -- a
     * linked {@link Throwable} object.
     *
     * @param t A {@link Throwable} object to link as the root cause of the
     *        exception.
     */
    public ProcessingException(Throwable t) {
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
    public ProcessingException(String message, Throwable t) {
        super(message, t);
    }
}
