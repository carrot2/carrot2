
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import org.carrot2.util.attribute.constraint.IsConstraint;

/**
 * Builds {@link BindableDescriptor}s based on the provided bindable type instances.
 */
public class BindableDescriptorBuilder
{
    /**
     * Builds a {@link BindableDescriptor} for an initialized instance of a
     * {@link Bindable} type. Notice that the set of {@link AttributeDescriptor} found in
     * the returned {@link BindableDescriptor} may vary depending on how the provided
     * instance is initialized.
     * 
     * @param initializedInstance initialized instance of a {@link Bindable} type for
     *            which to build the descriptor
     * @return the descriptor built
     */
    public static BindableDescriptor buildDescriptor(Object initializedInstance)
    {
        return buildDescriptor(initializedInstance, new HashSet<Object>());
    }

    /**
     * Internal implementation of descriptor building.
     */
    private static BindableDescriptor buildDescriptor(
        Object initializedInstance, Set<Object> processedInstances)
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
        final BindableMetadata bindableMetadata = BindableMetadata.forClassWithParents(clazz);

        // Build descriptors for direct attributes
        final Map<String, AttributeDescriptor> attributeDescriptors = buildAttributeDescriptors(
            initializedInstance, bindableMetadata);

        // Build descriptors for nested bindables
        final Map<Field, BindableDescriptor> bindableDescriptors = new LinkedHashMap<>();

        final Collection<Field> fieldsFromBindableHierarchy = BindableUtils.getFieldsFromBindableHierarchy(clazz);
        for (final Field field : fieldsFromBindableHierarchy)
        {
            // Omit any static fields.
            if (Modifier.isStatic(field.getModifiers()))
            {
              continue;
            }

            if (Modifier.isPublic(field.getModifiers()))
            {
              try {
                Object fieldValue = field.get(initializedInstance);
                if (fieldValue != null && fieldValue.getClass().getAnnotation(Bindable.class) != null)
                {
                    bindableDescriptors.put(field, buildDescriptor(fieldValue, processedInstances));
                }
              } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not access public field: "
                    + field.getClass().getName() + "#" + field.getName(), e);
              }
            }
            else
            {
              assert noHiddenBindables(field, initializedInstance, Bindable.class);
            }
        }

        return new BindableDescriptor(clazz, bindableMetadata, bindableDescriptors,
            attributeDescriptors);
    }

    static boolean noHiddenBindables(Field field, Object initializedInstance, Class<? extends Annotation> markerAnnotation) {
      // Get class of runtime value
      Object fieldValue = null;
      try
      {
        field.setAccessible(true);
        fieldValue = field.get(initializedInstance);
        if (fieldValue != null && fieldValue.getClass().getAnnotation(markerAnnotation) != null) {
          throw new AssertionError("A non-public field contains a bindable object: "
              + field.getDeclaringClass().getName() + "#" + field.getName() + " => " + fieldValue.getClass().getName());
        }
      }
      catch (final SecurityException e)
      {
        // Ignore. We can't get access to the field.
      }
      catch (IllegalArgumentException e)
      {
        throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
        throw new RuntimeException(e);
      }

      return true;
    }

    /**
     * Builds descriptors for direct attributes.
     */
    private static Map<String, AttributeDescriptor> buildAttributeDescriptors(
        Object initializedInstance, BindableMetadata bindableMetadata)
    {
        final Class<?> clazz = initializedInstance.getClass();

        final Map<String, AttributeDescriptor> result = new LinkedHashMap<>();
        final Collection<Field> fieldsFromBindableHierarchy = BindableUtils
            .getFieldsFromBindableHierarchy(clazz);

        for (final Field field : fieldsFromBindableHierarchy)
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
     * Builds {@link AttributeDescriptor} for a field from a {@link Bindable} type.
     */
    private static AttributeDescriptor buildAttributeDescriptor(
        Object initializedInstance, Field field, BindableMetadata bindableMetadata)
    {
        Object defaultValue = null;

        try
        {
            defaultValue = field.get(initializedInstance);
        }
        catch (IllegalAccessException e)
        {
            throw new AssertionError("An attribute field must be public: " +
                field.getDeclaringClass().getName() + "#" + field.getName());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not retrieve default value of attribute: "
                + BindableUtils.getKey(field), e);
        }

        AttributeMetadata attributeMetadata = null;
        if (bindableMetadata != null)
        {
            attributeMetadata = bindableMetadata.getAttributeMetadata().get(
                field.getName());
        }
        return new AttributeDescriptor(field, defaultValue,
            getConstraintAnnotations(field), attributeMetadata);
    }

    /**
     * Gets constraint annotations for a field, ignoring any other annotations.
     */
    private static List<Annotation> getConstraintAnnotations(Field field)
    {
        final Annotation [] annotations = field.getAnnotations();
        final ArrayList<Annotation> constraintAnnotations = new ArrayList<>();
        for (Annotation annotation : annotations)
        {
            if (annotation.annotationType().isAnnotationPresent(IsConstraint.class))
            {
                constraintAnnotations.add(annotation);
            }
        }

        return constraintAnnotations;
    }
}
