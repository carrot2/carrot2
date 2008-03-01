/**
 *
 */
package org.carrot2.util.attribute.metadata;


import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;

/**
 *
 */
public class TitleExtractor implements MetadataExtractor
{
    public boolean extractMetadataItem(AbstractJavaEntity javaEntity,
        JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata)
    {
        final String comment = JavaDocBuilderUtils.toPlainText(javaEntity.getComment());
        if (comment == null)
        {
            return false;
        }

        final int next = JavaDocBuilderUtils
            .getEndOfFirstSentenceCharIndex(comment);
        if (next >= 0)
        {
            // Strip off the last "."
            final String firstSentence = comment.substring(0, next).trim();
            attributeMetadata.setTitle(firstSentence);
            return true;
        }
        else
        {
            attributeMetadata.setTitle(comment);
            return true;
        }
    }
}
