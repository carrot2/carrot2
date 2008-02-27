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
public class FilteringSubClass extends FilteringSuperClass
{
    @Init
    @Processing
    @Input
    @Attribute
    private int initProcessingInput = 10;

    @Init
    @Processing
    @Output
    @Attribute
    private int initProcessingOutput = 10;

    @Init
    @Processing
    @Input
    @Output
    @Attribute
    private int initProcessingInputOutput = 10;
}
