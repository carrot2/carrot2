package org.carrot2.util.attribute;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DocletTag;

/**
 * Extracts certain items of metadata from Java source.
 */
abstract class MetadataExtractor
{
    /**
     * Extracts some metadata from the provided Java source element and sets it on the
     * provided <code>attributeMetadata</code>.
     */
    abstract boolean extractMetadataItem(AbstractJavaEntity javaEntity,
        JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata);

    /**
     * Extracts attribute/ bindable descriptions. Anything in the JavaDoc comment that
     * follows after its first sentence becomes a description.
     */
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

            final int next = MetadataExtractorUtils
                .getEndOfFirstSentenceCharIndex(comment);
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

    /**
     * Extracts attribute/ bindable labels. Labels are extracted from the dedicated
     * (label) JavaDoc tag.
     */
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

    /**
     * Extracts attribute/ bindable titles. The first sentence of the JavaDoc comment
     * becomes the title.
     */
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

            final int next = MetadataExtractorUtils
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
}
