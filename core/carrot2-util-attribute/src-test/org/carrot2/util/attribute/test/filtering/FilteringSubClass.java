
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

package org.carrot2.util.attribute.test.filtering;

import org.carrot2.util.attribute.*;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class FilteringSubClass extends FilteringSuperClass
{
    @TestInit
    @TestProcessing
    @Input
    @Attribute
    @Level(AttributeLevel.BASIC)
    @Group("Group B")
    private int initProcessingInput = 10;

    @TestInit
    @TestProcessing
    @Output
    @Attribute
    @Level(AttributeLevel.MEDIUM)    
    @Group("Group B")
    private int initProcessingOutput = 10;

    @TestInit
    @TestProcessing
    @Input
    @Output
    @Attribute
    @Group("Group A")
    private int initProcessingInputOutput = 10;
}
