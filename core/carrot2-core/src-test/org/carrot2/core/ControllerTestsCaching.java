
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

import static org.easymock.EasyMock.isA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Required;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Tests caching functionality of a {@link Controller}.
 */
public abstract class ControllerTestsCaching extends ControllerTestsBase
{
    /**
     * Returns a controller that implements basic processing and results caching 
     * functionality. 
     */
    @SuppressWarnings("unchecked")
    public abstract Controller getCachingController(Class<? extends IProcessingComponent>... cachedComponentClasses);

    /**
     * @see ControllerTestsCaching#testConcurrentDocumentModifications()
     */
    @Bindable
    public static class ConcurrentComponent1 extends ProcessingComponentBase
    {
        volatile static CountDownLatch latch1;
        volatile static CountDownLatch latch2;

        @Processing
        @Input
        @Required
        @Attribute(key = AttributeNames.DOCUMENTS)
        public List<Document> documents;

        @Override @SuppressWarnings("unused")
        public void process() throws ProcessingException
        {
            /*
             * Iterate over documents' fields, slowly...
             */
            try
            {
                for (Document d : documents)
                {
                    for (Map.Entry<String, Object> f : d.getFields().entrySet())
                    {
                        latch1.countDown();
                        latch2.await();
                    }
                }
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @see ControllerTestsCaching#testConcurrentDocumentModifications()
     */
    @Bindable
    public static class ConcurrentComponent2 extends ConcurrentComponent1
    {
        @Override
        public void process() throws ProcessingException
        {
            try
            {
                latch1.await();
                for (Document d : documents)
                {
                    d.setField("new-field", new Object());
                }
                latch2.countDown();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Test @Ignore("Demonstrates concurrent modification exceptions if documents " +
    		"are shared between components and processing chains.")
    public void testConcurrentDocumentModifications() throws Exception
    {
        final Controller c = prepareController();

        final HashMap<String, Object> attrs = Maps.newHashMap();
        attrs.put(AttributeNames.DOCUMENTS, Arrays.asList(new Document("title", "summary")));

        ConcurrentComponent1.latch1 = new CountDownLatch(1);
        ConcurrentComponent1.latch2 = new CountDownLatch(1);

        Thread t = new Thread() {
            public void run() {
                c.process(attrs, ConcurrentComponent2.class);
            }
        };

        try {
            t.start();
            c.process(attrs, ConcurrentComponent1.class);
        } finally {
            t.join();
            c.dispose();
        }
    }    

    @Before
    public void disableOrderChecking()
    {
        if (!isPooling())
        {
            mocksControl.checkOrder(false);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Controller prepareController()
    {
        return getCachingController(IProcessingComponent.class);
    }

    @Test
    public void testCachingEqualCacheKeys()
    {
        invokeInitForCache(component1Mock);

        invokeProcessingWithInit(component1Mock);
        // second query runs entirely from cache
        invokeDisposal(component1Mock);

        mocksControl.replay();

        processingAttributes.put("instanceAttribute", "i");
        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessing(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));

        processingAttributes.put("data", "d");
        performProcessingDisposeAndVerifyMocks(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));
    }

    @Test
    public void testCachingDifferentCacheKeys()
    {
        invokeInitForCache(component1Mock);
        invokeProcessingWithInit(component1Mock);
        invokeDisposalForNoPool(component1Mock);
        invokeProcessingWithInitForNoPool(component1Mock);
        invokeDisposal(component1Mock);

        mocksControl.replay();

        processingAttributes.put("instanceAttribute", "i");
        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessing(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));

        processingAttributes.put("data", "d");
        processingAttributes.put("runtimeAttribute", "z");
        performProcessingDisposeAndVerifyMocks(Component1.class);
        assertEquals("diz", resultAttributes.get("data"));
    }
    
    @Test
    public void testCachingReuseResultsInDifferentComponentPipeline()
    {
        invokeInitForCache(component1Mock, component2Mock);
        invokeProcessingWithInit(component1Mock);

        // Next Component1 results come from the cache
        
        invokeProcessingWithInit(component2Mock);
        
        invokeDisposal(component1Mock, component2Mock);
        
        mocksControl.replay();
        
        processingAttributes.put("instanceAttribute", "i");
        processingAttributes.put("runtimeAttribute", "v");
        processingAttributes.put("data", "d");
        
        performProcessing(Component1.class);
        assertEquals("div", resultAttributes.get("data"));
        
        processingAttributes.put("data", "d");
        processingAttributes.put("runtimeAttribute", "v");
        performProcessingDisposeAndVerifyMocks(Component1.class, Component2.class);
        assertEquals("diviv", resultAttributes.get("data"));
    }

    @Test
    public void testCachingInitAttributesIgnoredInCacheKey()
    {
        invokeInitForCache(component1Mock);
        invokeProcessingWithInit(component1Mock);
        // second query runs entirely from cache
        invokeDisposal(component1Mock);

        mocksControl.replay();

        processingAttributes.put("instanceAttribute", "i");
        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessing(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));

        // Init attribute should be ignored during processing
        processingAttributes.put("instanceAttribute", "j");
        processingAttributes.put("data", "d");
        performProcessingDisposeAndVerifyMocks(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));
    }

    
    @Test
    @SuppressWarnings("unchecked")
    public void testOutputAttributesWithNullValuesOneComponentCached()
    {
        this.controller = getCachingController(ComponentWithInputOutputAttributes1.class);
        this.controller.init(Maps.<String, Object> newHashMap());

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
    
    private void invokeProcessingWithInitForNoPool(IProcessingComponent... components)
    {
        if (isPooling())
        {
            invokeProcessing(components);
        }
        else
        {
            invokeProcessingWithInit(components);
        }
    }

    private void invokeDisposalForNoPool(IProcessingComponent... components)
    {
        if (!isPooling())
        {
            invokeDisposal(components);
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
        if (!isPooling())
        {
            for (IProcessingComponent component : components)
            {
                component.init(isA(IControllerContext.class));
                component.dispose();
            }
        }
    }
}
