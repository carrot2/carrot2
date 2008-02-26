/**
 *
 */
package carrot2.util.attribute.test;

import carrot2.util.attribute.*;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class SubClass extends SuperClass
{
    /**
     * Subclass processing input.
     */
    @Processing
    @Input
    @Attribute
    private final String processingInputString = "input";
}
