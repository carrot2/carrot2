
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
import java.util.Map;
import java.util.function.Predicate;

import org.carrot2.util.attribute.AttributeBinder.BindingTracker;
import org.carrot2.util.attribute.AttributeBinder.IAttributeBinderAction;

/**
 * A very simple field value injector based on the {@link AttributeBinder}. The injector
 * can recursively inject values to the designated non-static fields of the root injection
 * receiver object. Such an injector can be useful to set references to common resources
 * (e.g. internal processing contexts) on an object hierarchy. 
 */
public class AttributeBinderInjector
{
    /**
     * Injects values to an object's non-static fields by value type.
     * 
     * @param injectableMarker a marker interface that designates injection receivers
     * @param values a mapping between field type and the value to set, <code>null</code>
     *            values are allowed and will be transferred to the object's fields. If
     *            the map does not contain a key equal an object field's type, the field's
     *            value will not be changed.
     * @param objects All objects whose fields should be set. Objects must be marked with the
     *            <code>injectableMarker</code> annotation. All objects' fields whose
     *            types are marked with the <code>injectableMarker</code> will also
     *            recursively receive value injection.
     */
    public static void injectByType(Class<? extends Annotation> injectableMarker,
        final Map<Class<?>, Object> values, Object... objects)
    {
        try
        {
            final IAttributeBinderAction [] actions = new AttributeBinder.IAttributeBinderAction []
            {
                new InjectByType(values)
            };
            for (Object o : objects)
            {
                if (o != null)
                {
                    Predicate<Field> p = (field) -> values.containsKey(field.getType());
                    AttributeBinder.bind(o, actions, p, injectableMarker);
                }
            }
        }
        catch (Exception e)
        {
            throw AttributeUtils.propagate(e);
        }
    }

    private static class InjectByType implements AttributeBinder.IAttributeBinderAction
    {
        final private Map<Class<?>, Object> values;

        InjectByType(Map<Class<?>, Object> values)
        {
            this.values = values;
        }

        @Override
        public void performAction(BindingTracker bindingTracker, int level,
            Object object, Field field, Object value, Predicate<Field> predicate)
            throws InstantiationException
        {
              if (!Modifier.isStatic(field.getModifiers()) && 
                  values.containsKey(field.getType()))
              {
                  if (!Modifier.isPublic(field.getModifiers())) {
                    throw AttributeBindingException.createWithNoKey("Could not assign to non-public field "
                        + object.getClass().getName() + "#" + field.getName()
                        + ", value " + value);
                  }

                  try
                  {
                    field.set(object, values.get(field.getType()));
                  }
                  catch (final Exception e)
                  {
                      throw AttributeBindingException.createWithNoKey("Could not assign field "
                          + object.getClass().getName() + "#" + field.getName()
                          + " with value " + value, e);
                  }
              }
        }
    }
}
