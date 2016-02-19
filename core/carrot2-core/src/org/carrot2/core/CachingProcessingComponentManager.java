
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

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.carrot2.core.Controller.IControllerStatisticsProvider;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.Pair;
import org.carrot2.util.attribute.*;

import org.carrot2.shaded.guava.common.cache.*;
import org.carrot2.shaded.guava.common.collect.*;
import org.carrot2.shaded.guava.common.util.concurrent.UncheckedExecutionException;

/**
 * An {@link IProcessingComponentManager} that implements processing results caching
 * functionality.
 * <p>
 * This manager wraps some delegate manager (e.g. a
 * {@link SimpleProcessingComponentManager} or a {@link PoolingProcessingComponentManager}
 * ) and wraps the components the delegate with a functionality that either returns the
 * results from cache or performs the processing if the result are not yet cached.
 * </p>
 */
public class CachingProcessingComponentManager implements IProcessingComponentManager,
    Controller.IControllerStatisticsProvider
{
    /** The delegate manager that prepares the actual processing components */
    final IProcessingComponentManager delegate;

    /**
     * Descriptors of {@link Input} and {@link Output} {@link Processing} attributes of
     * components whose output is to be cached.
     */
    private final Map<Pair<Class<? extends IProcessingComponent>, String>, InputOutputAttributeDescriptors> cachedComponentAttributeDescriptors = Maps
        .newHashMap();

    /**
     * A set of {@link IProcessingComponent}s whose data should be cached internally.
     */
    final Set<Class<? extends IProcessingComponent>> cachedComponentClasses;

    /**
     * Populates on-demand and caches the data from components of classes provided in
     * {@link #cachedComponentClasses}. The key of the cache is a map of all {@link Input}
     * {@link Processing} attributes of the component for which caching is performed. The
     * value of the cache is a map of all {@link Output} {@link Processing} attributes
     * produced by the component.
     */
    private Cache<AttributeMapCacheKey, Map<String,Object>> cache;

    /** Cache statistics keys. */
    static final String CACHE_MISSES = "cache.misses";
    static final String CACHE_HITS_TOTAL = "cache.hits.total";

    /**
     * Creates a {@link CachingProcessingComponentManager}.
     * 
     * @param delegate the manager to handle the preparation of the actual processing
     *            component instances
     * @param cachedComponentClasses classes of components whose output should be cached
     *            by the controller. If a superclass is provided here, e.g.
     *            {@link IDocumentSource}, all its subclasses will be subject to caching.
     *            If {@link IProcessingComponent} is provided here, output of all
     *            components will be cached.
     */
    @SafeVarargs
    public CachingProcessingComponentManager(IProcessingComponentManager delegate,
        Class<? extends IProcessingComponent>... cachedComponentClasses)
    {
        this.delegate = delegate;
        this.cachedComponentClasses = ImmutableSet.copyOf(cachedComponentClasses);

        // Initialize cache.
        cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .recordStats()
            .build();
    }

    public void init(IControllerContext context, Map<String, Object> attributes,
        ProcessingComponentConfiguration... configurations)
    {
        delegate.init(context, attributes, configurations);
    }

    public IProcessingComponent prepare(Class<? extends IProcessingComponent> clazz,
        String id, Map<String, Object> inputAttributes,
        Map<String, Object> outputAttributes)
    {
        // If the processing component is to be cached, wrap with our internal
        // processing component implementation that will do the caching.

        // One very important implementation detail is that the only moment we can pass
        // all input attributes (including the processing-time ones) to the component
        // wrapper is here when we create it. For this reason, the controller needs to
        // pass all attributes at this state, even though the other manager will likely
        // use only the init-time attributes. The same goes for output attributes,
        // these will be collected to the map we provide during the creation of the
        // wrapper.
        for (Class<?> cachedClass : cachedComponentClasses)
        {
            if (cachedClass.isAssignableFrom(clazz))
            {
                return new CachedProcessingComponent(clazz, id, inputAttributes,
                    outputAttributes);
            }
        }

        // Otherwise, return the original component
        return delegate.prepare(clazz, id, inputAttributes, outputAttributes);
    }

    @Override
    public void recycle(IProcessingComponent component, String id)
    {
        // If not our wrapper, recycle.
        if (!(component instanceof CachedProcessingComponent))
        {
            delegate.recycle(component, id);
        }

        // The wrapped actual components are recycled in ValueProducer when
        // they're asked to perform processing.
    }

    public void dispose()
    {
        try
        {
            delegate.dispose();
            if (cache != null)
            {
                cache.invalidateAll();
            }
        }
        finally
        {
            cache = null;
        }
    }

    public Map<String, Object> getStatistics()
    {
        // Return some custom statistics
        final CacheStats cacheStats = cache.stats();
        final Map<String, Object> stats = Maps.newHashMap();
        if (delegate instanceof IControllerStatisticsProvider) 
        {
            stats.putAll(((IControllerStatisticsProvider) delegate).getStatistics());
        }
        stats.put(CACHE_MISSES, cacheStats.missCount());
        stats.put(CACHE_HITS_TOTAL, cacheStats.hitCount());
        
        return stats;
    }

    // Two extra attributes to add to the input map. This way, they will also become
    // part of the cache key, which is what we need.
    private static final String COMPONENT_CLASS_KEY = 
        CachingProcessingComponentManager.class.getName() + ".componentClass";
    private static final String COMPONENT_ID_KEY = 
        CachingProcessingComponentManager.class.getName() + ".componentId";
    
    /** Any values put under this attribute will cause a cache bypass (dropping of the stale value). */
    public static final String CACHE_BYPASS_ATTR = 
        CachingProcessingComponentManager.class.getName() + ".cacheBypass";

    /**
     * A stub component that fetches the data from the cache and adds the results to the
     * attribute map.
     */
    private final class CachedProcessingComponent extends ProcessingComponentBase
    {
        private final Class<? extends IProcessingComponent> componentClass;
        private final String componentId;

        /** All input attributes, including processing-time ones. */
        private final Map<String, Object> inputAttributes;

        /** A map to store the output attributes in. */
        private final Map<String, Object> outputAttributes;

        CachedProcessingComponent(Class<? extends IProcessingComponent> componentClass,
            String componentId, Map<String, Object> inputAttributes,
            Map<String, Object> outputAttributes)
        {
            this.componentClass = componentClass;
            this.inputAttributes = inputAttributes;
            this.outputAttributes = outputAttributes;
            this.componentId = componentId;
        }

        @Override
        public void process() throws ProcessingException
        {
            final InputOutputAttributeDescriptors descriptors = prepareAttributeDescriptors();

            // Copy the output attributes produced by the preceding components. Normally,
            // this could be done by ControllerUtils, but the wrapper was created before
            // any processing took place anyway, so the inputAttributes did not have any
            // results yet.
            inputAttributes.putAll(outputAttributes);

            // We'll need @Input @Processing attributes for the cache key
            final Map<String, Object> inputProcessingAttributes = getAttributesForDescriptors(
                descriptors.inputProcessingDescriptors, inputAttributes);

            // Plus component class and id
            inputProcessingAttributes.put(COMPONENT_CLASS_KEY, componentClass);
            inputProcessingAttributes.put(COMPONENT_ID_KEY, componentId);

            // Get data from cache. If the result is not in the cache yet, it will
            // be created by the ValueProducer.
            final AttributeMapCacheKey key = new AttributeMapCacheKey(
                inputProcessingAttributes, inputAttributes);

            // Cache bypass.
            if (inputAttributes.containsKey(CACHE_BYPASS_ATTR) &&
                Boolean.valueOf(inputAttributes.get(CACHE_BYPASS_ATTR).toString())) {
                cache.invalidate(key);
            }

            try
            {
                final Map<String, Object> processingResult = cache.get(key, new ValueProducer(key));

                // Copy the results @Output @Processing attributes back to the result
                outputAttributes.putAll(getAttributesForDescriptors(
                    descriptors.outputDescriptors, processingResult));
            }
            catch (UncheckedExecutionException e)
            {
                throw ExceptionUtils.wrapAs(ProcessingException.class, e.getCause());
            }
            catch (ExecutionException e)
            {
                throw ExceptionUtils.wrapAs(ProcessingException.class, e.getCause());
            }
        }

        /**
         * Returns attribute descriptors for {@link Input} {@link Processing} and
         * {@link Output} {@link Processing} attributes of the component whose results
         * will be cached.
         */
        private InputOutputAttributeDescriptors prepareAttributeDescriptors()
        {
            InputOutputAttributeDescriptors descriptors = null;

            synchronized (cachedComponentAttributeDescriptors)
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
                        component = delegate.prepare(componentClass, componentId,
                            inputAttributes, Maps.<String, Object> newHashMap());

                        // Build and store descriptors
                        descriptors = new InputOutputAttributeDescriptors(
                            BindableDescriptorBuilder.buildDescriptor(component)
                                .only(Input.class, Processing.class).flatten().attributeDescriptors,
                            BindableDescriptorBuilder.buildDescriptor(component)
                                .only(Output.class).flatten().attributeDescriptors);

                        cachedComponentAttributeDescriptors.put(
                            new Pair<Class<? extends IProcessingComponent>, String>(
                                componentClass, componentId), descriptors);
                    }
                    finally
                    {
                        if (component != null)
                        {
                            delegate.recycle(component, componentId);
                        }
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
     * A compound cache key based on the input attributes map that ensures that possible
     * modifications to the attributes map or its values do not change the hashCode and
     * equality behavior of the key.
     */
    private static final class AttributeMapCacheKey
    {
        /** Input processing attributes, the key for the cache */
        private Map<String, Object> inputProcessingAttributes;

        /** Hash code for input processing attributes */
        private int hashCode;

        /**
         * All input attributes. This map is not part of the cache key, but we will need
         * it to properly retrieve entries from the cache.
         */
        private Map<String, Object> inputAttributes;

        private AttributeMapCacheKey(
            Map<String, Object> inputProcessingAttributes,
            Map<String, Object> inputAttributes)
        {
            /*
             * Empty attributes should never happen because the attributes object must
             * hold component identifiers, etc.
             */
            assert inputProcessingAttributes != null
                && inputProcessingAttributes.size() > 0;

            /*
             * In theory, we could make a shallow copy of the provided map, but if someone
             * wants to make modifications they'll make them anyway on the objects
             * contained in the map. To be completely safe, we'd have to make a deep copy.
             * To prevent simple errors, we make the map unmodifiable.
             */
            this.inputProcessingAttributes = Collections.unmodifiableMap(inputProcessingAttributes);
            this.hashCode = inputProcessingAttributes.hashCode();

            this.inputAttributes = inputAttributes;
        }

        /*
         * We assume that equal hash codes means equal objects, which is not true in case
         * of conflicts, but there is no other way really if we don't want to make deep
         * copies of the attribute map. If a conflict occurs, we would retrieve a stale
         * result from the cache (a result associated with a different query, possibly a
         * different component even). The cache is in-memory only and is rather small (so
         * that re-querying for documents and clusters does not cause duplicated
         * processing), conflicts do not seem like a big problem.
         */
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof AttributeMapCacheKey))
            {
                return false;
            }

            final boolean result = (obj.hashCode() == this.hashCode);
            if (result)
            {
                assert ((AttributeMapCacheKey) obj).inputProcessingAttributes
                    .equals(this.inputProcessingAttributes);
            }
            return result;
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }
    }

    /**
     * A cached data factory that actually performs the processing. This factory is called
     * only if the cache does not contain the requested value.
     */
    private final class ValueProducer 
        implements Callable<Map<String,Object>>
    {
        private final AttributeMapCacheKey key;

        public ValueProducer(AttributeMapCacheKey key)
        {
            this.key = key;
        }

        @Override
        public Map<String, Object> call() throws Exception
        {
            final Map<String, Object> inputProcessingAttributes = key.inputProcessingAttributes;

            @SuppressWarnings("unchecked")
            final Class<? extends IProcessingComponent> componentClass = 
                (Class<? extends IProcessingComponent>) 
                    inputProcessingAttributes.get(COMPONENT_CLASS_KEY);

            final String componentId = (String) inputProcessingAttributes.get(COMPONENT_ID_KEY);

            IProcessingComponent component = null;
            try
            {
                final Map<String, Object> attributes = Maps.newHashMap();
                component = delegate.prepare(componentClass, componentId, 
                    key.inputAttributes, attributes);

                ControllerUtils.performProcessing(component, inputProcessingAttributes, attributes);

                return attributes;
            }
            finally
            {
                if (component != null)
                {
                    delegate.recycle(component, componentId);
                }
            }
        }
    }

    /**
     * Stores a pair of maps of {@link Input} and {@link Output} descriptors.
     */
    private final static class InputOutputAttributeDescriptors
    {
        final Map<String, AttributeDescriptor> inputProcessingDescriptors;
        final Map<String, AttributeDescriptor> outputDescriptors;

        InputOutputAttributeDescriptors(
            Map<String, AttributeDescriptor> inputDescriptors,
            Map<String, AttributeDescriptor> outputDescriptors)
        {
            this.inputProcessingDescriptors = inputDescriptors;
            this.outputDescriptors = outputDescriptors;
        }
    }
}
