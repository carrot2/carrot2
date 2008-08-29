package org.carrot2.core;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.junit.Test;

import com.google.common.collect.*;

/**
 * Test cases for {@link CachingController}.
 */
public class CachingControllerTest extends ControllerTestBase
{
    protected ProcessingComponent cachedProcessingComponent1Mock;

    @Bindable
    public static class CachedProcessingComponent1 extends DelegatingProcessingComponent
        implements DocumentSource
    {
        @Init
        @Input
        @Attribute(key = "cachedDelegate1")
        @ImplementingClasses(classes =
        {
            ProcessingComponent.class
        }, strict = false)
        protected ProcessingComponent cachedDelegate1;

        @Override
        ProcessingComponent getDelegate()
        {
            return cachedDelegate1;
        }
    }

    @Bindable
    @SuppressWarnings("unused")
    public static class ComponentWithBindableReference extends ProcessingComponentBase
    {
        @Processing
        @Input
        @Attribute
        @ImplementingClasses(classes = BindableClass.class)
        private BindableClass inputProcessingBindableWithDefault = new BindableClass();

        @Processing
        @Input
        @Attribute
        @ImplementingClasses(classes = BindableClass.class)
        private BindableClass inputProcessingBindableWithoutDefault;
    }

    @Bindable
    public static class BindableClass
    {
        static int createdInstances = 0;

        public BindableClass()
        {
            createdInstances++;
        }
    }

    @Bindable
    public static class ComponentWithInitParameter extends ProcessingComponentBase
    {
        @Input
        @Init
        @Attribute(key = "init")
        private String init = "default";

        @Output
        @Processing
        @Attribute(key = "result")
        @SuppressWarnings("unused")
        private String result;

        @Override
        public void process() throws ProcessingException
        {
            result = init + init;
        }
    }

    @Bindable
    public static class ComponentWithProcessingParameter extends ProcessingComponentBase
    {
        @Input
        @Processing
        @Attribute(key = "processing")
        private String processing = "default";

        @Output
        @Processing
        @Attribute(key = "result")
        @SuppressWarnings("unused")
        private String result;

