/**
 * 
 */
package org.carrot2.core.attribute.metadata.tests;

import org.carrot2.core.attribute.*;
import org.carrot2.core.attribute.metadata.AttributeMetadataBuilder;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class NamedAttributes
{
    private static final String TEST = "test";
    
    @Init
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int noJavadoc;
    
    /**
     * @label overriden
     */
    @Init
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int labelOverride;
    
    /**
     * Title overriden.
     */
    @Init
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int titleOverride;
    
    /**
     * Title overriden. Description overriden.
     */
    @Init
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int titleDescriptionOverride;
    
    @Init
    @Input
    @Attribute(key = TEST)
    private int noDotInKey;
    
    @Init
    @Input
    @Attribute(key = AttributeMetadataBuilder.ATTRIBUTE_KEY_PARAMETER)
    private int classNotInSourcePath;
}
