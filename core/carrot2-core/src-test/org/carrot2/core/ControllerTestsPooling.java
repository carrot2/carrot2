
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

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.annotations.ThreadSafe;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests pooling functionality of a {@link Controller}.
 */
public abstract class ControllerTestsPooling extends ControllerTestsBase
{
    /**
     * Returns a controller that implements basic processing and component instance
     * pooling functionality.
     */
    public abstract Controller getPoolingController();

    @BeforeClass
    public static void enableAssertions()
    {
        PoolingProcessingComponentManager.NonPrimitiveInputAttributesCheck.makeAssertion = true;
    }
    
    @AfterClass
    public static void disableAssertions()
    {
        PoolingProcessingComponentManager.NonPrimitiveInputAttributesCheck.makeAssertion = false;
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
        invokeInit(component1Mock);
        invokeProcessing(component1Mock);
        invokeInit(component2Mock);
        invokeProcessing(component2Mock);
        invokeInit(component3Mock);
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
        invokeInit(component1Mock);
        invokeProcessing(component1Mock);
        invokeProcessing(component1Mock);
        invokeDisposal(component1Mock);

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
        assertThat((Object) result.getAttribute("bindable")).isSameAs(overridden);

        // Perform processing with no attributes. The initial value should be restored.
        processingAttributes.clear();
        performProcessingAndDispose(ComponentWithBindableReference.class);
        assertThat((Object) result.getAttribute("bindable")).isSameAs(initial);
    }

    @Test(expected = ProcessingException.class)
    public void testResettingRequiredProcessingAttributeToNull()
    {
        invokeInit(component1Mock);
        invokeProcessing(component1Mock);
        // beforeProcessing will fail because of missing required attributes
        // afterProcessing() still will be performed
        component1Mock.afterProcessing();
        invokeDisposal(component1Mock);

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
        BindableInstanceCounter.reset();
        initAttributes.put("initProcessing", BindableInstanceCounter.class);

        assertThat(BindableInstanceCounter.createdInstances).isEqualTo(0);

        performProcessing(ComponentWithInitProcessingInputReferenceAttribute.class);
        performProcessing(ComponentWithInitProcessingInputReferenceAttribute.class);
        assertThat(BindableInstanceCounter.createdInstances).isEqualTo(
            isCaching() ? 2 : eagerlyInitializedInstances());

        processingAttributes.put("initProcessing", BindableInstanceCounter.class);
        performProcessing(ComponentWithInitProcessingInputReferenceAttribute.class);
        performProcessingAndDispose(ComponentWithInitProcessingInputReferenceAttribute.class);
        assertThat(BindableInstanceCounter.createdInstances).isEqualTo(
            isCaching() ? 2 : eagerlyInitializedInstances() + 3 - 1);
    }

    @Test
    public void testComponentInstanceReused()
    {
        ComponentWithInstanceCounter.reset();
        performProcessing(ComponentWithInstanceCounter.class);
        assertThat(ComponentWithInstanceCounter.instanceCount).isEqualTo(eagerlyInitializedInstances());
        performProcessing(ComponentWithInstanceCounter.class);
        performProcessing(ComponentWithInstanceCounter.class);
        performProcessing(ComponentWithInstanceCounter.class);
        assertThat(ComponentWithInstanceCounter.instanceCount).isEqualTo(eagerlyInitializedInstances());
        performProcessingAndDispose(ComponentWithInstanceCounter.class);
        assertThat(ComponentWithInstanceCounter.instanceCount).isEqualTo(eagerlyInitializedInstances());
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
        
        static void reset()
        {
            instanceCount = 0;
        }
    }

    @Bindable
    @ThreadSafe
    public static class ComponentWithNonPrimitiveAttributes extends ProcessingComponentBase
    {
        @Init
        @Processing
        @Input
        @Attribute(key = "threadSafe")
        @ImplementingClasses(classes = {}, strict = false)
        public ThreadSafeClass threadSafe = new ThreadSafeClass();

        @Init
        @Input
        @Attribute(key = "nonThreadSafeInit")
        @ImplementingClasses(classes = {}, strict = false)
        public NonThreadSafeClass nonThreadSafeInit = new NonThreadSafeClass();
        
        @Processing
        @Input
        @Attribute(key = "nonThreadSafeProcessing")
        @ImplementingClasses(classes = {}, strict = false)
        public NonThreadSafeClass nonThreadSafeProcessing = new NonThreadSafeClass();
    }
    
    @ThreadSafe
    public static class ThreadSafeClass 
    {
    }
    
    public static class NonThreadSafeClass
    {
    }

    @Test(expected = AssertionError.class)
    public void warningOnNonThreadSafeInitInputInstanceAttributeProvidedOnInit()
    {
        initAttributes.put("threadSafe", new ThreadSafeClass());
        initAttributes.put("nonThreadSafeInit", new NonThreadSafeClass());
        performProcessingAndDispose(ComponentWithNonPrimitiveAttributes.class);
    }
    
    @Test(expected = AssertionError.class)
    public void warningOnNonThreadSafeProcessingInputInstanceAttributeProvidedOnInit()
    {
        initAttributes.put("threadSafe", new ThreadSafeClass());
        initAttributes.put("nonThreadSafeProcessing", new NonThreadSafeClass());
        performProcessingAndDispose(ComponentWithNonPrimitiveAttributes.class);
    }
    
    @Test
    public void noWarningOnNonThreadSafeInputClassAttributeProvidedOnInit()
    {
        initAttributes.put("threadSafe", ThreadSafeClass.class);
        initAttributes.put("nonThreadSafeInit", NonThreadSafeClass.class);
        initAttributes.put("nonThreadSafeProcessing", NonThreadSafeClass.class);
        performProcessingAndDispose(ComponentWithNonPrimitiveAttributes.class);
    }
    
    @Test
    public void noWarningOnNonThreadSafeInputInstanceFieldDefault()
    {
        performProcessingAndDispose(ComponentWithNonPrimitiveAttributes.class);
    }
}
