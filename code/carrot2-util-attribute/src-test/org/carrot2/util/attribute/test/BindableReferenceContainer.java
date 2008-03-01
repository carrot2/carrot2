/**
 *
 */
package org.carrot2.util.attribute.test;

import org.carrot2.util.attribute.*;

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
    private BindableReference bindableAttribute;

    /**
     * This is just a field, not a parameter, but if it's implementation is bindable,
     * its descriptors need to be considered as well.
     */
    private BindableReference bindableField = new BindableReferenceImpl1();
}
