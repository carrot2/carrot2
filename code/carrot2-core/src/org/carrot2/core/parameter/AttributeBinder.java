package org.carrot2.core.parameter;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AttributeBinder
{
    /*
     * 
     */
    private final static class FieldInstance
    {
        final Field field;
        final Object instance;

        public FieldInstance(Field f, Object o)
        {
            field = f;
            instance = o;
        }
    }

    /*
     * 
     */
    public static void bind(Object instance, Map<String, Object> values,
        BindingDirection bindingDirection) throws IllegalArgumentException
    {
        if (instance.getClass().getAnnotation(Bindable.class) == null)
        {
            throw new IllegalArgumentException("Class is not bindable: "
                + instance.getClass().getName());
        }

        final Map<String, FieldInstance> fields = getFields(instance, bindingDirection);

        if (bindingDirection == BindingDirection.IN || bindingDirection == BindingDirection.INOUT)
        {
            final Set<String> keySet = (values.size() > fields.size() ? fields.keySet()
                : values.keySet());
            for (String key : keySet)
            {
                final FieldInstance fi = fields.get(key);
                if (fi == null) continue;

                final Object value = values.get(key);
                if (value == null && !values.containsKey(key))
                {
                    continue;
                }

                try
                {
                    fi.field.set(fi.instance, value);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        if (bindingDirection == BindingDirection.OUT || bindingDirection == BindingDirection.INOUT)
        {
            for (Map.Entry<String, FieldInstance> kv : fields.entrySet())
            {
                final FieldInstance fi = kv.getValue();
                final String key = kv.getKey();

                try
                {
                    values.put(key, fi.field.get(fi.instance));
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /*
     * 
     */
    private static Map<String, FieldInstance> getFields(Object instance,
        BindingDirection bindingDirection)
    {
        final HashSet<Object> instances = new HashSet<Object>();
        final HashMap<String, FieldInstance> fields = new HashMap<String, FieldInstance>();
        getFields0(instances, fields, instance, bindingDirection);
        return fields;
    }

    /*
     * 
     */
    private static void getFields0(HashSet<Object> instances,
        HashMap<String, FieldInstance> fields, Object instance, BindingDirection bindingDirection)
    {
        instances.add(instance);

        final Field [] classFields = instance.getClass().getDeclaredFields();
        AccessibleObject.setAccessible(classFields, true);

        for (Field f : classFields)
        {
            final Attribute ann = f.getAnnotation(Attribute.class);
            if (ann != null)
            {
                if (bindingDirection == BindingDirection.INOUT || ann.bindingDirection() == BindingDirection.INOUT
                    || ann.bindingDirection() == bindingDirection)
                {
                    final String key;
                    if (ann.key().equals(""))
                    {
                        key = BindableUtils.getPrefix(instance) + "." + f.getName();
                    }
                    else
                    {
                        key = ann.key();
                    }

                    if (fields.put(key, new FieldInstance(f, instance)) != null)
                    {
                        throw new RuntimeException("A field with duplicated key exists.");
                    }
                }
            }
        }

        // recursive descend.
        for (Field f : classFields)
        {
            try
            {
                final Object fieldValue = f.get(instance);
                if (fieldValue != null)
                {
                    final Bindable ann = fieldValue.getClass().getAnnotation(
                        Bindable.class);
                    if (ann != null)
                    {
                        if (!instances.contains(fieldValue))
                        {
                            getFields0(instances, fields, fieldValue, bindingDirection);
                        }
                    }
                }
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

}
