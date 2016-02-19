
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.Pair;
import org.carrot2.util.annotations.ThreadSafe;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.AttributeBinder.AllAnnotationsPresentPredicate;
import org.carrot2.util.attribute.AttributeBinder.BindingTracker;
import org.carrot2.util.attribute.AttributeBinder.IAttributeBinderAction;
import org.carrot2.util.attribute.AttributeBindingException;
import org.carrot2.util.attribute.BindableUtils;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.pool.IActivationListener;
import org.carrot2.util.pool.IDisposalListener;
import org.carrot2.util.pool.IInstantiationListener;
import org.carrot2.util.pool.IParameterizedPool;
import org.carrot2.util.pool.IPassivationListener;
import org.carrot2.util.pool.SoftUnboundedPool;
import org.carrot2.util.resource.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.collect.ImmutableSet;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * An {@link IProcessingComponentManager} that pools instances of processing components.
 */
public class PoolingProcessingComponentManager implements IProcessingComponentManager
{
    /** Controller context */
    private IControllerContext context;

    /** Pool for component instances. */
    private volatile IParameterizedPool<IProcessingComponent, String> componentPool;

    /** A copy of init attributes */
    private Map<String, Object> initAttributes;

    /** Component configurations, may be empty. */
    private Map<String, ProcessingComponentConfiguration> componentIdToConfiguration;

    /**
     * Values of {@link Init} {@link Output} attributes collected during initialization.
     * As the {@link #prepare(Class, String, Map, Map)} method is expected to return these
     * and we are borrowing instances from the pool, we need to keep track of
     * initialization results in the ComponentInstantiationListener and pass them back in
     * the create() method.
     */
    private ConcurrentHashMap<Pair<Class<? extends IProcessingComponent>, String>, Map<String, Object>> initOutputAttributes = new ConcurrentHashMap<Pair<Class<? extends IProcessingComponent>, String>, Map<String, Object>>();

    /**
     * Creates a new {@link PoolingProcessingComponentManager} using the default pool
     * implementation {@link SoftUnboundedPool}).
     */
    public PoolingProcessingComponentManager()
    {
        this(new SoftUnboundedPool<IProcessingComponent, String>());
    }

    /**
     * Creates a new {@link PoolingProcessingComponentManager} with a custom pool
     * implementation.
     * 
     * @param componentPool the pool to be used by this manager
     */
    public PoolingProcessingComponentManager(
        IParameterizedPool<IProcessingComponent, String> componentPool)
    {
        this.componentPool = componentPool;

        final ComponentResetListener componentResetListener = new ComponentResetListener();
        componentPool.init(new ComponentInstantiationListener(), componentResetListener,
            componentResetListener, ComponentDisposalListener.INSTANCE);
    }

    @Override
    public void init(IControllerContext context, Map<String, Object> attributes,
        ProcessingComponentConfiguration... configurations)
    {
        assert context != null;

        // This will ensure that one manager is used with only one controller
        if (this.context != null)
        {
            throw new IllegalStateException("This manager has already been initialized.");
        }

        this.context = context;
        this.initAttributes = Maps.newHashMap(attributes);
        this.componentIdToConfiguration = ProcessingComponentConfiguration
            .indexByComponentId(configurations);
    }

    @Override
    public IProcessingComponent prepare(Class<? extends IProcessingComponent> clazz,
        String id, Map<String, Object> inputAttributes,
        Map<String, Object> outputAttributes)
    {
        try
        {
            final IProcessingComponent component = componentPool.borrowObject(clazz, id);

            // Pass @Init @Output attributes back to the caller.
            final Map<String, Object> initOutputAttrs = initOutputAttributes
                .get(new Pair<Class<? extends IProcessingComponent>, String>(component
                    .getClass(), id));
            if (initOutputAttrs != null)
            {
                outputAttributes.putAll(initOutputAttrs);
            }

            return component;
        }
        catch (final InstantiationException e)
        {
            throw new ComponentInitializationException(
                "Could not instantiate component class: " + clazz.getName(), e);
        }
        catch (final IllegalAccessException e)
        {
            throw new ComponentInitializationException(
                "Could not instantiate component class: " + clazz.getName(), e);
        }
    }

