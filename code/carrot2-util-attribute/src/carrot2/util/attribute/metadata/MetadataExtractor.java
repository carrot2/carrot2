/**
 *
 */
package carrot2.util.attribute.metadata;


import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;

/**
 *
 */
public interface MetadataExtractor
{
    public boolean extractMetadataItem(AbstractJavaEntity javaEntity,
        JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata);
}
