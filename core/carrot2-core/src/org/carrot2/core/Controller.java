
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

import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Init;
import org.carrot2.util.ReflectionUtils;
import org.carrot2.util.RollingWindowAverage;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.simpleframework.xml.Attribute;

import org.carrot2.shaded.guava.common.collect.ImmutableMap;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * A controller implementing the life cycle described in {@link IProcessingComponent}. Use
 * {@link ControllerFactory} to obtain controllers with different characteristics, e.g.
 * with or without pooling of {@link IProcessingComponent}, with or without caching of the
 * processing results. If further customizations are needed, you can provide your own
 * {@link IProcessingComponentManager} implementation.
 * <p>
 * Calls to {@link #process(Map, Class...)} are thread-safe, although some care should be
 * given to initialization. Controller instance should be initialized (using any of the
 * {@link #init()} methods) before other threads are allowed to see its instance.
 * {@link #dispose()} should be called after all threads leave
 * {@link #process(Map, Class...)} and {@link #process(Map, Object...)}.
 * </p>
 * <p>
 * Notice for {@link IProcessingComponent} developers: if data caching is used, values of
 * {@link Output} attributes produced by the components whose output is to be cached
 * (e.g., the {@link Document} instances in case {@link IDocumentSource} output is cached)
 * may be accessed concurrently and therefore must be thread-safe.
 * </p>
 * 
 * @see ControllerFactory
 */
public final class Controller implements Closeable
{
    /** If <code>true</code>, the controller has been closed and is no longer usable. */
    private volatile boolean closed = false;
    
    /** Place holder for controller-level shared data */
    private ControllerContextImpl context = new ControllerContextImpl();

    /**
     * Encapsulates different strategies of processing component management, e.g. pooling
     * of component instances or processing results caching.
     */
    IProcessingComponentManager componentManager;

    /**
     * Attributes provided for this controller at initialization time.
     */
    private Map<String, Object> initAttributes;

    /**
     * {@link ProcessingComponentConfiguration}s provided for this controller at
     * initialization time. If this map is empty, processing can be performed only with
     * components specified by class name.
     */
    private Map<String, ProcessingComponentConfiguration> componentIdToConfiguration;

    /**
     * Some statistics about processing performed in this controller, including: number of
     * queries, number of successful queries, processing times, cache utilization if
     * applicable.
     */
    private ProcessingStatistics statistics = new ProcessingStatistics();

    /**
     * Creates a simple controller with no processing component pooling and no results
     * caching. A controller with equivalent configuration can be obtained from
     * {@link ControllerFactory#createSimple()}, see that method for more information. For
     * more controller configurations, also see {@link ControllerFactory}.
     * 
     * @see ControllerFactory
     */
    Controller()
    {
        this(new SimpleProcessingComponentManager());
    }

    /**
     * Creates a controller with a custom {@link IProcessingComponentManager}, for experts
     * only. Use {@link ControllerFactory} to obtain controllers in typical
     * configurations.
     */
    public Controller(IProcessingComponentManager componentManager)
    {
        HttpAuthHub.setupAuthenticator();
        this.componentManager = componentManager;
    }

    /**
     * Initializes this controller with an empty {@link Init}-time attributes map. Calling
     * this method is optional, if {@link #process(Map, Object...)} is called on an
     * uninitialized controller, {@link #init()} will be called automatically.
     * 
     * @return this controller for convenience
     */
    public synchronized Controller init() throws ComponentInitializationException
    {
        return init(ImmutableMap.<String, Object> of());
    }

    /**
     * Initializes this controller with the provided @{@link Init}-time attributes. The
     * provided attributes will be applied to all processing components managed by this
     * controller. {@link Init}-time attributes can be overridden at processing time, see
     * {@link #process(Map, Class...)} or {@link #process(Map, Object...)}.
     * 
     * @param attributes initialization-time attributes to be applied to all processing
     *            components in this controller
     * @return this controller for convenience
     */
    public synchronized Controller init(Map<String, Object> attributes)
        throws ComponentInitializationException
    {
        return init(attributes, new ProcessingComponentConfiguration [0]);
    }

    /**
     * Initializes this controller with the provided {@link Init}-time attributes and
     * additional component-specific {@link Init}-time attributes. {@link Init}-time
     * attributes can be overridden at processing time, see
     * {@link #process(Map, Class...)} or {@link #process(Map, Object...)}.
     * 
     * @param attributes initialization-time attributes to be applied to all processing
     *            components in this controller
     * @param configurations additional component-specific {@link Init}-time attributes
     * @return this controller for convenience
     */
    public synchronized Controller init(Map<String, Object> attributes,
        ProcessingComponentConfiguration... configurations)
        throws ComponentInitializationException
    {
        checkClosed();

        if (componentIdToConfiguration != null)
        {
            throw new IllegalStateException("This controller is already initialized.");
        }

        initAttributes = Collections.unmodifiableMap(Maps.newHashMap(attributes));
        componentIdToConfiguration = ProcessingComponentConfiguration
            .indexByComponentId(configurations);

        componentManager.init(context, attributes, configurations);
        return this;
    }

    /**
     * Convenience method for performing processing with the provided query and number of
     * results. The typical use cases for this method is fetching the specified number of
     * results from an {@link IDocumentSource} and, optionally, clustering them with an
     * {@link IClusteringAlgorithm}.
     * <p>
     * For a method allowing to pass more attributes, see: {@link #process(Map, Class...)}.
     * </p>
     * 
     * @param query the query to use during processing
     * @param results the number of results to fetch. If <code>null</code> is provided,
     *            the default number of results will be requested.
     * @param processingComponentClasses classes of components to perform processing in
     *            the order they should be arranged in the pipeline. Each provided class
     *            must implement {@link IProcessingComponent}.
     * @return results of the processing
     */
    public ProcessingResult process(String query, Integer results,
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(AttributeNames.QUERY, query);
        if (results != null)
        {
            attributes.put(AttributeNames.RESULTS, results);
        }
        return process(attributes, processingComponentClasses);
    }

    /**
     * Convenience method for clustering the provided list of {@link Document}s. If the
     * query that generated the <code>documents</code> is available, it can be provided in
     * the <code>queryHint</code> parameter to increase the quality of clusters.
     * <p>
     * For a method allowing to pass more attributes, see: {@link #process(Map, Class...)}.
     * </p>
     * 
     * @param documents the documents to cluster
     * @param queryHint the query that generated the documents, optional, can be
     *            <code>null</code> if not available.
     * @param processingComponentClasses classes of components to perform processing in
     *            the order they should be arranged in the pipeline. Each provided class
     *            must implement {@link IProcessingComponent}.
     * @return results of the processing
     * @throws ProcessingException
     */
    public ProcessingResult process(List<Document> documents, String queryHint,
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(AttributeNames.DOCUMENTS, documents);
        if (StringUtils.isNotBlank(queryHint))
        {
            attributes.put(AttributeNames.QUERY, queryHint);
        }
        return process(attributes, processingComponentClasses);
    }

    /**
     * Performs processing using components designated by their class. If you initialized
     * this controller using {@link #init(Map, ProcessingComponentConfiguration...)} and
     * would like to designate components by their identifiers, call
     * {@link #process(Map, String...)} or {@link #process(Map, Object...)}.
     * 
     * @param attributes attributes to be used during processing. {@link Input} attributes
     *            will be transferred from this map to the corresponding fields. Keys of
     *            the map are computed based on the <code>key</code> parameter of the
     *            {@link Attribute} annotation. Controller will not modify the provided
     *            map, processing results will be available in the returned
     *            {@link ProcessingResult}.
     * @param processingComponentClasses classes of components to perform processing in
     *            the order they should be arranged in the pipeline. Each provided class
     *            must implement {@link IProcessingComponent}.
     * @return results of the processing
     */
    public ProcessingResult process(Map<String, Object> attributes,
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        return process(attributes, (Object []) processingComponentClasses);
    }

    /**
     * Performs processing using components designated by their identifiers. Identifiers
     * can be assigned to component configurations using the {@link #init()} method.
     * 
     * @param attributes attributes to be used during processing. {@link Input} attributes
     *            will be transferred from this map to the corresponding fields. Keys of
     *            the map are computed based on the <code>key</code> parameter of the
     *            {@link Attribute} annotation. Controller will not modify the provided
     *            map, processing results will be available in the returned
     *            {@link ProcessingResult}.
     * @param processingComponentIdsOrClassNames identifiers of components to perform
     *            processing in the order they should be arranged in the pipeline.
     *            Fully-qualified class names are also accepted. Each provided class must
     *            implement {@link IProcessingComponent}.
     * @return results of the processing
     * @see #init(Map, ProcessingComponentConfiguration...)
     */
    public ProcessingResult process(Map<String, Object> attributes,
        String... processingComponentIdsOrClassNames) throws ProcessingException
    {
        return process(attributes, (Object []) processingComponentIdsOrClassNames);
    }

    /**
     * Performs processing using components designated by their identifiers or classes.
     * 
     * @param attributes attributes to be used during processing. {@link Input} attributes
     *            will be transferred from this map to the corresponding fields. Keys of
     *            the map are computed based on the <code>key</code> parameter of the
     *            {@link Attribute} annotation. Controller will not modify the provided
     *            map, processing results will be available in the returned
     *            {@link ProcessingResult}.
     * @param processingComponentClassesOrIds classes or identifiers of components to
     *            perform processing in the order they should be arranged in the pipeline.
     *            Fully-qualified class names are also accepted. Each provided class must
     *            implement {@link IProcessingComponent}.
     * @return results of the processing
     * @see #init(Map, ProcessingComponentConfiguration...)
     */
    public ProcessingResult process(Map<String, Object> attributes,
        Object... processingComponentClassesOrIds) throws ProcessingException
    {
        checkClosed();

        // Automatically initialize the controller if the caller has not done that
        // explicitly. The extra synchronization overhead does exist, but it's very
        // small compared to the API simplification benefits.
        synchronized (this)
        {
            if (componentIdToConfiguration == null)
            {
                init();
            }
        }

        // Prepare components for processing
        final IProcessingComponent [] components = new IProcessingComponent [processingComponentClassesOrIds.length];
        final ProcessingComponentConfiguration [] configurations = new ProcessingComponentConfiguration [components.length];

        ProcessingResult processingResult = null;
        try
        {
            // Prepare final maps of all init- and processing-time input attributes
            final Map<String, Object> inputAttributes = Maps.newHashMap();
            for (int i = 0; i < processingComponentClassesOrIds.length; i++)
            {
                configurations[i] = resolveComponent(processingComponentClassesOrIds[i]);

                inputAttributes.putAll(initAttributes); // global attributes
                inputAttributes.putAll(configurations[i].attributes); // component-specific

                // We need to provide processing-time attributes here as well because
                // some component managers may need them. For example, caching manager,
                // for each request creates caching wrappers around regular components
                // and the wrappers must have access to processing-time attributes in case
                // the results has not yet been cached.
                inputAttributes.putAll(attributes);
            }

            // A copy of the input attributes
            final Map<String, Object> attributesCopy = Maps.newHashMap(attributes);

            // A modifiable map into which we'll be collecting all the results
            // Should we preserve unrelated entries from input on the output?
            final Map<String, Object> resultAttributes = Maps.newHashMap(attributes);

            // Perform processing
            for (int i = 0; i < components.length; i++)
            {
                // Create a component for processing. Depending on the manager, a new
                // component may be instantiated, a pooled one may be returned or we may
                // get some wrapper that performs some extra actions.
                components[i] = componentManager.prepare(
                    configurations[i].componentClass, 
                    configurations[i].componentId,
                    inputAttributes, resultAttributes);

                final long componentStart = System.currentTimeMillis();
                try
                {
                    // It would be tempting to provide the inputAttributes map
                    // in this invocation of performProcessing(). There is a subtle case
                    // that prevents us from doing so. inputAttributes contains also @Init
                    // attributes. If we provide them at processing time, useless
                    // instances of @Init @Processing @Input attributes may be created.
                    // See ControllerTestsPooling#testComponentConfigurationInitProcessingAttributeCreation()
                    // for a test case.
                    ControllerUtils.performProcessing(components[i], attributesCopy,
                        resultAttributes);

                    // Feed the output of this component as the next one's input.
                    attributesCopy.putAll(resultAttributes);
                }
                finally
                {
                    final long componentStop = System.currentTimeMillis();

                    // Log processing time
                    final long time = componentStop - componentStart;

                    // Count only regular processing components, omit wrappers
                    if (IDocumentSource.class
                        .isAssignableFrom(configurations[i].componentClass))
                    {
                        addTime(AttributeNames.PROCESSING_TIME_SOURCE, time,
                            resultAttributes);
                    }
                    if (IClusteringAlgorithm.class
                        .isAssignableFrom(configurations[i].componentClass))
                    {
                        addTime(AttributeNames.PROCESSING_TIME_ALGORITHM, time,
                            resultAttributes);
                    }
                    addTime(AttributeNames.PROCESSING_TIME_TOTAL, time, resultAttributes);
                }
            }

            try {
                processingResult = new ProcessingResult(resultAttributes);
            } catch (IllegalArgumentException e) {
                throw new ProcessingException(e);
            }
            return processingResult;
        }
        finally
        {
            statistics.update(processingResult);

            for (int i = 0; i < components.length; i++)
            {
                final IProcessingComponent component = components[i];
                if (component != null)
                {
                    // Recycle a component. A component manager may want to e.g. return
                    // the component to its internal pool.
                    componentManager.recycle(component, configurations[i].componentId);
                }
            }
        }
    }

    /**
     * Shuts down this controller. For proper shut down, make sure this method is called
     * after all threads left the processing methods. No calls processing will be
     * performed after invoking this method.
     */
    public void dispose()
    {
        if (closed) return;
        try
        {
            if (this.context != null)
            {
                componentManager.dispose();
                this.context.dispose();
                this.context = null;
            }
        }
        finally
        {
            this.closed = true;
        }
    }

    /** Implement closeable so that controller can be closed with Java 1.7 resource block. */
    @Override
    public void close()
    {
        dispose();
    }

    /**
     * Throws an exception if this controller has been closed.
     */
    private void checkClosed()
    {
        if (closed)
            throw new IllegalStateException("Controller closed.");
    }

    /**
     * Returns an internal {@link ProcessingComponentConfiguration} based on the component
     * id, class or class name.
     */
    private ProcessingComponentConfiguration resolveComponent(Object classOrId)
    {
        if (classOrId instanceof String)
        {
            // Check if there's a matching configuration
            final ProcessingComponentConfiguration configuration = 
                componentIdToConfiguration.get(classOrId);

            if (configuration != null)
            {
                return configuration;
            }

            // If not, check if the string denotes an existing class name
            try
            {
                classOrId = ReflectionUtils.classForName((String) classOrId);

                // Fall through to the condition below
            }
            catch (ClassNotFoundException e)
            {
                throw new IllegalArgumentException("Unknown component id: " + classOrId);
            }
        }

        if (classOrId instanceof Class<?>)
        {
            final Class<?> clazz = (Class<?>) classOrId;
            if (!IProcessingComponent.class.isAssignableFrom(clazz))
            {
                throw new IllegalArgumentException("Expected a Class<? extends "
                    + IProcessingComponent.class.getSimpleName() + "> but got: "
                    + clazz.getName());
            }

            return new ProcessingComponentConfiguration(
                clazz.asSubclass(IProcessingComponent.class), null);
        }

        throw new IllegalArgumentException("Expected a String or a Class<? extends "
            + IProcessingComponent.class.getSimpleName() + ">");
    }

    /**
     * Adds time to the specified time attribute.
     */
    private static void addTime(String key, Long timeToAdd, Map<String, Object> attributes)
    {
        final Long time = (Long) attributes.get(key);
        if (time == null)
        {
            attributes.put(key, timeToAdd);
        }
        else
        {
            attributes.put(key, time + timeToAdd);
        }
    }

    /**
     * Returns current statistics related to the processing performed in this controller,
     * including: number of queries, number of successful queries, processing times, cache
     * utilization if applicable.
     */
    public ControllerStatistics getStatistics()
    {
        return statistics.getStatistics();
    }

    /**
     * Some managers may want to use this interface to provide additional statistics to
     * the controller.
     */
    static interface IControllerStatisticsProvider
    {
        /**
         * Called when the controller is requested to provide current statistics.
         */
        public Map<String, Object> getStatistics();
    }

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

        ControllerStatistics getStatistics()
        {
            final Map<String, Object> extraStats;
            if (componentManager instanceof IControllerStatisticsProvider)
            {
                extraStats = ((IControllerStatisticsProvider) componentManager)
                    .getStatistics();
            }
            else
            {
                extraStats = Collections.emptyMap();
            }

            // The stats may be still a little off because synchronization does not
            // affect component manager specific stats. Complete accuracy is not
            // worth the extra synchronizations though.
            synchronized (this)
            {
                return new ControllerStatistics(
                    totalQueries,
                    goodQueries,
                    algorithmTimeAverage.getCurrentAverage(),
                    algorithmTimeAverage.getUpdatesInWindow(),
                    algorithmTimeAverage.getWindowSizeMillis(),
                    sourceTimeAverage.getCurrentAverage(),
                    sourceTimeAverage.getUpdatesInWindow(),
                    sourceTimeAverage.getWindowSizeMillis(),
                    totalTimeAverage.getCurrentAverage(),
                    totalTimeAverage.getUpdatesInWindow(),
                    totalTimeAverage.getWindowSizeMillis(),
                    (Long) extraStats.get(CachingProcessingComponentManager.CACHE_MISSES),
                    (Long) extraStats.get(CachingProcessingComponentManager.CACHE_HITS_TOTAL));
            }
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
