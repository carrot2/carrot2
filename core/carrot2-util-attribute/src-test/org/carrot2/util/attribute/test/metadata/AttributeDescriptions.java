
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

package org.carrot2.util.attribute.test.metadata;

import org.carrot2.util.attribute.*;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class AttributeDescriptions
{
    /**
     * @label label
     */
    @TestInit
    @Input
    @Attribute
    private int noDescriptionNoTitle;

    /**
     * Title.
     *
     * @label label
     */
    @TestInit
    @Input
    @Attribute
    private int noDescription;

    /**
     * Title. Single sentence description.
     */
    @TestInit
    @Input
    @Attribute
    private int singleSentenceDescription;

    /**
     * Title. Description sentence 1. Description sentence 2.
     */
    @TestInit
    @Input
    @Attribute
    private int twoSentenceDescription;

    /**
     * Title. Description
     *
     * with     extra
     * space.
     */
    @TestInit
    @Input
    @Attribute
    private int descriptionWithExtraSpace;
}
