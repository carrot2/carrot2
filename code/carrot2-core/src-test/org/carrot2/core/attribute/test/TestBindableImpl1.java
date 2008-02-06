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
public class TestBindableImpl1 implements TestBindable
{
    /**
     * Processing input int.
     */
    @Processing
    @Input
    @Attribute
    private int processingInputInt = 10;
}
