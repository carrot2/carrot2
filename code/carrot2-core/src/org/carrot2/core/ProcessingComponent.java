/**
 * 
 */
package org.carrot2.core;

import org.carrot2.core.parameters.BindingDirection;
import org.carrot2.core.parameters.BindingPolicy;

/**
 * Defines the life cycle of a Carrot2 processing component.
 * <p>
 * TODO: method names?
 * <p>
 * TODO: write proper lifecycle docs
 */
public interface ProcessingComponent
{
    /**
     * Invoked after the {@link BindingPolicy#INSTANTIATION} time parameters have been
     * bound. This method is called once in the life time of a processing component
     * object.
     * 
     * @throws InitializationException when initialization failed
     */
    public void init() throws InitializationException;

    /**
     * Invoked after the {@link BindingPolicy#RUNTIME} parameters and
     * {@link BindingDirection#IN} and {@link BindingDirection#INOUT} attributes have been
     * bound, but before a call to {@link #performProcessing()}. In this method, the
     * processing component should perform any initializations based on the runtime
     * parameters. This method is called once per "request cycle".
     * <p>
     * (TODO: need to come up with a good name for "request cycle": "query", "request"?).
     * 
     * @throws ProcessingException when processing cannot be performed (e.g. some
     *             parameters are not bound)
     */
    public void beforeProcessing() throws ProcessingException;

    /**
     * Performs the processing required to fulfill the request. After the call to this
     * method completes without an exception, {@link BindingDirection#OUT} and
     * {@link BindingDirection#INOUT} attributes will be collected. This method is called
     * once per "request cycle".
     * 
     * @throws ProcessingException when processing failed
     */
    public void performProcessing() throws ProcessingException;

    /**
     * Invoked after the processing has finished, no matter whether an exception has been
     * thrown or not. In this method, the processing component should dispose of any
     * resources it has allocated to fulfill the request. This method is called once per
     * "request cycle".
     */
    public void afterProcessing();

    /**
     * Invoked before this processing component is about to be destroyed. After the call
     * to this method, no other method of this processing component will be called.
     */
    public void destroy();
}
