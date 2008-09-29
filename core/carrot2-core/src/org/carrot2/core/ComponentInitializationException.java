package org.carrot2.core;

/**
 * Exception thrown if component initialization was unsuccessful.
 *
 * @see ProcessingComponent#init(ControllerContext)
 */
@SuppressWarnings("serial")
public class ComponentInitializationException extends ProcessingException
{
    public ComponentInitializationException(String message)
    {
        super(message);
    }

    public ComponentInitializationException(Throwable t)
    {
        super(t);
    }

    public ComponentInitializationException(String message, Throwable t)
    {
        super(message, t);
    }
}
