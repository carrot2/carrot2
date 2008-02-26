/**
 *
 */
package carrot2.util.attribute.test.filtering;

import carrot2.util.attribute.*;

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
    private final int initInput = 10;

    @Init
    @Output
    @Attribute
    private final int initOutput = 10;

    @Processing
    @Input
    @Attribute
    private final int processingInput = 10;

    @Processing
    @Output
    @Attribute
    private final int processingOutput = 10;
}
