
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.remote.controller.util;

import java.lang.reflect.Modifier;


public class ClassMapping
    implements PostConfigureCheck
{
    private Class  clazz;
    private String elementName;

    public ClassMapping()
    {
    }
    
    public ClassMapping(String elementName, String className)
        throws ClassNotFoundException
    {
        setClassName( className );
        setElementName( elementName );
    }


    public void setClassName(String className)
        throws ClassNotFoundException
    {
        this.clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())
            || Modifier.isPublic(clazz.getModifiers()) == false)
            throw new IllegalArgumentException("Class " + className
                + " must be a public non-abstract class.");
    }

    public String getClassName()
    {
        return clazz.getName();
    }

    public void setElementName(String elementName)
    {
        this.elementName = elementName;
    }
    
    public String getElementName()
    {
        return this.elementName;
    }

    public String assertConfigured()
    {
        if (clazz == null)
            return "Class name is required.";
        if (elementName == null || "".equals(elementName.trim()))
            return "Element name is required and cannot be an empty string.";
        return null;
    }
    
    public Object getNewInstance()
        throws InstantiationException, IllegalAccessException
    {
        return this.clazz.newInstance();
    }
}
