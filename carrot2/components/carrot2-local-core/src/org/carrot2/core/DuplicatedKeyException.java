
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

/**
 * Thrown when addition of a key-value pair failed because the key already
 * exist in the container.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public final class DuplicatedKeyException extends Exception {
    /**
     * Creates a new exception object with an additional message.
     *
     * @param message The cause of the error.
     */
    public DuplicatedKeyException(String message) {
        super(message);
    }
}
