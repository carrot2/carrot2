
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.List;
import java.util.Map;

/**
 * A container for local processes and local components. Local applications will
 * use this interface to set up all necessary processes and components as well
 * as to execute queries.
 *
 * @author Stanislaw Osinski
 * @author Dawid Weiss
 * 
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
     * @throws DuplicatedKeyException Thrown when <code>componentId</code> already
     *      exists in the controller.
     */
    public void addLocalComponentFactory(String componentId,
        final LocalComponentFactory factory)
        throws DuplicatedKeyException;

    /**
     * Adds a local process to the controller and associates the process with
     * given <code>processId</code>.
     * 
     * @param processId identifier of the new process
     * @param localProcess instance of the local process
     */
    public void addProcess(String processId, LocalProcess localProcess)
        throws InitializationException, MissingComponentException, DuplicatedKeyException;

    /**
     * Returns the name of the process associated with given
     * <code>processId</code>.
     * 
     * @throws MissingProcessException when there is no process associated with
     *             <code>processId</code>
     */
    public String getProcessName(String processId)
        throws MissingProcessException;

    /**
     * Returns the name of the component associated with given
     * <code>componentId</code>.
     * 
     * @throws MissingComponentException when there is no component associated
     *             with given <code>componentId</code>
     */
    public String getComponentName(String componentId)
        throws MissingComponentException;

    /**
     * Returns a {@link List}of identifiers of all processes that have been
     * added and successfully initialized by this local controller.
     * 
     * @return identifiers of all processes in this controller
     */
    public List getProcessIds();
}