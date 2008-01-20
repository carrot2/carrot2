/**
 * 
 */
package org.carrot2.core;

import org.carrot2.core.parameter.*;

/**
 * Defines the life cycle of a Carrot<sup>2</sup> processing component.
 * <p>
 * Any controller should invoke methods of a {@link ProcessingComponent} according to the
 * following pseudo-code (see methods for detailed description of each step):
 * 
 * <pre>
 * Map parameters = ...;
 * 
 * ProcessingComponent component = newInstance();
 * bindParameters(component, parameters, {@link BindingPolicy#INSTANTIATION});
 * component.{@link #init()};
 * 
 * while (pendingRequests())
 * {
 *     Map attributes = ...;
 *     
 *     bindParameters(component, parameters, {@link BindingPolicy#RUNTIME});
 *     bindAttributes(component, attributes, 
 *         {@link BindingDirection#IN}, {@link BindingDirection#INOUT});
 *     component.{@link #beforeProcessing()};
 * 
 *     try {
 *         component.{@link #process()};
 *         bindAttributes(component, attributes, 
 *             {@link BindingDirection#INOUT}, {@link BindingDirection#OUT});
 *     } finally {
 *         component.afterProcessing();
 *     }
 * }
 * 
 * component.{@link #dispose()};
 * </pre>
 * 
 * TODO: Would it be beneficial to have the controller 'clean up' attribute values to
 * their defaults? Currently the values in component fields remain there after processing
 * is finished and {@link #afterProcessing()} method should do the cleanup. There are pros
 * and cons. It isn't hard to imagine a public parameterless constructor simply calling
 * some private <code>reset()</code> method to reset field values to defaults. The
 * controller could reset field values to their original values, although there will be
 * problems with object references (which cannot be easily recreated).
 */
public interface ProcessingComponent
{
    /**
     * Invoked after the {@link BindingPolicy#INSTANTIATION} time parameters have been
     * bound. This method is called once in the life time of a processing component
     * object.
     * 
     * @throws ComponentInitializationException when initialization failed
     */
    public void init() throws ComponentInitializationException;

    /**
     * AOP-style hook invoked after the {@link BindingPolicy#RUNTIME} parameters and
     * {@link BindingDirection#IN} and {@link BindingDirection#INOUT} attributes have been
     * bound, but before a call to {@link #process()}. In this method, the processing
     * component should perform any initializations based on the runtime parameters. This
     * method is called once per request cycle described in the class header.
     * 
     * @throws ProcessingException when processing cannot be performed (e.g. some
     *             parameters are not bound)
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
