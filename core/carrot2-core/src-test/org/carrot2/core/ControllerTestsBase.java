
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

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.isA;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.carrot2.core.ControllerTestsPooling.ComponentWithInstanceCounter;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.tests.CarrotTestCase;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Base class for {@link Controller} tests.
 */
@ThreadLeakLingering(linger = 5000)
public abstract class ControllerTestsBase extends CarrotTestCase
{
    protected IMocksControl mocksControl;

    protected IProcessingComponent component1Mock;
    protected IProcessingComponent component2Mock;
    protected IProcessingComponent component3Mock;

    protected IControllerContextListener contextListenerMock;

    protected Controller controller;

    protected Map<String, Object> initAttributes;
    protected Map<String, Object> processingAttributes;
    protected Map<String, Object> resultAttributes;
    protected ProcessingResult result;

    public abstract Controller prepareController();

    /** A caching controller is used.*/
    boolean caching;
    /** A pooling controller is used. */
    boolean pooling;

    public ControllerTestsBase()
    {
        Controller c = prepareController();

        /*
         * Determine caching/ pooling setup.
         */
        IProcessingComponentManager p = c.componentManager;
        if (p instanceof CachingProcessingComponentManager) {
            caching = !((CachingProcessingComponentManager) c.componentManager).cachedComponentClasses.isEmpty();
            p = ((CachingProcessingComponentManager) c.componentManager).delegate;
        }

        if (p instanceof PoolingProcessingComponentManager) {
            pooling = true;
        }

        c.dispose();
    }
    
    public final boolean isCaching() { return caching; }
    public final boolean isPooling() { return pooling; }

    @Before
    public void prepareMocks()
    {
        mocksControl = createStrictControl();

        component1Mock = mocksControl.createMock(IProcessingComponent.class);
        component2Mock = mocksControl.createMock(IProcessingComponent.class);
        component3Mock = mocksControl.createMock(IProcessingComponent.class);

        initAttributes = Maps.newHashMap();
        initAttributes.put("delegate1", component1Mock);
        initAttributes.put("delegate2", component2Mock);
        initAttributes.put("delegate3", component3Mock);
        initAttributes.put("instanceAttribute", "i");

        processingAttributes = Maps.newHashMap();
    }

    @After
    public void controllerDisposalCheck()
    {
        final boolean cleanedUp = (controller == null);
        if (!cleanedUp)
        {
            controller.dispose();
            controller = null;
            Assert.fail("Each test must dispose the controller.");
        }
    }

    public int eagerlyInitializedInstances()
    {
        return 1;
    }

    protected ProcessingResult performProcessing(Object... classes)
    {
        if (controller == null)
        {
            controller = prepareController();
            controller.init(initAttributes);
        }

        // Controller should not modify input attributes, so we wrap
        // them in an unmodifiable map.
        result = controller.process(Collections.unmodifiableMap(processingAttributes),
            classes);
        if (result != null)
        {
            resultAttributes = result.getAttributes();
        }
        return result;
    }

    protected ProcessingResult performProcessingAndDispose(Object... classes)
    {
        try
        {
            return performProcessing(classes);
        }
        finally
        {
            controller.dispose();
            controller = null;
        }
    }

    protected ProcessingResult performProcessingDisposeAndVerifyMocks(Object... classes)
    {
        try
        {
            return performProcessingAndDispose(classes);
        }
        finally
        {
            mocksControl.verify();
        }
    }

    protected void invokeInit(final IProcessingComponent... components)
    {
        for (IProcessingComponent component : components)
        {
            for (int i = 0; i < eagerlyInitializedInstances(); i++)
            {
                component.init(isA(IControllerContext.class));
            }
        }
    }

    protected void invokeProcessingWithInit(final IProcessingComponent... components)
    {
        for (IProcessingComponent component : components)
        {
            invokeInit(component);
            component.beforeProcessing();
            component.process();
            component.afterProcessing();
        }
    }

    protected void invokeProcessing(final IProcessingComponent... components)
    {
        for (IProcessingComponent component : components)
        {
            component.beforeProcessing();
            component.process();
            component.afterProcessing();
        }
    }

