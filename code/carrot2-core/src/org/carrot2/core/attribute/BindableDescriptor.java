/**
 * 
 */
package org.carrot2.core.attribute;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

/**
 *
 */
public class BindableDescriptor
{
    /**
     * Key: field <b>name</b>. TODO: this kind of sucks because its inconsistent with
     * attributes below, but I can't see any better way to do this right now.
     */
    public final Map<String, BindableDescriptor> bindableDescriptors;

    /**
     * Key: attribute key as returned by {@link BindableUtils#getKey(Class, String)}
     */
    public final Map<String, AttributeDescriptor> attributeDescriptors;

    public final BindableMetadata metadata;
    
    BindableDescriptor(BindableMetadata metadata, Map<String, BindableDescriptor> bindableDescriptors,
        Map<String, AttributeDescriptor> attributeDescriptors)
    {
        this.metadata = metadata;
        this.bindableDescriptors = bindableDescriptors;
        this.attributeDescriptors = attributeDescriptors;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null || !(obj instanceof BindableDescriptor))
        {
            return false;
        }

        BindableDescriptor other = (BindableDescriptor) obj;

        return ObjectUtils.equals(bindableDescriptors, other.bindableDescriptors)
            && ObjectUtils.equals(attributeDescriptors, other.attributeDescriptors);
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(bindableDescriptors)
            ^ ObjectUtils.hashCode(attributeDescriptors);
    }
}
