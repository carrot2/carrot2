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
public class CircularReferenceContainer
{
    /**
     * Circular reference.
     */
    @TestProcessing
    @Input
    @Attribute
    public CircularReferenceContainer circular;
}
