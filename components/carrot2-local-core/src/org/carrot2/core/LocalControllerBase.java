
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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.carrot2.core.controller.*;
import org.carrot2.core.controller.loaders.ComponentInitializationException;
import org.carrot2.util.resources.*;

/**
 * <p>A complete base implementation of the
 * {@link org.carrot2.core.LocalController} interface. Also
 * implements the {@link LocalControllerContext} interface.
 * 
 * <p>This class is in general not thread-safe at initialization time. It is safe to invoke
 * {@link #query(String, String, Map)} method from multiple threads after an instance has been
 * initialized. 
 *
 * <p>The {@link #destroy()} method must be called to remove internal object pools 
 * properly.   
 *
 * @author Stanislaw Osinski
 * @author Dawid Weiss
 * 
 * @version $Revision$
 */
public class LocalControllerBase implements LocalController, LocalControllerContext
{
    /**
     * Stores local component pools.
     * Map<String, GenericObjectPool>
     */
    protected final Map componentPools;

    /** Stores local processes */
    protected final Map processes;

    /**
     * Search and autoload component descriptors if they are missing. 
     */
    private boolean autoload;

    /**
     * A flag marking destroyed controller.
     */
    private volatile boolean destroyed;

    /**
     * Default component pool configuration. With this configuration the behaviour of the
     * pool should be like this:
     * <ul>
     * <li>when pool is exhausted, it will grow infinitely
     * <li>every 5 minutes, 3 oldest idle component instances will be evicted, but only
     * if they are older than 15 minutes
     * <li>one idle instance will always be available
     * </ul>
     */
    public static final GenericObjectPool.Config DEFAULT_COMPONENT_POOL_CONFIG;
    static {
        DEFAULT_COMPONENT_POOL_CONFIG = new GenericObjectPool.Config();
        DEFAULT_COMPONENT_POOL_CONFIG.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW;
        DEFAULT_COMPONENT_POOL_CONFIG.maxWait = 0; // irrelevant
        DEFAULT_COMPONENT_POOL_CONFIG.maxActive = 0; // irrelevant
        DEFAULT_COMPONENT_POOL_CONFIG.maxIdle = 10;
        DEFAULT_COMPONENT_POOL_CONFIG.timeBetweenEvictionRunsMillis = 1000 * 60 * 5;
        DEFAULT_COMPONENT_POOL_CONFIG.softMinEvictableIdleTimeMillis = 1000 * 60 * 15;
        DEFAULT_COMPONENT_POOL_CONFIG.minEvictableIdleTimeMillis = -1; // no hard eviction
        DEFAULT_COMPONENT_POOL_CONFIG.minIdle = 1;
        DEFAULT_COMPONENT_POOL_CONFIG.numTestsPerEvictionRun = 3;
        DEFAULT_COMPONENT_POOL_CONFIG.testOnBorrow = false;
        DEFAULT_COMPONENT_POOL_CONFIG.testOnReturn = false;
        DEFAULT_COMPONENT_POOL_CONFIG.testWhileIdle = false;        
    }

    /**
     * Component pool configuration with no background eviction. The behaviour of the
     * pool should be like this:
     * <ul>
     * <li>when pool is exhausted, it will grow infinitely
     * <li>one idle instance will always be available
     * </ul>
     */
    public static final GenericObjectPool.Config NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG;
    static {
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG = new GenericObjectPool.Config();
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW;
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.maxWait = 0; // irrelevant
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.maxActive = 0; // irrelevant
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.maxIdle = 5;
        // no background evictor.
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.timeBetweenEvictionRunsMillis = -1;
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.softMinEvictableIdleTimeMillis = -1;
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.minEvictableIdleTimeMillis = -1;
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.minIdle = 1;
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.testOnBorrow = false;
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.testOnReturn = false;
        NO_TIMED_EVICTION_COMPONENT_POOL_CONFIG.testWhileIdle = false;        
    }
    
    /** Component pool configuration */
    private GenericObjectPool.Config poolConfig;
    
