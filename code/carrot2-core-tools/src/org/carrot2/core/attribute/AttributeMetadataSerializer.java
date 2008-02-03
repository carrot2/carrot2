/**
 * 
 */
package org.carrot2.core.attribute;

import java.io.File;

/**
 *
 */
public class AttributeMetadataSerializer
{
    private AttributeMetadataBuilder attributeMetadataBuilder = new AttributeMetadataBuilder();

    public void addSourceTree(File directory)
    {
        attributeMetadataBuilder.addSourceTree(directory);
    }

    public void serialize()
    {

    }
}
