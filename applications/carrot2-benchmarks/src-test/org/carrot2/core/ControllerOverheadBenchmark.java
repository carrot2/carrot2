
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
import java.util.concurrent.*;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.junit.*;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.h2.*;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * This class measures the overhead added by the {@link Controller} in various
 * configurations. Processing in the components is relatively simple, so what we're
 * attempting to measure here is the impact of extra processing and synchronizations
 * within the controller itself.
 */
@AxisRange(min = 0)
@BenchmarkMethodChart(filePrefix = "individual")
@BenchmarkHistoryChart(filePrefix = "history")
@BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
public class ControllerOverheadBenchmark extends AbstractBenchmark
{
    // Controllers under tests
    static final Controller simpleController = ControllerFactory.createSimple();
    static final Controller poolingController = ControllerFactory.createPooling();
    static final Controller cachingOffController = ControllerFactory.createCaching();
    static final Controller cachingOnController = ControllerFactory
        .createCaching(IProcessingComponent.class);
    static final Controller poolingCachingOffController = ControllerFactory
        .createCachingPooling();
    static final Controller poolingCachingOnController = ControllerFactory
        .createCachingPooling(IProcessingComponent.class);

    // Test callables
    static final List<Map<String, Object>> processingAttributeMaps = Lists.newArrayList();
    static final Map<String, List<Callable<String>>> callables = Maps.newHashMap();

    // Executor
    static final ExecutorService executorService = Executors.newFixedThreadPool(20);
    static final int internalRounds = 100;

    @BeforeClass
    public static void prepareRunnables()
    {
        final Random random = new Random(0);
        for (int i = 0; i < 100; i++)
        {
            final Map<String, Object> attributes = Maps.newHashMap();
            attributes.put("int1", random.nextInt(100));
            attributes.put("int2", random.nextInt(100));
            attributes.put("double1", random.nextDouble());
            attributes.put("double2", random.nextDouble());
            attributes.put("data", "a"
                + Integer.toString(random.nextInt(20 + random.nextInt(10))));
            attributes.put("boolean1", random.nextBoolean());
            attributes.put("boolean2", random.nextBoolean());
            attributes.put("in1", null);
            processingAttributeMaps.add(attributes);
        }

        callables.put("simple", createCallables(simpleController));
        callables.put("pooling", createCallables(poolingController));
        callables.put("cachingOff", createCallables(cachingOffController));
        callables.put("cachingOn", createCallables(cachingOnController));
        callables.put("poolingCachingOff", createCallables(poolingCachingOffController));
        callables.put("poolingCachingOn", createCallables(poolingCachingOnController));
    }

    private static List<Callable<String>> createCallables(Controller controller)
    {
        final List<Callable<String>> callables = Lists.newArrayList();
        for (int r = 0; r < internalRounds; r++)
        {
            for (Map<String, Object> attributes : processingAttributeMaps)
            {
                callables.add(createCallable(attributes, controller));
            }
        }

        return callables;
    }

    private static Callable<String> createCallable(final Map<String, Object> attributes,
        final Controller controller)
    {
        return new Callable<String>()
        {
            public String call() throws Exception
            {
                return controller.process(attributes, ProcessingComponent1.class,
                    ProcessingComponent2.class).getAttribute("result");
            }
        };
    }

    @AfterClass
    public static void disposeControllers()
    {
        simpleController.dispose();
        poolingController.dispose();
        cachingOffController.dispose();
        cachingOnController.dispose();
        poolingCachingOffController.dispose();
        poolingCachingOnController.dispose();
    }

    @AfterClass
    public static void shutDownExecutor() throws InterruptedException
    {
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void testSimpleController() throws InterruptedException
    {
        testWithController("simple");
    }

    @Test
    public void testPoolingController() throws InterruptedException
    {
        testWithController("pooling");
    }

    @Test
    public void testCachingOnController() throws InterruptedException
    {
        testWithController("cachingOn");
    }

    @Test
    public void testCachingOffController() throws InterruptedException
    {
        testWithController("cachingOff");
    }

    @Test
    public void testPoolingCachingOnController() throws InterruptedException
    {
        testWithController("poolingCachingOff");
    }

    @Test
    public void testPoolingCachingOffController() throws InterruptedException
    {
        testWithController("poolingCachingOff");
    }

    private void testWithController(String controllerName) throws InterruptedException
    {
        executorService.invokeAll(callables.get(controllerName));
    }

    @Bindable
    public static class ProcessingComponent1 extends ProcessingComponentBase
    {
        @Processing
        @Input
        @Attribute(key = "int1")
        public int int1 = 0;

        @Processing
        @Input
        @Attribute(key = "double1")
        public double double1 = 1.0;

        @Processing
        @Input
        @Output
        @Attribute(key = "data")
        public String string1;

        @Processing
        @Input
        @Attribute(key = "boolean1")
        public boolean boolean1 = false;

        @Processing
        @Input
        @Attribute(key = "in1")
        public List<String> inAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "out2")
        public List<String> outAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "debug2")
        public List<String> debugAttribute = new ArrayList<String>();

        @Override
        public void process() throws ProcessingException
        {
            string1 += join("-", string1, int1, double1, boolean1);
        }
    }

    @Bindable
    public static class ProcessingComponent2 extends ProcessingComponentBase
    {
        @Processing
        @Input
        @Attribute(key = "int2")
        public int int2 = 0;

        @Processing
        @Input
        @Attribute(key = "double2")
        public double double2 = 1.0;

        @Processing
        @Input
        @Attribute(key = "data")
        public String string2;

        @Processing
        @Output
        @Attribute(key = "result")
        public String result;

        @Processing
        @Input
        @Attribute(key = "boolean2")
        public boolean boolean2 = false;

        @Processing
        @Input
        @Attribute(key = "in2")
        public List<String> inAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "out2")
        public List<String> outAttribute = new ArrayList<String>();

        @Processing
        @Output
        @Attribute(key = "debug2")
        public List<String> debugAttribute = new ArrayList<String>();

        @Override
        public void process() throws ProcessingException
        {
            result = join("-", string2, int2, double2, boolean2);
        }
    }

    private static String join(String separator, Object... objects)
    {
        final StringBuilder b = new StringBuilder(objects[0].toString());
        for (int i = 1; i < objects.length; i++)
        {
            b.append(separator);
            b.append(objects[i]);
        }
        return b.toString();
    }
}
