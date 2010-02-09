
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
     * Extracts attribute/ bindable descriptions. Anything in the JavaDoc comment that
     * follows after its first sentence becomes a description.
     */
    static DescriptionExtractor DESCRIPTION_EXTRACTOR = new DescriptionExtractor();

    /**
     * Extracts attribute/ bindable titles. The first sentence of the JavaDoc comment
     * becomes the title.
     */
    static TitleExtractor TITLE_EXTRACTOR = new TitleExtractor();

    /**
     * Extracts attribute labels from the "label" JavaDoc tag.
     */
    static SimpleTagExtractor LABEL_EXTRACTOR = new SimpleTagExtractor("label",
        new LabelSetter());

    /**
     * Extracts attribute group name from the "group" JavaDoc tag.
     */
    static SimpleTagExtractor GROUP_EXTRACTOR = new SimpleTagExtractor("group",
        new GroupSetter());

    /**
     * Extracts attribute levels from the "level" JavaDoc tag.
     */
    static SimpleTagExtractor LEVEL_EXTRACTOR = new SimpleTagExtractor("level",
        new LevelSetter());

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
    private static class DescriptionExtractor extends MetadataExtractor
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
     * Extracts attribute/ bindable titles. The first sentence of the JavaDoc comment
     * becomes the title.
     */
    private static class TitleExtractor extends MetadataExtractor
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

    /**
     * Extracts metadata from JavaDoc simple tags.
     */
    private static class SimpleTagExtractor extends MetadataExtractor
    {
        private String tagName;
        private IMetadataValueSetter valueSetter;

        public SimpleTagExtractor(String tagName, IMetadataValueSetter valueSetter)
        {
            this.tagName = tagName;
            this.valueSetter = valueSetter;
        }

        public boolean extractMetadataItem(AbstractJavaEntity javaEntity,
            JavaDocBuilder javaDocBuilder, CommonMetadata attributeMetadata)
        {
            final DocletTag labelTag = javaEntity.getTagByName(tagName);
            if (labelTag != null && !StringUtils.isBlank(labelTag.getValue()))
            {
                valueSetter.setMetadataValue(attributeMetadata, labelTag.getValue());
                return true;
            }
            else
            {
                return false;
            }
        }

        static interface IMetadataValueSetter
        {
            void setMetadataValue(CommonMetadata metadata, String value);
        }
    }

    private static class LabelSetter implements SimpleTagExtractor.IMetadataValueSetter
    {
        public void setMetadataValue(CommonMetadata metadata, String value)
        {
            metadata.setLabel(value);
        }
    }

    private static class GroupSetter implements SimpleTagExtractor.IMetadataValueSetter
    {
        public void setMetadataValue(CommonMetadata metadata, String value)
        {
            if (metadata instanceof AttributeMetadata)
            {
                ((AttributeMetadata) metadata).setGroup(value);
            }
        }
    }

    private static class LevelSetter implements SimpleTagExtractor.IMetadataValueSetter
    {
        public void setMetadataValue(CommonMetadata metadata, String value)
        {
            if (value == null)
            {
                return;
            }

            try
            {
                if (metadata instanceof AttributeMetadata)
                {
                    ((AttributeMetadata) metadata).setLevel(AttributeLevel.valueOf(value
                        .toUpperCase()));
                }
            }
            catch (Throwable e)
            {
                // Thrown if unknown enum
                org.slf4j.LoggerFactory.getLogger(MetadataExtractor.class).warn(
                    "Ignoring unknown attribute level: " + value);
            }
        }
    }
}
