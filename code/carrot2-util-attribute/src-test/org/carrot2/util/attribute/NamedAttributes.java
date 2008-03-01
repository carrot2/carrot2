/**
 *
 */
package org.carrot2.util.attribute;

import org.carrot2.util.attribute.test.metadata.TestAttributeNames;

/**
 *
 */
@SuppressWarnings("unused")
public class NamedAttributes
{
    private static final String TEST = "test";

    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int noJavadoc;

    /**
     * @label overriden
     */
    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int labelOverride;

    /**
     * Title overriden.
     */
    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int titleOverride;

    /**
     * Title overriden. Description overriden.
     */
    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int titleDescriptionOverride;

    @TestInit
    @Input
    @Attribute(key = TEST)
    private int noDotInKey;

    @TestInit
    @Input
    @Attribute(key = BindableMetadataBuilder.ATTRIBUTE_KEY_PARAMETER)
    private int classNotInSourcePath;
}
