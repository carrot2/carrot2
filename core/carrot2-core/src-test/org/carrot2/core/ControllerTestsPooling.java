package org.carrot2.core;

import static org.easymock.EasyMock.isA;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.carrot2.util.attribute.Bindable;
import org.junit.Test;

/**
 *
 */
public abstract class ControllerTestsPooling extends ControllerTestsBase
{
    abstract Controller getPoolingController();

    boolean hasCaching()
    {
        return false;
    }

    @Override
    public Controller prepareController()
    {
        return getPoolingController();
    }

    /**
     * Verifies that components are not disposed of right after processing.
     */
    @Test
    public void testRepeatedExecution3Components()
    {
        component1Mock.init(isA(IControllerContext.class));
        invokeProcessing(component1Mock);
        component2Mock.init(isA(IControllerContext.class));
        invokeProcessing(component2Mock);
        component3Mock.init(isA(IControllerContext.class));
        invokeProcessing(component3Mock);

        invokeDisposal(component1Mock, component2Mock, component3Mock);

        mocksControl.replay();

        processingAttributes.put("instanceAttribute", "i");
        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessing(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));

        processingAttributes.put("data", "d");
        performProcessingDisposeAndVerifyMocks(Component2.class, Component3.class);
        assertEquals("dirir", resultAttributes.get("data"));
    }

    @Test
    public void testResettingPrimitiveAttribute()
    {
        component1Mock.init(isA(IControllerContext.class));
        invokeProcessing(component1Mock);
        invokeProcessing(component1Mock);
        component1Mock.dispose();

        mocksControl.replay();

        processingAttributes.put("instanceAttribute", "i");
        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessing(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));

        // Clear attributes and check if they've been restored
        processingAttributes.clear();
        processingAttributes.put("data", "d");
        performProcessingDisposeAndVerifyMocks(Component1.class);
        assertEquals("di", resultAttributes.get("data"));
    }

    @Test
    public void testResettingReferenceAttribute()
    {
        // Capture the initial attribute value
        performProcessing(ComponentWithBindableReference.class);
        final BindableInstanceCounter initial = result.getAttribute("bindable");

        // Override with a different instance
        final BindableInstanceCounter overridden = new BindableInstanceCounter();
        processingAttributes.put("bindable", overridden);
        performProcessing(ComponentWithBindableReference.class);
        assertThat(result.getAttribute("bindable")).isSameAs(overridden);

        // Perform processing with no attributes. The initial value should be restored.
        processingAttributes.clear();
        performProcessingAndDispose(ComponentWithBindableReference.class);
        assertThat(result.getAttribute("bindable")).isSameAs(initial);
    }

    @Test(expected = ProcessingException.class)
    public void testResettingRequiredProcessingAttributeToNull()
    {
        component1Mock.init(isA(IControllerContext.class));
        invokeProcessing(component1Mock);
        // beforeProcessing will fail because of missing required attributes
        // afterProcessing() still will be performed
        component1Mock.afterProcessing();
        component1Mock.dispose();

        mocksControl.replay();

        processingAttributes.put("instanceAttribute", "i");
        processingAttributes.put("runtimeAttribute", "r");
        processingAttributes.put("data", "d");

        performProcessing(Component1.class);
        assertEquals("dir", resultAttributes.get("data"));

        // Clear attributes and check if they've been restored
        processingAttributes.clear();

        // This processing will throw an exception -- required attribute not provided.
        // This means the attribute has been restored to null.
        performProcessingDisposeAndVerifyMocks(Component1.class);
    }

    /**
     * Verifies that the controller does not needlessly create instances of non-primitive
     * attributes. This can happen if an init-time non-primitive attribute is to be set by
     * its class (which is instantiated by the binder). If the attribute class is provided
     * at init-time, no new instances should be created at processing time.
     */
    @Test
    public void testComponentConfigurationInitProcessingAttributeCreation()
    {
        BindableInstanceCounter.createdInstances = 0;
        initAttributes.put("initProcessing", BindableInstanceCounter.class);

        assertThat(BindableInstanceCounter.createdInstances).isEqualTo(0);

        performProcessing(ComponentWithInitProcessingInputReferenceAttribute.class);
        performProcessing(ComponentWithInitProcessingInputReferenceAttribute.class);
        assertThat(BindableInstanceCounter.createdInstances).isEqualTo(
            hasCaching() ? 2 : 1);

        processingAttributes.put("initProcessing", BindableInstanceCounter.class);
        performProcessing(ComponentWithInitProcessingInputReferenceAttribute.class);
        performProcessingAndDispose(ComponentWithInitProcessingInputReferenceAttribute.class);
        assertThat(BindableInstanceCounter.createdInstances).isEqualTo(
            hasCaching() ? 2 : 3);
    }

    @Test
    public void testComponentInstanceReused()
    {
        ComponentWithInstanceCounter.instanceCount = 0;
        performProcessing(ComponentWithInstanceCounter.class);
        assertThat(ComponentWithInstanceCounter.instanceCount).isEqualTo(1);
        performProcessing(ComponentWithInstanceCounter.class);
        performProcessing(ComponentWithInstanceCounter.class);
        performProcessing(ComponentWithInstanceCounter.class);
        assertThat(ComponentWithInstanceCounter.instanceCount).isEqualTo(1);
        performProcessingAndDispose(ComponentWithInstanceCounter.class);
        assertThat(ComponentWithInstanceCounter.instanceCount).isEqualTo(1);
    }
    
    @Bindable
    public static class ComponentWithInstanceCounter extends ProcessingComponentBase
    {
        static int instanceCount = 0;

        public ComponentWithInstanceCounter()
        {
            synchronized (ComponentWithInstanceCounter.class)
            {
                instanceCount++;
            }
        }
    }
}
