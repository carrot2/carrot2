package org.carrot2.core;

import static org.junit.Assert.assertEquals;

import org.carrot2.util.attribute.AttributeBindingException;
import org.junit.Test;

/**
 * Test cases for {@link CachingController}.
 */
public class CachingControllerTest extends ControllerTestBase
{
    @Override
    protected Controller createController()
    {
        return new CachingController();
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
        controller.dispose();
        assertEquals("di", attributes.get("data"));
    }

    @Test(expected = AttributeBindingException.class)
    public void testRestoringRequiredProcessingAttributeToNull()
    {
        processingComponent1Mock.init();
        processingComponent1Mock.beforeProcessing();
        processingComponent1Mock.process();
        processingComponent1Mock.afterProcessing();
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
    }
}
