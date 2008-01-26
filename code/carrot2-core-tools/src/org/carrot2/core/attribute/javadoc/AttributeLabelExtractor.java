/**
 * 
 */
package org.carrot2.core.attribute.javadoc;

import org.dom4j.Element;

import com.sun.javadoc.*;

/**
 *
 */
public class AttributeLabelExtractor implements AttributeMetadataExtractor
{
    @Override
    public void extractMetadata(RootDoc rootDoc, Doc actualFieldDoc,
        Doc resolvedFieldDoc, Element attributeMetadata)
    {
        // Actual doc has priority over the resolved one
        Tag [] labels = actualFieldDoc.tags("label");
        if (labels.length == 0 && resolvedFieldDoc != null)
        {
            labels = resolvedFieldDoc.tags("label");
        }

        // Take the first label, in case more are specified
        if (labels.length > 0)
        {
            attributeMetadata.addElement("label").addText(labels[0].text());
        }
    }
}
