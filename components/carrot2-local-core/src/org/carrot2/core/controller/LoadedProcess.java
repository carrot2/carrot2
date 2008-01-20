
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

package org.carrot2.core.controller;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.LocalProcess;


/**
 * An information-holder class for {@link LocalProcess} objects instantiated by
 * {@link ProcessLoader} objects.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LoadedProcess {
    
    /**
     * An attribute that indicates whether this process is the default.
     */
    public final static String ATTRIBUTE_PROCESS_DEFAULT = "process.default";
    
    /**
     * The identifier string of this process.
     */
    private final String id;

    /**
     * The process instance.
     */
    private final LocalProcess process;

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
