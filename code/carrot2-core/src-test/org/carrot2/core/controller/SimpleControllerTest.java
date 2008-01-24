/**
 * 
 */
package org.carrot2.core.controller;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.easymock.IMocksControl;
import org.junit.*;

/**
 *
 */
public class SimpleControllerTest
{
    private IMocksControl mocksControl;

    private ProcessingComponent processingComponent1Mock;
    private ProcessingComponent processingComponent2Mock;
    private ProcessingComponent processingComponent3Mock;

    private SimpleController controller;

    private Map<String, Object> attributes;

    @Bindable
    public static class ProcessingComponent1 extends DelegatingProcessingComponent
    {
        @Init
        @Input
        @Attribute(key = "delegate1")
        private ProcessingComponent delegate1;

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
        private ProcessingComponent delegate2;

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
        private ProcessingComponent delegate3;

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

        processingComponent1Mock = mocksControl.createMock(DocumentSource.class);
        processingComponent2Mock = mocksControl.createMock(DocumentSource.class);
        processingComponent3Mock = mocksControl.createMock(DocumentSource.class);

        attributes = new HashMap<String, Object>();
        attributes.put("delegate1", processingComponent1Mock);
        attributes.put("delegate2", processingComponent2Mock);
        attributes.put("delegate3", processingComponent3Mock);

        controller = new SimpleController();
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

        attributes.put("instanceParameter", "i");
        attributes.put("runtimeParameter", "r");
        attributes.put("data", "d");

        controller.process(attributes, ProcessingComponent1.class);

        mocksControl.verify();

        assertEquals("dir", attributes.get("data"));
    }

    @Test
    public void testNormalExecution3Components()
    {
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        processingComponent3Mock.init();

        processingComponent1Mock.beforeProcessing();
        processingComponent2Mock.beforeProcessing();
        processingComponent3Mock.beforeProcessing();

        processingComponent1Mock.process();
        processingComponent2Mock.process();
        processingComponent3Mock.process();

        processingComponent1Mock.afterProcessing();
        processingComponent2Mock.afterProcessing();
        processingComponent3Mock.afterProcessing();

        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();

        mocksControl.replay();

        attributes.put("instanceParameter", "i");
        attributes.put("runtimeParameter", "r");

        attributes.put("data", "d");

        controller.process(attributes, ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);

        assertEquals("dir", attributes.get("data"));
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionWhileCreatingInstances()
    {
        mocksControl.replay();

        controller.process(attributes, ProcessingComponent1.class,
            ProcessingComponentWithoutDefaultConstructor.class);
    }

    // TODO: The tests below fail for me. Is there anything special
    // about them?

    @Test(expected = InitializationException.class)
    public void testExceptionWhileInit()
    {
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        mocksControl.andThrow(new InitializationException(null));
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();
        mocksControl.replay();

        controller.process(attributes, ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionBeforeProcessing()
    {
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        processingComponent3Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent2Mock.beforeProcessing();
        mocksControl.andThrow(new ProcessingException("no message"));
        processingComponent1Mock.afterProcessing();
        processingComponent2Mock.afterProcessing();
        processingComponent3Mock.afterProcessing();
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();
        mocksControl.replay();

        controller.process(attributes, ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);
    }

    @Test(expected = ProcessingException.class)
    public void testExceptionDuringProcessing()
    {
        processingComponent1Mock.init();
        processingComponent2Mock.init();
        processingComponent3Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent2Mock.beforeProcessing();
        processingComponent3Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent2Mock.process();
        mocksControl.andThrow(new ProcessingException("no message"));
        processingComponent1Mock.afterProcessing();
        processingComponent2Mock.afterProcessing();
        processingComponent3Mock.afterProcessing();
        processingComponent1Mock.dispose();
        processingComponent2Mock.dispose();
        processingComponent3Mock.dispose();
        mocksControl.replay();

        controller.process(attributes, ProcessingComponent1.class,
            ProcessingComponent2.class, ProcessingComponent3.class);
    }
}
