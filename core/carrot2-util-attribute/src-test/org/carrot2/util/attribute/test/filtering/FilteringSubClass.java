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
