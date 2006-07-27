
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.Set;

/**
 * Local component interface identifies objects that can be assembled in "local"
 * query processing chains. These local processing chains are designed to allow
 * components to directly communicate with each other via method calls, avoiding
 * the overhead of network data streaming and serialization/ deserialization
 * cost present in the general Carrot2 framework design.
 * 
 * <p>
 * The {@link LocalComponent} interface is the superclass for all three types of
 * local components:
 * 
 * <ul>
 * <li>{@link LocalInputComponent} -- components accepting user queries and
 * producing initial data.</li>
 * <li>{@link LocalFilterComponent} -- components somehow altering or enriching
 * the data.</li>
 * <li>{@link LocalOutputComponent} -- components gathering the result or doing
 * something with the result. For example, a visual component displaying the
 * data can implement this interface.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * {@link LocalComponent}contains several methods invoked as callbacks by a
 * query processing -- responsible object of type {@link LocalProcess}:
 * 
 * <ul>
 * <li>{@link LocalComponent#startProcessing(RequestContext)}</li>
 * <li>{@link LocalComponent#endProcessing()}</li>
 * <li>{@link LocalComponent#processingErrorOccurred()}</li>
 * <li>{@link LocalComponent#flushResources()}</li>
 * </ul>
 * 
 * <b>The order of calls to these methods is regulated by a contract specified
 * in the {@link LocalProcess}interface. Refer to the documentation of that
 * interface for details. </b>
 * </p>
 * 
 * <p>
 * The lifecycle of a {@link LocalProcess}interface is presented below. This
 * lifecycle should be respected by any containers or independent applications
 * that instantiate components:
 * 
 * <ul>
 * <li>{@link #init(LocalControllerContext)}method is invoked right after the
 * component has been instantiated. Upon returning from this method, the
 * component should be ready to process queries.</li>
 * <li>The component is ready to process queries. {@link LocalProcess}
 * instances will link the component in a processing chain with other components
 * and invoke the following methods: {@link #startProcessing(RequestContext)},
 * {@link #endProcessing()},{@link #processingErrorOccurred()}and {@link
 * #flushResources()}. The order of invokation of these methods is explained in
 * the contract of the {@link LocalProcess}interface.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * A class implementing the local component interface may expose some
 * capabilities and require other capabilities from the successor and
 * predecessor component in a processing chain.
 * </p>
 * 
 * <p>
 * Capabilities are {@link Set} objects, containing instances of any other Java
 * {@link Object}s. It up to the implementation to decide which objects
 * represent a capability. Usually, capabilities are simply interface names
 * converted to a {@link String}.
 * </p>
 * 
 * <p>
 * A component <tt>A</tt> is considered compatible with a successor component
 * <tt>B</tt> if <tt>B</tt>'s expected predecessor capabilities match
 * <tt>A</tt>'s exposed capabilities and <tt>A</tt>'s expected successor
 * capabilities match <tt>B</tt>'s exposed capabilities. A set of
 * capabilities <tt>X</tt> matches another set of capabilities <tt>Y</tt> if
 * for any object <tt>x</tt> in <tt>X</tt> there exist at least one object
 * <tt>y</tt> in <tt>Y</tt> for which <code>x.equals(y)</code> method
 * returns <code>true</code>. Equality is defined as in {@link
 * java.lang.Object#equals(Object)}.
 * </p>
 * 
 * <p>
 * The operation of a component may be customized using <i>properties </i>.
 * Properties passed to a component via
 * {@link #setProperty(String key, String value)} method are persistent, i.e.
 * they will be active if the component is reused. It is therefore reasonable to
 * set these properties in {@link LocalComponentFactory} at the time of
 * component's instantiation. Properties passed to the component in
 * {@link RequestContext} are volatile, i.e. they are valid only for the
 * duration of the currently processed request.
 * </p>
 * 
 * @author Dawid Weiss
 * @version $Revision$
 * 
 * @see LocalInputComponent
 * @see LocalOutputComponent
 * @see LocalFilterComponent
 */
public interface LocalComponent
{
    /**
     * The initialization method invoked by a component container right after
     * the component has been initialized. The component should check that all
     * required resources are available at this point and either throw an
     * exception, or return gracefully, signalling that it is ready to process
     * queries.
     * 
     * @param context An instance of {@link LocalControllerContext}object.
     * 
     * @throws InstantiationException Thrown if the component is unable to
     *             properly initialize and should not be used to process
     *             queries.
     */
    public void init(LocalControllerContext context)
        throws InstantiationException;

