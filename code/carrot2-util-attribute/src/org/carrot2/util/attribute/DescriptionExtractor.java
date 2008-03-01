/**
 *
 */
package org.carrot2.util.attribute;



import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;

/**
 *
 */
class DescriptionExtractor implements MetadataExtractor
{
    public boolean extractMetadataItem(AbstractJavaEntity javaEntity,
        JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata)
    {
        final String comment = JavaDocBuilderUtils.toPlainText(javaEntity.getComment());
        if (comment == null)
        {
            return false;
        }

        final int next = JavaDocBuilderUtils.getEndOfFirstSentenceCharIndex(comment);
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
