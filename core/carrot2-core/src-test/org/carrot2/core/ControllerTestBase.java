package org.carrot2.core;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.carrot2.core.attribute.Init;
import org.carrot2.util.attribute.*;
import org.easymock.IMocksControl;
import org.junit.*;

import com.google.common.collect.Maps;

/**
 * Base test cases that each implementation of {@link Controller} must pass.
 */
public abstract class ControllerTestBase
{
    protected IMocksControl mocksControl;

    protected ProcessingComponent processingComponent1Mock;
    protected ProcessingComponent processingComponent2Mock;
    protected ProcessingComponent processingComponent3Mock;

    protected Controller controller;

    protected Map<String, Object> attributes;

    protected abstract Controller createController();

    @Bindable
    public static class ProcessingComponent1 extends DelegatingProcessingComponent
    {
        @Init
        @Input
        @Attribute(key = "delegate1")
        protected ProcessingComponent delegate1;

        @Override
        ProcessingComponent getDelegate()
        {
            return delegate1;
        }
    }

    @Bindable
    public static class ProcessingComponent2 extends DelegatingProcessingComponent
    {
        @Init
        @Input
        @Attribute(key = "delegate2")
        protected ProcessingComponent delegate2;

        @Override
        ProcessingComponent getDelegate()
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
        protected ProcessingComponent delegate3;

        @Override
        ProcessingComponent getDelegate()
        {
            return delegate3;
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

    @Before
    public void init()
    {
        mocksControl = createStrictControl();

        processingComponent1Mock = mocksControl.createMock(ProcessingComponent.class);
        processingComponent2Mock = mocksControl.createMock(ProcessingComponent.class);
        processingComponent3Mock = mocksControl.createMock(ProcessingComponent.class);

        Map<String, Object> initAttributes = Maps.newHashMap();
        initAttributes.put("delegate1", processingComponent1Mock);
        initAttributes.put("delegate2", processingComponent2Mock);
        initAttributes.put("delegate3", processingComponent3Mock);
        initAttributes.put("instanceAttribute", "i");

        controller = createController();
        controller.init(initAttributes);

        attributes = Maps.newHashMap();
    }

    @After
    public void verifyMocks()
    {
        mocksControl.verify();
    }

    @Test
    public void testNormalExecution1Component()
    {
        processingComponent1Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
        processingComponent1Mock.dispose();

        mocksControl.replay();

        attributes.put("runtimeAttribute", "r");
        attributes.put("data", "d");

        performProcessingAndDispose(ProcessingComponent1.class);

        assertEquals("dir", attributes.get("data"));
    }

    @Test
    public void testNormalExecution3Components()
    {
        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        processingComponent3Mock.init();
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

        attributes.put("instanceAttribute", "i");
        attributes.put("runtimeAttribute", "r");

        attributes.put("data", "d");

        performProcessingAndDispose(ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);

        assertEquals("diririr", attributes.get("data"));
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionWhileCreatingInstances()
    {
        // Depending on implementation, the controller may or may not create/ initialize
        // the instance of the first component. That doesn't make a big difference.
        processingComponent1Mock.init();
        mocksControl.times(0, 1);
        processingComponent1Mock.dispose();
        mocksControl.times(0, 1);
        mocksControl.replay();

        performProcessingAndDispose(ProcessingComponent1.class,
            ProcessingComponentWithoutDefaultConstructor.class);
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionBeforeProcessing()
    {
        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        processingComponent3Mock.init();
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

        performProcessingAndDispose(ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionDuringProcessing()
    {
        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        processingComponent3Mock.init();
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

        performProcessingAndDispose(ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);
    }

    @Test(expected = ComponentInitializationException.class)
    public void testExceptionWhileInit()
    {
        mocksControl.checkOrder(false); // we don't care about the order of initialization
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        mocksControl.andThrow(new ComponentInitializationException((String) null));
        mocksControl.checkOrder(false); // we don't care about the order of disposal
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        mocksControl.checkOrder(true);
        mocksControl.replay();

        performProcessingAndDispose(ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);
    }

    protected void performProcessing(Class<?>... classes)
    {
        controller.process(attributes, classes);
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
        }
    }
}
