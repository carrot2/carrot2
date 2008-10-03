package org.carrot2.workbench.editors;

/**
 * Empty implementation adapter for {@link IAttributeListener}.
 */
public abstract class AttributeListenerAdapter implements IAttributeListener
{
    public void valueChanged(AttributeEvent event)
    {
        // Empty.
    }
    
    public void valueChanging(AttributeEvent event)
    {
        // Empty.
    }
}
