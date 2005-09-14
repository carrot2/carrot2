
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

package com.dawidweiss.carrot.local.controller;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.local.controller.loaders.ComponentInitializationException;

import org.apache.commons.pool.*;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * The local controller class is a container for components, processes and a
 * facade for execution of queries.
 *
 * <p>
 * The controller class uses several final static control variables. The value
 * of these variables can be altered by passing a system property to the JVM.
 * 
 * <ul>
 * <li>
 * <code>carrot2.local.controller.blockTimeout</code> - controls the timeout
 * with which processes wait for availability of a component in the pool. If
 * this timeout expires, the process may be interrupted due to a component's
 * inavailability. The default is 5 seconds.
 * </li>
 * </ul>
 * </p>
 */
public final class LocalController implements LocalControllerContext {
    /**
     * Processes available in this controller.
     */
    private HashMap processes = new HashMap();

    /**
     * Component factories available in this controller.
     */
    private HashMap factories = new HashMap();

    /** 
     * A map of object pools. Each pool is identified by its component's
     * factory identifier.
     */
    private HashMap instances = new HashMap();
    
    /** 
     * Controls the timeout with which processes wait for availability of a
     * component in the pool.
     */
    private final static int blockTimeout;

    /**
     * See class documentation for information about static final variables and
     * how to change their values.
     */
    static {
        int timeout;

        try {
            timeout = Integer.parseInt(System.getProperty(
                        "carrot2.local.controller.blockTimeout", "5000"));
        } catch (Exception e) {
            timeout = 5000;
        }

        blockTimeout = timeout;
    }

    /**
     * Creates a new LocalController object.
     */
    public LocalController() {
    }
    
    /**
     * Adds a component factory to the list of available components.
     */
    public void addComponentFactory(String key,
        final LocalComponentFactory factory, int maxPoolSize)
        throws DuplicatedKeyException, ComponentInitializationException {
        if (factories.containsKey(key)) {
            throw new DuplicatedKeyException("id");
        }

        try {
            // attempt to create a single instance and see if it instantiated ok.
            factory.getInstance().init(this);
        } catch (InstantiationException e) {
            throw new ComponentInitializationException("Could not create a new component instance.", e);
        }

        // attempt to create a pool of instances.
        PoolableObjectFactory poolFactory = new BasePoolableObjectFactory() {
                public Object makeObject() throws Exception {
                    LocalComponent component = factory.getInstance();
                    component.init(LocalController.this);

                    return component;
                }
            };

        ObjectPool pool = new GenericObjectPool(poolFactory, maxPoolSize,
                GenericObjectPool.WHEN_EXHAUSTED_BLOCK, blockTimeout);

        factories.put(key, factory);
        instances.put(key, pool);
    }

    /**
     * Adds a {@link LocalProcess} identified by an <code>key</code> to this
     * controller.
     *
     * <p>
     * All component factories that the process may require must be already
     * present in the controller.
     * </p>
     *
     * @param key The key to add the process to the controller.
     * @param process An instance of {@link LocalProcess}
     *
     * @throws DuplicatedKeyException Thrown if the key already exists.
     * @throws Exception Thrown if initialization of the process failed. This
     *         is a generic exception thrown directory from the initialization
     *         code of the process.
     */
    public void addProcess(String key, LocalProcess process)
        throws DuplicatedKeyException, Exception {
        // check if no duplicate process is being added
        if (processes.containsKey(key)) {
            throw new DuplicatedKeyException(key);
        }

        process.initialize(this);

        processes.put(key, process);
    }

    /**
     * @return Returns an array of process identifiers.
     */
    public String[] getProcessNames() {
        Set keys = processes.keySet();

        return (String[]) keys.toArray(new String[keys.size()]);
    }

    /**
     * @return Returns an array of component factories' names.
     */
    public String[] getComponentFactoryNames() {
        Set keys = this.factories.keySet();

        return (String[]) keys.toArray(new String[keys.size()]);
    }

    /**
     * Executes a query using the specified local process.
     *
     * @param processKey The identifier of a process to use.
     * @param query The query to execute. The format of the query depends on
     *        the input component used.
     * @param attributes Request attributes passed to components involved in
     *        query's execution as part of the request context.
     *
     * @return Returns the result acquired from the process (see {@link
     *         LocalProcess#query(RequestContext, String)}).
     *
     * @throws MissingProcessException Thrown if there is no process with the
     *         specified id.
     * @throws Exception Thrown from the process if execution fails for some
     *         reason.
     */
    public Object query(String processKey, String query, Map attributes)
        throws MissingProcessException, Exception {
        LocalProcess process = (LocalProcess) processes.get(processKey);

        if (process == null) {
            throw new MissingProcessException();
        }

        Map fallbackMap = new FallbackMap(attributes);

        RequestContextImpl requestContext = new RequestContextImpl(this,
                fallbackMap);

        try {
            return process.query(requestContext, query);
        } finally {
            requestContext.returnResources();
        }
    }

