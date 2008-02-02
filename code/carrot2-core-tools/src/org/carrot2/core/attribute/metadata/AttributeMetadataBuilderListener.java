/**
 * 
 */
package org.carrot2.core.attribute.metadata;

import java.util.Map;

import com.thoughtworks.qdox.model.JavaClass;

/**
 *
 */
public interface AttributeMetadataBuilderListener
{
    public void attributeMetadataForBindableBuilt(JavaClass bindable,
        Map<String, AttributeMetadata> metadata);
}
