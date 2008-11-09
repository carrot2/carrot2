
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.carrot2.core.attribute.*;
import org.carrot2.util.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.AttributeBinder.IAttributeBinderAction;
import org.carrot2.util.attribute.AttributeBinder.AttributeBinderActionCollect;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.pool.*;
import org.carrot2.util.resource.ClassResource;

import com.google.common.base.Function;
import com.google.common.collect.*;

/**
 * A controller implementing the life cycle described in {@link IProcessingComponent} with
 * support for component pooling and, optionally, data caching.
 * <p>
 * Calls to {@link #process(Map, Class...)} are thread-safe, although some care should be
 * given to initialization: {@link #init(Map)} should be called before other threads are
 * allowed to see this object and {@link #dispose()} should be called after all threads
 * leave {@link #process(Map, Class...)}.
 * <p>
 * Notice for {@link IProcessingComponent} developers: if data caching is used (see
 * {@link #CachingController(Class...)}), values of {@link Output} attributes produced by
 * the components whose output is to be cached (e.g., the {@link Document} instances in
 * case {@link IDocumentSource} output is cached) may be accessed concurrently and
 * therefore must be thread-safe.
 */
public final class CachingController implements IController
{
    /** Private monitor for multi-threaded critical sections. */
    final Object reentrantLock = new Object();

    /** Pool for component instances. */
    private volatile SoftUnboundedPool<IProcessingComponent, String> componentPool;

    /**
     * Original values of {@link Processing} attributes that will be restored in the
     * component after processing finishes.
     * <p>
     * Access monitor: {#link #reentrantLock}.
     */
    private final Map<Pair<Class<? extends IProcessingComponent>, String>, Map<String, Object>> resetAttributes = Maps
        .newHashMap();

    /**
     * Descriptors of {@link Input} and {@link Output} {@link Processing} attributes of
     * components whose output is to be cached.
     */
    private final Map<Pair<Class<? extends IProcessingComponent>, String>, InputOutputAttributeDescriptors> cachedComponentAttributeDescriptors = Maps
        .newHashMap();

    /**
     * Maintains a mapping between component ids and their classes. Initialized in
     * {@link #init(Map, ComponentConfiguration...)}, used in
     * {@link #process(Map, String...)}.
     */
    private FromIdProcessingComponentClassResolver processingComponentClassResolver;

    /**
     * A set of {@link IProcessingComponent}s whose data should be cached internally.
     */
    private final Set<Class<? extends IProcessingComponent>> cachedComponentClasses;

    /**
     * Populates on-demand and caches the data from components of classes provided in
     * {@link #cachedComponentClasses}. The key of the cache is a map of all {@link Input}
     * {@link Processing} attributes of the component for which caching is performed. The
     * value of the cache is a map of all {@link Output} {@link Processing} attributes
     * produced by the component.
     */
    private SelfPopulatingCache dataCache;

    /** Ehcache manager */
    private CacheManager cacheManager;

    /** Controller context for this controller. */
    private ControllerContextImpl context;

    /** Tracks various processing statistics */
    private ProcessingStatistics statistics = new ProcessingStatistics();

    /**
     * Creates a new caching controller.
     * 
     * @param cachedComponentClasses classes of components whose output should be cached
     *            by the controller. If a superclass is provided here, e.g.
     *            {@link IDocumentSource}, all its subclasses will be subject to caching. If
     *            {@link IProcessingComponent} is provided here, output of all components
     *            will be cached.
     */
    public CachingController(
        Class<? extends IProcessingComponent>... cachedComponentClasses)
    {
        this.cachedComponentClasses = Sets.newHashSet(cachedComponentClasses);
    }

    /*
     * 
     */
    public void init(Map<String, Object> initAttributes)
        throws ComponentInitializationException
    {
        init(initAttributes, EMPTY_COMPONENT_CONFIGURATION_ARRAY);
    }

    /**
     * Initializes this component based on the provided {@link ProcessingComponentSuite}.
     */
    public void init(Map<String, Object> globalInitAttributes,
        ProcessingComponentSuite processingComponentSuite)
        throws ComponentInitializationException
    {
        final List<ProcessingComponentConfiguration> componentConfigurations = Lists
            .<ProcessingComponentDescriptor, ProcessingComponentConfiguration> transform(
                processingComponentSuite.getComponents(),
                new Function<ProcessingComponentDescriptor, ProcessingComponentConfiguration>()
                {
                    public ProcessingComponentConfiguration apply(
                        ProcessingComponentDescriptor descriptor)
                    {
                        return descriptor.getComponentConfiguration();
                    }
                });

        init(globalInitAttributes,
            componentConfigurations
                .toArray(new ProcessingComponentConfiguration [componentConfigurations
                    .size()]));
    }

