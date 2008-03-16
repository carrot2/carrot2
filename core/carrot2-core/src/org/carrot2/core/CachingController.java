package org.carrot2.core;

import java.util.Map;

import org.carrot2.util.pool.*;

/**
 * A controller implementing the life cycle described in <{@link ProcessingComponent}
 * which pools processing component instances. Processing performed using this controller
 * is thread-safe.
 */
public class CachingController implements Controller
{
    private ObjectPool<ProcessingComponent> componentPool = new ObjectPool<ProcessingComponent>();

    private ComponentInstantiationListener componentInstantiationListener;

    public void init(Map<String, Object> attributes)
        throws ComponentInitializationException
    {
        componentInstantiationListener = new ComponentInstantiationListener(attributes);

        // Here we can also pre-populate the pool with some component instances
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

            // Perform processing
            for (final ProcessingComponent element : processingComponents)
            {
                try
                {
                    ControllerUtils.beforeProcessing(element, attributes);
                    ControllerUtils.performProcessing(element, attributes);
                }
                finally
                {
                    ControllerUtils.afterProcessing(element, attributes);
                }
            }

            return new ProcessingResult(attributes);
        }
        finally
        {
            for (ProcessingComponent processingComponent : processingComponents)
            {
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

        ComponentInstantiationListener(Map<String, Object> attributes)
        {
            this.attributes = attributes;
        }

        public void objectInstantiated(ProcessingComponent component)
        {
            try
            {
                ControllerUtils.init(component, attributes);
            }
            catch (ComponentInitializationException e)
            {
                // If init() throws any exception, this exception will
                // be propagated to the borrowObject() call.
                component.dispose();
                throw e;
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
