
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

package org.carrot2.webapp;

/**
 * An exception thrown from the broadcaster's iterator when there is an internal
 * exception in the broadcaster. 
 * 
 * This exception wraps another exception -- it does not contain any specific message.
 * 
 * @author Dawid Weiss
 */
public final class BroadcasterException extends RuntimeException {
    BroadcasterException(Throwable t) {
        super(t);
    }
}
