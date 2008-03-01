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
public class FilteringSuperClass
{
    private FilteringReferenceClass reference = new FilteringReferenceClass();

    @TestInit
    @Input
    @Attribute
    private int initInput = 10;

    @TestInit
    @Output
    @Attribute
    private int initOutput = 10;

    @TestInit
    @Input
    @Output
    @Attribute
    private int initInputOutput = 10;

    @TestProcessing
    @Input
    @Attribute
    private int processingInput = 10;

    @TestProcessing
    @Output
    @Attribute
    private int processingOutput = 10;

    @TestProcessing
    @Input
    @Output
    @Attribute
    private int processingInputOutput = 10;
}
