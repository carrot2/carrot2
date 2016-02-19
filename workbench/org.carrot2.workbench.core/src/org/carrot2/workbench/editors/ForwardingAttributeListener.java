
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

package org.carrot2.workbench.editors;

import java.util.Collection;


/**
 * A class that forwards {@Link IAttributeListener} events to a collection
 * of other {@link IAttributeListener}s.
 */
public final class ForwardingAttributeListener implements IAttributeListener
{
    private Collection<IAttributeListener> listeners;

    public ForwardingAttributeListener(Collection<IAttributeListener> listeners)
    {
        this.listeners = listeners;
    }

    public void valueChanged(AttributeEvent event)
    {
        for (IAttributeListener listener : listeners)
        {
            listener.valueChanged(event);
        }
    }

    public void valueChanging(AttributeEvent event)
    {
        for (IAttributeListener listener : listeners)
        {
            listener.valueChanging(event);
        }
    }
}
