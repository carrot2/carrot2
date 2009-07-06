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

import java.io.*;

import org.carrot2.util.ExceptionUtils;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.simpleframework.xml.load.Persister;

/**
 * Utilities for creating {@link IMemento}s using <code>org.simpleframework.xml</code>
 * library.
 */
public final class SimpleXmlMemento
{
    /**
     * Creates an {@link XMLMemento} from a serializable object.
     */
    public static IMemento toMemento(Object benchmarkSettings) throws IOException
    {
        try
        {
            final StringWriter w = new StringWriter();
            new Persister().write(benchmarkSettings, w);
            XMLMemento memento = XMLMemento.createReadRoot(new StringReader(w.toString()));
            return memento;
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAs(IOException.class, e);
        }
    }

    /**
     * Reads an object from a memento. The memento's type (root) must be aligned with
     * the XML root for simple XML. 
     */
    public static <T> T fromMemento(Class<T> clazz, IMemento memento) throws IOException
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
}
