/**
 * 
 */
package org.carrot2.core.attribute;

import java.util.Map;

import com.google.common.collect.Maps;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * Stores attribute metadata in a map using fully qualified class names as keys.
 */
public class MemoryStorageBindableMetadataBuilderListener implements
    BindableMetadataBuilderListener
{
    private Map<String, BindableMetadata> bindableMetadata = Maps.newHashMap();

    @Override
    public void bindableMetadataBuilt(JavaClass bindable,
        BindableMetadata metadata)
    {
        bindableMetadata.put(bindable.getFullyQualifiedName(), metadata);
    }

    public Map<String, BindableMetadata> getBindableMetadata()
    {
        return bindableMetadata;
    }
}
