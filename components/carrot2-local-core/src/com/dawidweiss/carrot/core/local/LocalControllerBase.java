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

import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;

/**
 * A complete (modulo TODOs below) base implementation of the
 * {@link com.dawidweiss.carrot.core.local.LocalController}interface. Also
 * implements the {@link LocalControllerContext}interface.
 * 
 * <p>
 * 
 * TODO: implement {@link #explainIncompatibility(String, String)}properly
 * TODO: implement {@link #isComponentSequenceCompatible(String, String)}
 * properly
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LocalControllerBase implements LocalController,
    LocalControllerContext
{
    /** Stores local component pools */
    protected Map componentPools;

    /** Stores local processes */
    protected Map processes;

    /**
     * Creates a new instance of the controller.
     */
    public LocalControllerBase()
    {
        componentPools = new HashMap();
        processes = new LinkedHashMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalController#addLocalComponentFactory(java.lang.String,
     *      com.dawidweiss.carrot.core.local.LocalComponentFactory)
     */
    public void addLocalComponentFactory(String componentId,
        final LocalComponentFactory factory)
    {
        PoolableObjectFactory poolableObjectFactory = new BasePoolableObjectFactory()
        {
            public Object makeObject() throws Exception
            {
                LocalComponent component = factory.getInstance();
                component.init(LocalControllerBase.this);
                return component;
            }
        };

        componentPools.put(componentId, new GenericObjectPool(
            poolableObjectFactory, 5));
    }

    /**
     * Borrows a component with given <code>componentId</code> from the
     * internal component instance pool.
     * 
     * @param componentId
     * @return component instance
     * @throws MissingComponentException when no factory has been registered
     *             with given <code>componentId</code>
     * @throws Exception when other problems occur
     */
    public LocalComponent borrowComponent(String componentId)
        throws MissingComponentException, Exception
    {
        ObjectPool pool = (ObjectPool) componentPools.get(componentId);
        if (pool == null)
        {
            throw new MissingComponentException("Component missing: "
                + componentId);
        }

        return (LocalComponent) pool.borrowObject();
    }

    /**
     * Returns the <code>component</code> with given <code>componentId</code>
     * to the internal component pool.
     * 
     * @param componentId
     * @param component
     * @throws Exception when problems occur
     */
    public void returnComponent(String componentId, LocalComponent component)
        throws Exception
    {
        ((ObjectPool) componentPools.get(componentId)).returnObject(component);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalController#query(java.lang.String,
     *      java.lang.String, java.util.Map)
     */
    public ProcessingResult query(String processId, String query,
        Map requestParameters) throws MissingProcessException, Exception
    {
        // Get the process
        LocalProcess process = (LocalProcess) processes.get(processId);
        if (process == null)
        {
            throw new MissingProcessException("Process missing: " + processId);
        }

        RequestContext requestContext = new RequestContextBase(this,
            requestParameters);

        Object queryResult;
        try
        {
            queryResult = process.query(requestContext, query);
        }
        finally
        {
            requestContext.dispose();
        }

        return new Result(queryResult, requestContext);
    }

    /**
     * Default implementation of the {@link ProcessingResult}interface.
     * 
     * @author stachoo
     */
    protected static class Result implements ProcessingResult
    {
        /** Query result */
        private Object result;

        /** Request context for this query */
        private RequestContext requestContext;

        /**
         * @param queryResult
         * @param requestContext
         */
        public Result(Object queryResult, RequestContext requestContext)
        {
            this.result = queryResult;
            this.requestContext = requestContext;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.dawidweiss.carrot.core.local.ProcessingResult#getQueryResult()
         */
        public Object getQueryResult()
        {
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.dawidweiss.carrot.core.local.ProcessingResult#getRequestContext()
         */
        public RequestContext getRequestContext()
        {
            return requestContext;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalController#addProcess(java.lang.String,
     *      com.dawidweiss.carrot.core.local.LocalProcess)
     */
    public void addProcess(String processId, LocalProcess localProcess)
        throws Exception
    {
        localProcess.initialize(this);
        processes.put(processId, localProcess);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalControllerContext#isComponentFactoryAvailable(java.lang.String)
     */
    public boolean isComponentFactoryAvailable(String key)
    {
        return componentPools.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalControllerContext#getComponentClass(java.lang.String)
     */
    public Class getComponentClass(String componentId)
        throws MissingComponentException, Exception
    {
        ObjectPool pool = (ObjectPool) componentPools.get(componentId);
        if (pool == null)
        {
            throw new MissingComponentException("Component missing: "
                + componentId);
        }

        LocalComponent component = (LocalComponent) pool.borrowObject();
        Class componentClass = component.getClass();
        pool.returnObject(component);

        return componentClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalControllerContext#isComponentSequenceCompatible(java.lang.String,
     *      java.lang.String)
     */
    public boolean isComponentSequenceCompatible(String keyComponentFrom,
        String keyComponentTo) throws MissingComponentException, Exception
    {
        // TODO: implement this properly
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalControllerContext#explainIncompatibility(java.lang.String,
     *      java.lang.String)
     */
    public String explainIncompatibility(String from, String to)
        throws MissingComponentException, Exception
    {
        return "No explanation yet, sorry.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalController#getProcessNames()
     */
    public List getProcessIds()
    {
        return new ArrayList(processes.keySet());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalController#getProcessName(java.lang.String)
     */
    public String getProcessName(String processId)
        throws MissingProcessException
    {
        LocalProcess process = (LocalProcess) processes.get(processId);
        if (process == null)
        {
            throw new MissingProcessException("No such process: " + processId);
        }

        return process.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalController#getProcessDescription(java.lang.String)
     */
    public String getProcessDescription(String processId)
        throws MissingProcessException
    {
        LocalProcess process = (LocalProcess) processes.get(processId);
        if (process == null)
        {
            throw new MissingProcessException("No such process: " + processId);
        }

        return process.getDescription();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalController#getComponentName(java.lang.String)
     */
    public String getComponentName(String componentId)
        throws MissingComponentException, Exception
    {
        LocalComponent localComponent = borrowComponent(componentId);
        String name = localComponent.getName();
        returnComponent(componentId, localComponent);

        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalController#getComponentDescription(java.lang.String)
     */
    public String getComponentDescription(String componentId)
        throws MissingComponentException, Exception
    {
        LocalComponent localComponent = borrowComponent(componentId);
        String description = localComponent.getDescription();
        returnComponent(componentId, localComponent);

        return description;
    }
}