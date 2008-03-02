package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;
import java.util.*;

import org.apache.commons.lang.ObjectUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * Provides a full description of a {@link Bindable} type, including
 * {@link AttributeDescriptor}s for all attributes defined by the type. Also provides
 * some human-readable metadata for the {@link Bindable} type itself.
 * {@link BindableDescriptor}s are immutable.
 */
public class BindableDescriptor
{
    /**
     * Descriptors for attributes defined in the {@link Bindable}. Keys in the map
     * correspond to attribute keys as defined in {@link Attribute#key()}.
     */
    public final Map<String, AttributeDescriptor> attributeDescriptors;

    /**
     * Descriptors for other {@link Bindable} types referenced by this descriptor. Keys in
     * this map correspond to <b>names of fields</b> that hold the references.
     */
    public final Map<String, BindableDescriptor> bindableDescriptors;

    /**
     * Human-readable metadata about this {@link Bindable} type.
     */
    public final BindableMetadata metadata;

    /**
     * An internal constructor.
     */
    BindableDescriptor(BindableMetadata metadata,
        Map<String, BindableDescriptor> bindableDescriptors,
        Map<String, AttributeDescriptor> attributeDescriptors)
    {
        this.metadata = metadata;
        this.bindableDescriptors = Collections.unmodifiableMap(bindableDescriptors);
        this.attributeDescriptors = Collections.unmodifiableMap(attributeDescriptors);
    }

    /**
     * Preserves descriptors for which the provided <code>predicate</code> returns
     * <code>true</code>. Notice that {@link BindableDescriptor}s are immutable, so
     * the filtered descriptor set is returned rather than filtering being applied to the
     * receiver.
     * 
     * @param predicate predicate to the applied
     * @return a new {@link BindableDescriptor} with the descriptors filtered.
     */
    public BindableDescriptor only(Predicate<AttributeDescriptor> predicate)
    {
        final Map<String, AttributeDescriptor> filteredAttributeDescriptors = Maps
            .newLinkedHashMap();
        outer: for (final Map.Entry<String, AttributeDescriptor> entry : attributeDescriptors
            .entrySet())
        {
            final AttributeDescriptor descriptor = entry.getValue();
            if (!predicate.apply(descriptor))
            {
                continue outer;
            }
            filteredAttributeDescriptors.put(entry.getKey(), descriptor);
        }

        // Now recursively filter bindable descriptors
        final Map<String, BindableDescriptor> filteredBindableDescriptors = Maps
            .newLinkedHashMap();
        for (final Map.Entry<String, BindableDescriptor> entry : bindableDescriptors
            .entrySet())
        {
            filteredBindableDescriptors.put(entry.getKey(), entry.getValue().only(
                predicate));
        }

        return new BindableDescriptor(this.metadata, filteredBindableDescriptors,
            filteredAttributeDescriptors);
    }

    /**
     * Preserves descriptors that match all of the provided binding time and filtering
     * annotation restrictions. Notice that {@link BindableDescriptor}s are immutable, so
     * the filtered descriptor set is returned rather than filtering being applied to the
     * receiver.
     * 
     * @param annotationClasses binding time and direction annotation classes to be
     *            matched.
     * @return a new {@link BindableDescriptor} with the descriptors filtered.
     */
    @SuppressWarnings("unchecked")
    public BindableDescriptor only(final Class<? extends Annotation>... annotationClasses)
    {
        if (annotationClasses.length == 0)
        {
            return this;
        }

        return only(new Predicate<AttributeDescriptor>()
        {
            public boolean apply(AttributeDescriptor descriptor)
            {
                for (final Class<? extends Annotation> annotationClass : annotationClasses)
                {
                    if (descriptor.getAnnotation(annotationClass) == null)
                    {
                        return false;
                    }
                }

                return true;
            }
        });
    }

    /**
     * Returns a flattened structure of attribute descriptors. After flattening,
     * {@link #attributeDescriptors} contains descriptors of direct and referenced
     * attributes and {@link #bindableDescriptors} is empty.
     * 
     * @return flattened descriptor
     */
    public BindableDescriptor flatten()
    {
        // Copy attributes to a new map
        final Map<String, AttributeDescriptor> flatDescriptors = Maps.newLinkedHashMap();
        flatDescriptors.putAll(attributeDescriptors);

        // Recursively flatten the references
        for (final BindableDescriptor descriptor : bindableDescriptors.values())
        {
            flatDescriptors.putAll(descriptor.flatten().attributeDescriptors);
        }

        return new BindableDescriptor(metadata,
            new HashMap<String, BindableDescriptor>(), flatDescriptors);
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

        final BindableDescriptor other = (BindableDescriptor) obj;

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