        @Override
        public void process() throws ProcessingException
        {
            result = processing + processing;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Controller createController()
    {
        return new CachingController(CachedProcessingComponent1.class);
    }

    @Override
    protected void beforeControllerInit(Map<String, Object> initAttributes)
    {
        super.beforeControllerInit(initAttributes);

        cachedProcessingComponent1Mock = mocksControl
            .createMock(ProcessingComponent.class);
        initAttributes.put("cachedDelegate1", cachedProcessingComponent1Mock);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testUninitialized()
    {
        CachingController cachingController = new CachingController();
        cachingController.process(null, ProcessingComponent1.class);
    }

    @Test
    public void testRepeatedExecution1Component()
    {
        processingComponent1Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.dispose();

        mocksControl.replay();

        attributes.put("instanceAttribute", "i");
        attributes.put("runtimeAttribute", "r");
        attributes.put("data", "d");

        performProcessing(ProcessingComponent1.class);
        assertEquals("dir", attributes.get("data"));
        attributes.put("data", "d");
        performProcessing(ProcessingComponent1.class);
        assertEquals("dir", attributes.get("data"));

        controller.dispose();
        mocksControl.verify();
    }

    @Test
    public void testRepeatedExecution3Components()
    {
        processingComponent1Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent2Mock.init();
        processingComponent3Mock.init();
        mocksControl.checkOrder(true);
        processingComponent2Mock.beforeProcessing();
        processingComponent2Mock.process();
        processingComponent2Mock.afterProcessing();
        processingComponent3Mock.beforeProcessing();
        processingComponent3Mock.process();
        processingComponent3Mock.afterProcessing();
        mocksControl.checkOrder(false); // we don't care about the order of disposal
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();
        mocksControl.checkOrder(true);

        mocksControl.replay();

        attributes.put("instanceAttribute", "i");
        attributes.put("runtimeAttribute", "r");
        attributes.put("data", "d");

        performProcessing(ProcessingComponent1.class);
        assertEquals("dir", attributes.get("data"));
        attributes.put("data", "d");
        performProcessing(ProcessingComponent2.class, ProcessingComponent3.class);
        assertEquals("dirir", attributes.get("data"));

        controller.dispose();
        mocksControl.verify();
    }

    @Test
    public void testAttributeRestoring()
    {
        processingComponent1Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.dispose();

        mocksControl.replay();

        attributes.put("instanceAttribute", "i");
        attributes.put("runtimeAttribute", "r");
        attributes.put("data", "d");

        performProcessing(ProcessingComponent1.class);
        assertEquals("dir", attributes.get("data"));

        // Clear attributes and check if they've been restored
        attributes.clear();
        attributes.put("data", "d");
        performProcessing(ProcessingComponent1.class);
        assertEquals("di", attributes.get("data"));

        controller.dispose();
        mocksControl.verify();
    }

    @Test
    public void testReferenceAttributeRestoring()
    {
        assertEquals(0, BindableClass.createdInstances);
        performProcessing(ComponentWithBindableReference.class);
        assertEquals(2, BindableClass.createdInstances);
        performProcessing(ComponentWithBindableReference.class);
        assertEquals(3, BindableClass.createdInstances);
        performProcessing(ComponentWithBindableReference.class);
        assertEquals(4, BindableClass.createdInstances);
    }

    @Test(expected = AttributeBindingException.class)
    public void testRestoringRequiredProcessingAttributeToNull()
    {
        processingComponent1Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        // beforeProcessing will fail because of missing required attributes
        // afterProcessing() still will be performed
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.dispose();

        mocksControl.replay();

        attributes.put("instanceAttribute", "i");
        attributes.put("runtimeAttribute", "r");
        attributes.put("data", "d");

        performProcessing(ProcessingComponent1.class);
        assertEquals("dir", attributes.get("data"));

        // Clear attributes and check if they've been restored
        attributes.clear();

        // This processing will throw an exception -- required attribute not provided
        try
        {
            performProcessing(ProcessingComponent1.class);
        }
        finally
        {
            controller.dispose();
        }
        mocksControl.verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrency() throws InterruptedException
    {
        mocksControl.checkOrder(false);
        processingComponent1Mock.init();
        processingComponent1Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        mocksControl.andAnswer(new DelayedAnswer<Object>(500));
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        mocksControl.andAnswer(new DelayedAnswer<Object>(500));
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.dispose();
        processingComponent1Mock.dispose();
        mocksControl.checkOrder(true);

        mocksControl.replay();

        final Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                final Map<String, Object> localAttributes = Maps.newHashMap(attributes);
                localAttributes.put("instanceAttribute", "i");
                localAttributes.put("runtimeAttribute", "r");
                localAttributes.put("data", "d");
                controller.process(localAttributes, ProcessingComponent1.class);
                assertEquals("dir", localAttributes.get("data"));
            }
        });
        thread.start();

        final Map<String, Object> localAttributes = Maps.newHashMap(attributes);
        localAttributes.put("instanceAttribute", "i");
        localAttributes.put("runtimeAttribute", "r");
        localAttributes.put("data", "d");
        controller.process(localAttributes, ProcessingComponent1.class);
        assertEquals("dir", localAttributes.get("data"));

        thread.join();

        controller.dispose();
        mocksControl.verify();
    }

    @Test
    public void testCachingNoConcurrency()
    {
        cachedProcessingComponent1Mock.init();
        cachedProcessingComponent1Mock.beforeProcessing();
        cachedProcessingComponent1Mock.process();
        cachedProcessingComponent1Mock.afterProcessing();
        // second query runs entirely from cache
        cachedProcessingComponent1Mock.dispose();

        mocksControl.replay();

        attributes.put("instanceAttribute", "i");
        attributes.put("runtimeAttribute", "r");
        attributes.put("data", "d");

        performProcessing(CachedProcessingComponent1.class);
        assertEquals("dir", attributes.get("data"));

        attributes.put("data", "d");
        performProcessing(CachedProcessingComponent1.class);
        assertEquals("dir", attributes.get("data"));

        controller.dispose();
        mocksControl.verify();
    }

