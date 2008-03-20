package org.carrot2.core;

import java.util.Map;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.pool.*;

import com.google.common.collect.Maps;

/**
 * A controller implementing the life cycle described in <{@link ProcessingComponent}
 * which pools processing component instances. Processing performed using this controller
 * is thread-safe.
 */
public class CachingController implements Controller
{
    /** Pool for component instances */
    private ObjectPool<ProcessingComponent> componentPool = new ObjectPool<ProcessingComponent>();

    private Map<Class<?>, Map<String, Object>> originalAttributeValues = Maps
        .newHashMap();

    private ComponentInstantiationListener componentInstantiationListener;

    public void init(Map<String, Object> attributes)
        throws ComponentInitializationException
    {
        componentInstantiationListener = new ComponentInstantiationListener(attributes,
            originalAttributeValues);
    }

    @SuppressWarnings("unchecked")
    public ProcessingResult process(Map<String, Object> attributes,
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        final ProcessingComponent [] processingComponents = new ProcessingComponent [processingComponentClasses.length];
        try
        {
            // Borrow instances of processing components.
            for (int i = 0; i < processingComponents.length; i++)
            {
                try
                {
                    processingComponents[i] = componentPool.borrowObject(
                        (Class<ProcessingComponent>) processingComponentClasses[i],
                        componentInstantiationListener);
                }
                catch (final InstantiationException e)
                {
                    throw new ComponentInitializationException(
                        "Could not instantiate component class: "
                            + processingComponentClasses[i].getName(), e);
                }
                catch (final IllegalAccessException e)
                {
                    throw new ComponentInitializationException(
                        "Could not instantiate component class: "
                            + processingComponentClasses[i].getName(), e);
                }
            }

            ControllerUtils.performProcessingWithTimeMeasurement(attributes,
                processingComponents);

            return new ProcessingResult(attributes);
        }
        finally
        {
            for (ProcessingComponent processingComponent : processingComponents)
            {
                // Reset attribute values
                try
                {
                    if (processingComponent != null)
                    {
                        // Here's a little hack: we need to disable checking
                        // for required attributes, otherwise, we won't be able
                        // to reset @Required input attributes to null
                        AttributeBinder.bind(processingComponent,
                            new AttributeBinder.AttributeBinderAction []
                            {
                                new AttributeBinder.AttributeBinderActionBind(
                                    Input.class, originalAttributeValues
                                        .get(processingComponent.getClass()), false)
                            }, Input.class, Processing.class);
                    }
                }
                catch (Exception e)
                {
                    throw new ProcessingException("Could not reset attribute values", e);
                    // Do not return to the pool in this case
                }

                componentPool.returnObject(processingComponent);
            }
        }
    }

    public void dispose()
    {
        componentPool.dispose(ComponentDisposalListener.INSTANCE);
    }

    /**
     * Initializes newly created component instances.
     */
    private final static class ComponentInstantiationListener implements
        ObjectInstantiationListener<ProcessingComponent>
    {
        private Map<String, Object> attributes;
        private Map<Class<?>, Map<String, Object>> originalAttributeValues;

        ComponentInstantiationListener(Map<String, Object> attributes,
            Map<Class<?>, Map<String, Object>> originalAttributeValues)
        {
            this.attributes = attributes;
            this.originalAttributeValues = originalAttributeValues;
        }

        @SuppressWarnings("unchecked")
        public void objectInstantiated(ProcessingComponent component)
        {
            try
            {
                ControllerUtils.init(component, attributes);
                synchronized (originalAttributeValues)
                {
                    Map<String, Object> attributes = originalAttributeValues
                        .get(component.getClass());
                    if (attributes == null)
                    {
                        attributes = Maps.newHashMap();

                        // We only unbind @Processing attributes here, so components
                        // must not change @Init attributes during processing.
                        // We could unbind @Init attributes also, but this may be
                        // costly when Class -> Object coercion happens.
                        AttributeBinder.unbind(component, attributes, Input.class,
                            Processing.class);
                        originalAttributeValues.put(component.getClass(), attributes);
                    }
                }
            }
            catch (Exception e)
            {
                // If init() throws any exception, this exception will
                // be propagated to the borrowObject() call.
                component.dispose();
                if (e instanceof ComponentInitializationException)
                {
                    throw (ComponentInitializationException) e;
                }
                else
                {
                    throw new ComponentInitializationException(e);
                }
            }
        }
    }

    /**
     * Disposes of components on shut down.
     */
    private final static class ComponentDisposalListener implements
        ObjectDisposalListener<ProcessingComponent>
    {
        final static ComponentDisposalListener INSTANCE = new ComponentDisposalListener();

        public void objectDisposed(ProcessingComponent component)
        {
            component.dispose();
        }
    }

}
