/**
 * 
 */
package org.carrot2.core.attribute;

import com.thoughtworks.qdox.model.JavaClass;

/**
 *
 */
public interface BindableMetadataBuilderListener
{
    public void bindableMetadataBuilt(JavaClass bindable,
        BindableMetadata bindableMetadata);
}
