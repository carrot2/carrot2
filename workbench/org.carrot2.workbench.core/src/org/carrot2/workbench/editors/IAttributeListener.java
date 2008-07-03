package org.carrot2.workbench.editors;

import java.util.EventListener;

/**
 * Listener receiving attribute modification events from an {@link IAttributeEditor}.
 */
public interface IAttributeListener extends EventListener
{
    /**
     * Invoked when the attribute's value has changed. 
     */
    public void attributeChange(AttributeChangedEvent event);
}
