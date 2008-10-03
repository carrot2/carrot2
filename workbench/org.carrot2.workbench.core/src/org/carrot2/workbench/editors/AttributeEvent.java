package org.carrot2.workbench.editors;

import java.util.EventObject;

/**
 * Event holding data related to events on attributes.
 */
@SuppressWarnings("serial")
public class AttributeEvent extends EventObject
{
    /** Attribute key. */
    public final String key;
    
    /** Attribute value. */
    public final Object value;

    /*
     * 
     */
    public AttributeEvent(IAttributeEditor source)
    {
        this(source, source.getAttributeKey(), source.getValue());
    }

    /*
     * 
     */
    public AttributeEvent(Object source, String key, Object value)
    {
        super(source);
        this.key = key;
        this.value = value;
    }
}
