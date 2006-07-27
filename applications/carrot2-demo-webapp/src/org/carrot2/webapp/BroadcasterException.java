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
