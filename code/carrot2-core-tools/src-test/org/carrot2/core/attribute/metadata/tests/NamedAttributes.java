/**
 * 
 */
package org.carrot2.core.attribute.metadata.tests;

import org.carrot2.core.attribute.*;

/**
 *
 */
@Bindable
@SuppressWarnings("unused")
public class NamedAttributes
{
    /**
     * 
     */
    @Init
    @Input
    @Attribute(key = AttributeNames.DOCUMENTS)
    private int singleWordLabel;
}
