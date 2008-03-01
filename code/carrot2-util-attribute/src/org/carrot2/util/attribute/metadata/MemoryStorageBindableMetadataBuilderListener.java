/**
 *
 */
package org.carrot2.util.attribute.metadata;

import java.util.Map;


import com.google.common.collect.Maps;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * Stores attribute metadata in a map using fully qualified class names as keys.
 */
public class MemoryStorageBindableMetadataBuilderListener implements
    BindableMetadataBuilderListener
{
    private final Map<String, BindableMetadata> bindableMetadata = Maps.newLinkedHashMap();

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
