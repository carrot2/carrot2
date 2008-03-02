package org.carrot2.core;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;

/**
 * Defines the life cycle of a Carrot<sup>2</sup> processing component. The life cycle
 * governs how {@link ProcessingComponent} instances are initialized and disposed of ({@link #init()},
 * {@link #dispose()}) and how processing proceeds ({@link #beforeProcessing()},
 * {@link #process()}, {@link #afterProcessing()}). See {@link SimpleController} for a
 * reference implementation of this life cycle.
 */
public interface ProcessingComponent
{
    /*
     * TODO: Would it be beneficial to have the controller 'clean up' attribute values to
     * their defaults? Currently the values in component fields remain there after
     * processing is finished and {@link #afterProcessing()} method should do the cleanup.
     * There are pros and cons. It isn't hard to imagine a public parameterless
     * constructor simply calling some private <code>reset()</code> method to reset
     * field values to defaults. The controller could reset field values to their original
     * values, although there will be problems with object references (which cannot be
     * easily recreated). [SO] Absolutely, we must have this if we want to have a
     * controller that pools component instances. In this case we could read values of
     * {@link Processing} {@link Input} attributes after component is initialized (but
     * before it serves any request) and then restore these values after the request is
     * handled.
     */

    /**
     * Invoked after component's attributes marked with {@link Init} and {@link Input}
     * annotations have been bound, but before calls to any other methods of this
     * component. After a call to this method completes without an exception, attributes
     * marked with {@link Init} {@link Output} will be collected. In this method,
     * components should perform initializations based on the initialization-time
     * attributes. This method is called <b>once</b> in the life time of a processing
     * component instance.
     * 
     * @throws ComponentInitializationException when initialization failed. If thrown, the
     *             {@link #dispose()} method will be called on this component instance to
     *             allow clean-up actions. The instance will not be used for any further
     *             processing and should be made reclaimable to the garbage collector.
     *             Finally, the exception will be rethrown from the controller method that
     *             caused the component to initialize.
     */
    public void init() throws ComponentInitializationException;

    /**
     * Invoked after the attributes marked with {@link Processing} and {@link Input}
     * annotations have been bound, but before a call to {@link #process()}. In this
     * method, the processing component should perform any initializations based on the
     * runtime attributes. This method is called once per request.
     * 
     * @throws ProcessingException when processing cannot start, e.g. because some
     *             attributes were not bound. If thrown, the {@link #process()} method
     *             will not be called. Instead, {@link #afterProcessing()} will be called
     *             immediately to allow clean-up actions, and the component will be ready
     *             to accept further requests or to be disposed of. Finally, the exception
     *             will be rethrown from the controller method that caused the component
     *             to perform processing.
     */
    public void beforeProcessing() throws ProcessingException;

    /**
     * Performs the processing required to fulfill the request. This method is called once
     * per request.
     * 
     * @throws ProcessingException when processing failed. If thrown, the
     *             {@link #afterProcessing()} method will be called and the component will
     *             be ready to accept further requests or to be disposed of. Finally, the
     *             exception will be rethrown from the controller method that caused the
     *             component to perform processing.
     */
    public void process() throws ProcessingException;

    /**
     * Invoked after the processing has finished, no matter whether an exception has been
     * thrown or not. After a call to this method completes, attributes marked with
     * {@link Processing} and {@link Output} annotations will be collected. In this
     * method, the processing component should dispose of any resources it has allocated
     * to fulfill the request. No matter whether a call to this method completes
     * successfully or with an exception, the component will be ready to accept further
     * requests or to be disposed of. This method is called once per request.
     */
    public void afterProcessing();

    /**
     * Invoked before this processing component is about to be destroyed. In this method,
     * the processing component should release any resources it allocated during
     * initialization. After the call to this method, no other method of this processing
     * component will be called and the component should be made reclaimable to the
     * garbage collector. Exceptions thrown by this method will be ignored. This method is
     * called once in the life time of a processing component instance.
     */
    public void dispose();
}
