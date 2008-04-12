package org.carrot2.workbench.editors;

import java.util.EventObject;

@SuppressWarnings("serial")
public class AttributeChangeEvent extends EventObject
{
    public final String key;
    public final Object value;

    public AttributeChangeEvent(IAttributeEditor source)
    {
        super(source);
        key = source.getAttributeKey();
        value = source.getValue();
    }

}
