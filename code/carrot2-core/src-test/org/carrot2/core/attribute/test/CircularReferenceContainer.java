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
public class CircularReferenceContainer
{
    /**
     * Circular reference.
     */
    @Processing
    @Input
    @Attribute
    public CircularReferenceContainer circular;
}
