/*
 * RequestContextBase.java
 * 
 * Created on 2004-06-28
 */
package com.dawidweiss.carrot.core.local;

import java.util.*;

/**
 * A complete base implementation of the {@link RequestConext}interface used by
 * the {@link com.dawidweiss.carrot.core.local.LocalControllerBase}.
 * 
 * @author stachoo
 */
public class RequestContextBase implements RequestContext
{
    /** Parameters of this request */
    protected Map requestParameters;

    /** An instance of the local controller */
    protected LocalControllerBase controller;

    /** A list of borrowed components */
    protected List borrowedComponents;

    /** Indicates whether this context has been disposed of */
    protected boolean disposed;

    /**
     * Creates a new instance with given local controller instance and given
     * request parameters.
     * 
     * @param controller the instance of {@link LocalControllerBase}that
     *            created this context
     * @param requestParameters request parameters for this context
     */
    public RequestContextBase(LocalControllerBase controller,
        Map requestParameters)
    {
        this.controller = controller;
        this.requestParameters = requestParameters;
        this.borrowedComponents = new ArrayList();
        this.disposed = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.RequestContext#getRequestParameters()
     */
    public Map getRequestParameters()
    {
        return requestParameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.RequestContext#getComponentInstance(java.lang.String)
     */
    public LocalComponent getComponentInstance(String key)
        throws MissingComponentException, Exception
    {
        if (disposed)
        {
            throw new IllegalStateException(
                "This context has already been disposed of");
        }

        LocalComponent component = controller.borrowComponent(key);

        // Can't use a map because it is possible in theory that the same
        // component (same id) can me used more than once in the same process.
        borrowedComponents.add(key);
        borrowedComponents.add(component);

        return component;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.RequestContext#dispose()
     */
    public void dispose() throws Exception
    {
        // Set disposed first, just in case we get errors during returning
        // components
        disposed = true;

        // Return all borrowe components
        for (Iterator iter = borrowedComponents.iterator(); iter.hasNext();)
        {
            String componentId = (String) iter.next();
            LocalComponent localComponent = (LocalComponent) iter.next();

            controller.returnComponent(componentId, localComponent);
        }
    }
}