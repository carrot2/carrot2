
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

import static org.easymock.EasyMock.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.easymock.*;
import org.junit.*;

import com.google.common.collect.Maps;

/**
 * Base test cases that each implementation of {@link IController} must pass.
 */
public abstract class ControllerTestBase
{
    protected IMocksControl mocksControl;

    protected IProcessingComponent processingComponent1Mock;
    protected IProcessingComponent processingComponent2Mock;
    protected IProcessingComponent processingComponent3Mock;
    protected IProcessingComponent processingComponent4Mock;

    protected IControllerContextListener contextListenerMock;

    protected IController controller;

    protected Map<String, Object> processingAttributes;

    protected abstract IController createController();

    @Bindable
    public static class ProcessingComponent1 extends DelegatingProcessingComponent
        implements IDocumentSource
    {
        @Init
        @Input
        @Attribute(key = "delegate1")
        @ImplementingClasses(classes =
        {
            IProcessingComponent.class
        }, strict = false)
        protected IProcessingComponent delegate1;

        @Override
        IProcessingComponent getDelegate()
        {
            return delegate1;
        }
    }

    @Bindable
    public static class ProcessingComponent2 extends DelegatingProcessingComponent
        implements IClusteringAlgorithm
    {
        @Init
        @Input
        @Attribute(key = "delegate2")
        @ImplementingClasses(classes =
        {
            IProcessingComponent.class
        }, strict = false)
        protected IProcessingComponent delegate2;

        @Override
        IProcessingComponent getDelegate()
        {
            return delegate2;
        }
    }

    @Bindable
    public static class ProcessingComponent3 extends DelegatingProcessingComponent
    {
        @Init
        @Input
        @Attribute(key = "delegate3")
        @ImplementingClasses(classes =
        {
            IProcessingComponent.class
        }, strict = false)
        protected IProcessingComponent delegate3;

        @Override
        IProcessingComponent getDelegate()
        {
            return delegate3;
        }
    }

    @Bindable
    public static class ProcessingComponent4 extends DelegatingProcessingComponent
    {
        @Init
        @Input
        @Attribute(key = "delegate4")
        @ImplementingClasses(classes =
        {
            IProcessingComponent.class
        }, strict = false)
        protected IProcessingComponent delegate4;

        @Init
        @Input
        @Attribute(key = "contextListener")
        @ImplementingClasses(classes =
        {
            IControllerContextListener.class
        }, strict = false)
        protected IControllerContextListener contextListener;

        @Override
        public void init(IControllerContext context)
        {
            super.init(context);

            context.addListener(contextListener);
        }

        @Override
        IProcessingComponent getDelegate()
        {
            return delegate4;
        }
    }
    
    @Bindable
    public static class ProcessingComponentWithoutDefaultConstructor extends
        ProcessingComponentBase
    {
        private ProcessingComponentWithoutDefaultConstructor()
        {
        }
    }
    
    @Bindable
    public static class ProcessingComponent5_1 extends ProcessingComponentBase
        implements IDocumentSource
    {
        @Processing @Input @Output
        @Attribute(key = "key1")
        protected String key1;
        
        @Processing @Input @Output
        @Attribute(key = "key2")
        protected String key2;
        
        @Override
        public void process() throws ProcessingException
        {
            super.process();
            key2 = "value";
        }
    }

    @Bindable
    public static class ProcessingComponent5_2 extends ProcessingComponentBase
        implements IClusteringAlgorithm
    {
        @Processing @Input @Output
        @Attribute(key = "key1")
        protected String key1 = "default";

        @Processing @Input @Output
        @Attribute(key = "key2")
        protected String key2 = "default";
    }

    @Before
    public void init()
    {
        mocksControl = createStrictControl();

        processingComponent1Mock = mocksControl.createMock(IProcessingComponent.class);
        processingComponent2Mock = mocksControl.createMock(IProcessingComponent.class);
        processingComponent3Mock = mocksControl.createMock(IProcessingComponent.class);
        processingComponent4Mock = mocksControl.createMock(IProcessingComponent.class);

        contextListenerMock = mocksControl.createMock(IControllerContextListener.class);

        Map<String, Object> initAttributes = Maps.newHashMap();
        initAttributes.put("delegate1", processingComponent1Mock);
        initAttributes.put("delegate2", processingComponent2Mock);
        initAttributes.put("delegate3", processingComponent3Mock);
        initAttributes.put("delegate4", processingComponent4Mock);

        initAttributes.put("contextListener", contextListenerMock);

        initAttributes.put("instanceAttribute", "i");
        beforeControllerInit(initAttributes);

        controller = createController();
        controller.init(initAttributes);

        processingAttributes = Maps.newHashMap();
    }
    
    @After
    public void cleanup()
    {
        if (controller != null)
        {
            controller.dispose();
            controller = null;
        }
    }

