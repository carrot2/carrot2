/**
 * 
 */
package org.carrot2.core.attribute.test.filtering;

import org.carrot2.core.attribute.*;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class FilteringSuperClass
{
    private FilteringReferenceClass reference = new FilteringReferenceClass();
    
    @Init
    @Input
    @Attribute
    private int initInput = 10;

    @Init
    @Output
    @Attribute
    private int initOutput = 10;

    @Init
    @Input
    @Output
    @Attribute
    private int initInputOutput = 10;

    @Processing
    @Input
    @Attribute
    private int processingInput = 10;

    @Processing
    @Output
    @Attribute
    private int processingOutput = 10;

    @Processing
    @Input
    @Output
    @Attribute
    private int processingInputOutput = 10;
}
