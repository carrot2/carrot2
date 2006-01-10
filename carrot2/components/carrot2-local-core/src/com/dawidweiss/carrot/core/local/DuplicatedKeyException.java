
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

package com.dawidweiss.carrot.core.local;

/**
 * Thrown when addition of a key-value pair failed because the key already
 * exist in the container.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class DuplicatedKeyException extends RuntimeException {
    /**
     * Creates a new exception object.
     */
    public DuplicatedKeyException() {
        super();
    }

    /**
     * Creates a new exception object with an additional message.
     *
     * @param message The cause of the error.
     */
    public DuplicatedKeyException(String message) {
        super(message);
    }
}
