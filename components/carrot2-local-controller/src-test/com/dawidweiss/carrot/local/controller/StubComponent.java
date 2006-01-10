
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

package com.dawidweiss.carrot.local.controller;

import com.dawidweiss.carrot.core.local.*;

import java.util.Set;


/**
 *  A {@link LocalComponent} stub implementation.
 */
public class StubComponent implements LocalComponent {
    /** */
    private boolean flushedResources;

    /** */
    private boolean processingError;

    /** */
    private LocalControllerContext context;

    /** */
    private boolean initialized;

    /** */
    private LocalComponent next;

    /** */
    private Set predecessorCapabilities;

    /** */
    private Set successorCapabilities;

    /** */
    private Set componentCapabilities;

    /** */
    private String id;

    private String name;

    private String description;

    /**
     * Creates a new StubComponent object.
     */
    public StubComponent(String id, Set componentCapabilities,
        Set predecessorCapabilities, Set successorCapabilities,
        String name, String description) {
        this.id = id;
        this.componentCapabilities = componentCapabilities;
        this.successorCapabilities = successorCapabilities;
        this.predecessorCapabilities = predecessorCapabilities;
        this.name = name;
        this.description = description;
    }

    /* */
    public void init(LocalControllerContext context)
        throws InstantiationException {
        this.initialized = true;
        this.context = context;
    }

    /* */
    public boolean isInitialized() {
        return initialized;
    }

    /* */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException {
        pushOutput(id, "begin");

        if (!(this instanceof StubOutputComponent)) {
            next.startProcessing(requestContext);
        }
    }

    /* */
    public void pushOutput(String id, String message) {
        if (next instanceof StubComponent) {
            ((StubComponent) next).pushOutput(id, message);
        }
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#endProcessing()
     */
    public void endProcessing() throws ProcessingException {
        pushOutput(id, "end");

        if (!(this instanceof StubOutputComponent)) {
            next.endProcessing();
        }
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities() {
        return this.successorCapabilities;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities() {
        return this.predecessorCapabilities;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities() {
        return this.componentCapabilities;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalFilterComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
     */
    public void setNext(LocalComponent next) {
        this.next = next;
    }

    /* */
    public LocalControllerContext getLocalControllerContext() {
        return context;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#processingErrorOccurred()
     */
    public void processingErrorOccurred() {
        this.processingError = true;

        if (!(this instanceof StubOutputComponent)) {
            next.processingErrorOccurred();
        }
    }

    /* */
    public boolean isProcessingError() {
        return this.processingError;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources() {
        this.flushedResources = true;

        if (!(this instanceof StubOutputComponent)) {
            next.flushResources();
        }
    }

    /* */
    public boolean isFlushResources() {
        return this.flushedResources;
    }

    /* */
    public void setProperty(String key, String value) {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
