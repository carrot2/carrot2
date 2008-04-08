package org.carrot2.workbench.editors.factory;

import org.eclipse.core.runtime.IConfigurationElement;

public class DedicatedEditorWrapper extends AttributeEditorWrapper
{
    public static final String ATT_ATTRIBUTE_ID = "attributeId";
    public static final String ATT_COMPONENT_CLASS = "componentClass";

    private String attributeId;
    private String componentClass;

    public DedicatedEditorWrapper(IConfigurationElement element)
    {
        super(element);
        attributeId = getAttribute(element, ATT_ATTRIBUTE_ID);
        componentClass = getAttribute(element, ATT_COMPONENT_CLASS);
    }

    public String getAttributeId()
    {
        return attributeId;
    }

    public String getComponentClass()
    {
        return componentClass;
    }

}
