
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
public class FilteringSubClass extends FilteringSuperClass
{
    /**
     * @level basic
     * @group Group B
     */
    @TestInit
    @TestProcessing
    @Input
    @Attribute
    private int initProcessingInput = 10;

    /**
     * @level medium
     * @group Group B
     */
    @TestInit
    @TestProcessing
    @Output
    @Attribute
    private int initProcessingOutput = 10;

    /**
     * @group Group A
     */
    @TestInit
    @TestProcessing
    @Input
    @Output
    @Attribute
    private int initProcessingInputOutput = 10;
}
