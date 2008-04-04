/**
 *
 */
package org.carrot2.util.attribute.test.binder;

import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

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
    @TestInit
    @Input
    @Attribute
    @ImplementingClasses(classes =
    {
        BindableReferenceImpl1.class, BindableReferenceImpl2.class
    })
    private BindableReference bindableAttribute;

    /**
     * This is just a field, not a parameter, but if it's implementation is bindable, its
     * descriptors need to be considered as well.
     */
    private BindableReference bindableField = new BindableReferenceImpl1();
}
