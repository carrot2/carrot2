/**
 * 
 */
package org.carrot2.core.attribute;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaField;

/**
 *
 */
public class AttributeLabelExtractor implements AttributeMetadataExtractor
{
    @Override
    public boolean extractMetadataItem(JavaField attributeField,
        JavaDocBuilder javaDocBuilder, AttributeMetadata attributeMetadata)
    {
        final DocletTag labelTag = attributeField.getTagByName("label");
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