    protected void beforeControllerInit(Map<String, Object> initAttributes)
    {
    }

    @Test
    public void testOutputAttributesWithNullValues()
    {
        performProcessing(
            ProcessingComponent5_1.class,
            ProcessingComponent5_2.class);
        
        Assert.assertEquals("default", processingAttributes.get("key1"));
        Assert.assertEquals("value", processingAttributes.get("key2"));
        
        processingAttributes.clear();
        processingAttributes.put("key1", null);
        processingAttributes.put("key2", null);
        
        performProcessing(
            ProcessingComponent5_1.class,
            ProcessingComponent5_2.class);
        
        Assert.assertEquals(null, processingAttributes.get("key1"));
        Assert.assertEquals("value", processingAttributes.get("key2"));
    }
    
    @Test
    public void testNormalExecution1Component()
    {
        processingComponent1Mock.init(isA(IControllerContext.class));
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.dispose();

        mocksControl.replay();

        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessingAndDispose(ProcessingComponent1.class);

        assertEquals("dir", processingAttributes.get("data"));
        mocksControl.verify();
    }

    @Test
    public void testNormalExecution3Components()
    {
        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init(isA(IControllerContext.class));
        processingComponent2Mock.init(isA(IControllerContext.class));
        processingComponent3Mock.init(isA(IControllerContext.class));
        mocksControl.checkOrder(true);

        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();

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

        processingAttributes.put("instanceAttribute", "i");
        processingAttributes.put("runtimeAttribute", "r");

        processingAttributes.put("data", "d");

        performProcessingAndDispose(ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);

        assertEquals("diririr", processingAttributes.get("data"));
        mocksControl.verify();
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionWhileCreatingInstances()
    {
        // Depending on implementation, the controller may or may not create/ initialize
        // the instance of the first component. That doesn't make a big difference.
        processingComponent1Mock.init(isA(IControllerContext.class));
        mocksControl.times(0, 1);
        processingComponent1Mock.dispose();
        mocksControl.times(0, 1);
        mocksControl.replay();

        try
        {
            performProcessingAndDispose(ProcessingComponent1.class,
                ProcessingComponentWithoutDefaultConstructor.class);
        }
        finally
        {
            mocksControl.verify();
        }
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionBeforeProcessing()
    {
        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init(isA(IControllerContext.class));
        processingComponent2Mock.init(isA(IControllerContext.class));
        processingComponent3Mock.init(isA(IControllerContext.class));
        mocksControl.checkOrder(true);

        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        processingComponent2Mock.beforeProcessing();
        mocksControl.andThrow(new ProcessingException("no message"));
        processingComponent2Mock.afterProcessing();

        mocksControl.checkOrder(false); // we don't care about the order of disposal
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();
        mocksControl.checkOrder(true);

        mocksControl.replay();

        processingAttributes.put("data", "d");
        try
        {
            performProcessingAndDispose(ProcessingComponent1.class,
                ProcessingComponent2.class, ProcessingComponent3.class);
        }
        finally
        {
            mocksControl.verify();
        }
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionDuringProcessing()
    {
        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init(isA(IControllerContext.class));
        processingComponent2Mock.init(isA(IControllerContext.class));
        processingComponent3Mock.init(isA(IControllerContext.class));
        mocksControl.checkOrder(true);

        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        processingComponent2Mock.beforeProcessing();
        processingComponent2Mock.process();
        mocksControl.andThrow(new ProcessingException("no message"));
        processingComponent2Mock.afterProcessing();

        mocksControl.checkOrder(false); // we don't care about the order of disposal
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();
        mocksControl.checkOrder(true);

        mocksControl.replay();

        processingAttributes.put("data", "d");
        try
        {
            performProcessingAndDispose(ProcessingComponent1.class,
                ProcessingComponent2.class, ProcessingComponent3.class);
        }
        finally
        {
            mocksControl.verify();
        }
    }

    @Test(expected = ComponentInitializationException.class)
    public void testExceptionWhileInit()
    {
        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init(isA(IControllerContext.class));
        processingComponent2Mock.init(isA(IControllerContext.class));
        mocksControl.andThrow(new ComponentInitializationException((String) null));
        mocksControl.checkOrder(false); // we don't care about the order of disposal
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        mocksControl.checkOrder(true);
        mocksControl.replay();

        try
        {
            performProcessingAndDispose(ProcessingComponent1.class,
                ProcessingComponent2.class, ProcessingComponent3.class);
        }
        finally
        {
            mocksControl.verify();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNormalExecutionTimeMeasurement()
    {
        final long c1Time = 500;
        final long c2Time = 1000;
        final long c3Time = 1500;
        final long totalTime = c1Time + c2Time + c3Time;
        final double tolerance = 0.3;

        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init(isA(IControllerContext.class));
        processingComponent2Mock.init(isA(IControllerContext.class));
        processingComponent3Mock.init(isA(IControllerContext.class));
        mocksControl.checkOrder(true);

        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        mocksControl.andAnswer(new DelayedAnswer<Object>(c1Time));
        processingComponent1Mock.afterProcessing();

        processingComponent2Mock.beforeProcessing();
        mocksControl.andAnswer(new DelayedAnswer<Object>(c2Time));
        processingComponent2Mock.process();
        processingComponent2Mock.afterProcessing();

        processingComponent3Mock.beforeProcessing();
        processingComponent3Mock.process();
        processingComponent3Mock.afterProcessing();
        mocksControl.andAnswer(new DelayedAnswer<Object>(c3Time));

        mocksControl.checkOrder(false); // we don't care about the order of disposal
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();
        mocksControl.checkOrder(true);

        mocksControl.replay();

        processingAttributes.put("data", "d");
        performProcessingAndDispose(ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);

        checkTimes(c1Time, c2Time, totalTime, tolerance);
        mocksControl.verify();
    }

    @Test(expected = ProcessingException.class)
    @SuppressWarnings("unchecked")
    public void testTimeMeasurementWithException()
    {
        final long c1Time = 500;
        final long c2Time = 1000;
        final long totalTime = c1Time + c2Time;
        final double tolerance = 0.3;

        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init(isA(IControllerContext.class));
        processingComponent2Mock.init(isA(IControllerContext.class));
        processingComponent3Mock.init(isA(IControllerContext.class));
        mocksControl.checkOrder(true);

        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        mocksControl.andAnswer(new DelayedAnswer<Object>(c1Time));
        processingComponent1Mock.afterProcessing();

        processingComponent2Mock.beforeProcessing();
        mocksControl.andAnswer(new DelayedAnswer<Object>(c2Time));
        processingComponent2Mock.process();
        processingComponent2Mock.afterProcessing();

        processingComponent3Mock.beforeProcessing();
        mocksControl.andThrow(new ProcessingException("no message"));
        processingComponent3Mock.afterProcessing();

        mocksControl.checkOrder(false); // we don't care about the order of disposal
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();
        mocksControl.checkOrder(true);

        mocksControl.replay();

        try
        {
            processingAttributes.put("data", "d");
            performProcessingAndDispose(ProcessingComponent1.class,
                ProcessingComponent2.class, ProcessingComponent3.class);
        }
        finally
        {
            checkTimes(c1Time, c2Time, totalTime, tolerance);
            mocksControl.verify();
        }
    }

    @Test
    public void testContextDisposal()
    {
        processingComponent4Mock.init(isA(IControllerContext.class));
        processingComponent4Mock.beforeProcessing();
        processingComponent4Mock.process();
        processingComponent4Mock.afterProcessing();
        
        processingComponent4Mock.dispose();
        expectLastCall().times(0, 1);

        contextListenerMock.beforeDisposal(isA(IControllerContext.class));
        
        mocksControl.replay();

        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");
        
        performProcessingAndDispose(ProcessingComponent4.class);

        mocksControl.verify();
    }

    protected void checkTimes(final long c1Time, final long c2Time, final long totalTime,
        final double tolerance)
    {
        assertThat(
            ((Long) (processingAttributes.get(AttributeNames.PROCESSING_TIME_TOTAL))).longValue())
            .as("Total time").isLessThan((long) (totalTime * (1 + tolerance)))
            .isGreaterThan((long) (totalTime * (1 - tolerance)));

        assertThat(
            ((Long) (processingAttributes.get(AttributeNames.PROCESSING_TIME_SOURCE))).longValue())
            .as("Source time").isLessThan((long) (c1Time * (1 + tolerance)))
            .isGreaterThan((long) (c1Time * (1 - tolerance)));

        assertThat(
            ((Long) (processingAttributes.get(AttributeNames.PROCESSING_TIME_ALGORITHM)))
                .longValue()).as("Alorithm time").isLessThan(
            (long) (c2Time * (1 + tolerance))).isGreaterThan(
            (long) (c2Time * (1 - tolerance)));
    }

    protected void performProcessing(Class<?>... classes)
    {
        controller.process(processingAttributes, classes);
    }

    protected void performProcessingAndDispose(Class<?>... classes)
    {
        try
        {
            performProcessing(classes);
        }
        finally
        {
            controller.dispose();
            controller = null;
        }
    }

    protected static class DelayedAnswer<T> implements IAnswer<T>
    {
        private long delayMilis;

        public DelayedAnswer(long delayMilis)
        {
            this.delayMilis = delayMilis;
        }

        public T answer() throws Throwable
        {
            Thread.sleep(delayMilis);
            return null;
        }
    }
}
