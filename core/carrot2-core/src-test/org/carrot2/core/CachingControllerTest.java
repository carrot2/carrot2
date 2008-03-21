package org.carrot2.core;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.attribute.Init;
import org.carrot2.util.attribute.*;
import org.junit.Ignore;
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
    {
        @Init
        @Input
        @Attribute(key = "cachedDelegate1")
        protected ProcessingComponent cachedDelegate1;

        @Override
        ProcessingComponent getDelegate()
        {
            return cachedDelegate1;
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

    /**
     * TODO: This test is ignored for now. When the number of threads is > 1, it sometimes
     * reports that the number of calls to afterProcessing() is smaller than required by
     * 1. Simple debugging with System.outs shows that the number is correct, so it might
     * be EasyMock's proxy that is not thread-safe and doesn't record invocations
     * accurately. Definitely worth checking at some point.
     */
    @Test
    @Ignore
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

    /**
     * TODO: This test is ignored for now. When the number of threads is > 1, it sometimes
     * reports that the number of calls to afterProcessing() is smaller than required by
     * 1. Simple debugging with System.outs shows that the number is correct, so it might
     * be EasyMock's proxy that is not thread-safe and doesn't record invocations
     * accurately. Definitely worth checking at some point.
     */
    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void testStress() throws InterruptedException, ExecutionException
    {
        final Random random = new Random();
        final int numberOfThreads = 5 + random.nextInt(5);
        final int numberOfQueries = 100 + random.nextInt(100);
        final String [] data = new String [numberOfQueries];
        final Set<String> uniqueQueries = Sets.newHashSet();
        for (int i = 0; i < data.length; i++)
        {
            data[i] = Integer.toString(random.nextInt(5));
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
                    localAttributes.put("delay", 100);
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
        executorService.awaitTermination(100, TimeUnit.SECONDS);

        controller.dispose();
        mocksControl.verify();
    }
}