    /**
     * Default implementation of the {@link ProcessingResult} interface.
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

        public Object getQueryResult()
        {
            return result;
        }

        public RequestContext getRequestContext()
        {
            return requestContext;
        }
    }    
    
    /**
     * Creates a new instance of the controller with the default component 
     * pool settings.
     */
    public LocalControllerBase()
    {
        this(DEFAULT_COMPONENT_POOL_CONFIG);
    }
    
    /**
     * Creates a new instance of the controller with the specified configuration
     * of the component pools.
     */
    public LocalControllerBase(GenericObjectPool.Config componentPoolConfig)
    {
        componentPools = new HashMap();
        processes = new LinkedHashMap();
        this.poolConfig = componentPoolConfig;
    }

    /*
     * 
     */
    public void addLocalComponentFactory(String componentId, LocalComponentFactory factory)
        throws DuplicatedKeyException
    {
        if (componentPools.containsKey(componentId)) {
            throw new DuplicatedKeyException("Component factory with " +
                    "this id already exists: " + componentId);
        }
        
        // http://issues.carrot2.org/browse/CARROT-145
        // An anonymous class here would hold synthetic backreference to outer
        // LocalControllerBase.this instance. This in turn would never let 
        // the LocalControllerBase be finalized and garbage collected.
        final PoolableObjectFactory poolableObjectFactory = 
            new PoolableComponentFactory(factory, this);

        componentPools.put(componentId, 
            new GenericObjectPool(poolableObjectFactory, poolConfig));
    }