    @Test
    public void testOptionalAttributeNotDefined()
    {
        cachedProcessingComponent1Mock.init();
        cachedProcessingComponent1Mock.beforeProcessing();
        cachedProcessingComponent1Mock.process();
        cachedProcessingComponent1Mock.afterProcessing();
        // second query runs entirely from cache
        cachedProcessingComponent1Mock.dispose();

        mocksControl.replay();

        attributes.put("instanceAttribute", "i");
        attributes.put("data", "d");

        performProcessing(CachedProcessingComponent1.class);
        assertEquals("di", attributes.get("data"));

        attributes.put("data", "d");
        performProcessing(CachedProcessingComponent1.class);
        assertEquals("di", attributes.get("data"));

        controller.dispose();
        mocksControl.verify();
    }

    @Test
    public void testNoCachingNoConcurrency()
    {
        cachedProcessingComponent1Mock.init();
        cachedProcessingComponent1Mock.beforeProcessing();
        cachedProcessingComponent1Mock.process();
        cachedProcessingComponent1Mock.afterProcessing();
        cachedProcessingComponent1Mock.beforeProcessing();
        cachedProcessingComponent1Mock.process();
        cachedProcessingComponent1Mock.afterProcessing();
        cachedProcessingComponent1Mock.dispose();

        mocksControl.replay();

        attributes.put("instanceAttribute", "i");
        attributes.put("runtimeAttribute", "r");
        attributes.put("data", "d");

        performProcessing(CachedProcessingComponent1.class);
        assertEquals("dir", attributes.get("data"));

        attributes.put("data", "d");
        attributes.put("runtimeAttribute", "z");
        performProcessing(CachedProcessingComponent1.class);
        assertEquals("diz", attributes.get("data"));

        controller.dispose();
        mocksControl.verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCachingWithConcurrency() throws InterruptedException
    {
        cachedProcessingComponent1Mock.init();
        cachedProcessingComponent1Mock.beforeProcessing();
        cachedProcessingComponent1Mock.process();
        mocksControl.andAnswer(new DelayedAnswer<Object>(500));
        cachedProcessingComponent1Mock.afterProcessing();
        // second query runs entirely from cache
        cachedProcessingComponent1Mock.dispose();

        mocksControl.replay();

        final Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                final Map<String, Object> localAttributes = Maps.newHashMap(attributes);
                localAttributes.put("instanceAttribute", "i");
                localAttributes.put("runtimeAttribute", "r");
                localAttributes.put("data", "d");
                controller.process(localAttributes, CachedProcessingComponent1.class);
                assertEquals("dir", localAttributes.get("data"));
            }
        });
        thread.start();

        final Map<String, Object> localAttributes = Maps.newHashMap(attributes);
        localAttributes.put("instanceAttribute", "i");
        localAttributes.put("runtimeAttribute", "r");
        localAttributes.put("data", "d");
        controller.process(localAttributes, CachedProcessingComponent1.class);
        assertEquals("dir", localAttributes.get("data"));

        thread.join();

