/**
 *
 */
package org.carrot2.util.attribute.test.binder;

import org.carrot2.util.attribute.*;

/**
 *
 */
@Bindable
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
