/**
 *
 */
package org.carrot2.util.attribute;



import com.thoughtworks.qdox.model.JavaClass;

/**
 *
 */
interface BindableMetadataBuilderListener
{
    public void bindableMetadataBuilt(JavaClass bindable,
        BindableMetadata bindableMetadata);
}
