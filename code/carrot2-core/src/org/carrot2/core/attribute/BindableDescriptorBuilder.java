/**
 * 
 */
package org.carrot2.core.attribute;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.carrot2.core.constraint.Constraint;
import org.carrot2.util.resource.Resource;
import org.carrot2.util.resource.ResourceUtilsFactory;
import org.simpleframework.xml.load.Persister;

import com.google.common.collect.Maps;

/**
 * TODO: implement a simple API for querying/ filtering the descriptor tree:
 * 
 * <pre>
 *  Querying the descriptors:
 *  - filter: Input, Output, both (priority: high)
 *  - filter: Init, Processing (priority: high)
 *  - filter: level (priority: medium)
 *  - filter: group
 *  - filter: text search in title/label/description (for filtering like in Eclipse)
 * 
 *  - organization: tree according to components (priority: high)
 *  - organization: flat list (priority: high)
 *  - organization: tree according to group
 * </pre>
 */
public class BindableDescriptorBuilder
{
    /**
     *
     */
    public static BindableDescriptor buildDescriptor(Object initializedInstance)
    {
        final Class<?> clazz = initializedInstance.getClass();
        if (clazz.getAnnotation(Bindable.class) == null)
        {
            throw new IllegalArgumentException("Provided instance must be @Bindable");
        }

        // Build descriptors for direct attributes
        Map<String, AttributeDescriptor> attributeDescriptors = buildAttributeDescriptors(initializedInstance);

        // Build descriptors for nested bindables
        Map<String, BindableDescriptor> bindableDescriptors = Maps.newHashBiMap();

        Collection<Field> fieldsFromBindableHierarchy = BindableUtils
            .getFieldsFromBindableHierarchy(clazz);
        for (Field field : fieldsFromBindableHierarchy)
        {
            // Get class of runtime value
            Object fieldValue = null;
            try
            {
                field.setAccessible(true);
                fieldValue = field.get(initializedInstance);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Could not retrieve default value of field: "
                    + field.getClass().getName() + "#" + field.getName());
            }

            // Descend only for non-null values
            if (fieldValue != null
                && field.getClass().getAnnotation(Bindable.class) != null)
            {
                bindableDescriptors.put(field.getName(), buildDescriptor(fieldValue));
            }
        }

        return new BindableDescriptor(bindableDescriptors, attributeDescriptors);
    }

    /**
     *
     */
    private static Map<String, AttributeDescriptor> buildAttributeDescriptors(
        Object initializedInstance)
    {
        final Class<?> clazz = initializedInstance.getClass();
        if (clazz.getAnnotation(Bindable.class) == null)
        {
            throw new IllegalArgumentException("Provided instance must be @Bindable");
        }

        // Load metadata
        BindableMetadata bindableMetadata = buildMetadataForBindableHierarchy(clazz);

        Map<String, AttributeDescriptor> result = Maps.newHashMap();
        Collection<Field> fieldsFromBindableHierarchy = BindableUtils
            .getFieldsFromBindableHierarchy(clazz);

        for (Field field : fieldsFromBindableHierarchy)
        {
            if (field.getAnnotation(Attribute.class) != null)
            {
                result.put(BindableUtils.getKey(field), buildAttributeDescriptor(
                    initializedInstance, field, bindableMetadata));
            }
        }

        return result;
    }

    /**
     *
     */
    private static BindableMetadata buildMetadataForBindableHierarchy(final Class<?> clazz)
    {
        Collection<Class<?>> classesFromBindableHerarchy = BindableUtils
            .getClassesFromBindableHerarchy(clazz);

        final BindableMetadata bindableMetadata = getBindableMetadata(clazz);

        for (Class<?> bindableClass : classesFromBindableHerarchy)
        {
            if (bindableClass != clazz)
            {
                final BindableMetadata moreMetadata = getBindableMetadata(bindableClass);
                bindableMetadata.getAttributeMetadata().putAll(
                    moreMetadata.getAttributeMetadata());
            }
        }

        return bindableMetadata;
    }

    /**
     *
     */
    private static BindableMetadata getBindableMetadata(final Class<?> clazz)
    {
        Resource metadataXml = ResourceUtilsFactory.getDefaultResourceUtils().getFirst(
            clazz.getSimpleName() + ".xml", clazz);
        BindableMetadata bindableMetadata = null;
        if (metadataXml != null)
        {
            try
            {
                bindableMetadata = new Persister().read(BindableMetadata.class,
                    metadataXml.open());
            }
            catch (Exception e)
            {
                throw new RuntimeException("Could not load attribute metadata from: "
                    + metadataXml, e);
            }
        }
        return bindableMetadata;
    }

    /**
     *
     */
    private static AttributeDescriptor buildAttributeDescriptor(
        Object initializedInstance, Field field, BindableMetadata bindableMetadata)
    {
        final Constraint constraint = BindableUtils.getConstraint(field);
        Object defaultValue = null;

        try
        {
            field.setAccessible(true);
            defaultValue = field.get(initializedInstance);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not retrieve default value of attribute: "
                + BindableUtils.getKey(field));
        }

        return new AttributeDescriptor(field, defaultValue, constraint, bindableMetadata
            .getAttributeMetadata().get(field.getName()));
    }
}
