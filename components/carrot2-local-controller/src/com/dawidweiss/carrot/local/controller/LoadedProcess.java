
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
     * Creates a new loaded process instance.
     *
     * @param id The id of the process.
     * @param process Initialized process instance.
     */
    public LoadedProcess(String id, LocalProcess process) {
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
}
