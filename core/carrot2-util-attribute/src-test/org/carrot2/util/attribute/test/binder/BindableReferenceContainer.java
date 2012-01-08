
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
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
    private IBindableReference bindableAttribute;

    /**
     * This is just a field, not a parameter, but if it's implementation is bindable, its
     * descriptors need to be considered as well.
     */
    private IBindableReference bindableField = new BindableReferenceImpl1();
}
