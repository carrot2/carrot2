/**
 *
 */
package org.carrot2.util.attribute.metadata;


import com.thoughtworks.qdox.model.JavaClass;

/**
 *
 */
public interface BindableMetadataBuilderListener
{
    public void bindableMetadataBuilt(JavaClass bindable,
        BindableMetadata bindableMetadata);
}
