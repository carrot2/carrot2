/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.concurrent.*;

import org.carrot2.util.attribute.Output;
import org.junit.*;

import com.google.common.collect.*;

/**
 * Tests common functionality of a {@link Controller}. The fact that we need to resort to
 * having {@link #hasCaching()} and {@link #hasPooling()} methods here isn't pretty, but
 * makes testing a lot easier.
 */
public abstract class ControllerTestsCommon extends ControllerTestsBase
{
    /**
     * Returns a controller that implements at least basic processing functionality. All
     * simple, pooling and caching controllers fit here.
     */
    public abstract Controller getSimpleController();

    public boolean hasCaching()
    {
        return false;
    }

    public boolean hasPooling()
    {
        return false;
    }

    @Before
    public void disableOrderChecking()
    {
        if (hasCaching() && !hasPooling())
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
    @Test
    public void testStress() throws InterruptedException, ExecutionException
    {
        // Prepare random data
        final Random random = new Random();

        // When there's no caching, make fewer queries to speed up tests
        final int numberOfQueriesBase = hasCaching() ? 1000 : 50;
        final int numberOfQueries = numberOfQueriesBase
            + random.nextInt(numberOfQueriesBase);
        final int numberOfThreads = 25 + random.nextInt(10);
        final String [] data = new String [numberOfQueries];
        final Set<String> uniqueQueries = Sets.newHashSet();
        for (int i = 0; i < data.length; i++)
        {
            data[i] = Integer.toString(random.nextInt(20 + random.nextInt(10)));
            uniqueQueries.add(data[i]);
        }
        final int numberOfUniqueQueries = uniqueQueries.size();

        // Calculated expected invocation counts
        final int numberOfCreatedComponentsMin;
        final int numberOfCreatedComponentsMax;
        final int numberOfProcessingRequests;
        if (!hasCaching() && !hasPooling())
        {
            numberOfCreatedComponentsMin = numberOfCreatedComponentsMax = numberOfQueries;
            numberOfProcessingRequests = numberOfQueries;
        }
        else if (!hasCaching() && hasPooling())
        {
            numberOfCreatedComponentsMin = 1;
            numberOfCreatedComponentsMax = numberOfThreads;
            numberOfProcessingRequests = numberOfQueries;
        }
        else if (hasCaching() && !hasPooling())
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
        expectLastCall()
            .times(numberOfCreatedComponentsMin, numberOfCreatedComponentsMax);
        for (int i = 0; i < numberOfProcessingRequests; i++)
        {
            component1Mock.beforeProcessing();
            component1Mock.process();
            expectLastCall().andAnswer(new DelayedAnswer<Object>(random.nextInt(100)));
            component1Mock.afterProcessing();
        }
        component1Mock.dispose();
        expectLastCall()
            .times(numberOfCreatedComponentsMin, numberOfCreatedComponentsMax);
        mocksControl.replay();

        // Perform processing
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<String>> callables = Lists.newArrayList();
        for (final String string : data)
        {
            callables.add(new Callable<String>()
            {
                public String call() throws Exception
                {
                    Map<String, Object> localAttributes = Maps
                        .newHashMap(processingAttributes);
                    localAttributes.put("runtimeAttribute", string);
                    localAttributes.put("data", "d");
                    final ProcessingResult localResult = controller.process(
                        localAttributes, Component1.class);
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

    @Test
    public void testNormalExecutionTimeMeasurement()
    {
        final long c1Time = 250;
        final long c2Time = 500;
        final long c3Time = 750;
        final long totalTime = c1Time + c2Time + c3Time;
        final double tolerance = 0.5;

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

        if (hasCaching())
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

        performProcessingDisposeAndVerifyMocks(ComponentWithContextListener.class);
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

        assertThat(resultByClass.getAttribute("result")).isEqualTo("defaultdefault");
        assertThat(resultByClassName.getAttribute("result")).isEqualTo("defaultdefault");
        assertThat(resultById.getAttribute("result")).isEqualTo("defaultdefault");

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
        prepareController().init(
            ImmutableMap.<String, Object> of(),
            new ProcessingComponentConfiguration(ComponentWithInitParameter.class,
                "component", ImmutableMap.of("init", (Object) "v1")),
            new ProcessingComponentConfiguration(ComponentWithInitParameter.class,
                "component", ImmutableMap.of("init", (Object) "v2")));
    }

    @Test
    public void testEmptyStatsInNewController()
    {
        final ControllerStatistics statistics = prepareController().getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.totalQueries).isEqualTo(0);
        assertThat(statistics.goodQueries).isEqualTo(0);
        assertThat(statistics.algorithmTimeAverageInWindow).isEqualTo(0);
        assertThat(statistics.algorithmTimeMeasurementsInWindow).isEqualTo(0);
        assertThat(statistics.sourceTimeAverageInWindow).isEqualTo(0);
        assertThat(statistics.sourceTimeMeasurementsInWindow).isEqualTo(0);
        assertThat(statistics.totalTimeAverageInWindow).isEqualTo(0);
        assertThat(statistics.totalTimeMeasurementsInWindow).isEqualTo(0);

        if (hasCaching())
        {
            assertThat(statistics.cacheMisses).isEqualTo(0);
            assertThat(statistics.cacheHitsTotal).isEqualTo(0);
            assertThat(statistics.cacheHitsMemory).isEqualTo(0);
            assertThat(statistics.cacheHitsDisk).isEqualTo(0);
        }
        else
        {
            assertThat((Object) statistics.cacheMisses).isNull();
            assertThat((Object) statistics.cacheHitsTotal).isNull();
            assertThat((Object) statistics.cacheHitsMemory).isNull();
            assertThat((Object) statistics.cacheHitsDisk).isNull();
        }
    }

    @Test
    public void testStatsOneGoodQuery()
    {
        processingAttributes.put("data", "d");

        mocksControl.resetToNice();

        component1Mock.process();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(300));

        component2Mock.process();
        expectLastCall().andAnswer(new DelayedAnswer<Object>(300));

        mocksControl.replay();

        performProcessing(Component1.class, Component2.class);

        final ControllerStatistics statistics = controller.getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.totalQueries).isEqualTo(1);
        assertThat(statistics.goodQueries).isEqualTo(1);
        assertThat(statistics.algorithmTimeAverageInWindow).isGreaterThanOrEqualTo(200);
        assertThat(statistics.algorithmTimeMeasurementsInWindow).isEqualTo(1);
        assertThat(statistics.sourceTimeAverageInWindow).isGreaterThanOrEqualTo(200);
        assertThat(statistics.sourceTimeMeasurementsInWindow).isEqualTo(1);
        assertThat(statistics.totalTimeAverageInWindow).isGreaterThanOrEqualTo(400);
        assertThat(statistics.totalTimeMeasurementsInWindow).isEqualTo(1);
        if (hasCaching())
        {
            assertThat(statistics.cacheMisses).isEqualTo(2);
            assertThat(statistics.cacheHitsTotal).isEqualTo(0);
            assertThat(statistics.cacheHitsMemory).isEqualTo(0);
            assertThat(statistics.cacheHitsDisk).isEqualTo(0);
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

        if (hasCaching())
        {
            assertThat(statistics.cacheMisses).isEqualTo(1);
            assertThat(statistics.cacheHitsTotal).isEqualTo(1);
            assertThat(statistics.cacheHitsMemory).isEqualTo(1);
            assertThat(statistics.cacheHitsDisk).isEqualTo(0);
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

            if (hasCaching())
            {
                assertThat(statistics.cacheMisses).isEqualTo(2);
                assertThat(statistics.cacheHitsTotal).isEqualTo(0);
                assertThat(statistics.cacheHitsMemory).isEqualTo(0);
                assertThat(statistics.cacheHitsDisk).isEqualTo(0);
            }

            controller.dispose();
            controller = null;
        }
    }

    /**
     * The tests invoking this method are almost the same for all controllers. The only
     * exception is a caching non-pooling controller, which does extra component
     * init/dispose cycles to prepare attribute descriptors. This method helps to cover
     * this case.
     */
    private void invokeInitForCache(final IProcessingComponent... components)
    {
        if (hasCaching() && !hasPooling())
        {
            for (IProcessingComponent component : components)
            {
                component.init(isA(IControllerContext.class));
                component.dispose();
            }
        }
    }
}
