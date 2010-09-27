package org.carrot2.util.attribute.metadata;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

/**
 * Information about a nested bindable field.
 */
public class BindableFieldInfo
{
    /**
     * The nested bindable field declaration.
     */
    private final VariableElement field;
    
    /**
     * {@link #field}'s declared {@Bindable} type.
     */
    private final Element element;

    public BindableFieldInfo(VariableElement field, Element element)
    {
        this.field = field;
        this.element = element;
    }
    
    public VariableElement getField()
    {
        return field;
    }
    
    public Element getFieldElement()
    {
        return element;
    }
}
