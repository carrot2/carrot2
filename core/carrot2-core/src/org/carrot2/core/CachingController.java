package org.carrot2.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.Pair;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.AttributeBinder.AttributeBinderAction;
import org.carrot2.util.attribute.AttributeBinder.AttributeBinderActionCollect;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.pool.*;
import org.carrot2.util.resource.ClassResource;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A controller implementing the life cycle described in {@link ProcessingComponent} with
 * support for component pooling and, optionally, data caching.
 * <p>
 * Calls to {@link #process(Map, Class...)} are thread-safe, although some care should be
 * given to initialization: {@link #init(Map)} should be called before other threads are
 * allowed to see this object and {@link #dispose()} should be called after all threads
 * leave {@link #process(Map, Class...)}.
 */
public final class CachingController implements Controller
{
    /** Private monitor for multi-threaded critical sections. */
    final Object reentrantLock = new Object();

    /** Pool for component instances. */
    private volatile SoftUnboundedPool<ProcessingComponent, String> componentPool;

    /**
     * Original values of {@link Processing} attributes that will be restored in the
     * component after processing finishes.
     * <p>
     * Access monitor: {#link #reentrantLock}.
     */
    private final Map<Pair<Class<? extends ProcessingComponent>, String>, Map<String, Object>> resetAttributes = Maps
        .newHashMap();

    /**
     * Descriptors of {@link Input} and {@link Output} {@link Processing} attributes of
     * components whose output is to be cached.
     */
    private final Map<Pair<Class<? extends ProcessingComponent>, String>, InputOutputAttributeDescriptors> cachedComponentAttributeDescriptors = Maps
        .newHashMap();

    /**
     * Maintains a mapping between component ids and their classes. Initialized in
     * {@link #init(Map, ComponentConfiguration...)}, used in
     * {@link #process(Map, String...)}.
     */
    private FromIdProcessingComponentClassResolver processingComponentClassResolver;

    /**
     * A set of {@link ProcessingComponent}s whose data should be cached internally.
     */
    private final Set<Class<? extends ProcessingComponent>> cachedComponentClasses;

    /**
     * Populates on-demand and caches the data from components of classes provided in
     * {@link #cachedComponentClasses}. The key of the cache is a map of all
     * {@link Input} {@link Processing} attributes of the component for which caching is
     * performed. The value of the cache is a map of all {@link Output} {@link Processing}
     * attributes produced by the component.
     */
    private SelfPopulatingCache dataCache;

    /** Ehcache manager */
    private CacheManager cacheManager;

    /**
     * Creates a new caching controller.
     * 
     * @param cachedComponentClasses classes of components whose output should be cached
     *            by the controller. If a superclass is provided here, e.g.
     *            {@link DataSource}, all its subclasses will be subject to caching. If
     *            {@link ProcessingComponent} is provided here, output of all components
     *            will be cached.
     */
    public CachingController(
        Class<? extends ProcessingComponent>... cachedComponentClasses)
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
     * An additional method to initialize this component, which enables processing with
     * differently configured instances of the same {@link ProcessingComponent} class.
     * Processing with components initialized in this method can be peformed using
     * {@link #process(Map, String...)}.
     * 
     * @param globalInitAttributes see {@link Controller#init(Map)}
     * @param componentConfigurations component configurations to be used. Identifiers of
     *            the provided components must be unique.
     */
    public void init(Map<String, Object> globalInitAttributes,
        ComponentConfiguration... componentConfigurations)
        throws ComponentInitializationException
    {
        // Prepare component-specific init attributes
        final Map<Pair<Class<? extends ProcessingComponent>, String>, Map<String, Object>> componentSpecificInitAttributes = Maps
            .newHashMap();
        final Map<String, Class<? extends ProcessingComponent>> idToComponentClass = Maps
            .newHashMap();
        for (ComponentConfiguration componentConfiguration : componentConfigurations)
        {
            final Map<String, Object> mergedAttributes = Maps
                .newHashMap(globalInitAttributes);
            mergedAttributes.putAll(componentConfiguration.initAttributes);

            componentSpecificInitAttributes.put(
                new Pair<Class<? extends ProcessingComponent>, String>(
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
        componentPool = new SoftUnboundedPool<ProcessingComponent, String>(
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

    /*
     *
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
     * @param attributes see {@link Controller#process(Map, Class...)}
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
        ProcessingComponentClassResolver<T> resolver, T... componentIds)
    {
        final SoftUnboundedPool<ProcessingComponent, String> componentPool = this.componentPool;
        if (componentPool == null)
        {
            throw new IllegalStateException("Initialize the controller first.");
        }

        final String actualComponentIds[] = new String [componentIds.length];
        final ProcessingComponent [] processingComponents = new ProcessingComponent [componentIds.length];
        try
        {
            // Borrow instances of processing components.
            for (int i = 0; i < processingComponents.length; i++)
            {
                final Pair<Class<? extends ProcessingComponent>, String> resolved = resolver
                    .resolve(componentIds[i]);

                actualComponentIds[i] = resolved.objectB;
                processingComponents[i] = getProcessingComponent(resolved.objectA,
                    resolved.objectB, attributes);
            }

            ControllerUtils.performProcessingWithTimeMeasurement(attributes,
                processingComponents);

            return new ProcessingResult(attributes);
        }
        finally
        {
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
    private ProcessingComponent getProcessingComponent(
        Class<? extends ProcessingComponent> componentClass, String id,
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
    private ProcessingComponent borrowProcessingComponent(
        Class<? extends ProcessingComponent> componentClass, String componentId)
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
        componentPool.dispose();
        cacheManager.shutdown();
    }

    /**
     * Resolves {@link ProcessingComponent} classes based on the provided componentId.
     */
    private static interface ProcessingComponentClassResolver<T>
    {
        Pair<Class<? extends ProcessingComponent>, String> resolve(T componentId);
    }

    /**
     * Resolves {@link ProcessingComponent} classes from component ids being the classes
     * themselves.
     */
    private static class IdentityProcessingComponentClassResolver implements
        ProcessingComponentClassResolver<Class<?>>
    {
        final static IdentityProcessingComponentClassResolver INSTANCE = new IdentityProcessingComponentClassResolver();

        @SuppressWarnings("unchecked")
        public Pair<Class<? extends ProcessingComponent>, String> resolve(
            Class<?> componentId)
        {
            return new Pair<Class<? extends ProcessingComponent>, String>(
                (Class<? extends ProcessingComponent>) componentId, null);
        }
    }

    /**
     * Resolves {@link ProcessingComponent} classes from the component ids, based on the
     * provided id-class mapping.
     */
    private static class FromIdProcessingComponentClassResolver implements
        ProcessingComponentClassResolver<String>
    {
        private final Map<String, Class<? extends ProcessingComponent>> idToComponentClass;

        public FromIdProcessingComponentClassResolver(
            Map<String, Class<? extends ProcessingComponent>> idToComponentClass)
        {
            this.idToComponentClass = idToComponentClass;
        }

        @SuppressWarnings("unchecked")
        public Pair<Class<? extends ProcessingComponent>, String> resolve(
            String componentId)
        {
            Class<? extends ProcessingComponent> resultClass;
            String resultComponentId = componentId;

            resultClass = idToComponentClass.get(componentId);
            if (resultClass == null)
            {
                try
                {
                    resultClass = (Class<? extends ProcessingComponent>) Class
                        .forName(componentId);

                    // The component id was coerced to a generic class,
                    // so we're not using a specific version of a component.
                    resultComponentId = null;
                }
                catch (ClassNotFoundException e)
                {
                    throw new ProcessingException("Unknown component id: " + componentId);
                }
            }

            return new Pair<Class<? extends ProcessingComponent>, String>(resultClass,
                resultComponentId);
        }
    }

    /**
     * Transforms values of attributes that are bound only at {@link Processing} time and
     * have {@link ImplementingClasses} constraints into the classes of the values.
     */
    private final static class ToClassAttributeTransformer implements
        AttributeBinder.AttributeTransformer
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
        InstantiationListener<ProcessingComponent, String>
    {
        private final Map<String, Object> initAttributes;
        private final Map<Pair<Class<? extends ProcessingComponent>, String>, Map<String, Object>> componentSpecificInitAttributes;

        ComponentInstantiationListener(
            Map<String, Object> initAttributes,
            Map<Pair<Class<? extends ProcessingComponent>, String>, Map<String, Object>> componentSpecificInitAttributes)
        {
            this.initAttributes = initAttributes;
            this.componentSpecificInitAttributes = componentSpecificInitAttributes;
        }

        @SuppressWarnings("unchecked")
        public void objectInstantiated(ProcessingComponent component, String parameter)
        {
            try
            {
                final Map<String, Object> specificInitAttributes = componentSpecificInitAttributes
                    .get(new Pair<Class<? extends ProcessingComponent>, String>(component
                        .getClass(), parameter));

                // Initialize the component first
                if (specificInitAttributes != null)
                {
                    ControllerUtils.init(component, specificInitAttributes);
                }
                else
                {
                    ControllerUtils.init(component, initAttributes);
                }

                // If this is the first component we initialize, remember attribute
                // values so that they can be reset on returning to the pool.
                synchronized (reentrantLock)
                {
                    // Attribute values for resetting
                    final Class<? extends ProcessingComponent> componentClass = component
                        .getClass();
                    Map<String, Object> attributes = resetAttributes
                        .get(new Pair<Class<? extends ProcessingComponent>, String>(
                            componentClass, parameter));
                    if (attributes == null)
                    {
                        attributes = Maps.newHashMap();

                        // We only unbind @Processing attributes here, so components
                        // must not change @Init attributes during processing.
                        // We could unbind @Init attributes also, but this may be
                        // costly when Class -> Object coercion happens.
                        AttributeBinder.bind(component, new AttributeBinderAction []
                        {
                            new AttributeBinderActionCollect(Input.class, attributes,
                                ToClassAttributeTransformer.INSTANCE),
                        }, Input.class, Processing.class);

                        resetAttributes.put(
                            new Pair<Class<? extends ProcessingComponent>, String>(
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
        DisposalListener<ProcessingComponent, String>
    {
        final static ComponentDisposalListener INSTANCE = new ComponentDisposalListener();

        public void dispose(ProcessingComponent component, String parameter)
        {
            component.dispose();
        }
    }

    /**
     * Resets {@link Processing} attribute values before the component is returned to the
     * pool.
     */
    private final class ComponentPassivationListener implements
        PassivationListener<ProcessingComponent, String>
    {
        @SuppressWarnings("unchecked")
        public void passivate(ProcessingComponent processingComponent, String parameter)
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
                        .get(new Pair<Class<? extends ProcessingComponent>, String>(
                            processingComponent.getClass(), parameter));
                }

                AttributeBinder.bind(processingComponent,
                    new AttributeBinder.AttributeBinderAction []
                    {
                        new AttributeBinder.AttributeBinderActionBind(Input.class, map,
                            false)
                    }, Input.class, Processing.class);
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
        private final Class<? extends ProcessingComponent> componentClass;
        private final String componentId;
        private final Map<String, Object> attributes;

        CachedProcessingComponent(Class<? extends ProcessingComponent> componentClass,
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

            attributes.putAll(getAttributesForDescriptors(descriptors.outputDescriptors,
                (Map<String, Object>) dataCache.get(inputAttributes).getObjectValue()));
        }

        /**
         * Returns attribute descriptors for {@link Input} {@link Processing} and
         * {@link Output} {@link Processing} attributes of the component whose results
         * will be cached.
         */
        @SuppressWarnings("unchecked")
        private InputOutputAttributeDescriptors prepareDescriptors(
            Class<? extends ProcessingComponent> componentClass)
        {
            InputOutputAttributeDescriptors descriptors = null;

            synchronized (reentrantLock)
            {
                descriptors = cachedComponentAttributeDescriptors
                    .get(new Pair<Class<? extends ProcessingComponent>, String>(
                        componentClass, componentId));
                if (descriptors == null)
                {
                    // Need to borrow a component for a while to build descriptors
                    ProcessingComponent component = null;
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
                            new Pair<Class<? extends ProcessingComponent>, String>(
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

            final Class<? extends ProcessingComponent> componentClass = (Class<? extends ProcessingComponent>) inputAttributes
                .get(COMPONENT_CLASS_KEY);
            final String componentId = (String) inputAttributes.get(COMPONENT_ID_KEY);

            ProcessingComponent component = null;
            try
            {
                component = componentPool.borrowObject(componentClass, componentId);
                Map<String, Object> attributes = Maps.newHashMap(inputAttributes);
                try
                {
                    ControllerUtils.beforeProcessing(component, attributes);
                    ControllerUtils.performProcessing(component, attributes);
                }
                finally
                {
                    ControllerUtils.afterProcessing(component, attributes);
                }

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

    private final static ComponentConfiguration [] EMPTY_COMPONENT_CONFIGURATION_ARRAY = new ComponentConfiguration [0];

    /**
     * Represents a specific configuration of a {@link ProcessingComponent}.
     */
    public static class ComponentConfiguration
    {
        /**
         * The specific {@link ProcessingComponent} class.
         */
        public final Class<? extends ProcessingComponent> componentClass;

        /**
         * Identifier of the component.
         */
        public final String componentId;

        /**
         * Initialization attributes for this component configuration.
         */
        public final Map<String, Object> initAttributes;

        /**
         * Creates a new component configuration.
         * 
         * @param componentClass the specific {@link ProcessingComponent} class.
         * @param componentId identifier of the component.
         * @param initAttributes initialization attributes for this component
         *            configuration.
         */
        public ComponentConfiguration(
            Class<? extends ProcessingComponent> componentClass, String componentId,
            Map<String, Object> initAttributes)
        {
            this.componentClass = componentClass;
            this.componentId = componentId;
            this.initAttributes = initAttributes;
        }
    }
}
