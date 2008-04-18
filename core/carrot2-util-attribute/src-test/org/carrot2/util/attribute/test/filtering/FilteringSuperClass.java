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
     */
    @TestInit
    @Input
    @Output
    @Attribute
    private int initInputOutput = 10;

    /**
     * @group Group C
     */
    @TestProcessing
    @Input
    @Attribute
    private int processingInput = 10;

    /**
     * @group Group C
     */
    @TestProcessing
    @Output
    @Attribute
    private int processingOutput = 10;

    /**
     * @group Group B
     */
    @TestProcessing
    @Input
    @Output
    @Attribute
    private int processingInputOutput = 10;
}
