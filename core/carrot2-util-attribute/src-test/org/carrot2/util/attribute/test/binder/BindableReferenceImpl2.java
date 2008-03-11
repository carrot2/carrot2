/**
 *
 */
package org.carrot2.util.attribute.test.binder;

import org.carrot2.util.attribute.*;

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
    @TestInit
    @Input
    @Attribute
    private int initInputInt = 12;
}
