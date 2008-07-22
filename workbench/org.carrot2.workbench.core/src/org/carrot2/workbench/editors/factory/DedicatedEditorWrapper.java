package org.carrot2.workbench.editors.factory;

import static org.carrot2.workbench.core.helpers.ExtensionConfigurationUtils.*;
import org.eclipse.core.runtime.IConfigurationElement;

public class DedicatedEditorWrapper extends AttributeEditorWrapper
{
    public static final String ATT_ATTRIBUTE_ID = "attribute-id";
    public static final String ATT_COMPONENT_CLASS = "component-class";

    public final String attributeId;
    public final String componentClass;

    public DedicatedEditorWrapper(IConfigurationElement element)
    {
        super(element);

        attributeId = getAttribute(element, ATT_ATTRIBUTE_ID);
        componentClass = getAttribute(element, ATT_COMPONENT_CLASS);
    }

}
