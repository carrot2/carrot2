/**
 * 
 */
package org.carrot2.core.attribute;

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
