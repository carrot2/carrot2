/**
 * 
 */
package org.carrot2.core.attribute;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;

/**
 *
 */
public class TitleExtractor implements MetadataExtractor
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
        if (next >= 0)
        {
            // Strip off the last "."
            final String firstSentence = commentTrimmed.substring(0, next).trim();
            attributeMetadata.setTitle(firstSentence);
            return true;
        }
        else
        {
            attributeMetadata.setTitle(commentTrimmed);
            return true;
        }
    }
}
