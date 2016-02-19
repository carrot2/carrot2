
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

package org.carrot2.util;

import java.util.Stack;

/**
 * Small utility to push a system property and then restore the previous value.
 */
public final class SystemPropertyStack
{
    private final String propertyName;
    private final Stack<String> values = new Stack<String>();

    public SystemPropertyStack(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public void pop()
    {
        String value = values.pop();
        if (value != null)
            System.setProperty(propertyName, value);
        else
            System.clearProperty(propertyName);        
    }

    public void push(String newValue)
    {
        values.push(System.getProperty(propertyName));
        System.setProperty(propertyName, newValue);
    }
}
