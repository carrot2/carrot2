package org.carrot2.workbench.editors.factory;

import static org.apache.commons.lang.StringUtils.isBlank;

import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.eclipse.core.runtime.IConfigurationElement;

public class DedicatedEditorWrapper extends AttributeEditorWrapper
{
    public static final String ATT_SOURCE_ID = "sourceId";
    public static final String ATT_ALGORITHM_ID = "algorithmId";
    public static final String ATT_ATTRIBUTE_ID = "attributeId";

    private String sourceId;
    private String algorithmId;
    private String attributeId;

    public DedicatedEditorWrapper(IConfigurationElement element)
    {
        super(element);
        sourceId = getAttribute(element, ATT_SOURCE_ID, false);
        algorithmId = getAttribute(element, ATT_ALGORITHM_ID, false);
        attributeId = getAttribute(element, ATT_ATTRIBUTE_ID);
        if (isBlank(sourceId) && isBlank(algorithmId))
        {
            throw new IllegalArgumentException(
                "One of 'sourceId' and 'algorithmId' must be set!");
        }
        if (!isBlank(sourceId) && !isBlank(algorithmId))
        {
            throw new IllegalArgumentException(
                "Only one of 'sourceId' and 'algorithmId' can be set!");
        }
        if (!isBlank(sourceId))
        {
            if (ComponentLoader.SOURCE_LOADER.getComponent(sourceId) == null)
            {
                throw new IllegalArgumentException("There is no source with id '"
                    + sourceId + "'!");
            }
        }
        if (!isBlank(algorithmId))
        {
            if (ComponentLoader.ALGORITHM_LOADER.getComponent(algorithmId) == null)
            {
                throw new IllegalArgumentException("There is no algorithm with id '"
                    + algorithmId + "'!");
            }
        }
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
