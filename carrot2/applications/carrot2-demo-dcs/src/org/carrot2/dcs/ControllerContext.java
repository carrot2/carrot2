
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

package org.carrot2.dcs;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.controller.*;
import org.carrot2.core.controller.loaders.ComponentInitializationException;
import org.carrot2.core.impl.ArrayInputComponent;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.util.StringUtils;

/**
 * {@link LocalController} context with loaded components and processes.
 */
public class ControllerContext
{
    /**
     * Local logger instance.
     */
    private final static Logger logger = Logger.getLogger(ControllerContext.class);

    /**
     * Name of a synthetic process for reading input from an XML stream and placing the result in an array.
     */
    public static final String STREAM_TO_RAWDOCS = ".internal.xml-to-rawdoc-array";

    /**
     * Name of a synthetic process for saving the results (clusters, documents) to an XML stream.
     */
    public static final String RESULTS_TO_XML = ".internal.results-to-xml";

    /**
     * Name of a synthetic process for saving the results (clusters, documents) to a JSON stream.
     */
    public static final String RESULTS_TO_JSON = ".internal.results-to-json";

    /**
     * A map of short abbreviated names of output formats to output process' identifiers.
     */
    private static Map OUTPUT_SHORT_TO_PROCESS;

    /**
     * A map of short abbreviated names of output formats to MIME content type identifiers.
     */
    private static Map OUTPUT_SHORT_TO_CONTENTTYPE;
    
    /**
     * Identifier of the default clustering algorithm
     */
    private String defaultProcessId = null;
    
    /*
     * Initialize static maps. 
     */
    static {
        OUTPUT_SHORT_TO_PROCESS = new HashMap();
        OUTPUT_SHORT_TO_PROCESS.put("xml", RESULTS_TO_XML);
        OUTPUT_SHORT_TO_PROCESS.put("json", RESULTS_TO_JSON);
        
        OUTPUT_SHORT_TO_CONTENTTYPE = new HashMap();
        OUTPUT_SHORT_TO_CONTENTTYPE.put("xml", "text/xml");
        OUTPUT_SHORT_TO_CONTENTTYPE.put("json", "text/json");
    }

    /** 
     * Local Carrot2 controller.
     */
    private final LocalControllerBase controller;

    /**
     *
     */
    public ControllerContext()
    {
        this.controller = new LocalControllerBase();
        this.controller.setComponentAutoload(true);
    }

