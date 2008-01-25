/**
 * 
 */
package org.carrot2.core;

import org.carrot2.core.attribute.Input;
import org.carrot2.core.attribute.Processing;
import org.carrot2.core.controller.SimpleController;

/**
 * Defines the life cycle of a Carrot<sup>2</sup> processing component. See
 * {@link SimpleController} for a reference impelementation of componenty life cycle.
 * <p>
 * TODO: Would it be beneficial to have the controller 'clean up' attribute values to
 * their defaults? Currently the values in component fields remain there after processing
 * is finished and {@link #afterProcessing()} method should do the cleanup. There are pros
 * and cons. It isn't hard to imagine a public parameterless constructor simply calling
 * some private <code>reset()</code> method to reset field values to defaults. The
 * controller could reset field values to their original values, although there will be
 * problems with object references (which cannot be easily recreated). [SO] Absolutely, we
 * must have this if we want to have a controller that pools component instances. In this
 * case we could read values of {@link Processing} {@link Input} attributes after
 * component is initialized (but before it serves any request) and then restore these
 * values after the request is handled.
 */
public interface ProcessingComponent
{
    /**
     * Invoked after the {@link BindingPolicy#INSTANTIATION} time attributes have been
     * bound. This method is called once in the life time of a processing component
     * object.
     * 
     * @throws ComponentInitializationException when initialization failed
     */
    public void init() throws ComponentInitializationException;

    /**
     * AOP-style hook invoked after the {@link BindingPolicy#RUNTIME} attributes and
     * {@link BindingDirection#IN} and {@link BindingDirection#INOUT} attributes have been
     * bound, but before a call to {@link #process()}. In this method, the processing
     * component should perform any initializations based on the runtime attributes. This
     * method is called once per request cycle described in the class header.
     * 
     * @throws ProcessingException when processing cannot be performed (e.g. some
     *             attributess cannot be bound)
     */
    public void beforeProcessing() throws ProcessingException;

    /**
     * Performs the processing required to fulfill the request. After the call to this
     * method completes without an exception, {@link BindingDirection#OUT} and
     * {@link BindingDirection#INOUT} attributes will be collected. This method is called
     * once per request cycle.
     * 
     * @throws ProcessingException when processing failed
     */
    public void process() throws ProcessingException;

    /**
     * AOP-style hook invoked after the processing has finished, no matter whether an
     * exception has been thrown or not. In this method, the processing component should
     * dispose of any resources it has allocated to fulfill the request. This method is
     * called once per request cycle.
     */
    public void afterProcessing();

    /**
     * Invoked before this processing component is about to be destroyed. After the call
     * to this method, no other method of this processing component will be called.
     */
    public void dispose();
}