        controller.dispose();
        mocksControl.verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStress() throws InterruptedException, ExecutionException
    {
        final Random random = new Random();
        final int numberOfThreads = 25 + random.nextInt(10);
        final int numberOfQueries = 1000 + random.nextInt(1000);
        final String [] data = new String [numberOfQueries];
        final Set<String> uniqueQueries = Sets.newHashSet();
        for (int i = 0; i < data.length; i++)
        {
            data[i] = Integer.toString(random.nextInt(20 + random.nextInt(10)));
            uniqueQueries.add(data[i]);
        }

        // Record a call for each unique query
        mocksControl.checkOrder(false);
        cachedProcessingComponent1Mock.init();
        mocksControl.times(1, numberOfThreads);
        for (int i = 0; i < uniqueQueries.size(); i++)
        {
            cachedProcessingComponent1Mock.beforeProcessing();
            cachedProcessingComponent1Mock.process();
            mocksControl.andAnswer(new DelayedAnswer<Object>(random.nextInt(100)));
            cachedProcessingComponent1Mock.afterProcessing();
        }
        cachedProcessingComponent1Mock.dispose();
        mocksControl.times(1, numberOfThreads);
        mocksControl.replay();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<String>> callables = Lists.newArrayList();
        for (final String string : data)
        {
            callables.add(new Callable<String>()
            {
                public String call() throws Exception
                {
                    Map<String, Object> localAttributes = Maps.newHashMap(attributes);
                    localAttributes.put("runtimeAttribute", string);
                    localAttributes.put("data", "d");
                    controller.process(localAttributes, CachedProcessingComponent1.class);
                    return (String) localAttributes.get("data");
                }
            });
        }

        List<Future<String>> results = executorService.invokeAll(callables);
        int i = 0;
        for (Future<String> future : results)
        {
            assertEquals("di" + data[i++], future.get());
        }

        executorService.shutdown();

        controller.dispose();
        mocksControl.verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComponentConfigurationDefaultInitAttributes()
    {
        final CachingController controller = new CachingController();
        final Map<String, Object> attributes = Maps.newHashMap();

        controller.init(attributes);

        ProcessingResult resultByClass = controller.process(attributes,
            ComponentWithInitParameter.class);
        ProcessingResult resultById = controller.process(attributes,
            ComponentWithInitParameter.class.getName());

        assertThat(resultByClass.getAttributes()).isNotEmpty();
        assertEquals(resultByClass.getAttributes(), resultById.getAttributes());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComponentConfigurationDifferentIntiAttributes()
    {
        final CachingController controller = new CachingController();
        final Map<String, Object> attributes = Maps.newHashMap();

        final Map<String, Object> globalInitAttributes = Maps.newHashMap();
        final Map<String, Object> conf1Attributes = Maps.immutableMap("init",
            (Object) "v1");
        final Map<String, Object> conf2Attributes = Maps.immutableMap("init",
            (Object) "v2");

        controller.init(globalInitAttributes, new ProcessingComponentConfiguration(
            ComponentWithInitParameter.class, "conf1", conf1Attributes),
            new ProcessingComponentConfiguration(ComponentWithInitParameter.class,
                "conf2", conf2Attributes));

        ProcessingResult result1 = controller.process(attributes, "conf1");
        assertThat(result1.getAttributes()).contains(entry("result", "v1v1"));

        ProcessingResult result2 = controller.process(attributes, "conf2");
        assertThat(result2.getAttributes()).contains(entry("result", "v2v2"));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComponentConfigurationProcessingAttributeAtInitTime()
    {
        final CachingController controller = new CachingController();
        final Map<String, Object> attributes = Maps.newHashMap();

        final Map<String, Object> globalInitAttributes = Maps.newHashMap();
        final Map<String, Object> conf1Attributes = Maps.immutableMap("processing",
            (Object) "v1");
        final Map<String, Object> conf2Attributes = Maps.immutableMap("processing",
            (Object) "v2");

        controller.init(globalInitAttributes, new ProcessingComponentConfiguration(
            ComponentWithProcessingParameter.class, "conf1", conf1Attributes),
            new ProcessingComponentConfiguration(ComponentWithProcessingParameter.class,
                "conf2", conf2Attributes));

        ProcessingResult result1 = controller.process(attributes, "conf1");
        assertThat(result1.getAttributes()).contains(entry("result", "v1v1"));

        ProcessingResult result2 = controller.process(attributes, "conf2");
        assertThat(result2.getAttributes()).contains(entry("result", "v2v2"));
    }

    @Test(expected = ComponentInitializationException.class)
    @SuppressWarnings("unchecked")
    public void testComponentConfigurationDuplicateComponentId()
    {
        final CachingController controller = new CachingController();

        final Map<String, Object> globalInitAttributes = Maps.newHashMap();
        final Map<String, Object> conf1Attributes = Maps.immutableMap("init",
            (Object) "v1");
        final Map<String, Object> conf2Attributes = Maps.immutableMap("init",
            (Object) "v2");

        controller.init(globalInitAttributes, new ProcessingComponentConfiguration(
            ComponentWithInitParameter.class, "conf1", conf1Attributes),
            new ProcessingComponentConfiguration(ComponentWithInitParameter.class,
                "conf1", conf2Attributes));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCachedExecutionTimeMeasurement()
    {
        final long c1Time = 500;
        final long c2Time = 300;
        final long totalTime = c1Time + c2Time;
        final double tolerance = 0.3;

        mocksControl.checkOrder(false); // we don't care about the order of initialization
        cachedProcessingComponent1Mock.init();
        processingComponent2Mock.init();
        mocksControl.checkOrder(true);

        cachedProcessingComponent1Mock.beforeProcessing();
        cachedProcessingComponent1Mock.process();
        mocksControl.andAnswer(new DelayedAnswer<Object>(c1Time));
        cachedProcessingComponent1Mock.afterProcessing();

        processingComponent2Mock.beforeProcessing();
        processingComponent2Mock.process();
        mocksControl.andAnswer(new DelayedAnswer<Object>(c2Time));
        processingComponent2Mock.afterProcessing();

        mocksControl.checkOrder(false); // we don't care about the order of disposal
        cachedProcessingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        mocksControl.checkOrder(true);

        mocksControl.replay();

        attributes.put("data", "d");
        performProcessingAndDispose(CachedProcessingComponent1.class,
            ProcessingComponent2.class);

        checkTimes(c1Time, c2Time, totalTime, tolerance);
        mocksControl.verify();
    }

    @Test
    public void testNoStats()
    {
        final CachingControllerStatistics statistics = getCachingController()
            .getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.totalQueries).isEqualTo(0);
        assertThat(statistics.goodQueries).isEqualTo(0);
        assertThat(statistics.algorithmTimeAverageInWindow).isEqualTo(0);
        assertThat(statistics.algorithmTimeMeasurementsInWindow).isEqualTo(0);
        assertThat(statistics.sourceTimeAverageInWindow).isEqualTo(0);
        assertThat(statistics.sourceTimeMeasurementsInWindow).isEqualTo(0);
        assertThat(statistics.totalTimeAverageInWindow).isEqualTo(0);
        assertThat(statistics.totalTimeMeasurementsInWindow).isEqualTo(0);
        assertThat(statistics.cacheMisses).isEqualTo(0);
        assertThat(statistics.cacheTotalHits).isEqualTo(0);
        assertThat(statistics.cacheMemoryHits).isEqualTo(0);
        assertThat(statistics.cacheDiskHits).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStatsOneGoodQueryNotCached()
    {
        attributes.put("data", "d");

        mocksControl.checkOrder(false);
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        mocksControl.checkOrder(true);

        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        mocksControl.andAnswer(new DelayedAnswer<Object>(300));
        processingComponent1Mock.afterProcessing();

        processingComponent2Mock.beforeProcessing();
        processingComponent2Mock.process();
        mocksControl.andAnswer(new DelayedAnswer<Object>(300));
        processingComponent2Mock.afterProcessing();

        mocksControl.checkOrder(false);
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        mocksControl.checkOrder(true);
        
        mocksControl.replay();

        performProcessing(ProcessingComponent1.class, ProcessingComponent2.class);

        final CachingControllerStatistics statistics = getCachingController()
            .getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.totalQueries).isEqualTo(1);
        assertThat(statistics.goodQueries).isEqualTo(1);
        assertThat(statistics.algorithmTimeAverageInWindow).isGreaterThanOrEqualTo(200);
        assertThat(statistics.algorithmTimeMeasurementsInWindow).isEqualTo(1);
        assertThat(statistics.sourceTimeAverageInWindow).isGreaterThanOrEqualTo(200);
        assertThat(statistics.sourceTimeMeasurementsInWindow).isEqualTo(1);
        assertThat(statistics.totalTimeAverageInWindow).isGreaterThanOrEqualTo(400);
        assertThat(statistics.totalTimeMeasurementsInWindow).isEqualTo(1);
        assertThat(statistics.cacheMisses).isEqualTo(0);
        assertThat(statistics.cacheTotalHits).isEqualTo(0);
        assertThat(statistics.cacheMemoryHits).isEqualTo(0);
        assertThat(statistics.cacheDiskHits).isEqualTo(0);
        
        controller.dispose();
    }

    @Test
    public void testStatsOneGoodQueryCached()
    {
        attributes.put("data", "d");
        performProcessing(CachedProcessingComponent1.class);

        final CachingControllerStatistics statistics = getCachingController()
            .getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.totalQueries).isEqualTo(1);
        assertThat(statistics.goodQueries).isEqualTo(1);
        assertThat(statistics.cacheMisses).isEqualTo(1);
        assertThat(statistics.cacheTotalHits).isEqualTo(0);
        assertThat(statistics.cacheMemoryHits).isEqualTo(0);
        assertThat(statistics.cacheDiskHits).isEqualTo(0);
        
        controller.dispose();
    }

    @Test
    public void testStatsTwoGoodQueriesCached()
    {
        attributes.put("data", "d");
        performProcessing(CachedProcessingComponent1.class);
        attributes.put("data", "d");
        performProcessing(CachedProcessingComponent1.class);

        final CachingControllerStatistics statistics = getCachingController()
            .getStatistics();
        assertThat(statistics).isNotNull();
        assertThat(statistics.totalQueries).isEqualTo(2);
        assertThat(statistics.goodQueries).isEqualTo(2);
        assertThat(statistics.cacheMisses).isEqualTo(1);
        assertThat(statistics.cacheTotalHits).isEqualTo(1);
        assertThat(statistics.cacheMemoryHits).isEqualTo(1);
        assertThat(statistics.cacheDiskHits).isEqualTo(0);
        
        controller.dispose();
    }

    @Test(expected = RuntimeException.class)
    public void testStatsGoodQueryOneErrorQueryNotCached()
    {
        attributes.put("data", "d");

        processingComponent3Mock.init();
        processingComponent3Mock.beforeProcessing();
        processingComponent3Mock.process();
        processingComponent3Mock.afterProcessing();
        
        mocksControl.checkOrder(false);
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        mocksControl.checkOrder(true);
        
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        mocksControl.andThrow(new RuntimeException());
        processingComponent1Mock.afterProcessing();

        mocksControl.checkOrder(false);
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();
        mocksControl.checkOrder(true);
        
        mocksControl.replay();
        
        try
        {
            performProcessing(ProcessingComponent3.class);
            performProcessing(ProcessingComponent1.class, ProcessingComponent2.class);
        }
        finally
        {
            final CachingControllerStatistics statistics = getCachingController()
                .getStatistics();
            assertThat(statistics).isNotNull();
            assertThat(statistics.totalQueries).isEqualTo(2);
            assertThat(statistics.goodQueries).isEqualTo(1);
            assertThat(statistics.cacheMisses).isEqualTo(0);
            assertThat(statistics.cacheTotalHits).isEqualTo(0);
            assertThat(statistics.cacheMemoryHits).isEqualTo(0);
            assertThat(statistics.cacheDiskHits).isEqualTo(0);
            
            controller.dispose();
        }
    }

    private CachingController getCachingController()
    {
        return (CachingController) controller;
    }
}
