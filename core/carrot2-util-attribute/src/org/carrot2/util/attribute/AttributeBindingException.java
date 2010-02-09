
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

/**
 * An exception thrown when problems occur in
 * {@link AttributeBinder#bind(Object, java.util.Map, Class, Class...)}.
 */
@SuppressWarnings("serial")
public class AttributeBindingException extends RuntimeException
{
    /**
     * Key of the attribute involved as defined by {@link Attribute#key()}.
     */
    public final String attributeKey;

    /**
     *
     */
    public AttributeBindingException(String attributeKey)
    {
        this.attributeKey = attributeKey;
    }

    /**
     *
     */
    public AttributeBindingException(String attributeKey, String message, Throwable cause)
    {
        super(message, cause);
        this.attributeKey = attributeKey;
    }

    /**
     *
     */
    public AttributeBindingException(String attributeKey, String message)
    {
        super(message);
        this.attributeKey = attributeKey;
    }

    /**
     *
     */
    public AttributeBindingException(String attributeKey, Throwable cause)
    {
        super(cause);
        this.attributeKey = attributeKey;
    }
}
