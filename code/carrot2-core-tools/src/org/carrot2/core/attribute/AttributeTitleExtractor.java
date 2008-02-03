/**
 * 
 */
package org.carrot2.core.attribute;

import java.text.BreakIterator;
import java.util.Locale;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaField;

/**
 *
 */
public class AttributeTitleExtractor implements AttributeMetadataExtractor
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

        final int first = sentenceIterator.first();
        final int next = sentenceIterator.next();
        if (first >= 0 && next > first)
        {
            // Strip off the last "."
            final String firstSentence = comment.substring(first, next).trim();
            if (firstSentence.endsWith("."))
            {
                attributeMetadata.setTitle(firstSentence.substring(0, firstSentence
                    .length() - 1));
            }
            else
            {
                attributeMetadata.setTitle(firstSentence);
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}
