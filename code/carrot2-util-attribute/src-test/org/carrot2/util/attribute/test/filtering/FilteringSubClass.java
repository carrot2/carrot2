/**
 *
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
    private int initProcessingInput = 10;

    @TestInit
    @TestProcessing
    @Output
    @Attribute
    private int initProcessingOutput = 10;

    @TestInit
    @TestProcessing
    @Input
    @Output
    @Attribute
    private int initProcessingInputOutput = 10;
}
