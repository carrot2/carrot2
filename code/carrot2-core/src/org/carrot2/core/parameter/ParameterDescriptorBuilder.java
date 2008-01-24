package org.carrot2.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;

import org.carrot2.core.constraint.*;

/**
 * 
 */
public class ParameterDescriptorBuilder
{
    /**
     *
     */
    public static Collection<ParameterDescriptor> getParameterDescriptors(
        Object instance, Class<? extends Annotation> bindingTimeAnnotation,
        Class<? extends Annotation> bindingDirectionAnnotation)
    {
        // Output array of parameters.
        final ArrayList<ParameterDescriptor> params = new ArrayList<ParameterDescriptor>();

        // Get the field names that correspond to the requested policy.
        final Collection<Field> bindableFields = getParameterFieldMap(
            instance.getClass(), bindingTimeAnnotation, bindingDirectionAnnotation)
            .values();

        for (final Field field : bindableFields)
        {
            final String fieldName = BindableUtils.getKey(field);

            Constraint constraint = BindableUtils.getConstraint(field);

            Object fieldValue;
            try
            {
                fieldValue = field.get(instance);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Could not retrieve default value of field: "
                    + fieldName);
            }

            params.add(new ParameterDescriptor(fieldName, fieldValue, constraint, field));
        }

        return params;
    }

    /**
     *
     */
    static Map<String, Field> getParameterFieldMap(Class<?> clazz,
        Class<? extends Annotation> bindingTimeAnnotation,
        Class<? extends Annotation> bindingDirectionAnnotation)
    {
        final Collection<Field> fieldSet = BindableUtils
            .getFieldsFromBindableHierarchy(clazz);
        final Field [] fields = fieldSet.toArray(new Field [fieldSet.size()]);
        final Map<String, Field> result = new HashMap<String, Field>(fields.length);

        AccessibleObject.setAccessible(fields, true);
        for (final Field field : fields)
        {
            // For binding time and direction we only need to check if we receive
            // a non-value -- this means the field has the annotation we want
            if (field.getAnnotation(Parameter.class) == null
                || field.getAnnotation(bindingDirectionAnnotation) == null
                || field.getAnnotation(bindingTimeAnnotation) == null)
            {
                continue;
            }

            if (result.put(BindableUtils.getKey(field), field) != null)
            {
                throw new RuntimeException("A field with duplicated key exist: "
                    + BindableUtils.getKey(field));
            }
        }

        return result;
    }
}
