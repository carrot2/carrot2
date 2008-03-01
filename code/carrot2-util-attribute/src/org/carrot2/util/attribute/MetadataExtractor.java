/**
 *
 */
package org.carrot2.util.attribute;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DocletTag;

/**
 *
 */
abstract class MetadataExtractor
{
    abstract boolean extractMetadataItem(AbstractJavaEntity javaEntity,
        JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata);

    static class DescriptionExtractor extends MetadataExtractor
    {
        public boolean extractMetadataItem(AbstractJavaEntity javaEntity,
            JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata)
        {
            final String comment = MetadataExtractorUtils.toPlainText(javaEntity
                .getComment());
            if (comment == null)
            {
                return false;
            }

            final int next = MetadataExtractorUtils.getEndOfFirstSentenceCharIndex(comment);
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

    static class LabelExtractor extends MetadataExtractor
    {
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

    static class TitleExtractor extends MetadataExtractor
    {
        public boolean extractMetadataItem(AbstractJavaEntity javaEntity,
            JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata)
        {
            final String comment = MetadataExtractorUtils.toPlainText(javaEntity
                .getComment());
            if (comment == null)
            {
                return false;
            }

            final int next = MetadataExtractorUtils.getEndOfFirstSentenceCharIndex(comment);
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
}
