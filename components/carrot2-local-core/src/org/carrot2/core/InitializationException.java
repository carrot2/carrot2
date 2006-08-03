package org.carrot2.core;

/**
 * An exception thrown when a process cannot be initialized
 * due to some problem or incompatibility. 
 * 
 * @author Dawid Weiss
 */
public class InitializationException extends Exception {
    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
