/**
 * 
 */
package org.carrot2.core.attribute;

import java.util.Map;

/**
 *
 */
public class BindableDescriptor
{
    public final Map<String, BindableDescriptor> bindableDescriptors;
    public final Map<String, AttributeDescriptor> attributeDescriptors;

    BindableDescriptor(Map<String, BindableDescriptor> bindableDescriptors,
        Map<String, AttributeDescriptor> attributeDescriptors)
    {
        this.bindableDescriptors = bindableDescriptors;
        this.attributeDescriptors = attributeDescriptors;
    }
}
