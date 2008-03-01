/**
 *
 */
package org.carrot2.util.attribute;



import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;

/**
 *
 */
interface MetadataExtractor
{
    public boolean extractMetadataItem(AbstractJavaEntity javaEntity,
        JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata);
}
