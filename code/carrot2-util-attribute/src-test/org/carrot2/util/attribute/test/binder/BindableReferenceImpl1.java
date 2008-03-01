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
public class BindableReferenceImpl1 implements BindableReference
{
    /**
     * Processing input int.
     */
    @TestProcessing
    @Input
    @Attribute
    private int processingInputInt = 10;
}
