
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import org.carrot2.util.attribute.test.metadata.TestAttributeNames;

/**
 * Test named attribute container.
 */
@Bindable
@SuppressWarnings("unused")
public class NamedAttributes
{
    private static final String TEST = "test";

    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int noJavadoc;

    /**
     * @label overridden
     */
    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int labelOverride;

    /**
     * Title overridden.
     */
    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL)
    private int titleOverride;

    /**
     * Title overridden. Description overridden.
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
