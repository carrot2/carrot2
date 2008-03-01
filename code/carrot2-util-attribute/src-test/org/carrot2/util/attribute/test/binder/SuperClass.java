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
public class SuperClass
{
    /**
     * Super class init input int.
     */
    @TestInit
    @Input
    @Attribute
    private int initInputInt = 5;
}
