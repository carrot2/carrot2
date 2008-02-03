/**
 * 
 */
package org.carrot2.core.attribute.test;

import org.carrot2.core.attribute.*;

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
    @Init
    @Input
    @Attribute
    private int initInputInt = 5;
}