    /**
     * Signals start of query processing. The component should allocate
     * necessary resources and prepare for query processing.
     * 
     * <p>
     * It is important to <b>invoke <code>startProcessing(RequestContext)</code>
     * method on any successor components that this component may have </b>.
     * This "chained" invocation is necessary to ensure that processing chain's
     * successive components are properly initialized.
     * </p>
     * 
     * <p>
     * After the obligatory call to <code>startProcessing()</code> method on
     * the successor component, data processing and any other data-related
     * method invocatins on the successor may proceed.
     * </p>
     * 
     * @param requestContext A {@link RequestContext}interface instance passed
     *            by the container processing the query. The context may be used
     *            to retrieve parameters associated with the query (see {@link
     *            RequestContext#getRequestParameters()} method).
     * 
     * @throws ProcessingException Thrown if the component encountered a problem
     *             in processing the query. May also be thrown from a chained
     *             successor component.
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException;

    /**
     * Signals that the chain's predecessor components ended processing and that
     * no more data will be passed to the component. The component should end
     * its own processing, invoke any necessary data-related methods on the
     * successor component and finally invoke the <code>endProcessing()</code>
     * method to finalize processing in subsequent components.
     * 
     * @throws ProcessingException Thrown if the component encountered a problem
     *             in processing the query. May also be thrown from a chained
     *             successor component. It is allowed to throw an exception and
     *             not invoke <code>endProcessing()</code> method on
     *             subsequent components. In such case, all components will
     *             receive a {@link #processingErrorOccurred()}call.
     */
    public void endProcessing() throws ProcessingException;

    /**
     * This method is invoked if an exception (checked or unchecked) in any of
     * the components has been thrown in between the first {@link
     * #startProcessing(RequestContext)} method invocation and {@link
     * #endProcessing()} method invocation on the last component in the
     * processing chain.
     * 
     * <p>
     * A component must <b>always </b> propagate this call to the subsequent
     * component in the chain.
     * </p>
     */
    public void processingErrorOccurred();

    /**
     * This method is <b>always </b> invoked after the processing of a query has
     * ended (either due to an exception, or a successfull run).
     * 
     * <p>
     * The component must release any resources it allocated for the execution
     * of the query. All instance fields should be cleared to ensure that no
     * garbage data remains for the subsequent query processing execution.
     * </p>
     * 
     * <p>
     * A component must <b>always </b> propagate this call to the subsequent
     * component in the chain.
     * </p>
     */
    public void flushResources();

    /**
     * Returns a set of capabilities required of the successor component. See
     * {@link LocalComponent}'s class documentation for a more detailed
     * description of capabilities.
     * 
     * @return A {@link java.util.Set}object with capabilities. The object
     *         returned may be empty, but should never be null.
     */
    public Set getRequiredSuccessorCapabilities();

    /**
     * Returns a set of capabilities required of the predecessor component. See
     * {@link LocalComponent}'s class documentation for a more detailed
     * description of capabilities.
     * 
     * @return A {@link java.util.Set}object with capabilities. The object
     *         returned may be empty, but should never be null.
     */
    public Set getRequiredPredecessorCapabilities();

    /**
     * Returns a set of capabilities that this component exposes. See {@link
     * LocalComponent}'s class documentation for a more detailed description of
     * capabilities.
     * 
     * @return A {@link java.util.Set}object with capabilities. The object
     *         returned may be empty, but should never be null.
     */
    public Set getComponentCapabilities();

    /**
     * Sets a persistent property for this component. Properties set using this
     * method remain valid for all subsequent queries (in case the component is
     * reused).
     * 
     * <p>
     * It is reasonable to set the persistent properties of a component at the
     * time of its instantiation (in the {@link LocalComponentFactory} object).
     * </p>
     * 
     * @param key The key of the property to set.
     * @param value The value of the property.
     */
    public void setProperty(String key, String value);

    /**
     * Returns a human-readable name of this component or <code>null</code> if
     * the name is not available.
     * 
     * @return a human-readable name of this component or <code>null</code> if
     *         the name is not available.
     */
    public String getName();

    /**
     * Returns a description for this component or <code>null</code> if the
     * description is not available.
     * 
     * @return description for this component or <code>null</code> if the
     *         description is not available.
     */
    public String getDescription();
}