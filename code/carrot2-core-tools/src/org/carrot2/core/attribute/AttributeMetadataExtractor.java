/**
 * 
 */
package org.carrot2.core.attribute;

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
