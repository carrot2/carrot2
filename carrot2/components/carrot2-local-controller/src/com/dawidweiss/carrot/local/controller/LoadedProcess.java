
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

import java.util.HashMap;
import java.util.Map;

import com.dawidweiss.carrot.core.local.LocalProcess;


/**
 * An information-holder class for {@link LocalProcess} objects instantiated by
 * {@link ProcessLoader} objects.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LoadedProcess {
    /**
     * The identifier string of this process.
     */
    private String id;

    /**
     * The process instance.
     */
    private LocalProcess process;

    /**
     * Any extra process attributes.
     */
    private final Map attributes;
    
    /**
     * Creates a new loaded process instance.
     *
     * @param id The id of the process.
     * @param process Initialized process instance.
     */
    public LoadedProcess(String id, LocalProcess process) {
        this(id, process, new HashMap());
    }
    
    /**
     * Creates a new loaded process instance with
     * a set of custom attributes (the map is referenced, not copied). 
     */
    public LoadedProcess(String id, LocalProcess process, Map attributes) {
        this.attributes = attributes;
        this.id = id;
        this.process = process;
    }

    /**
     * @return Returns the id of this process.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Returns the instance of the process.
     */
    public LocalProcess getProcess() {
        return process;
    }

    /**
     * @return Returns a map of loaded attributes (never null).
     */
    public Map getAttributes() {
        return attributes;
    }
}
