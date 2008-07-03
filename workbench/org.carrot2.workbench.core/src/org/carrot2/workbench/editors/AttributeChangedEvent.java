package org.carrot2.workbench.editors;

import java.util.EventObject;

/**
 * Post-attribute change event data.
 */
@SuppressWarnings("serial")
public class AttributeChangedEvent extends EventObject
{
    public final String key;
    public final Object value;

    public AttributeChangedEvent(IAttributeEditor source)
    {
        this(source, source.getAttributeKey(), source.getValue());
    }

    public AttributeChangedEvent(Object source, String key, Object value)
    {
        super(source);
        this.key = key;
        this.value = value;
    }
}
