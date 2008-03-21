package org.carrot2.core;

import java.io.IOException;
import java.util.*;

import net.sf.ehcache.*;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.pool.*;
import org.carrot2.util.resource.ClassResource;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A controller implementing the life cycle described in <{@link ProcessingComponent}
 * which pools processing component instances. Processing performed using this controller
 * is thread-safe.
 */
public class CachingController implements Controller
{
    /** Pool for component instances */
    private ObjectPool<ProcessingComponent> componentPool;

    /**
     * Original values of {@link Processing} attributes that will be restored in the
     * component after processing finishes.
     */
    private final Map<Class<?>, Map<String, Object>> resetAttributes = Maps.newHashMap();

    /**
     * Descriptors of {@link Input} and {@link Output} {@link Processing} attributes of
     * components whose output is to be cached.
     */
    private final Map<Class<?>, InputOutputAttributeDescriptors> cachedComponentAttributeDescriptors = Maps
        .newHashMap();

    /**
     * A set of {@link ProcessingComponent}s whose data should be cached internally.
     */
    private final Set<Class<? extends ProcessingComponent>> cachedComponentClasses;

    /**
     * Caches the data from components of classes provided in
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
     *            by the controller
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
        componentPool = new ObjectPool<ProcessingComponent>(
            new ComponentInstantiationListener(initAttributes), null,
            new ComponentPassivationListener(), ComponentDisposalListener.INSTANCE);

        try
        {
            cacheManager = CacheManager.create(new ClassResource(CachingController.class,
                "/controller-ehcache.xml").open());
        }
        catch (IOException e)
        {
            throw new ComponentInitializationException("Could not initalize cache", e);
        }

        if (!cacheManager.cacheExists("data"))
        {
            cacheManager.addCache("data");
        }
        dataCache = new SelfPopulatingCache(cacheManager.getCache("data"),
            new CachedDataFactory());
    }

    /*
     *
     */
    @SuppressWarnings("unchecked")
    public ProcessingResult process(Map<String, Object> attributes,
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        if (componentPool == null)
        {
            throw new IllegalStateException("Initialize the controller first.");
        }

        final ProcessingComponent [] processingComponents = new ProcessingComponent [processingComponentClasses.length];
        try
        {
            // Borrow instances of processing components.
            for (int i = 0; i < processingComponents.length; i++)
            {
                processingComponents[i] = getProcessingComponent(
                    (Class<? extends ProcessingComponent>) processingComponentClasses[i],
                    attributes);
            }

            ControllerUtils.performProcessingWithTimeMeasurement(attributes,
                processingComponents);

            return new ProcessingResult(attributes);
        }
        finally
        {
            for (ProcessingComponent processingComponent : processingComponents)
            {
                if (!(processingComponent instanceof CachedProcessingComponent))
                {
                    componentPool.returnObject(processingComponent);
                }
            }
        }
    }

    /**
     * Borrows a processing component from the pool or creates a
     * {@link CachedProcessingComponent} for caching.
     */
    @SuppressWarnings("unchecked")
    private ProcessingComponent getProcessingComponent(
        Class<? extends ProcessingComponent> componentClass,
        Map<String, Object> attributes)
    {
        for (Class<?> clazz : cachedComponentClasses)
        {
            if (clazz.isAssignableFrom(componentClass))
            {
                return new CachedProcessingComponent(componentClass, attributes);
            }
        }

        return borrowProcessingComponent(componentClass);
    }

