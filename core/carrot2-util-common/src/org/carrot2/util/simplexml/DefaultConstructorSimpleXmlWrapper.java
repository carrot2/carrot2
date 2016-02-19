
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.simplexml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * A generic wrapper for values of classes with default constructors. This wrapper
 * serializes the value's class name and upon deserialization request, it invokes the
 * class' default constructor and returns the newly created instance.
 */
@Root(name = "value")
public class DefaultConstructorSimpleXmlWrapper implements ISimpleXmlWrapper<Object>
{
    @Attribute(name = "class")
    private Class<?> clazz;

    public Object getValue()
    {
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Unable to deserialize instance of "
                + clazz.getName(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Unable to deserialize instance of "
                + clazz.getName(), e);
        }
    }

    public void setValue(Object value)
    {
        clazz = value.getClass();
    }
}
