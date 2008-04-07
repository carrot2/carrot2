package org.carrot2.workbench.editors.factory;

import org.eclipse.core.runtime.IConfigurationElement;

public class DedicatedEditorWrapper extends AttributeEditorWrapper
{

    private String sourceId;
    private String algorithmId;
    private String attributeId;

    protected DedicatedEditorWrapper(IConfigurationElement element)
    {
        super(element);
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public String getAlgorithmId()
    {
        return algorithmId;
    }

    public String getAttributeId()
    {
        return attributeId;
    }

}
