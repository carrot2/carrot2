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
public class BindableReferenceImpl1 implements BindableReference
{
    /**
     * Processing input int.
     */
    @Processing
    @Input
    @Attribute
    private int processingInputInt = 10;
}
