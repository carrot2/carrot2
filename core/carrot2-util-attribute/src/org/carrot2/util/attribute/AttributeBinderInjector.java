
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
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

import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.AttributeBinder.BindingTracker;
import org.carrot2.util.attribute.AttributeBinder.IAttributeBinderAction;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

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
     * @param object the object whose fields to set. The object must be marked with the
     *            <code>injectableMarker</code> annotation. All object's fields whose
     *            types are marked with the <code>injectableMarker</code> will also
     *            recursively receive value injection.
     */
    public static void injectByType(Class<? extends Annotation> injectableMarker,
        Map<Class<?>, Object> values, Object... objects)
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
                    AttributeBinder.bind(o, actions, Predicates.<Field> alwaysTrue(),
                        injectableMarker);
                }
            }
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
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
            try
            {
                if (!Modifier.isStatic(field.getModifiers())
                    && values.containsKey(field.getType()))
                {
                    field.set(object, values.get(field.getType()));
                }
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