    protected void invokeDisposal(final IProcessingComponent... components)
    {
        // Depending on the component management strategy, order of disposal may or may
        // not be deterministic. It's not deterministic e.g. for pooled components
        // because the components are disposed of when the pool is being shut down.
        // In that case, the iteration order over the pool's entries is arbitrary.
        // For this reason, we don't care about disposal order.
        mocksControl.checkOrder(false);
        for (IProcessingComponent component : components)
        {
            for (int i = 0; i < eagerlyInitializedInstances(); i++)
            {
                component.dispose();
            }
        }
        mocksControl.checkOrder(true);
    }

    protected void checkTimes(final long c1Time, final long c2Time, final long totalTime,
        final double tolerance)
    {
        assertThat(
            ((Long) (resultAttributes.get(AttributeNames.PROCESSING_TIME_TOTAL)))
                .longValue()).as("Total time")
            .isLessThan((long) (totalTime * (1 + tolerance) + 100 * tolerance))
            .isGreaterThan((long) (totalTime * (1 - tolerance) - 100 * tolerance));

        assertThat(
            ((Long) (resultAttributes.get(AttributeNames.PROCESSING_TIME_SOURCE)))
                .longValue()).as("Source time")
            .isLessThan((long) (c1Time * (1 + tolerance) + 100 * tolerance))
            .isGreaterThan((long) (c1Time * (1 - tolerance) - 100 * tolerance));

        assertThat(
            ((Long) (resultAttributes.get(AttributeNames.PROCESSING_TIME_ALGORITHM)))
                .longValue()).as("Algorithm time")
            .isLessThan((long) (c2Time * (1 + tolerance) + 100 * tolerance))
            .isGreaterThan((long) (c2Time * (1 - tolerance) - 100 * tolerance));
    }

    @Bindable
    public static class Component1 extends DelegatingProcessingComponent implements
        IDocumentSource
    {
        @Init
        @Input
        @Attribute(key = "delegate1")
        @ImplementingClasses(classes =
        {
            IProcessingComponent.class
        }, strict = false)
        public IProcessingComponent delegate1;

        @Override
        IProcessingComponent getDelegate()
        {
            return delegate1;
        }
    }

    @Bindable
    public static class Component2 extends DelegatingProcessingComponent implements
        IClusteringAlgorithm
    {
        @Init
        @Input
        @Attribute(key = "delegate2")
        @ImplementingClasses(classes =
        {
            IProcessingComponent.class
        }, strict = false)
        public IProcessingComponent delegate2;

        @Override
        IProcessingComponent getDelegate()
        {
            return delegate2;
        }
    }

    @Bindable
    public static class Component3 extends DelegatingProcessingComponent
    {
        @Init
        @Input
        @Attribute(key = "delegate3")
        @ImplementingClasses(classes =
        {
            IProcessingComponent.class
        }, strict = false)
        public IProcessingComponent delegate3;

        @Override
        IProcessingComponent getDelegate()
        {
            return delegate3;
        }
    }

    @Bindable
    public static class ComponentWithoutDefaultConstructor extends
        ProcessingComponentBase
    {
        private ComponentWithoutDefaultConstructor()
        {
        }
    }

    @Bindable
    public static class ComponentWithContextListener extends
        DelegatingProcessingComponent
    {
        @Init
        @Input
        @Attribute(key = "delegateWithContextListener")
        @ImplementingClasses(classes =
        {
            IProcessingComponent.class
        }, strict = false)
        public IProcessingComponent delegate;

        @Init
        @Input
        @Attribute(key = "contextListener")
        @ImplementingClasses(classes =
        {
            IControllerContextListener.class
        }, strict = false)
        public IControllerContextListener contextListener;

        static AtomicBoolean contextListenerSubscribed = new AtomicBoolean();
        
        @Override
        public void init(IControllerContext context)
        {
            super.init(context);

            if (contextListenerSubscribed.compareAndSet(false, true)) {
                context.addListener(contextListener);
            }
        }

        @Override
        IProcessingComponent getDelegate()
        {
            return delegate;
        }
    }

