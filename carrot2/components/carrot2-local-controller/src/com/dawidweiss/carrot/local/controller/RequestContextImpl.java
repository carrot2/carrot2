
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

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.MissingComponentException;
import com.dawidweiss.carrot.core.local.RequestContext;

import java.util.ArrayList;
import java.util.Map;


/**
 * An implementation of a {@link RequestContext} interface.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class RequestContextImpl implements RequestContext {
    /**
     * Local controller that this request context is assigned to.
     */
    private LocalController controllerContext;

    /**
     * Request parameters.
     */
    private Map requestParams;

    /**
     * A list of resources used so far in this request. This usually includes
     * component instances acquired from the controller.
     */
    private ArrayList usedResources = new ArrayList();

    /**
     * Creates a new RequestContextImpl object.
     *
     * @param controllerContext The controller to assign this request context
     *        to.
     * @param requestParams Request parameters.
     */
    public RequestContextImpl(LocalController controllerContext,
        Map requestParams) {
        this.requestParams = requestParams;
        this.controllerContext = controllerContext;
    }

    /** 
     * @return Returns a map of request parameters.
     */
    public Map getRequestParameters() {
        return requestParams;
    }

    /**
     * @return Returns an instance of a component with the specified
     * identifier. 
     *
     * @see RequestContext#getComponentInstance(String)
     */
    public LocalComponent getComponentInstance(String key)
        throws MissingComponentException, Exception {
        LocalComponent component = this.controllerContext.borrowComponent(key);
        this.usedResources.add(key);
        this.usedResources.add(component);

        return component;
    }

    /**
     * Returns all used resources back to where they where acquired from.
     */
    public void returnResources() {
        Exception error = null;

        for (int i = 0; i < usedResources.size(); i += 2) {
            try {
                this.controllerContext.returnComponent((String) usedResources.get(
                        i), (LocalComponent) usedResources.get(i + 1));
            } catch (Exception e) {
                error = e;
            }
        }

        if (error != null) {
            throw new RuntimeException("Error releasing resources.", error);
        }
    }

    public void dispose() throws Exception {
    }
}
