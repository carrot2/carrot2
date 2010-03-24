package org.carrot2.core;

import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;

import org.junit.*;

import com.google.common.collect.Maps;

/**
 *
 */
public abstract class ControllerTestsCaching extends ControllerTestsBase
{
    abstract Controller getCachingController(Class<? extends IProcessingComponent>... cachedComponentClasses);

    boolean hasPooling()
    {
        return false;
    }

    @Before
    public void disableOrderChecking()
    {
        if (!hasPooling())
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
        if (hasPooling())
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
        if (!hasPooling())
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
        if (!hasPooling())
        {
            for (IProcessingComponent component : components)
            {
                component.init(isA(IControllerContext.class));
                component.dispose();
            }
        }
    }
}
