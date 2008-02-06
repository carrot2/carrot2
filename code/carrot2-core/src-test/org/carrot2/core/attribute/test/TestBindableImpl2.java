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
public class TestBindableImpl2 implements TestBindable
{
    /**
     * Init input int.
     */
    @Init
    @Input
    @Attribute
    private int initInputInt = 12;
}