    /**
     * Borrows a component from the pool and conerts exceptions to
     * {@link ComponentInitializationException}.
     */
    private ProcessingComponent borrowProcessingComponent(
        Class<? extends ProcessingComponent> componentClass)
    {
        try
        {
            return componentPool.borrowObject(componentClass);
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
     *
     */
    public void dispose()
    {
        componentPool.dispose();
        cacheManager.shutdown();
    }

    /**
     * Initializes newly created component instances, remembers attribute values so that
     * they can be reset after the component gets returned to the pool.
     */
    private final class ComponentInstantiationListener implements
        ObjectInstantiationListener<ProcessingComponent>
    {
        private final Map<String, Object> initAttributes;

        ComponentInstantiationListener(Map<String, Object> initAttributes)
        {
            this.initAttributes = initAttributes;
        }

        @SuppressWarnings("unchecked")
        public void objectInstantiated(ProcessingComponent component)
        {
            try
            {
                // Initialize the component first
                ControllerUtils.init(component, initAttributes);

                // If this is the first component we initialize, remember attribute
                // values so that they can be reset on returning to the pool.
                synchronized (CachingController.this)
                {
                    // Attribute values for resetting
                    final Class<? extends ProcessingComponent> componentClass = component
                        .getClass();
                    Map<String, Object> attributes = resetAttributes.get(componentClass);
                    if (attributes == null)
                    {
                        attributes = Maps.newHashMap();

                        // We only unbind @Processing attributes here, so components
                        // must not change @Init attributes during processing.
                        // We could unbind @Init attributes also, but this may be
                        // costly when Class -> Object coercion happens.
                        AttributeBinder.unbind(component, attributes, Input.class,
                            Processing.class);
                        resetAttributes.put(componentClass, attributes);
                    }
                }
            }
            catch (Exception e)
            {
                // If init() throws any exception, this exception will
                // be propagated to the borrowObject() call.
                component.dispose();
                if (e instanceof ComponentInitializationException)
                {
                    throw (ComponentInitializationException) e;
                }
                else
                {
                    throw new ComponentInitializationException(e);
                }
            }
        }
    }

    /**
     * Disposes of components on shut down.
     */
    private final static class ComponentDisposalListener implements
        ObjectDisposalListener<ProcessingComponent>
    {
        final static ComponentDisposalListener INSTANCE = new ComponentDisposalListener();

        public void dispose(ProcessingComponent component)
        {
            component.dispose();
        }
    }

    /**
     * Resets {@link Processing} attribute values before the component is returned to the
     * pool.
     */
    private final class ComponentPassivationListener implements
        ObjectPassivationListener<ProcessingComponent>
    {
        @SuppressWarnings("unchecked")
        public void passivate(ProcessingComponent processingComponent)
        {
            // Reset attribute values
            try
            {
                // Here's a little hack: we need to disable checking
                // for required attributes, otherwise, we won't be able
                // to reset @Required input attributes to null
                AttributeBinder.bind(processingComponent,
                    new AttributeBinder.AttributeBinderAction []
                    {
                        new AttributeBinder.AttributeBinderActionBind(Input.class,
                            resetAttributes.get(processingComponent.getClass()), false)
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

    /**
     * A stub component that fetches the data from the cache and adds the results to the
     * attribute map.
     */
    @Bindable
    private final class CachedProcessingComponent extends ProcessingComponentBase
    {
        private final Class<? extends ProcessingComponent> componentClass;
        private final Map<String, Object> attributes;

        CachedProcessingComponent(Class<? extends ProcessingComponent> componentClass,
            Map<String, Object> attributes)
        {
            this.componentClass = componentClass;
            this.attributes = attributes;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void process() throws ProcessingException
        {
            final InputOutputAttributeDescriptors descriptors = prepareDescriptors(componentClass);

            final Map<String, Object> inputAttributes = getAttributesForDescriptors(
                descriptors.inputDescriptors, attributes);
            inputAttributes.put(COMPONENT_CLASS_KEY, componentClass);

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

            synchronized (CachingController.this)
            {
                descriptors = cachedComponentAttributeDescriptors.get(componentClass);
                if (descriptors == null)
                {
                    // Need to borrow a component for a while to build descriptors
                    ProcessingComponent component = null;
                    try
                    {
                        component = borrowProcessingComponent(componentClass);

                        // Build and store descriptors
                        descriptors = new InputOutputAttributeDescriptors(
                            BindableDescriptorBuilder.buildDescriptor(component, false)
                                .only(Input.class, Processing.class).flatten().attributeDescriptors,
                            BindableDescriptorBuilder.buildDescriptor(component, false)
                                .only(Output.class, Processing.class).flatten().attributeDescriptors);

                        cachedComponentAttributeDescriptors.put(componentClass,
                            descriptors);
                    }
                    finally
                    {
                        componentPool.returnObject(component);
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

            Class<? extends ProcessingComponent> componentClass = (Class<? extends ProcessingComponent>) inputAttributes
                .get(COMPONENT_CLASS_KEY);

            ProcessingComponent component = null;
            try
            {
                component = componentPool.borrowObject(componentClass);
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
                componentPool.returnObject(component);
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
}
