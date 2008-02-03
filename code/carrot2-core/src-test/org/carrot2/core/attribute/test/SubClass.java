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
public class SubClass extends SuperClass
{
    /**
     * Subclass processing input.
     */
    @Processing
    @Input
    @Attribute
    private String processingInputString = "input";
}
