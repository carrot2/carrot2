
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

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.fest.assertions.MapAssert.entry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.carrotsearch.randomizedtesting.annotations.Nightly;
import org.carrot2.shaded.guava.common.collect.ImmutableMap;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * Tests common functionality of a {@link Controller}. The fact that we need to resort to
 * having {@link #isCaching()} and {@link #isPooling()} methods here isn't pretty, but
 * makes testing a lot easier.
 */
public abstract class ControllerTestsCommon extends ControllerTestsBase
{
    /**
     * Returns a controller that implements at least basic processing functionality. All
     * simple, pooling and caching controllers fit here.
     */
    public abstract Controller getSimpleController();

    @Before
    public void disableOrderChecking()
    {
        if (isCaching() && !isPooling())
        {
            mocksControl.checkOrder(false);
        }
    }

    @Override
    public Controller prepareController()
    {
        return getSimpleController();
    }

    @Test
    public void testNormalExecution1Component()
    {
        invokeInitForCache(component1Mock);
        invokeProcessingWithInit(component1Mock);
        invokeDisposal(component1Mock);

        mocksControl.replay();

        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessingDisposeAndVerifyMocks(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));
    }

    @Test
    public void testNormalExecution3Components()
    {
        invokeInitForCache(component1Mock, component2Mock, component3Mock);
        invokeProcessingWithInit(component1Mock, component2Mock, component3Mock);
        invokeDisposal(component1Mock, component2Mock, component3Mock);

        mocksControl.replay();

        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessingDisposeAndVerifyMocks(Component1.class, Component2.class,
            Component3.class);

        assertEquals("diririr", resultAttributes.get("data"));
    }

    @Test
    public void testInputAttributesCopiedOnOutput()
    {
        testNormalExecution1Component();
        assertEquals("r", resultAttributes.get("runtimeAttribute"));
    }

    @Test
    public void testUnrelatedProcessingAttributesCopiedOnOutput()
    {
        processingAttributes.put("unrelated", 10);
        testNormalExecution1Component();
        assertEquals(10, resultAttributes.get("unrelated"));
    }

    /**
     * An attempt to validate the correctness of processing in a multithreaded setting.
     * Also demonstrates characteristics of each controller configuration, i.e. the number
     * of created component instances and processing requests.
     */
    @Test @Nightly
    public void testStress() throws InterruptedException, ExecutionException
    {
        // If there's no caching, make fewer queries to speed up tests
        final int numberOfQueriesBase = isCaching() ? 1000 : 50;
        final int numberOfQueries = randomIntBetween(numberOfQueriesBase, 2 * numberOfQueriesBase);
        final int numberOfThreads = randomIntBetween(5, 30);
        final String [] data = new String [numberOfQueries];
        final Set<String> uniqueQueries = Sets.newHashSet();
        for (int i = 0; i < data.length; i++)
        {
            data[i] = Integer.toString(randomIntBetween(1, 30));
            uniqueQueries.add(data[i]);
        }
        final int numberOfUniqueQueries = uniqueQueries.size();

        // Calculated expected invocation counts
        final int numberOfCreatedComponentsMin;
        final int numberOfCreatedComponentsMax;
        final int numberOfProcessingRequests;
        if (!isCaching() && !isPooling())
        {
            numberOfCreatedComponentsMin = numberOfCreatedComponentsMax = numberOfQueries;
            numberOfProcessingRequests = numberOfQueries;
        }
        else if (!isCaching() && isPooling())
        {
            numberOfCreatedComponentsMin = 1;
            numberOfCreatedComponentsMax = numberOfThreads;
            numberOfProcessingRequests = numberOfQueries;
        }
        else if (isCaching() && !isPooling())
        {
            // The +1 is to cover the fact that the cache needs to create a component
            // to read its attribute descriptors. This is done once per controller per
            // component configuration.
            numberOfCreatedComponentsMin = numberOfCreatedComponentsMax = numberOfUniqueQueries + 1;
            numberOfProcessingRequests = numberOfUniqueQueries;
        }
        else
        {
            numberOfCreatedComponentsMin = 1;
            numberOfCreatedComponentsMax = numberOfUniqueQueries;
            numberOfProcessingRequests = numberOfUniqueQueries;
        }

        // We're not using processing invocation utility methods which initialize
        // the controller, so we need to prepare one on our own.
        controller = prepareController();
        controller.init(initAttributes);

        // Record calls
        mocksControl.checkOrder(false);

        component1Mock.init(isA(IControllerContext.class));
        expectLastCall().times(numberOfCreatedComponentsMin, numberOfCreatedComponentsMax);
        for (int i = 0; i < numberOfProcessingRequests; i++)
        {
            component1Mock.beforeProcessing();
            component1Mock.process();
            expectLastCall().andAnswer(new DelayedAnswer<Object>(randomInt(100)));
            component1Mock.afterProcessing();
        }
        component1Mock.dispose();
        expectLastCall().times(numberOfCreatedComponentsMin, numberOfCreatedComponentsMax);
        mocksControl.replay();

        // Perform processing
        final List<Thread> children = Collections.synchronizedList(Lists.<Thread>newArrayList());
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, new ThreadFactory()
        {
            public Thread newThread(Runnable r)
            {
                Thread t = new Thread(r);
                children.add(t);
                return t;
            }
        });

        List<Callable<String>> callables = Lists.newArrayList();
        for (final String string : data)
        {
            callables.add(new Callable<String>()
            {
                public String call() throws Exception
                {
                    Map<String, Object> localAttributes = Maps.newHashMap(processingAttributes);
                    localAttributes.put("runtimeAttribute", string);
                    localAttributes.put("data", "d");
                    final ProcessingResult localResult = controller.process(localAttributes, Component1.class);
                    return localResult.getAttribute("data");
                }
            });
        }

        // Validate results
        List<Future<String>> results = executorService.invokeAll(callables);
        int i = 0;
        for (Future<String> future : results)
        {
            assertEquals("di" + data[i++], future.get());
        }
        executorService.shutdown();
        for (Thread t : children)
            t.join();

        controller.dispose();
        controller = null;
        mocksControl.verify();
    }

    @Test(expected = ComponentInitializationException.class)
    public void testExceptionWhileCreatingInstances()
    {
        invokeInitForCache(component1Mock);
        invokeProcessingWithInit(component1Mock);
        invokeDisposal(component1Mock);

        mocksControl.replay();

        processingAttributes.put("data", "d");
        performProcessingDisposeAndVerifyMocks(Component1.class,
            ComponentWithoutDefaultConstructor.class);
    }

    @Test(expected = ComponentInitializationException.class)
    public void testExceptionWhileInit()
    {
        invokeInitForCache(component1Mock, component2Mock);

        invokeProcessingWithInit(component1Mock);

        component2Mock.init(isA(IControllerContext.class));
        expectLastCall().andThrow(new ComponentInitializationException((String) null));

        component2Mock.dispose();
        invokeDisposal(component1Mock);

        mocksControl.replay();

        processingAttributes.put("data", "d");
        performProcessingDisposeAndVerifyMocks(Component1.class, Component2.class);
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionBeforeProcessing()
    {
        invokeInitForCache(component1Mock, component2Mock);

        invokeProcessingWithInit(component1Mock);

        invokeInit(component2Mock);
        component2Mock.beforeProcessing();
        expectLastCall().andThrow(new ProcessingException("Before processing exception"));
        component2Mock.afterProcessing();

        invokeDisposal(component1Mock, component2Mock);

        mocksControl.replay();

        processingAttributes.put("data", "d");
        performProcessingDisposeAndVerifyMocks(Component1.class, Component2.class);
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionDuringProcessing()
    {
        invokeInitForCache(component1Mock);
        invokeProcessingWithInit(component1Mock);

        invokeInitForCache(component2Mock);
        invokeInit(component2Mock);
        component2Mock.beforeProcessing();
        component2Mock.process();
        expectLastCall().andThrow(new ProcessingException("Processing exception"));
        component2Mock.afterProcessing();

        invokeDisposal(component1Mock, component2Mock);

        mocksControl.replay();

        processingAttributes.put("data", "d");
        performProcessingDisposeAndVerifyMocks(Component1.class, Component2.class);
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionAfterProcessing()
    {
        invokeInitForCache(component1Mock, component2Mock);

        invokeProcessingWithInit(component1Mock);

        invokeInit(component2Mock);
        component2Mock.beforeProcessing();
        component2Mock.process();
        component2Mock.afterProcessing();
        expectLastCall().andThrow(new ProcessingException("After processing exception"));

        invokeDisposal(component1Mock, component2Mock);

        mocksControl.replay();

        processingAttributes.put("data", "d");
        performProcessingDisposeAndVerifyMocks(Component1.class, Component2.class);
    }

    @Test @Nightly
    public void testNormalExecutionTimeMeasurement()
    {
        final long c1Time = 250;
        final long c2Time = 500;
        final long c3Time = 750;
        final long totalTime = c1Time + c2Time + c3Time;
        final double tolerance = 1;

        mocksControl.resetToNice();

        component1Mock.process();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(c1Time));
        component2Mock.beforeProcessing();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(c2Time));
        component3Mock.afterProcessing();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(c3Time));
        component1Mock.process();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(c1Time));
        component2Mock.beforeProcessing();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(c2Time));
        component3Mock.afterProcessing();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(c3Time));

        mocksControl.replay();

        processingAttributes.put("data", "d");
        performProcessing(Component1.class, Component2.class, Component3.class);

        checkTimes(c1Time, c2Time, totalTime, tolerance);

        processingAttributes.put("data", "d");
        performProcessingAndDispose(Component1.class, Component2.class, Component3.class);

        if (isCaching())
        {
            checkTimes(0, 0, 0, tolerance);
        }
        else
        {
            checkTimes(c1Time, c2Time, totalTime, tolerance);
        }
    }

    @Test
    public void testContextDisposal()
    {
        final IProcessingComponent processingComponentWithContextListenerMock = mocksControl
            .createMock(IProcessingComponent.class);
        final IControllerContextListener contextListenerMock = mocksControl
            .createMock(IControllerContextListener.class);

        initAttributes.put("delegateWithContextListener",
            processingComponentWithContextListenerMock);
        initAttributes.put("contextListener", contextListenerMock);

        invokeInitForCache(processingComponentWithContextListenerMock);
        invokeProcessingWithInit(processingComponentWithContextListenerMock);
        invokeDisposal(processingComponentWithContextListenerMock);
        contextListenerMock.beforeDisposal(isA(IControllerContext.class));

        mocksControl.replay();

        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        try {
            performProcessingDisposeAndVerifyMocks(ComponentWithContextListener.class);
        } finally {
            ComponentWithContextListener.contextListenerSubscribed.set(false);
        }
    }

    @Test
    public void testCollectionOfInitOutputAttributes()
    {
        performProcessingAndDispose(ComponentWithInitOutputAttribute.class);
        Assert.assertEquals("initOutput", resultAttributes.get("initOutput"));
    }

    /**
     * Verifies that {@link Output} attributes with <code>null</code> default values don't
     * clear default values in components further down the processing chain.
     */
    @Test
    public void testOutputAttributesWithNullValues()
    {
        performProcessing(ComponentWithInputOutputAttributes1.class,
            ComponentWithInputOutputAttributes2.class);

        Assert.assertEquals("default", resultAttributes.get("key1"));
        Assert.assertEquals("value", resultAttributes.get("key2"));

        processingAttributes.clear();
        processingAttributes.put("key1", null);
        processingAttributes.put("key2", null);

        performProcessingAndDispose(ComponentWithInputOutputAttributes1.class,
            ComponentWithInputOutputAttributes2.class);

        Assert.assertEquals(null, resultAttributes.get("key1"));
        Assert.assertEquals("value", resultAttributes.get("key2"));
    }

    @Test
    public void testProcessingInvocationMethods()
    {
        controller = prepareController().init(
            ImmutableMap.<String, Object> of(),
            new ProcessingComponentConfiguration(ComponentWithInitParameter.class,
                "component", ImmutableMap.<String, Object> of()));

        final Map<String, Object> attributes = Maps.newHashMap();

        final ProcessingResult resultByClass = controller.process(attributes,
            ComponentWithInitParameter.class);
        final ProcessingResult resultByClassName = controller.process(attributes,
            ComponentWithInitParameter.class.getName());
        final ProcessingResult resultById = controller.process(attributes, "component");

        assertThat((String) resultByClass.getAttribute("result")).isEqualTo("defaultdefault");
        assertThat((String) resultByClassName.getAttribute("result")).isEqualTo("defaultdefault");
        assertThat((String) resultById.getAttribute("result")).isEqualTo("defaultdefault");

        controller.dispose();
        controller = null;
    }

    @Test
    public void testPassingRequiredProcessingAttribute()
    {
        controller = prepareController();

        final Map<String, Object> attributes = Maps.newHashMap();

        controller.process(attributes, ComponentWithOutputAttribute.class,
            ComponentWithRequiredProcessingAttribute.class);

        controller.dispose();
        controller = null;
    }

    @Test
    public void testComponentConfigurationDifferentInitAttributes()
    {
        controller = prepareController().init(
            ImmutableMap.<String, Object> of(),
            new ProcessingComponentConfiguration(ComponentWithInitParameter.class,
                "component1", ImmutableMap.of("init", (Object) "v1")),
            new ProcessingComponentConfiguration(ComponentWithInitParameter.class,
                "component2", ImmutableMap.of("init", (Object) "v2")));

        final Map<String, Object> attributes = Maps.newHashMap();

        assertThat(controller.process(attributes, "component1").getAttributes())
            .includes(entry("result", "v1v1"));

        assertThat(controller.process(attributes, "component2").getAttributes())
            .includes(entry("result", "v2v2"));

        controller.dispose();
        controller = null;
    }

    @Test
    public void testComponentConfigurationProcessingAttributeAtInitTime()
    {
        controller = prepareController().init(
            ImmutableMap.<String, Object> of(),
            new ProcessingComponentConfiguration(ComponentWithProcessingParameter.class,
                "component1", ImmutableMap.of("processing", (Object) "v1")),
            new ProcessingComponentConfiguration(ComponentWithProcessingParameter.class,
                "component2", ImmutableMap.of("processing", (Object) "v2")));

        final Map<String, Object> attributes = Maps.newHashMap();

        assertThat(controller.process(attributes, "component1").getAttributes())
            .includes(entry("result", "v1v1"));
        assertThat(controller.process(attributes, "component2").getAttributes())
            .includes(entry("result", "v2v2"));

        controller.dispose();
        controller = null;
    }

    @Test
    public void testInitProcessingInputRequiredAttributeProvidedDuringProcessing()
    {
        processingAttributes.put("initProcessing", "test");
        performProcessingAndDispose(ComponentWithInitProcessingInputRequiredAttribute.class);

        assertThat((String) resultAttributes.get("result")).isEqualTo("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComponentConfigurationDuplicateComponentId()
    {
        Controller controller = prepareController();
        try {
            controller.init(
                ImmutableMap.<String, Object> of(),
                new ProcessingComponentConfiguration(ComponentWithInitParameter.class,
                    "component", ImmutableMap.of("init", (Object) "v1")),
                new ProcessingComponentConfiguration(ComponentWithInitParameter.class,
                    "component", ImmutableMap.of("init", (Object) "v2")));
        } finally {
            controller.dispose();
        }
    }

    @Test
    public void testEmptyStatsInNewController()
    {
        final Controller controller = prepareController();
        final ControllerStatistics statistics = controller.getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.totalQueries).isEqualTo(0);
        assertThat(statistics.goodQueries).isEqualTo(0);
        assertThat(statistics.algorithmTimeAverageInWindow).isEqualTo(0);
        assertThat(statistics.algorithmTimeMeasurementsInWindow).isEqualTo(0);
        assertThat(statistics.sourceTimeAverageInWindow).isEqualTo(0);
        assertThat(statistics.sourceTimeMeasurementsInWindow).isEqualTo(0);
        assertThat(statistics.totalTimeAverageInWindow).isEqualTo(0);
        assertThat(statistics.totalTimeMeasurementsInWindow).isEqualTo(0);

        if (isCaching())
        {
            assertThat(statistics.cacheMisses).isEqualTo(0);
            assertThat(statistics.cacheHitsTotal).isEqualTo(0);
        }
        else
        {
            assertThat((Object) statistics.cacheMisses).isNull();
            assertThat((Object) statistics.cacheHitsTotal).isNull();
        }
        
        controller.dispose();
    }

    @Test
    public void testStatsOneGoodQuery()
    {
        final int delay = 100;
        final int halfDelay = delay / 2;
        processingAttributes.put("data", "d");

        mocksControl.resetToNice();

        component1Mock.process();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(delay));

        component2Mock.process();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(delay));

        mocksControl.replay();

        performProcessing(Component1.class, Component2.class);

        final ControllerStatistics statistics = controller.getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.totalQueries).isEqualTo(1);
        assertThat(statistics.goodQueries).isEqualTo(1);
        assertThat(statistics.algorithmTimeAverageInWindow).isGreaterThanOrEqualTo(halfDelay);
        assertThat(statistics.algorithmTimeMeasurementsInWindow).isEqualTo(1);
        assertThat(statistics.sourceTimeAverageInWindow).isGreaterThanOrEqualTo(halfDelay);
        assertThat(statistics.sourceTimeMeasurementsInWindow).isEqualTo(1);
        assertThat(statistics.totalTimeAverageInWindow).isGreaterThanOrEqualTo(2 * halfDelay);
        assertThat(statistics.totalTimeMeasurementsInWindow).isEqualTo(1);
        if (isCaching())
        {
            assertThat(statistics.cacheMisses).isEqualTo(2);
            assertThat(statistics.cacheHitsTotal).isEqualTo(0);
        }

        controller.dispose();
        controller = null;
    }

    @Test
    public void testStatsTwoGoodQueriesCached()
    {
        processingAttributes.put("data", "d");
        performProcessing(Component1.class);
        processingAttributes.put("data", "d");
        performProcessing(Component1.class);

        final ControllerStatistics statistics = controller.getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.totalQueries).isEqualTo(2);
        assertThat(statistics.goodQueries).isEqualTo(2);

        if (isCaching())
        {
            assertThat(statistics.cacheMisses).isEqualTo(1);
            assertThat(statistics.cacheHitsTotal).isEqualTo(1);
        }

        controller.dispose();
        controller = null;
    }

    @Test(expected = RuntimeException.class)
    public void testStatsOneGoodQueryOneErrorQuery()
    {
        mocksControl.resetToNice();

        processingAttributes.put("data", "d");

        component1Mock.process();
        expectLastCall().andThrow(new RuntimeException());
        component1Mock.afterProcessing();

        mocksControl.replay();

        try
        {
            performProcessing(Component3.class);
            performProcessing(Component1.class, Component2.class);
        }
        finally
        {
            final ControllerStatistics statistics = controller.getStatistics();
            assertThat(statistics).isNotNull();
            assertThat(statistics.totalQueries).isEqualTo(2);
            assertThat(statistics.goodQueries).isEqualTo(1);

            if (isCaching())
            {
                assertThat(statistics.cacheMisses).isEqualTo(2);
                assertThat(statistics.cacheHitsTotal).isEqualTo(0);
            }

            controller.dispose();
            controller = null;
        }
    }

    @Test
    public void settingInitAttributeToNull()
    {
        invokeInitForCache(component1Mock);
        invokeProcessingWithInit(component1Mock);
        invokeDisposal(component1Mock);

        mocksControl.replay();

        initAttributes.put("data", null);
        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessingDisposeAndVerifyMocks(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));
    }

    @Bindable
    public static class ComponentWithMapParameter extends ProcessingComponentBase
    {
        @Input
        @Processing
        @Attribute(key = "other")
        public Map<String,String> other;

        @Output
        @Processing
        @Attribute(key = "result")
        public String result;

        @Override
        public void process() throws ProcessingException
        {
            result = new TreeMap<>(other).toString();
        }
    }

    @Test
    public void testMapWithKeysAttribute()
    {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("k1", "v1");
        map1.put("k2", "v2");

        processingAttributes.put("other", map1);
        ProcessingResult pr = performProcessing(ComponentWithMapParameter.class);
        assertThat((Object) pr.getAttribute("result")).isEqualTo("{k1=v1, k2=v2}");

        if (isCaching())
        {
            pr = performProcessing(ComponentWithMapParameter.class);
            assertThat((String) pr.getAttribute("result")).isEqualTo("{k1=v1, k2=v2}");

            final ControllerStatistics statistics = controller.getStatistics();
            assertThat(statistics.cacheMisses).isEqualTo(1);
            assertThat(statistics.cacheHitsTotal).isEqualTo(1);
        }

        Map<String, String> map2 = new HashMap<String, String>();
        map2.putAll(map1);
        map1.put("k1", "v1_2");
        pr = performProcessing(ComponentWithMapParameter.class);
        assertThat((Object) pr.getAttribute("result")).isEqualTo("{k1=v1_2, k2=v2}");
        
        controller.dispose();
        controller = null;
    }

    /**
     * The tests invoking this method are almost the same for all controllers. The only
     * exception is a caching non-pooling controller, which does extra component
     * init/dispose cycles to prepare attribute descriptors. This method helps to cover
     * this case.
     */
    private void invokeInitForCache(final IProcessingComponent... components)
    {
        if (isCaching() && !isPooling())
        {
            for (IProcessingComponent component : components)
            {
                component.init(isA(IControllerContext.class));
                component.dispose();
            }
        }
    }
}