    @Bindable
    public static class ComponentWithInputOutputAttributes1 extends
        ProcessingComponentBase implements IDocumentSource
    {
        @Processing
        @Input
        @Output
        @Attribute(key = "key1")
        public String key1;

        @Processing
        @Input
        @Output
        @Attribute(key = "key2")
        public String key2;

        @Override
        public void process() throws ProcessingException
        {
            super.process();
            key2 = "value";
        }
    }

    @Bindable
    public static class ComponentWithInputOutputAttributes2 extends
        ProcessingComponentBase implements IClusteringAlgorithm
    {
        @Processing
        @Input
        @Output
        @Attribute(key = "key1")
        public String key1 = "default";

        @Processing
        @Input
        @Output
        @Attribute(key = "key2")
        public String key2 = "default";
    }

    @Bindable
    public static class ComponentWithInitParameter extends ProcessingComponentBase
    {
        @Input
        @Init
        @Attribute(key = "init")
        public String init = "default";

        @Output
        @Processing
        @Attribute(key = "result")
        public String result;

        @Override
        public void process() throws ProcessingException
        {
            result = init + init;
        }
    }
    
    @Bindable
    public static class ComponentWithInitOutputParameter extends ProcessingComponentBase
    {
        @Init
        @Output
        @Attribute(key = "nullValue")
        public String init;

        @Override
        public void process() throws ProcessingException {}
    }    

    @Bindable
    public static class ComponentWithProcessingParameter extends ProcessingComponentBase
    {
        @Input
        @Processing
        @Attribute(key = "processing")
        public String processing = "default";

        @Output
        @Processing
        @Attribute(key = "result")
        public String result;

        @Override
        public void process() throws ProcessingException
        {
            result = processing + processing;
        }
    }

    @Bindable
    public static class ComponentWithInitProcessingInputReferenceAttribute extends
        ProcessingComponentBase
    {
        @Input
        @Init
        @Processing
        @Attribute(key = "initProcessing")
        @ImplementingClasses(classes =
        {
            BindableInstanceCounter.class
        })
        public BindableInstanceCounter initProcessing;
    }

    @Bindable
    public static class ComponentWithInitProcessingInputRequiredAttribute extends
        ProcessingComponentBase
    {
        @Input
        @Init
        @Processing
        @Required
        @Attribute(key = "initProcessing")
        public String initProcessingRequired;

        @Output
        @Processing
        @Attribute(key = "result")
        public String result;

        @Override
        public void process() throws ProcessingException
        {
            result = initProcessingRequired;
        }
    }

    @Bindable
    public static class ComponentWithBindableReference extends ProcessingComponentBase
    {
        @Processing
        @Input
        @Output
        @Attribute(key = "bindable")
        @ImplementingClasses(classes = BindableInstanceCounter.class)
        public BindableInstanceCounter bindable = new BindableInstanceCounter();
    }

    @Bindable
    public static class ComponentWithInitOutputAttribute extends ProcessingComponentBase
        implements IClusteringAlgorithm
    {
        @Init
        @Output
        @Attribute(key = "initOutput")
        public String initOutput;

        @Init
        @Output
        @Attribute(key = "initOutputNull")
        public String initOutputNull;

        @Override
        public void init(IControllerContext context)
        {
            initOutput = "initOutput";
        }
    }

    @Bindable
    public static class ComponentWithOutputAttribute extends ProcessingComponentBase
    {
        @Output
        @Processing
        @Attribute(key = "attr")
        public String result;

        @Override
        public void process() throws ProcessingException
        {
            result = "anything";
        }
    }

    @Bindable
    public static class ComponentWithRequiredProcessingAttribute extends
        ProcessingComponentBase
    {
        @Input
        @Processing
        @Required
        @Internal
        @Attribute(key = "attr")
        public String result;

        @Override
        public void process() throws ProcessingException
        {
            if (result == null) throw new RuntimeException();
        }
    }

    @Bindable
    public static class BindableInstanceCounter
    {
        static int createdInstances = 0;

        public BindableInstanceCounter()
        {
            synchronized (ComponentWithInstanceCounter.class)
            {
                createdInstances++;
            }
        }

        public static void reset()
        {
            createdInstances = 0;
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
