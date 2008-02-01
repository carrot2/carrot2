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
public class AttributeTitleExtractor implements AttributeMetadataExtractor
{
    @Override
    public boolean extractMetadataItem(JavaField attributeField,
        JavaDocBuilder javaDocBuilder, AttributeMetadata attributeMetadata)
    {
        final String comment = attributeField.getComment();
        if (comment == null)
        {
            return false;
        }
            
        final BreakIterator sentenceIterator = BreakIterator
            .getSentenceInstance(Locale.ENGLISH);
        sentenceIterator.setText(comment);

        if (sentenceIterator.first() >= 0
            && sentenceIterator.next() > sentenceIterator.first())
        {
            // Strip off the last "."
            final String firstSentence = comment.substring(sentenceIterator.first(),
                sentenceIterator.next()).trim();
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
