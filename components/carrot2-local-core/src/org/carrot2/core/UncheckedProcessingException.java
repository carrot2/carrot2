package org.carrot2.core;

/**
 * A subclass of {@link ProcessingException} which signals
 * the process ended with a runtime exception (premature exit from
 * the processing chain).
 * 
 * @author Dawid Weiss
 */
public final class UncheckedProcessingException 
    extends ProcessingException
{
    public UncheckedProcessingException(String message, RuntimeException t) {
        super(message, t);
    }
}
