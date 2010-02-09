
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

package org.carrot2.util.attribute.test.filtering;

import org.carrot2.util.attribute.*;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class FilteringReferenceClass
{
    /**
     * @level basic
     */
    @TestInit
    @Input
    @Attribute
    private int initInput = 10;

    /**
     * @level medium
     */
    @TestInit
    @Output
    @Attribute
    private int initOutput = 10;

    /**
     * @level advanced
     * @group Group B
     */
    @TestProcessing
    @Input
    @Attribute
    private int processingInput = 10;

    /**
     * @group Group A
     */
    @TestProcessing
    @Output
    @Attribute
    private int processingOutput = 10;
}
