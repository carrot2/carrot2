
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.core.local;

/**
 * A local process embodies the logic needed to process queries and assemble
 * componenents in a processing chain.
 * 
 * <p>
 * Instances of this interface are passed to the controller component and
 * initialized using {@link #initialize(LocalControllerContext)} method. The
 * controller then invokes the {@link #query(RequestContext, String)} method
 * on a process instance to process user queries.
 * </p>
 * 
 * <p>
 * An instance of this interface should verify that all resources (components)
 * required to process a query are available. This verification should take
 * place in {@link #initialize(LocalControllerContext)} method.
 * </p>
 * 
 * <p>
 * There is a contract on the behavior of the {@link #query(RequestContext,
 * String)} method -- see below for details.
 * </p>
 *
 * @author Dawid Weiss
 *
 * @see LocalProcessBase Local process class implementation.
 */
public interface LocalProcess {
    /**
     * This method is invoked when an instance of this interface is added to a
     * controller component. The controller passes an instance of {@link
     * LocalControllerContext} which may be used to verify the availability of
     * all resources (components) required to process a query later on.
     *
     * @param context An instance of a {@link LocalControllerContext}.
     *
     * @throws Exception If for any reason the local process instance is unable
     *         to process queries, an exception should be thrown.
     */
    public void initialize(LocalControllerContext context)
        throws Exception;

    /**
     * A callback method that the controller invokes to process some user
     * query. The behavior of this method is bounded by the following
     * contract:
     * 
     * <p>
     * <b>METHOD CONTRACT:</b>
     * </p>
     * 
     * <p>
     * This method must be thread-safe. All components acquired from the
     * controller context for the execution of the query will be returned to
     * the pool on exit, so no references to these components should be
     * preserved after this method is exited.
     * </p>
     * 
     * <p>
     * All implementations of this method are required to perform these steps
     * (in order) to ensure that successive components in the processing chain
     * receive the expected stimuli.
     * 
     * <ol>
     * <li>
     * acquire component instances needed to process a query from the  {@link
     * RequestContext} object passed in this methods's arguments.
     * </li>
     * <li>
     * Link the acquired components in a processing chain using their
     * respective <code>setNext()</code> methods. The linking process must end
     * before the first call to {@link
     * LocalComponent#startProcessing(RequestContext)}. See contracts of
     * {@link LocalInputComponent} and {@link LocalFilterComponent}.
     * </li>
     * <li>
     * If the first component in a chain is an instance of {@link
     * LocalInputComponent} interface, the process should initialize it by
     * invoking {@link LocalInputComponent#setQuery(String)} method. In case
     * of other types of components, this step may not be necessary.
     * </li>
     * <li>
     * Initiate query processing by invoking {@link
     * LocalComponent#startProcessing(RequestContext)} method on the first
     * component in the chain. Components will propagate this call to their
     * successors (see {@link LocalComponent} class).
     * </li>
     * <li>
     * If {@link LocalComponent#startProcessing(RequestContext)} method
     * returned successfully, the process must invoke {@link
     * LocalComponent#endProcessing()} method on the first component in the
     * chain.
     * 
     * <p>
     * At this moment, the result of processing should be available in the last
     * component of the processing chain. If this  component is an instance of
     * {@link LocalOutputComponent} interface, then the result can be acquired
     * by invoking {@link LocalOutputComponent#getResult()} method. This value
     * should be returned as the result of the entire  <code>query</code>
     * method.
     * </p>
     * </li>
     * <li>
     * If {@link LocalComponent#startProcessing(RequestContext)} method threw
     * an exception (checked or unchecked), the process must invoke {@link
     * LocalComponent#processingErrorOccurred()} method on the first component
     * in the chain.
     * </li>
     * <li>
     * Query processing always ends with flushing resources of the components
     * involved (even if the method has thrown an exception). The process must
     * invoke  {@link LocalComponent#flushResources()} method on the first
     * component in the chain.
     * </li>
     * </ol>
     * </p>
     * 
     * <p>
     * The above contract is quite complex and tricky in implementation. If in
     * doubt, refer to the source code of the provided plain implementation of
     * a process -- {@link LocalProcessBase}. Even better, override {@link
     * LocalProcessBase} class and use the exposed hooks  to customize your
     * process.
     * </p>
     *
     * @param context The request context for the currently processed request.
     * @param query The query to process. The format of a query is not
     *        explicit. It should be something the first component is able to
     *        interpret.
     *
     * @return An instance of {@link java.lang.Object} that is usually the
     *         value returned from the last component in a processing chain.
     *         The exact type of the result depends on the output component.
     *         The application invoking the query method must know how to
     *         downcast it to a more specific type.
     *
     * @throws Exception If any problem occurred during the processing.
     *
     * @see LocalComponent
     * @see LocalProcessBase Local process class implementation.
     */
    public Object query(RequestContext context, String query)
        throws Exception;
    
    /**
     * @return returns a name for this process or
     * <code>null</code> if name is not available.
     */
    public String getName();
 
    /**
     * @return returns a description for this process
     * or <code>null</code> if description is not available.
     */
    public String getDescription();
}
