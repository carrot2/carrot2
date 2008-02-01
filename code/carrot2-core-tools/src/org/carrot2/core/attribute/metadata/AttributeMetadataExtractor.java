/**
 * 
 */
package org.carrot2.core.attribute.metadata;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaField;

/**
 *
 */
public interface AttributeMetadataExtractor
{
    public boolean extractMetadataItem(JavaField attributeField,
        JavaDocBuilder javaDocBuilder, AttributeMetadata attributeMetadata);
}
