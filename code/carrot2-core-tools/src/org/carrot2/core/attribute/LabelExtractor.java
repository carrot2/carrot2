/**
 * 
 */
package org.carrot2.core.attribute;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DocletTag;

/**
 *
 */
public class LabelExtractor implements MetadataExtractor
{
    @Override
    public boolean extractMetadataItem(AbstractJavaEntity javaEntity,
        JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata)
    {
        final DocletTag labelTag = javaEntity.getTagByName("label");
        if (labelTag != null && !StringUtils.isBlank(labelTag.getValue()))
        {
            attributeMetadata.setLabel(labelTag.getValue());
            return true;
        }
        else
        {
            return false;
        }
    }
}
