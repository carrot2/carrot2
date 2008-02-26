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
public class CircularReferenceContainer
{
    /**
     * Circular reference.
     */
    @Processing
    @Input
    @Attribute
    public CircularReferenceContainer circular;
}
