/**
 * 
 */
package org.carrot2.core.attribute.metadata;

import java.util.Map;

import com.google.common.collect.Maps;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * Stores attribute metadata in a map using fully qualified class names as keys.
 */
public class MapStorageAttributeMetadataBuilderListener implements
    AttributeMetadataBuilderListener
{
    private Map<String, Map<String, AttributeMetadata>> attributeMetadata = Maps
        .newHashMap();

    @Override
    public void attributeMetadataForBindableBuilt(JavaClass bindable,
        Map<String, AttributeMetadata> metadata)
    {
        attributeMetadata.put(bindable.getFullyQualifiedName(), metadata);
    }

    public Map<String, Map<String, AttributeMetadata>> getAttributeMetadata()
    {
        return attributeMetadata;
    }
}
