/**
 * 
 */
package org.carrot2.core.attribute;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

import org.carrot2.core.constraint.Constraint;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.Resource;
import org.carrot2.util.resource.ResourceUtilsFactory;
import org.simpleframework.xml.load.Persister;

import com.google.common.collect.Maps;

/**
 * TODO: implement a simple API for querying/ filtering the descriptor tree:
 * 
 * <pre>
 *  - filter: level (priority: medium)
 *  - filter: group
 *  - filter: text search in title/label/description (for filtering like in Eclipse)
 * 
 *  - organization: tree according to components (priority: high)
 *  - organization: flat list (priority: high)
 *  - organization: tree according to group
 *  
 *  - sorting: by declaration order (default)
 *  - sorting: by label
 *  - sorting: by level
 * </pre>
 */
public class BindableDescriptorBuilder
{
    /**
     *
     */
    public static BindableDescriptor buildDescriptor(Object initializedInstance)
    {
        return buildDescriptor(initializedInstance, new HashSet<Object>());
    }

    /**
     *
     */
    private static BindableDescriptor buildDescriptor(Object initializedInstance,
        Set<Object> processedInstances)
    {
        final Class<?> clazz = initializedInstance.getClass();
        if (clazz.getAnnotation(Bindable.class) == null)
        {
            throw new IllegalArgumentException("Provided instance must be @Bindable");
        }

        if (!processedInstances.add(initializedInstance))
        {
            throw new UnsupportedOperationException(
                "Circular references are not supported");
        }

        // Load metadata
        BindableMetadata bindableMetadata = buildMetadataForBindableHierarchy(clazz);

        // Build descriptors for direct attributes
        Map<String, AttributeDescriptor> attributeDescriptors = buildAttributeDescriptors(
            initializedInstance, bindableMetadata);

        // Build descriptors for nested bindables
        Map<String, BindableDescriptor> bindableDescriptors = Maps.newLinkedHashMap();

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
                && fieldValue.getClass().getAnnotation(Bindable.class) != null)
            {
                bindableDescriptors.put(field.getName(), buildDescriptor(fieldValue,
                    processedInstances));
            }
        }

        return new BindableDescriptor(bindableMetadata, bindableDescriptors,
            attributeDescriptors);
    }

    /**
     *
     */
    private static Map<String, AttributeDescriptor> buildAttributeDescriptors(
        Object initializedInstance, BindableMetadata bindableMetadata)
    {
        final Class<?> clazz = initializedInstance.getClass();

        Map<String, AttributeDescriptor> result = Maps.newLinkedHashMap();
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
                bindableMetadata.getInternalAttributeMetadata().putAll(
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
            InputStream inputStream = null;
            try
            {
                inputStream = metadataXml.open();
                bindableMetadata = new Persister().read(BindableMetadata.class,
                    inputStream);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Could not load attribute metadata from: "
                    + metadataXml, e);
            }
            finally
            {
                CloseableUtils.closeIgnoringException(inputStream);
            }

            return bindableMetadata;
        }
        else
        {
            throw new RuntimeException("Could not load attribute metadata from: "
                + clazz.getSimpleName() + ".xml");
        }
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
