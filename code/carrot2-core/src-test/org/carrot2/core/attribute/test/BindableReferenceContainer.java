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
public class BindableReferenceContainer
{
    /**
     * Test Bindable.
     */
    @Init
    @Input
    @Attribute
    private TestBindable bindableAttribute;

    /**
     * This is just a field, not a parameter, but if it's implementation is bindable,
     * its descriptors need to be considered as well.
     */
    private TestBindable bindableField = new TestBindableImpl1();
}
