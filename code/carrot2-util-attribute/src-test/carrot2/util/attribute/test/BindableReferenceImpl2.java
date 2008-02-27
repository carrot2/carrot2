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
public class BindableReferenceImpl2 implements BindableReference
{
    /**
     * Init input int.
     */
    @Init
    @Input
    @Attribute
    private int initInputInt = 12;
}
