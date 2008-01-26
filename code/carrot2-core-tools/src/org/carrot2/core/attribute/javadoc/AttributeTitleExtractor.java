/**
 * 
 */
package org.carrot2.core.attribute.javadoc;

import org.dom4j.Element;

import com.sun.javadoc.*;

/**
 *
 */
public class AttributeTitleExtractor implements AttributeMetadataExtractor
{
    @Override
    public void extractMetadata(RootDoc rootDoc, Doc actualFieldDoc,
        Doc resolvedFieldDoc, Element attributeMetadata)
    {
        // If the actual field has non-empty Javadoc, ignore the resolved doc
        Doc docSource = actualFieldDoc;
        if (docSource.firstSentenceTags().length == 0 && resolvedFieldDoc != null)
        {
            docSource = resolvedFieldDoc;
        }
        
        final Tag [] firstSentenceTags = docSource.firstSentenceTags();

        StringBuilder stringBuilder = new StringBuilder();
        for (Tag tag : firstSentenceTags)
        {
            stringBuilder.append(tag.text());
        }

        // Remove the final period.
        if (stringBuilder.length() > 0
            && stringBuilder.charAt(stringBuilder.length() - 1) == '.')
        {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        // Add element
        final Element title = attributeMetadata.addElement("title");
        title.addText(stringBuilder.toString());
    }
}
