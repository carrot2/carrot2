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
public class BindableReferenceContainer
{
    /**
     * Test Bindable.
     */
    @Init
    @Input
    @Attribute
    private BindableReference bindableAttribute;

    /**
     * This is just a field, not a parameter, but if it's implementation is bindable,
     * its descriptors need to be considered as well.
     */
    private final BindableReference bindableField = new BindableReferenceImpl1();
}