    /**
     * An additional method to initialize this component, which enables processing with
     * differently configured instances of the same {@link IProcessingComponent} class.
     * Processing with components initialized in this method can be performed using
     * {@link #process(Map, String...)}.
     * 
     * @param globalInitAttributes see {@link IController#init(Map)}. Global initialization
     *            attributes will be overridden by component-specific initialization
     *            attributes, if provided.
     * @param componentConfigurations component configurations to be used. Identifiers of
     *            the provided components must be unique.
     */
    public void init(Map<String, Object> globalInitAttributes,
        ProcessingComponentConfiguration... componentConfigurations)
        throws ComponentInitializationException
    {
        context = new ControllerContextImpl();

        // Prepare component-specific init attributes
        final Map<Pair<Class<? extends IProcessingComponent>, String>, Map<String, Object>> componentSpecificInitAttributes = Maps
            .newHashMap();
        final Map<String, Class<? extends IProcessingComponent>> idToComponentClass = Maps
            .newHashMap();
        for (ProcessingComponentConfiguration componentConfiguration : componentConfigurations)
        {
            final Map<String, Object> mergedAttributes = Maps
                .newHashMap(globalInitAttributes);
            mergedAttributes.putAll(componentConfiguration.attributes);

            componentSpecificInitAttributes.put(
                new Pair<Class<? extends IProcessingComponent>, String>(
                    componentConfiguration.componentClass,
                    componentConfiguration.componentId), mergedAttributes);

            if (idToComponentClass.put(componentConfiguration.componentId,
                componentConfiguration.componentClass) != null)
            {
                throw new ComponentInitializationException("Duplicate component id: "
                    + componentConfiguration.componentId);
            }
        }
        processingComponentClassResolver = new FromIdProcessingComponentClassResolver(
            idToComponentClass);

        // Create the pool
        componentPool = new SoftUnboundedPool<IProcessingComponent, String>(
            new ComponentInstantiationListener(Maps.newHashMap(globalInitAttributes),
                componentSpecificInitAttributes), null,
            new ComponentPassivationListener(), ComponentDisposalListener.INSTANCE);

        // Initialize cache if needed
        if (!cachedComponentClasses.isEmpty())
        {
            try
            {
                cacheManager = CacheManager.create(new ClassResource(
                    CachingController.class, "/controller-ehcache.xml").open());
            }
            catch (IOException e)
            {
                throw new ComponentInitializationException("Could not initalize cache.",
                    e);
            }

            if (!cacheManager.cacheExists("data"))
            {
                cacheManager.addCache("data");
            }
            dataCache = new SelfPopulatingCache(cacheManager.getCache("data"),
                new CachedDataFactory());
        }
    }

    /**
     * A generic method that runs processing for a given set of component instances
     * (created from a sequence of classes).
     */
    public ProcessingResult process(Map<String, Object> attributes,
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        return processInternal(attributes,
            IdentityProcessingComponentClassResolver.INSTANCE, processingComponentClasses);
    }

    /**
     * An additional method for performing processing using configurations provided in
     * {@link #init(Map, ComponentConfiguration...)}.
     * 
     * @param attributes see {@link IController#process(Map, Class...)}
     * @param processingComponentIds identifiers of components to be involved in
     *            processing, in the order they should be arranged in the pipeline.
     */
    public ProcessingResult process(Map<String, Object> attributes,
        String... processingComponentIds) throws ProcessingException
    {
        return processInternal(attributes, processingComponentClassResolver,
            processingComponentIds);
    }

