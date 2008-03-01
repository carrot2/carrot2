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
    @Init
    @Input
    @Attribute
    private int initInput = 10;

    @Init
    @Output
    @Attribute
    private int initOutput = 10;

    @Processing
    @Input
    @Attribute
    private int processingInput = 10;

    @Processing
    @Output
    @Attribute
    private int processingOutput = 10;
}
