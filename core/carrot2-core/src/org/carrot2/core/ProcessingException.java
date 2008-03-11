package org.carrot2.core;

/**
 * An exception thrown if processing failed. For certain specific failure reasons,
 * subclasses of this exception have been defined.
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