    /**
     * Internal implementation if processing. We need it to have all the logic (borrowing,
     * returning components etc.) at one place.
     */
    private <T> ProcessingResult processInternal(Map<String, Object> attributes,
        IProcessingComponentClassResolver<T> resolver, T... componentIds)
    {
        if (this.context == null)
        {
            throw new IllegalStateException("Controller not initialized.");
        }

        final SoftUnboundedPool<IProcessingComponent, String> componentPool = this.componentPool;
        if (componentPool == null)
        {
            throw new IllegalStateException("Initialize the controller first.");
        }

        final String actualComponentIds[] = new String [componentIds.length];
        final IProcessingComponent [] processingComponents = new IProcessingComponent [componentIds.length];
        ProcessingResult processingResult = null;
        try
        {
            // Borrow instances of processing components.
            for (int i = 0; i < processingComponents.length; i++)
            {
                final Pair<Class<? extends IProcessingComponent>, String> resolved = resolver
                    .resolve(componentIds[i]);

                actualComponentIds[i] = resolved.objectB;
                processingComponents[i] = getProcessingComponent(resolved.objectA,
                    resolved.objectB, attributes);
            }

            for (IProcessingComponent processingComponent : processingComponents)
            {
                ControllerUtils.performProcessing(processingComponent, attributes,
                    !(processingComponent instanceof CachedProcessingComponent));
            }

            processingResult = new ProcessingResult(attributes);
            return processingResult;
        }
        finally
        {
            statistics.update(processingResult);

            for (int i = 0; i < processingComponents.length; i++)
            {
                if (!(processingComponents[i] instanceof CachedProcessingComponent))
                {
                    componentPool.returnObject(processingComponents[i],
                        actualComponentIds[i]);
                }
            }
        }
    }

    /**
     * Borrows a processing component from the pool or creates a
     * {@link CachedProcessingComponent} for caching.
     */
    private IProcessingComponent getProcessingComponent(
        Class<? extends IProcessingComponent> componentClass, String id,
        Map<String, Object> attributes)
    {
        for (Class<?> clazz : cachedComponentClasses)
        {
            if (clazz.isAssignableFrom(componentClass))
            {
                return new CachedProcessingComponent(componentClass, id, attributes);
            }
        }

        return borrowProcessingComponent(componentClass, id);
    }

    /**
     * Borrows a component from the pool and converts exceptions to
     * {@link ComponentInitializationException}.
     */
    private IProcessingComponent borrowProcessingComponent(
        Class<? extends IProcessingComponent> componentClass, String componentId)
    {
        try
        {
            return componentPool.borrowObject(componentClass, componentId);
        }
        catch (final InstantiationException e)
        {
            throw new ComponentInitializationException(
                "Could not instantiate component class: " + componentClass.getName(), e);
        }
        catch (final IllegalAccessException e)
        {
            throw new ComponentInitializationException(
                "Could not instantiate component class: " + componentClass.getName(), e);
        }
    }

    /*
     * We are making an implicit assumption that init(), process() and dispose() will be
     * called sequentially. This may or may not be true, especially with regard to data
     * visibility between threads in process() and dispose(). If a number of threads is
     * inside process(), calling dispose() may cause unpredictable side-effects
     * (exceptions from internal pools?).
     */
    public void dispose()
    {
        if (context != null)
        {
            componentPool.dispose();

            if (cacheManager != null)
            {
                cacheManager.shutdown();
            }

            context.dispose();
            this.context = null;
        }
    }

    /**
     * Returns processing statistics for this controller. The returned object is immutable
     * and will not be updated after more queries are handled, to obtain updated
     * statistics, call this method again.
     */
    public CachingControllerStatistics getStatistics()
    {
        return statistics.getStatistics();
    }

    /**
     * Resolves {@link IProcessingComponent} classes based on the provided componentId.
     */
    private static interface IProcessingComponentClassResolver<T>
    {
        Pair<Class<? extends IProcessingComponent>, String> resolve(T componentId);
    }

    /**
     * Resolves {@link IProcessingComponent} classes from component ids being the classes
     * themselves.
     */
    private static class IdentityProcessingComponentClassResolver implements
        IProcessingComponentClassResolver<Class<?>>
    {
        final static IdentityProcessingComponentClassResolver INSTANCE = new IdentityProcessingComponentClassResolver();

        @SuppressWarnings("unchecked")
        public Pair<Class<? extends IProcessingComponent>, String> resolve(
            Class<?> componentId)
        {
            return new Pair<Class<? extends IProcessingComponent>, String>(
                (Class<? extends IProcessingComponent>) componentId, null);
        }
    }

