/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local;

import java.util.*;

/**
 * A container for local processes and local components. Local applications will
 * use this interface to set up all necessary processes and components as well
 * as to to execute queries.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface LocalController
{
    /**
     * Requests the process identified by <code>processId</code> to process a
     * <code>query</code>. The contents of <code>requestParameters</code>
     * will be accessible for all components in the chain through the
     * {@link RequestContext}during processing of the query.
     * 
     * @param processId Identifier of the process requested to handle the query
     * @param query Query to be processed. Format of the query is not specified
     *            here, as it depends on what the input component of the invoked
     *            process can handle.
     * @param requestParameters parameters to be available for components during
     *            processing of this query. Components are free to add their own
     *            properties to the context. These will be available for the
     *            caller through the returned {@link ProcessingResult}instance.
     * @return Results of processing
     * @throws MissingProcessException when there is no component with given
     *             <code>processId</code>
     * @throws Exception if an error occurs during query processing
     */
    public ProcessingResult query(String processId, String query,
        Map requestParameters) throws MissingProcessException, Exception;

    /**
     * Adds a factory that will be used to produce components associated with
     * given <code>componentId</code>.
     * 
     * @param componentId
     * @param factory
     */
    public void addLocalComponentFactory(String componentId,
        final LocalComponentFactory factory);

    /**
     * Adds a local process to the controller and associates the process with
     * given <code>processId</code>.
     * 
     * @param processId identifier of the new process
     * @param localProcess instance of the local process
     * @throws Exception if an error occurrs during initialization of the
     *             process.
     */
    public void addProcess(String processId, LocalProcess localProcess)
        throws Exception;

    /**
     * Returns the name of the process associated with given
     * <code>processId</code>.
     * 
     * @param processId
     * @return process name
     * @throws MissingProcessException when there is no process associated with
     *             <code>processId</code>
     */
    public String getProcessName(String processId)
        throws MissingProcessException;

    /**
     * Returns description of the process associated with given
     * <code>processId</code>.
     * 
     * @param processId
     * @return process description
     * @throws MissingProcessException when there is no process associated with
     *             given <code>processId</code>
     */
    public String getProcessDescription(String processId)
        throws MissingProcessException;

    /**
     * Returns the name of the component associated with given
     * <code>componentId</code>.
     * 
     * @param componentId
     * @return component name
     * @throws Exception when component could not be instantiated
     * @throws MissingComponentException when there is no component associated
     *             with given <code>componentId</code>
     */
    public String getComponentName(String componentId)
        throws MissingComponentException, Exception;

    /**
     * Returns the description of the component associated with given
     * <code>componentId</code>.
     * 
     * @param componentId
     * @return component description
     * @throws Exception when component could not be instantiated
     * @throws MissingComponentException when there is no component associated
     *             with given <code>componentId</code>
     */
    public String getComponentDescription(String componentId)
        throws MissingComponentException, Exception;

    /**
     * Returns a {@link List}of identifiers of all processes that have been
     * added and successfully initialized by this local controller.
     * 
     * @return identifiers of all processes in this controller
     */
    public List getProcessIds();
}