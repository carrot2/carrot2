/**
 * 
 */
package org.carrot2.core.attribute.javadoc;

import org.dom4j.Element;

import com.sun.javadoc.*;

/**
 *
 */
public class AttributeDescriptionExtractor implements AttributeMetadataExtractor
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
        
        // Add element
        final Element title = attributeMetadata.addElement("description");
        title.addText(docSource.commentText());
    }
}
