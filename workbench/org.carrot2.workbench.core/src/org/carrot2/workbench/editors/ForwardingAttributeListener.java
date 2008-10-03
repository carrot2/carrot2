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
