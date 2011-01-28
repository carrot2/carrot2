
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

package org.carrot2.core;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.Pair;
import org.carrot2.util.attribute.*;
import org.carrot2.util.pool.*;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

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
                ControllerUtils.init(component, initAttrs, initOutputAttrs, false,
                    context);

                // Capture @Init @Output attributes
                initOutputAttributes.putIfAbsent(
                    new Pair<Class<? extends IProcessingComponent>, String>(component
                        .getClass(), parameter), ImmutableMap.copyOf(initOutputAttrs));

                // To support a very natural scenario where processing attributes are
                // provided/overridden during initialization, we'll also bind processing
                // attributes here. We need to switch off checking for required attributes
                // as the required processing attributes will be most likely provided at
                // request time. We explicitly skip @Init @Processing attributes here as
                // @Init attributes have already been bound above.
                try
                {
                    AttributeBinder.set(component, initAttrs, false,
                        new Predicate<Field>()
                        {
                            public boolean apply(Field field)
                            {
                                return field.getAnnotation(Input.class) != null
                                    && (field.getAnnotation(Processing.class) != null && field
                                        .getAnnotation(Init.class) == null);
                            }
                        });
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
        private ConcurrentHashMap<ReferenceEquality, Map<String, Object>> resetValues = 
            new ConcurrentHashMap<ReferenceEquality, Map<String, Object>>();

        @SuppressWarnings("unchecked")
        public void activate(IProcessingComponent processingComponent, String parameter)
        {
            // Remember values of @Input @Processing attributes
            final Map<String, Object> originalValues = Maps.newHashMap();
            try
            {
                AttributeBinder.get(processingComponent, originalValues, 
                    Input.class, Processing.class);

                resetValues.put(new ReferenceEquality(processingComponent),
                                originalValues);
            }
            catch (Exception e)
            {
                throw new ProcessingException("Could not unbind attribute values", e);
            }
        }

        @SuppressWarnings("unchecked")
        public void passivate(IProcessingComponent processingComponent, String parameter)
        {
            // Reset values of @Input @Processing attributes back to original values
            try
            {
                // Here's a little hack: we need to disable checking
                // for required attributes, otherwise, we won't be able
                // to reset @Required input attributes to null
                AttributeBinder.set(processingComponent, 
                    resetValues.get(new ReferenceEquality(processingComponent)),
                    false, Input.class, Processing.class);
            }
            catch (Exception e)
            {
                throw new ProcessingException("Could not reset attribute values", e);
            }
        }
    }
}
