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
public class SubClass extends SuperClass
{
    /**
     * Subclass processing input.
     */
    @TestProcessing
    @Input
    @Attribute
    private String processingInputString = "input";
}
