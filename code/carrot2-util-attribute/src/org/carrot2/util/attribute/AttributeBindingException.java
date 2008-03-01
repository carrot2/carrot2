package org.carrot2.util.attribute;

/**
 * TODO: add fields specifying e.g. the key of the attribute in question?
 */
@SuppressWarnings("serial")
public class AttributeBindingException extends RuntimeException
{
    public AttributeBindingException()
    {
    }

    public AttributeBindingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AttributeBindingException(String message)
    {
        super(message);
    }

    public AttributeBindingException(Throwable cause)
    {
        super(cause);
    }
    
}
