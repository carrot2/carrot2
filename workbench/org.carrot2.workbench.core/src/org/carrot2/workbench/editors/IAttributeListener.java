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
    
    /**
     * Invoked when the editor's value has been changed, but changes
     * to the attribute have not been yet committed.
     * <p>
     * Not all editors must send this kind of event. Typically, editors with text
     * boxes will emit on-keystroke content changes.
     */
    public void contentChanging(IAttributeEditor editor, Object value);
}
