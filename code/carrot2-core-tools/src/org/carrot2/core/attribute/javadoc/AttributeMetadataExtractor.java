/**
 * 
 */
package org.carrot2.core.attribute.javadoc;

import org.carrot2.core.attribute.AttributeNames;
import org.dom4j.Element;

import com.sun.javadoc.Doc;
import com.sun.javadoc.RootDoc;

/**
 *
 */
public interface AttributeMetadataExtractor
{
    /**
     * Extracts metadata from the provided fields and adds appropriate elements/
     * attributes to the <code>attributeMetadata</code>.
     * @param rootDoc TODO
     * @param actualFieldDoc field doc provided explicitly for the attribute field
     * @param resolvedFieldDoc field doc provided for the corresponding field in
     *            {@link AttributeNames}, can be <code>null</code> if no matching field
     *            in {@link AttributeNames} exists.
     * @param attributeMetadata element to which extracted metadata can be added
     */
    public void extractMetadata(RootDoc rootDoc, Doc actualFieldDoc,
        Doc resolvedFieldDoc, Element attributeMetadata);
}