    /**
     * Borrows an instance of a component from the pool.
     * 
     * <p>
     * <b>The component must be returned</b> using  {@link
     * #returnComponent(String, LocalComponent)} method
     * </p>
     *
     * @param key The identifier of a component factory.
     *
     * @return Returns a usable instance of the {@link LocalComponent} class.
     *
     * @throws MissingComponentException If there is no component factory with
     *         the provided identifier.
     * @throws Exception If the component cannot be acquired for some reason.
     */
    public LocalComponent borrowComponent(String key)
        throws MissingComponentException, Exception {
        Object pool = instances.get(key);

        if (pool == null) {
            throw new MissingComponentException(key);
        }

        return (LocalComponent) (((ObjectPool) pool).borrowObject());
    }

    /**
     * Returns an instance of a component previously acquired using {@link
     * #borrowComponent(String)} back to the pool.
     *
     * @param key The identifier of the factory that produced this component.
     *        This argument must be identical with the key that was used to
     *        acquire the component.
     * @param component The component acquired from the pool.
     *
     * @throws Exception Exception is unlikely, but may be thrown from the pool
     *         if the component cannot be returned to it for some reason.
     */
    public void returnComponent(String key, LocalComponent component)
        throws Exception {
        ((ObjectPool) instances.get(key)).returnObject(component);
    }

    /** 
     * Checks if there is a component factory with the provided id.
     *
     * @param key The identifier of the factory to look for.
     *
     * @return <code>true</code> if the factory exists, false otherwise.
     */
    public boolean isComponentFactoryAvailable(String key) {
        return factories.containsKey(key);
    }

    /**
     * Verifies whether an ordered pair of components is compatible in the
     * sense explained in the documentation of {@link LocalComponent} class.
     *
     * @param keyComponentFrom The first component (predecessor in a processing
     *        chain)
     * @param keyComponentTo The second component (successor in a processing
     *        chain)
     *
     * @return <code>true</code> if the two components are compatible,
     *         <code>false</code> otherwise.
     *
     * @throws MissingComponentException If there is no component factory with
     *         the provided identifier.
     * @throws Exception If sample components for testing cannot be acquired
     *         for some reason.
     */
    public boolean isComponentSequenceCompatible(String keyComponentFrom,
        String keyComponentTo) throws MissingComponentException, Exception {
        LocalComponent from = null;
        LocalComponent to = null;

        try {
            from = this.borrowComponent(keyComponentFrom);
            to = this.borrowComponent(keyComponentTo);

            return CapabilityMatchVerifier.isCompatible(from, to);
        } finally {
            if (from != null) {
                this.returnComponent(keyComponentFrom, from);
            }

            if (to != null) {
                this.returnComponent(keyComponentTo, to);
            }
        }
    }

    /**
     * Returns the class of a component with the provided identifier.
     *
     * @see com.dawidweiss.carrot.core.local.LocalControllerContext#getComponentClass(java.lang.String)
     */
    public Class getComponentClass(String key)
        throws MissingComponentException, Exception {
        LocalComponent c = null;

        try {
            c = this.borrowComponent(key);

            return c.getClass();
        } finally {
            if (c != null) {
                this.returnComponent(key, c);
            }
        }
    }

    /**
     * Explains incompatibility between two components, if they  are
     * incompatible.
     *
     * @see com.dawidweiss.carrot.core.local.LocalControllerContext#explainIncompatibility(java.lang.String,
     *      java.lang.String)
     */
    public String explainIncompatibility(String keyComponentFrom,
        String keyComponentTo) throws MissingComponentException, Exception {
        LocalComponent from = null;
        LocalComponent to = null;

        try {
            from = this.borrowComponent(keyComponentFrom);
            to = this.borrowComponent(keyComponentTo);

            return CapabilityMatchVerifier.explain(from, to);
        } finally {
            if (from != null) {
                this.returnComponent(keyComponentFrom, from);
            }

            if (to != null) {
                this.returnComponent(keyComponentTo, to);
            }
        }
    }

	/**
     * @param key Factory identifier. 
     *  
     * @return Returns a named {@link LocalComponentFactory} object from
     * the controller, or <code>null</code> if the factory is not available.
     * 
     * @see #getComponentFactoryNames()
	 */
	public LocalComponentFactory getFactory(String key) {
        return (LocalComponentFactory) this.factories.get(key);
	}

    /**
     * Returns a named {@link LocalProcess} object from
     * the controller, or <code>null</code> if the process is not available.
     * 
     * <p><b>The process instance should not be used for executing queries
     * concurrently with the controller.</b></p>
     * 
     * @param key Process identifier. 
     *  
     * @return Returns a named {@link LocalProcess} object from
     * the controller, or <code>null</code> if the process is not available.
     * 
     * @see #getProcessNames()
     */
    public LocalProcess getProcess(String key) {
        return (LocalProcess) this.processes.get(key);
    }
}