    @Override
    public void recycle(IProcessingComponent component, String id)
    {
        componentPool.returnObject(component, id);
    }

    @Override
    public void dispose()
    {
        componentPool.dispose();
    }

    /**
     * Initializes newly created component instances, remembers attribute values so that
     * they can be reset after the component gets returned to the pool.
     */
    private final class ComponentInstantiationListener implements
        IInstantiationListener<IProcessingComponent, String>
    {
        public void objectInstantiated(IProcessingComponent component, String parameter)
        {
            try
            {
                final Map<String, Object> initAttrs = Maps.newHashMap(initAttributes);

                if (parameter != null)
                {
                    initAttrs
                        .putAll(componentIdToConfiguration.get(parameter).attributes);
                }

                final Map<String, Object> initOutputAttrs = Maps.newHashMap();

                checkNonPrimitiveInstances(component, initAttrs,
                    new AllAnnotationsPresentPredicate(Input.class, Init.class));
                ControllerUtils.init(component, initAttrs, initOutputAttrs, false,
                    context);

                // Capture @Init @Output attributes
                initOutputAttributes.putIfAbsent(
                    new Pair<Class<? extends IProcessingComponent>, String>(component
                        .getClass(), parameter), 
                        Collections.unmodifiableMap(Maps.newHashMap(initOutputAttrs)));

                // To support a very natural scenario where processing attributes are
                // provided/overridden during initialization, we'll also bind processing
                // attributes here. We need to switch off checking for required attributes
                // as the required processing attributes will be most likely provided at
                // request time. We explicitly skip @Init @Processing attributes here as
                // @Init attributes have already been bound above.
                try
                {
                    final Predicate<Field> predicate = new Predicate<Field>()
                    {
                        public boolean apply(Field field)
                        {
                            return field.getAnnotation(Input.class) != null
                                && (field.getAnnotation(Processing.class) != null && field
                                    .getAnnotation(Init.class) == null);
                        }
                    };
                    checkNonPrimitiveInstances(component, initAttrs, predicate);
                    AttributeBinder.set(component, initAttrs, false, predicate);
                }
                catch (AttributeBindingException e)
                {
                    throw new ComponentInitializationException(
                        "Could not initialize component", e);
                }
                catch (InstantiationException e)
                {
                    throw new ComponentInitializationException(
                        "Could not initialize component", e);
                }
            }
            catch (Exception e)
            {
                // If init() throws any exception, this exception will
                // be propagated to the borrowObject() call.
                component.dispose();

                throw ExceptionUtils.wrapAs(ComponentInitializationException.class, e);
            }
        }

        /**
         * Performs safety checks aimed at reporting attempts to set non-primitive
         * non-thread-safe primitive instances during initialization. These may lead to
         * hard-to-trace bugs.
         * 
         * @see "https://issues.apache.org/jira/browse/SOLR-2282"
         */
        void checkNonPrimitiveInstances(IProcessingComponent processingComponent,
            Map<String, Object> inputAttributes, Predicate<Field> predicate)
            throws InstantiationException
        {
            AttributeBinder.bind(processingComponent, new IAttributeBinderAction []
            {
                new NonPrimitiveInputAttributesCheck(inputAttributes)
            }, predicate);
        }
    }

    /**
     * An {@link IAttributeBinderAction} that checks for non-primitive instances passed
     * for binding at init time. If they are not declared {@link ThreadSafe}, an info is
     * logged.
     */
    static final class NonPrimitiveInputAttributesCheck implements IAttributeBinderAction
    {
        static boolean makeAssertion = false;

        private static final Logger log = LoggerFactory
            .getLogger(NonPrimitiveInputAttributesCheck.class);

        static final Set<Class<?>> ALLOWED_PLAIN_TYPES = ImmutableSet.<Class<?>> of(
            Boolean.class, Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, Character.class, File.class, String.class,
            Calendar.class, Date.class);

        static final Set<Class<?>> ALLOWED_ASSIGNABLE_TYPES = ImmutableSet.<Class<?>> of(
            Enum.class, IResource.class, Collection.class, Map.class);

