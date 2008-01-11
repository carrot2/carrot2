package org.carrot2.core;


/**
 * Generic processing exception thrown if something went wrong. See specific
 * subclasses for different reasons this exception may be thrown.
 */
@SuppressWarnings("serial")
public class ProcessingException extends RuntimeException
{
    public ProcessingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ProcessingException(String message)
    {
        super(message);
    }

    public ProcessingException(Throwable cause)
    {
        super(cause);
    }
}
