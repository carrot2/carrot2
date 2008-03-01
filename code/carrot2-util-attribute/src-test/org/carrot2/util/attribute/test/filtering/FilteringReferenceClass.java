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
public class FilteringReferenceClass
{
    @TestInit
    @Input
    @Attribute
    private int initInput = 10;

    @TestInit
    @Output
    @Attribute
    private int initOutput = 10;

    @TestProcessing
    @Input
    @Attribute
    private int processingInput = 10;

    @TestProcessing
    @Output
    @Attribute
    private int processingOutput = 10;
}
