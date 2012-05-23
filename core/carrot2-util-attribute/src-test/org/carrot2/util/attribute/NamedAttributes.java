
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import org.carrot2.util.attribute.test.metadata.TestAncestorAttributes;
import org.carrot2.util.attribute.test.metadata.TestAttributeNames;

/**
 * Test named attribute container.
 */
@Bindable(inherit = TestAncestorAttributes.class)
@SuppressWarnings("unused")
public class NamedAttributes
{
    private static final String TEST = "test";

    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL, inherit = true)
    private int noJavadoc;

    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL, inherit = true)
    @Label("overridden")
    private int labelOverride;

    /**
     * Title overridden.
     */
    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL, inherit = true)
    private int titleOverride;

    /**
     * Title overridden. Description overridden.
     */
    @TestInit
    @Input
    @Attribute(key = TestAttributeNames.TITLE_DESCRIPTION_LABEL, inherit = true)
    private int titleDescriptionOverride;

    @TestInit
    @Input
    @Attribute(key = TEST)
    private int noDotInKey;
}