    /**
     * @return Returns an array of names of component factories.
     */
    public final String[] getComponentFactoryNames() {
        final Set keys = this.componentPools.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    /**
     * Borrows a component with given <code>componentId</code> from the
     * internal component instance pool.
     * 
     * @throws MissingComponentException when no factory has been registered
     *             with given <code>componentId</code>
     */
    public LocalComponent borrowComponent(String componentId)
        throws MissingComponentException
    {
        ObjectPool pool = (ObjectPool) componentPools.get(componentId);
        if (pool == null)
        {
            throw new MissingComponentException(componentId);
        }

        try {
            return (LocalComponent) pool.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException("Could not acquire component instance from the pool: "
                + componentId, e);
        }
    }

    /**
     * Returns the <code>component</code> with given <code>componentId</code>
     * to the internal component pool.
     */
    public void returnComponent(String componentId, LocalComponent component)
    {
        try {
            ((ObjectPool) componentPools.get(componentId)).returnObject(component);
        } catch (Exception e) {
            throw new RuntimeException("Could not put the component back to the pool.", e);
        }
    }

    /**
     * Execute a given query against the process <code>processId</code>.
     */
    public ProcessingResult query(String processId, String query,
        Map requestParameters) throws MissingProcessException, ProcessingException
    {
        if (destroyed) {
            throw new ProcessingException("Controller destroyed.");
        }
        
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
     * Add a local process with the specified id to the controller.
     */
    public void addProcess(String processId, LocalProcess localProcess) 
        throws InitializationException, MissingComponentException, DuplicatedKeyException
    {
        if (processes.containsKey(processId))
            throw new DuplicatedKeyException("Process with this identifier " +
                    "already exists: " + processId);
        if (autoload) {
            boolean initialized = false;
            do {
                try {
                    localProcess.initialize(this);
                    initialized = true;
                } catch (MissingComponentException e) {
                    // At least one component is missing. Try autoloading.
                    LoadedComponentFactory loadedComponent;
                    try {
                        loadedComponent = searchComponentFactory(e.getMissingComponentId());
                        if (loadedComponent == null) {
                            /* nothing found, rethrow missing component exception */
                            throw e;
                        }
                        // Component factory found, add it and retry.
                        this.addLocalComponentFactory(loadedComponent.getId(),
                                loadedComponent.getFactory());
                    } catch (IOException ioe) {
                        throw new InitializationException("Missing component autoloading failed: "
                                + e.getMissingComponentId(), ioe);
                    } catch (ComponentInitializationException cie) {
                        throw new InitializationException("Missing component autoloading failed: "
                                + e.getMissingComponentId(), cie);
                    }
                }
            } while (!initialized);
        } else {
            localProcess.initialize(this);
        }
        processes.put(processId, localProcess);
    }

    /**
     * Component factory auto-lookup. We convert the identifier
     * of a component factory to resource name and then try to read that
     * resource (if found). 
     * 
     * @throws MissingComponentException If nothing can be found, this exception is rethrown.
     */
    private LoadedComponentFactory searchComponentFactory(String componentId)
        throws IOException, ComponentInitializationException
    {
        final ControllerHelper helper = new ControllerHelper();
        final String [] nameCandidates = getPotentialComponentNames(componentId, helper);

        final ResourceUtils resUtils = ResourceUtilsFactory.getDefaultResourceUtils();
        InputStream stream = null;
        for (int i = 0; i < nameCandidates.length; i++) {
            // Construct resource name
            final String resourceName = nameCandidates[i];

            // Look for the descriptor.
            final Resource res = resUtils.getFirst(resourceName, LocalControllerBase.class);

            if (res != null) {
                // Something found. Try reading it.
                stream = res.open();
                try {
                    return helper.loadComponentFactory(
                            helper.getExtension(nameCandidates[i]), stream);
                } catch (LoaderExtensionUnknownException e) {
                    // theoretically unreachable...
                    throw new RuntimeException("Loader unknown?");
                }
            }
        }
        
        // Nothing found.
        return null;
    }
    
    /**
     * Generates potential component descriptor names from its it.
     */
    private String [] getPotentialComponentNames(String componentId, ControllerHelper helper) {
        final Object [] extensions = helper.getRecognizedComponentFactoryExtensions().toArray();
        final String [] baseNames = new String [] {
                componentId
        };

        final String [] candidates = new String [extensions.length * baseNames.length];
        int k = 0;
        for (int i = 0; i < baseNames.length; i++) {
            for (int j = 0; j < extensions.length; j++) {
                candidates[k] = baseNames[i] + "." + extensions[j];
                k++;
            }            
        }
        return candidates;
    }

    /**
     * Returns <code>true</code> if a given component factory is available.
     */
    public boolean isComponentFactoryAvailable(String key)
    {
        return componentPools.containsKey(key);
    }

    /**
     * Returns the class of a given component factory.
     */
    public Class getComponentClass(String componentId)
        throws MissingComponentException
    {
        LocalComponent component = borrowComponent(componentId);
        try {
            return component.getClass();
        } finally {
            returnComponent(componentId, component);
        }
    }

    /**
     * Returns <code>true</code> if a given sequence of components is available
     * and compatible.
     */
    public boolean isComponentSequenceCompatible(String keyComponentFrom,
        String keyComponentTo) throws MissingComponentException
    {
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
     * Explains the reason of incompatibility between two components.
     */
    public String explainIncompatibility(String keyComponentFrom, String keyComponentTo)
        throws MissingComponentException
    {
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
     * Returns a copy of the process identifier list.
     */
    public List getProcessIds()
    {
        return new ArrayList(processes.keySet());
    }

    /**
     * Returns the name of a process with the given identifier.
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

    public String getComponentName(String componentId)
        throws MissingComponentException
    {
        final LocalComponent localComponent = borrowComponent(componentId);
        try {
            return localComponent.getName();
        } finally {
            returnComponent(componentId, localComponent);
        }
    }

    /**
     * Enable or disable component autoloading. Missing components
     * will be searched in classpath by converting their identifiers
     * to a resource name.
     */
    public void setComponentAutoload(boolean autoloadOn) {
        this.autoload = autoloadOn;
    }

    /**
     * Cleanup the controller, clear all pools etc. There is no synchronization between
     * this method and {@link #query(String, String, Map)}, so assure synchronization 
     * externally.
     */
    public void destroy() {
        this.destroyed = true;

        // Destroy existing pools.
        for (Iterator i = this.componentPools.values().iterator(); i.hasNext();)
        {
            final GenericObjectPool pool = (GenericObjectPool) i.next();
            try
            {
                pool.close();
            }
            catch (Exception e)
            {
                // We can't do much more.
            }
        }

        // Clear internal maps.
        this.componentPools.clear();
        this.processes.clear();
    }

    /**
     * Cleanup on finalize, if not done manually.
     */
    protected final void finalize() throws Throwable
    {
        super.finalize();

        if (!this.destroyed)
        {
            this.destroy();
        }
    }
}