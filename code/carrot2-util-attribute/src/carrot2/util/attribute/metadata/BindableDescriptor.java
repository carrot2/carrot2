/**
 *
 */
package carrot2.util.attribute.metadata;

import java.lang.annotation.Annotation;
import java.util.*;

import org.apache.commons.lang.ObjectUtils;

import carrot2.util.attribute.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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

    BindableDescriptor(BindableMetadata metadata,
        Map<String, BindableDescriptor> bindableDescriptors,
        Map<String, AttributeDescriptor> attributeDescriptors)
    {
        this.metadata = metadata;
        this.bindableDescriptors = Collections.unmodifiableMap(bindableDescriptors);
        this.attributeDescriptors = Collections.unmodifiableMap(attributeDescriptors);
    }

    /**
     * Filters out descriptors for which the provided <code>predicate</code> returns
     * <code>false</code>.
     *
     * @param predicate predicate to the applied
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
        for (final Map.Entry<String, BindableDescriptor> entry : bindableDescriptors.entrySet())
        {
            filteredBindableDescriptors.put(entry.getKey(), entry.getValue().only(
                predicate));
        }

        return new BindableDescriptor(this.metadata, filteredBindableDescriptors,
            filteredAttributeDescriptors);
    }

    /**
     * Filters out descriptors that do not match at least one of the provided binding time
     * and binding direction restrictions.
     *
     * @param bindingAnnotationClasses binding time and direction annotation classes to be
     *            matched. Classes other than {@link Input}, {@link Output},
     *            {@link Init} and {@link Processing} will be ignored.
     */
    @SuppressWarnings("unchecked")
    public BindableDescriptor only(final Class<?>... bindingAnnotationClasses)
    {
        return only(new Predicate<AttributeDescriptor>()
        {
            public boolean apply(AttributeDescriptor descriptor)
            {
                final Set<Class<? extends Annotation>> annotationClasses = Sets
                    .newHashSet(Input.class, Output.class, Init.class, Processing.class);
                annotationClasses.retainAll(Arrays.asList(bindingAnnotationClasses));

                for (final Class<? extends Annotation> annotationClass : annotationClasses)
                {
                    if (!descriptor.hasBindingAnnotation(annotationClass))
                    {
                        return false;
                    }
                }

                return true;
            }
        });
    }

    /**
     * Returns a flattened structure of attribute descriptors. After flattening
     * {@link #attributeDescriptors} contains descriptors of direct and referenced
     * attributes and {@link #bindableDescriptors} is empty.
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
