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
public class FilteringSuperClass
{
    private final FilteringReferenceClass reference = new FilteringReferenceClass();

    @Init
    @Input
    @Attribute
    private final int initInput = 10;

    @Init
    @Output
    @Attribute
    private final int initOutput = 10;

    @Init
    @Input
    @Output
    @Attribute
    private final int initInputOutput = 10;

    @Processing
    @Input
    @Attribute
    private final int processingInput = 10;

    @Processing
    @Output
    @Attribute
    private final int processingOutput = 10;

    @Processing
    @Input
    @Output
    @Attribute
    private final int processingInputOutput = 10;
}