    /**
     * Resolves {@link IProcessingComponent} classes from the component ids, based on the
     * provided id-class mapping.
     */
    private static class FromIdProcessingComponentClassResolver implements
        IProcessingComponentClassResolver<String>
    {
        private final Map<String, Class<? extends IProcessingComponent>> idToComponentClass;

        public FromIdProcessingComponentClassResolver(
            Map<String, Class<? extends IProcessingComponent>> idToComponentClass)
        {
            this.idToComponentClass = idToComponentClass;
        }

        @SuppressWarnings("unchecked")
        public Pair<Class<? extends IProcessingComponent>, String> resolve(
            String componentId)
        {
            Class<? extends IProcessingComponent> resultClass;
            String resultComponentId = componentId;

            resultClass = idToComponentClass.get(componentId);
            if (resultClass == null)
            {
                try
                {
                    resultClass = (Class<? extends IProcessingComponent>) Thread
                        .currentThread().getContextClassLoader().loadClass(componentId);

                    // The component id was coerced to a generic class,
                    // so we're not using a specific version of a component.
                    resultComponentId = null;
                }
                catch (ClassNotFoundException e)
                {
                    throw new ProcessingException("Unknown component id: " + componentId);
                }
            }

            return new Pair<Class<? extends IProcessingComponent>, String>(resultClass,
                resultComponentId);
        }
    }

    /**
     * Transforms values of attributes that are bound only at {@link Processing} time and
     * have {@link ImplementingClasses} constraints into the classes of the values.
     */
    private final static class ToClassAttributeTransformer implements
        AttributeBinder.IAttributeTransformer
    {
        static ToClassAttributeTransformer INSTANCE = new ToClassAttributeTransformer();

        public Object transform(Object value, String key, Field field,
            Class<? extends Annotation> bindingDirectionAnnotation,
            Class<? extends Annotation>... filteringAnnotations)
        {
            if (value != null && field.getAnnotation(ImplementingClasses.class) != null
                && field.getAnnotation(Init.class) == null)
            {
                return value.getClass();
            }
            return value;
        }
    }

