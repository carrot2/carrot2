/**
 * 
 */
package org.carrot2.core.attribute;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;

/**
 *
 */
public class DescriptionExtractor implements MetadataExtractor
{
    @Override
    public boolean extractMetadataItem(AbstractJavaEntity javaEntity,
        JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata)
    {
        final String comment = JavaDocBuilderUtils.normalizeSpaces(javaEntity
            .getComment());
        if (comment == null)
        {
            return false;
        }

        final String commentTrimmed = comment.trim();
        if (commentTrimmed.length() == 0)
        {
            return false;
        }

        final int next = JavaDocBuilderUtils
            .getEndOfFirstSenteceCharIndex(commentTrimmed);
        if (next > 0 && next < comment.length())
        {
            final String description = comment.substring(next + 1).trim();
            if (description.length() > 0)
            {
                attributeMetadata.setDescription(description);
                return true;
            }
        }
        return false;
    }
}
