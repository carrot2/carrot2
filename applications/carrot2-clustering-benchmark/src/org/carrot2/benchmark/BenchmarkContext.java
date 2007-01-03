
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.controller.ControllerHelper;
import org.carrot2.core.controller.LoadedProcess;
import org.carrot2.core.controller.loaders.ComponentInitializationException;

/**
 * @author Stanislaw Osinski
 */
public class BenchmarkContext
{
    /** Local Carrot2 controller */
    private LocalControllerBase controller;

    /**
     * A list of {@link org.carrot2.core.controller.LoadedProcess} objects
     * loaded from processes folder.
     */
    private List loadedProcesses;

    public BenchmarkContext()
    {
        this.controller = new LocalControllerBase();
        this.controller.setComponentAutoload(true);
    }

    /**
     * Initialize the demo context, create local controller and component
     * factories.
     */
    public void initialize() throws InitializationException,
        MissingComponentException, DuplicatedKeyException, IOException,
        ComponentInitializationException
    {
        final ControllerHelper cl = new ControllerHelper();

        final File componentsDir = new File("components");
        if (componentsDir.isDirectory() == false)
        {
            throw new RuntimeException("Components directory not found: "
                + componentsDir.getAbsolutePath());
        }
        final File processesDir = new File("processes");
        if (processesDir.isDirectory() == false)
        {
            throw new RuntimeException("Components directory not found: "
                + componentsDir.getAbsolutePath());
        }

        cl.addAll(controller, cl.loadComponentFactoriesFromDir(componentsDir));
        this.loadedProcesses = Arrays.asList(cl
            .loadProcessesFromDir(processesDir));

        //
        // Add scripted/ custom components and processes
        //
        for (Iterator i = loadedProcesses.iterator(); i.hasNext();)
        {
            final LoadedProcess lp = (LoadedProcess) i.next();
            controller.addProcess(lp.getId(), lp.getProcess());
        }
    }

    public LocalControllerBase getController()
    {
        return controller;
    }
}
