package org.carrot2.util.attribute;

/**
 * 
 */
@SuppressWarnings("serial")
public class AttributeBindingException extends RuntimeException
{
    public final String attributeKey;

    public AttributeBindingException(String attributeKey)
    {
        this.attributeKey = attributeKey;
    }

    public AttributeBindingException(String attributeKey, String message, Throwable cause)
    {
        super(message, cause);
        this.attributeKey = attributeKey;
    }

    public AttributeBindingException(String attributeKey, String message)
    {
        super(message);
        this.attributeKey = attributeKey;
    }

    public AttributeBindingException(String attributeKey, Throwable cause)
    {
        super(cause);
        this.attributeKey = attributeKey;
    }
}