    /**
     * Initialize the demo context, create local controller and component factories.
     */
    public void initialize(File descriptorsDir, Logger infoLogger)
    {
        final ControllerHelper cl = new ControllerHelper();

        // Add default input/ output components.
        addInputOutputComponents();

        // Now add the user-defined components and processes.
        if (!descriptorsDir.exists() || !descriptorsDir.isDirectory())
        {
            infoLogger.warn("Descriptors directory does not exist: " + descriptorsDir.getAbsolutePath());
            return;
        }

        try
        {
            // Load components.
            final FileFilter componentFilter = new FileFilter()
            {
                private final FileFilter subFilter = cl.getComponentFilter();

                public boolean accept(File file)
                {
                    return this.subFilter.accept(file) && !file.getName().startsWith("alg-");
                }
            };

            LoadedComponentFactory [] factories = cl.loadComponentFactoriesFromDir(descriptorsDir, componentFilter);
            for (int i = 0; i < factories.length; i++)
            {
                final String componentId = factories[i].getId();
                try
                {
                    controller.addLocalComponentFactory(componentId, factories[i].getFactory());
                    logger.debug("Loaded component: " + componentId);
                    infoLogger.debug("Loaded component: " + componentId);
                }
                catch (Exception e)
                {
                    logger.warn("Error loading component: " + componentId, e);
                    infoLogger.warn("Error loading component: " + componentId + " (" + e.getMessage() + ")");
                }
            }

            // Load processes.
            final FileFilter processFilter = new FileFilter()
            {
                public boolean accept(File file)
                {
                    return file.getName().startsWith("alg-");
                }
            };

            final LoadedProcess [] loadedProcesses = cl.loadProcessesFromDir(descriptorsDir, processFilter);
            for (int i = 0; i < loadedProcesses.length; i++)
            {
                final String processId = loadedProcesses[i].getId();

                Object processDefaultAttribute = loadedProcesses[i].getAttributes().get(
                    LoadedProcess.ATTRIBUTE_PROCESS_DEFAULT);
                if (defaultProcessId == null && processDefaultAttribute != null
                    && "true".equalsIgnoreCase(processDefaultAttribute.toString()))
                {
                    defaultProcessId = processId;
                    infoLogger.info("Setting the context-level default process id to: "
                        + defaultProcessId);
                    logger.info("Setting the context-level default process id to: "
                        + defaultProcessId);
                }

                try
                {
                    controller.addProcess(loadedProcesses[i].getId(), loadedProcesses[i]
                        .getProcess());
                    logger.debug("Loaded algorithm: " + processId);
                    infoLogger.info("Loaded algorithm: " + processId);
                }
                catch (Exception e)
                {
                    logger.warn("Error loading algorithm: " + processId, e);
                    infoLogger.warn("Error loading algorithm: " + processId + " ("
                        + StringUtils.chainExceptionMessages(e) + ")");
                }
            }
            
            if (defaultProcessId == null && loadedProcesses.length > 0)
            {
                defaultProcessId = loadedProcesses[0].getId();
                infoLogger.info("Setting the context-level default process id "
                    + "to the first on the list: " + defaultProcessId);
                logger.info("Setting the context-level default process id "
                    + "to the first on the list: " + defaultProcessId);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unhandled exception.", e);
        }
        catch (ComponentInitializationException e)
        {
            throw new RuntimeException("Unhandled exception.", e);
        }
        catch (LoaderExtensionUnknownException e)
        {
            throw new RuntimeException("Unhandled exception.", e);
        }
    }

    /**
     * Adds default input and output sink components to the controller.
     */
    private void addInputOutputComponents()
    {
        try
        {
            // Add an input producer component now.
            final LocalComponentFactory inputFactory = new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new ArrayInputComponent();
                }
            };
            controller.addLocalComponentFactory("input", inputFactory);

            // Add an output collector.
            final LocalComponentFactory outputFactory = new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new ArrayOutputComponent();
                }
            };
            controller.addLocalComponentFactory("output", outputFactory);

            // Add aliases so that porting algorithms from the webapp is simpler.
            controller.addLocalComponentFactory("input-demo-webapp", inputFactory);
            controller.addLocalComponentFactory("output-demo-webapp", outputFactory);

            //
            // Add internal processes for reading/saving data streams.
            //
            try
            {
                controller.addProcess(STREAM_TO_RAWDOCS, new LocalProcessBase("input-xml-stream", "output",
                    new String []
                    {
                        "filter-rawdocument-enumerator"
                    }));
                controller.addProcess(RESULTS_TO_XML, new LocalProcessBase("input-array", "output-array",
                    new String []
                    {
                        "filter-save-xml"
                    }));
                controller.addProcess(RESULTS_TO_JSON, new LocalProcessBase("input-array", "output-array",
                    new String []
                    {
                        "filter-save-json"
                    }));
            }
            catch (Exception e)
            {
                throw new RuntimeException("Unexpected exception when configuring internal processes.", e);
            }
        }
        catch (DuplicatedKeyException e)
        {
            // not-reachable
            throw new RuntimeException("Unreachable block.", e);
        }
    }

    /**
     * Returns the process controller preconfigured in the context.
     */
    public LocalController getController()
    {
        return this.controller;
    }

    /**
     * Returns the default process id or <code>null</code> if none of the processes
     * had the <code>process.default</code> attribute set to <code>true</code>.
     * 
     * @return
     */
    public String getDefaultProcessId() {
        return defaultProcessId;
    }
    
    /**
     * Returns the identifier of a process for its abbreviated name.
     * 
     * @see #OUTPUT_SHORT_TO_PROCESS
     */
    public static String getOutputProcessId(String abbreviated)
    {
        final String processId = (String) OUTPUT_SHORT_TO_PROCESS.get(abbreviated);
        if (processId == null)
        {
            throw new IllegalArgumentException("An abbreviation for an unknown process: " + abbreviated);
        }
        return processId;
    }

    /**
     * Returns the content type for the selected output format.
     */
    public static String getContentTypeFor(String abbreviated)
    {
        final String contentType = (String) OUTPUT_SHORT_TO_CONTENTTYPE.get(abbreviated);
        if (contentType == null)
        {
            throw new IllegalArgumentException("An abbreviation for an unknown process: " + abbreviated);
        }
        return contentType;
    }

    /**
     * Returns a list of process identifiers (filtered from internal processes).
     */
    public List getProcessIds()
    {
        final ArrayList list = new ArrayList(getController().getProcessIds());
        for (Iterator i = list.iterator(); i.hasNext();)
        {
            final String processId = (String) i.next();
            if (processId.startsWith("."))
            {
                i.remove();
            }
        }
        return list;
    }
}