/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.helpers;

import java.beans.Introspector;
import java.io.*;

import org.apache.commons.lang.StringUtils;
import org.carrot2.util.ExceptionUtils;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Persister;

/**
 * Utilities for creating {@link IMemento}s using <code>org.simpleframework.xml</code>
 * library.
 */
public final class SimpleXmlMemento
{
    /**
     * Creates an {@link XMLMemento} from Simple XML-annotated bean.
     */
    static IMemento toMemento(Object benchmarkSettings) throws IOException
    {
        try
        {
            final StringWriter w = new StringWriter();
            new Persister().write(benchmarkSettings, w);
            XMLMemento memento = XMLMemento
                .createReadRoot(new StringReader(w.toString()));
            return memento;
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAs(IOException.class, e);
        }
    }

    /**
     * Reads an object from a {@link IMemento}. The memento's type (root) must equal the
     * bean's {@link Root} annotation name attribute.
     */
    static <T> T fromMemento(Class<T> clazz, IMemento memento) throws IOException
    {
        try
        {
            final StringWriter sw = new StringWriter();
            final XMLMemento m = XMLMemento.createWriteRoot(memento.getType());
            m.putMemento(memento);
            m.save(sw);
            return new Persister().read(clazz, new StringReader(sw.toString()));
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAs(IOException.class, e);
        }
    }

    /**
     * A shortcut for:
     * 
     * <pre>
     * fromMemento(clazz, memento.getChild(childName))
     * </pre>
     * 
     * verifying precondition that only one child of a given name exists.
     */
    static <T> T fromMemento(Class<T> clazz, IMemento memento, String childName)
        throws IOException
    {
        final IMemento [] children = memento.getChildren(childName);
        if (children.length != 1)
        {
            throw new IOException("Expected a single node named '" + childName
                + "' under memento '" + memento.getType() + "'.");
        }
        return fromMemento(clazz, children[0]);
    }

    /**
     * Convert a memento to a string.
     */
    public static String toString(IMemento memento)
    {
        if (!(memento instanceof XMLMemento))
        {
            XMLMemento m = XMLMemento.createWriteRoot(memento.getType());
            m.putMemento(memento);
            memento = m;
        }

        try
        {
            final StringWriter w = new StringWriter();
            ((XMLMemento) memento).save(w);
            return w.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a child node to a given memento, named after the object's {@link Root}
     * annotation.
     */
    public static void addChild(IMemento memento, Object object) throws IOException
    {
        checkObject(object);

        final IMemento child = toMemento(object);
        memento.createChild(child.getType()).putMemento(child);
    }

    /**
     * Returns an object deserialized from a child node of a given memento.
     */
    public static <T> T getChild(Class<T> clazz, IMemento memento) throws IOException
    {
        Root root = clazz.getAnnotation(Root.class);

        if (root == null)
        {
            throw new IllegalArgumentException("Missing @Root annotation on: "
                + clazz.getName());
        }

        String childName = root.name();
        if (StringUtils.isEmpty(childName))
        {
            childName = getClassName(clazz);
        }

        IMemento [] children = memento.getChildren(childName);
        if (children.length != 1)
        {
            throw new IOException("More than one child named '" + childName + "':"
                + children.length);
        }

        return fromMemento(clazz, children[0]);
    }

    /**
     * Check if the target contains simple XML's annotation.
     */
    private static void checkObject(Object object)
    {
        Root root = object.getClass().getAnnotation(Root.class);
        if (root == null)
        {
            throw new IllegalArgumentException("Missing @Root annotation on: "
                + object.getClass());
        }
    }

    /**
     * Mimics SimpleXML's naming for classes without {@link Root#name()}.
     */
    private static String getClassName(Class<?> type)
    {
        if (type.isArray()) type = type.getComponentType();
        final String name = type.getSimpleName();
        if (type.isPrimitive()) return name;
        else return Introspector.decapitalize(name);
    }
}