    /**
     * Initializes newly created component instances, remembers attribute values so that
     * they can be reset after the component gets returned to the pool.
     */
    private final class ComponentInstantiationListener implements
        IInstantiationListener<IProcessingComponent, String>
    {
        private final Map<String, Object> initAttributes;
        private final Map<Pair<Class<? extends IProcessingComponent>, String>, Map<String, Object>> componentSpecificInitAttributes;

        ComponentInstantiationListener(
            Map<String, Object> initAttributes,
            Map<Pair<Class<? extends IProcessingComponent>, String>, Map<String, Object>> componentSpecificInitAttributes)
        {
            this.initAttributes = initAttributes;
            this.componentSpecificInitAttributes = componentSpecificInitAttributes;
        }

        @SuppressWarnings("unchecked")
        public void objectInstantiated(IProcessingComponent component, String parameter)
        {
            try
            {
                final Map<String, Object> specificInitAttributes = componentSpecificInitAttributes
                    .get(new Pair<Class<? extends IProcessingComponent>, String>(component
                        .getClass(), parameter));

                final Map<String, Object> actualInitAttributes;
                if (specificInitAttributes != null)
                {
                    actualInitAttributes = specificInitAttributes;
                }
                else
                {
                    actualInitAttributes = initAttributes;
                }

                // Initialize the component first.
                ControllerUtils.init(component, actualInitAttributes, false, context);

                // To support a very natural scenario where processing attributes are
                // provided/overridden during initialization, we'll also bind processing
                // attributes here. Also, we need to switch off checking for required
                // attributes as the required processing attributes will be most likely
                // provided at request time.
                AttributeBinder.bind(component, actualInitAttributes, false, Input.class,
                    Processing.class);

                // If this is the first component we initialize, remember attribute
                // values so that they can be reset on returning to the pool.
                synchronized (reentrantLock)
                {
                    // Attribute values for resetting
                    final Class<? extends IProcessingComponent> componentClass = component
                        .getClass();
                    Map<String, Object> attributes = resetAttributes
                        .get(new Pair<Class<? extends IProcessingComponent>, String>(
                            componentClass, parameter));
                    if (attributes == null)
                    {
                        attributes = Maps.newHashMap();

                        // We only unbind @Processing attributes here, so components
                        // must not change @Init attributes during processing.
                        // We could unbind @Init attributes also, but this may be
                        // costly when Class -> Object coercion happens.
                        AttributeBinder.bind(component, new IAttributeBinderAction []
                        {
                            new AttributeBinderActionCollect(Input.class, attributes,
                                ToClassAttributeTransformer.INSTANCE),
                        }, Input.class, Processing.class);

                        resetAttributes.put(
                            new Pair<Class<? extends IProcessingComponent>, String>(
                                componentClass, parameter), attributes);
                    }
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
    private final class ComponentPassivationListener implements
        IPassivationListener<IProcessingComponent, String>
    {
        @SuppressWarnings("unchecked")
        public void passivate(IProcessingComponent processingComponent, String parameter)
        {
            // Reset attribute values
            try
            {
                // Here's a little hack: we need to disable checking
                // for required attributes, otherwise, we won't be able
                // to reset @Required input attributes to null
                final Map<String, Object> map;
                synchronized (reentrantLock)
                {
                    map = resetAttributes
                        .get(new Pair<Class<? extends IProcessingComponent>, String>(
                            processingComponent.getClass(), parameter));
                }

                AttributeBinder.bind(processingComponent, map, false, Input.class,
                    Processing.class);
            }
            catch (Exception e)
            {
                throw new ProcessingException("Could not reset attribute values", e);
            }
        }
    }

    private static final String COMPONENT_CLASS_KEY = CachingController.class.getName()
        + ".componentClass";
    private static final String COMPONENT_ID_KEY = CachingController.class.getName()
        + ".componentId";

    /**
     * A stub component that fetches the data from the cache and adds the results to the
     * attribute map.
     */
    @Bindable
    private final class CachedProcessingComponent extends ProcessingComponentBase
    {
        private final Class<? extends IProcessingComponent> componentClass;
        private final String componentId;
        private final Map<String, Object> attributes;

        CachedProcessingComponent(Class<? extends IProcessingComponent> componentClass,
            String componentId, Map<String, Object> attributes)
        {
            this.componentClass = componentClass;
            this.attributes = attributes;
            this.componentId = componentId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void process() throws ProcessingException
        {
            final InputOutputAttributeDescriptors descriptors = prepareDescriptors(componentClass);

            final Map<String, Object> inputAttributes = getAttributesForDescriptors(
                descriptors.inputDescriptors, attributes);
            inputAttributes.put(COMPONENT_CLASS_KEY, componentClass);
            inputAttributes.put(COMPONENT_ID_KEY, componentId);

            try
            {
                final Map<String, Object> processingResult = (Map<String, Object>) dataCache
                    .get(inputAttributes).getObjectValue();

                // Copy the actual results
                attributes.putAll(getAttributesForDescriptors(
                    descriptors.outputDescriptors, processingResult));

                ControllerUtils
                    .addTime(AttributeNames.PROCESSING_TIME_ALGORITHM,
                        (Long) processingResult
                            .get(AttributeNames.PROCESSING_TIME_ALGORITHM), attributes);
                ControllerUtils.addTime(AttributeNames.PROCESSING_TIME_SOURCE,
                    (Long) processingResult.get(AttributeNames.PROCESSING_TIME_SOURCE),
                    attributes);
                ControllerUtils.addTime(AttributeNames.PROCESSING_TIME_TOTAL,
                    (Long) processingResult.get(AttributeNames.PROCESSING_TIME_TOTAL),
                    attributes);

            }
            catch (CacheException e)
            {
                throw ExceptionUtils.wrapAs(ProcessingException.class, e.getCause());
            }
        }

        /**
         * Returns attribute descriptors for {@link Input} {@link Processing} and
         * {@link Output} {@link Processing} attributes of the component whose results
         * will be cached.
         */
        @SuppressWarnings("unchecked")
        private InputOutputAttributeDescriptors prepareDescriptors(
            Class<? extends IProcessingComponent> componentClass)
        {
            InputOutputAttributeDescriptors descriptors = null;

            synchronized (reentrantLock)
            {
                descriptors = cachedComponentAttributeDescriptors
                    .get(new Pair<Class<? extends IProcessingComponent>, String>(
                        componentClass, componentId));
                if (descriptors == null)
                {
                    // Need to borrow a component for a while to build descriptors
                    IProcessingComponent component = null;
                    try
                    {
                        component = borrowProcessingComponent(componentClass, componentId);

                        // Build and store descriptors
                        descriptors = new InputOutputAttributeDescriptors(
                            BindableDescriptorBuilder.buildDescriptor(component, false)
                                .only(Input.class, Processing.class).flatten().attributeDescriptors,
                            BindableDescriptorBuilder.buildDescriptor(component, false)
                                .only(Output.class, Processing.class).flatten().attributeDescriptors);

                        cachedComponentAttributeDescriptors.put(
                            new Pair<Class<? extends IProcessingComponent>, String>(
                                componentClass, componentId), descriptors);
                    }
                    finally
                    {
                        componentPool.returnObject(component, componentId);
                    }
                }
            }

            return descriptors;
        }

        /**
         * Returns a map with only with values corresponding to the provided descriptors.
         */
        Map<String, Object> getAttributesForDescriptors(
            final Map<String, AttributeDescriptor> inputDescriptors,
            Map<String, Object> attributes)
        {
            final Map<String, Object> attributesForDrescriptors = Maps.newHashMap();
            for (AttributeDescriptor descriptor : inputDescriptors.values())
            {
                if (attributes.containsKey(descriptor.key))
                {
                    attributesForDrescriptors.put(descriptor.key, attributes
                        .get(descriptor.key));
                }
            }
            return attributesForDrescriptors;
        }
    }

    /**
     * A cached data factory that actually performs the processing. This factory is called
     * only if the cache does not contain the requested value.
     */
    private final class CachedDataFactory implements CacheEntryFactory
    {
        @SuppressWarnings("unchecked")
        public Object createEntry(Object key) throws Exception
        {
            final Map<String, Object> inputAttributes = (Map<String, Object>) key;

            final Class<? extends IProcessingComponent> componentClass = (Class<? extends IProcessingComponent>) inputAttributes
                .get(COMPONENT_CLASS_KEY);
            final String componentId = (String) inputAttributes.get(COMPONENT_ID_KEY);

            IProcessingComponent component = null;
            try
            {
                component = componentPool.borrowObject(componentClass, componentId);
                final Map<String, Object> attributes = Maps.newHashMap(inputAttributes);
                ControllerUtils.performProcessing(component, attributes, true);

                return attributes;
            }
            finally
            {
                componentPool.returnObject(component, componentId);
            }
        }
    }

    /**
     * Stores a pair of maps of {@link Input} and {@link Output} descriptors.
     */
    private final static class InputOutputAttributeDescriptors
    {
        final Map<String, AttributeDescriptor> inputDescriptors;
        final Map<String, AttributeDescriptor> outputDescriptors;

        InputOutputAttributeDescriptors(
            Map<String, AttributeDescriptor> inputDescriptors,
            Map<String, AttributeDescriptor> outputDescriptors)
        {
            this.inputDescriptors = inputDescriptors;
            this.outputDescriptors = outputDescriptors;
        }
    }

    private final static ProcessingComponentConfiguration [] EMPTY_COMPONENT_CONFIGURATION_ARRAY = new ProcessingComponentConfiguration [0];

    /**
     * Tracks various statistics about processing performed in this component
     */
    final class ProcessingStatistics
    {
        /** Total queries processed (including erroneous) */
        long totalQueries = 0;

        /** Queries that resulted in a processing exception */
        long goodQueries = 0;

        /** Document source processing rolling average time */
        RollingWindowAverage sourceTimeAverage = new RollingWindowAverage(
            5 * RollingWindowAverage.MINUTE, 10 * RollingWindowAverage.SECOND);

        /** Clustering algorithm processing rolling average time */
        RollingWindowAverage algorithmTimeAverage = new RollingWindowAverage(
            5 * RollingWindowAverage.MINUTE, 10 * RollingWindowAverage.SECOND);

        /** Total processing time rolling average time */
        RollingWindowAverage totalTimeAverage = new RollingWindowAverage(
            5 * RollingWindowAverage.MINUTE, 10 * RollingWindowAverage.SECOND);

        /**
         * Updates the statistics
         */
        void update(ProcessingResult processingResult)
        {
            synchronized (this)
            {
                totalQueries++;
                if (processingResult != null)
                {
                    goodQueries++;

                    final Map<String, Object> attributes = processingResult
                        .getAttributes();
                    addTimeToAverage(attributes, AttributeNames.PROCESSING_TIME_SOURCE,
                        sourceTimeAverage);
                    addTimeToAverage(attributes,
                        AttributeNames.PROCESSING_TIME_ALGORITHM, algorithmTimeAverage);
                    addTimeToAverage(attributes, AttributeNames.PROCESSING_TIME_TOTAL,
                        totalTimeAverage);
                }
            }
        }

        CachingControllerStatistics getStatistics()
        {
            return new CachingControllerStatistics(this, dataCache.getStatistics());
        }

        private void addTimeToAverage(Map<String, Object> attributes, String key,
            RollingWindowAverage average)
        {
            final Long time = (Long) attributes.get(key);
            if (time != null)
            {
                average.add(System.currentTimeMillis(), time);
            }
        }
    }
}