        // A number of safe typically used classes
        static final Set<String> ALLOWED_PLAIN_TYPES_BY_NAME = ImmutableSet.of(
            "org.apache.lucene.store.FSDirectory",
            "org.apache.lucene.store.RAMDirectory",
            "org.apache.lucene.store.MMapDirectory",
            "org.apache.lucene.store.SimpleFSDirectory",
            "org.apache.lucene.store.SimpleFSDirectory");

        private final Map<String, Object> values;

        NonPrimitiveInputAttributesCheck(Map<String, Object> values)
        {
            this.values = values;
        }

        @Override
        public void performAction(BindingTracker bindingTracker, int level,
            Object object, Field field, Object fieldValue, Predicate<Field> predicate)
            throws InstantiationException
        {
            final String key = BindableUtils.getKey(field);
            final Object value = values.get(key);
            if (value == null || Class.class.isInstance(value)
                || Proxy.isProxyClass(value.getClass())
                || value.getClass().getAnnotation(ThreadSafe.class) != null)
            {
                return;
            }

            // If there's an @Init @Input non-primitive attribute whose type
            // is not declared as @ThreadSafe, log a warning.
            final Class<?> valueType = value.getClass();

            if (!ALLOWED_PLAIN_TYPES.contains(valueType)
                && !ALLOWED_PLAIN_TYPES_BY_NAME.contains(valueType.getName())
                && !isAllowedAssignableType(valueType))
            {
                log.info("An object of a non-@ThreadSafe class " + valueType.getName()
                    + " bound at initialization-time to attribute " + key
                    + ". Make sure this is intended.");
                if (makeAssertion)
                {
                    assert false : "An object of a non-@ThreadSafe class "
                        + valueType.getName()
                        + " bound at initialization-time to attribute " + key
                        + ". Make sure this intended.";
                }
            }
        }

        private static boolean isAllowedAssignableType(Class<?> attributeType)
        {
            for (Class<?> clazz : ALLOWED_ASSIGNABLE_TYPES)
            {
                if (clazz.isAssignableFrom(attributeType))
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Disposes of components on shut down.
     */
    private final static class ComponentDisposalListener implements
        IDisposalListener<IProcessingComponent, String>
    {
        final static ComponentDisposalListener INSTANCE = new ComponentDisposalListener();

        public void dispose(IProcessingComponent component, String parameter)
        {
            component.dispose();
        }
    }

    /**
     * Resets {@link Processing} attribute values before the component is returned to the
     * pool.
     */
    private static final class ComponentResetListener implements
        IPassivationListener<IProcessingComponent, String>,
        IActivationListener<IProcessingComponent, String>
    {
        /**
         * Stores values of {@link Processing} attributes for the duration of processing.
         */
        private ConcurrentHashMap<ReferenceEquality, Map<String, Object>> resetValues = new ConcurrentHashMap<ReferenceEquality, Map<String, Object>>();

        public void activate(IProcessingComponent processingComponent, String parameter)
        {
            // Remember values of @Input @Processing attributes
            final Map<String, Object> originalValues = Maps.newHashMap();
            try
            {
                AttributeBinder.get(processingComponent, originalValues, Input.class,
                    Processing.class);

                resetValues.put(new ReferenceEquality(processingComponent),
                    originalValues);
            }
            catch (Exception e)
            {
                throw new ProcessingException("Could not unbind attribute values", e);
            }
        }

        public void passivate(IProcessingComponent processingComponent, String parameter)
        {
            // Reset values of @Input @Processing attributes back to original values
            try
            {
                // Here's a little hack: we need to disable checking
                // for required attributes, otherwise, we won't be able
                // to reset @Required input attributes to null
                final Map<String, Object> originalAttributes = resetValues
                    .get(new ReferenceEquality(processingComponent));
                if (originalAttributes != null) {
                    AttributeBinder.set(processingComponent, originalAttributes, false,
                        Input.class, Processing.class);
                }
            }
            catch (Exception e)
            {
                throw new ProcessingException("Could not reset attribute values", e);
            }
        }
    }
}
