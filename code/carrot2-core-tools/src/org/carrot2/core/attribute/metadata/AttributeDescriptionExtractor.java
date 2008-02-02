/**
 * 
 */
package org.carrot2.core.attribute.metadata;

import java.text.BreakIterator;
import java.util.Locale;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaField;

/**
 *
 */
public class AttributeDescriptionExtractor implements AttributeMetadataExtractor
{
    @Override
    public boolean extractMetadataItem(JavaField attributeField,
        JavaDocBuilder javaDocBuilder, AttributeMetadata attributeMetadata)
    {
        final String comment = JavaDocBuilderUtils.normalizeSpaces(attributeField
            .getComment());
        if (comment == null)
        {
            return false;
        }

        final BreakIterator sentenceIterator = BreakIterator
            .getSentenceInstance(Locale.ENGLISH);
        sentenceIterator.setText(comment);

        final int next = sentenceIterator.next();
        if (next > 0 && next < comment.length())
        {
            attributeMetadata.setDescription(comment.substring(next).trim());
            return true;
        }
        else
        {
            return false;
        }
    }
}
