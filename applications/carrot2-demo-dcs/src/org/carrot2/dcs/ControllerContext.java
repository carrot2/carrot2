package org.carrot2.dcs;

import java.io.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.controller.*;
import org.carrot2.core.controller.loaders.ComponentInitializationException;
import org.carrot2.core.impl.*;
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

    /** Local Carrot2 controller */
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
                final String processName = loadedProcesses[i].getId();
                try
                {
                    controller.addProcess(loadedProcesses[i].getId(), loadedProcesses[i].getProcess());
                    logger.debug("Loaded algorithm: " + processName);
                    infoLogger.info("Loaded algorithm: " + processName);
                }
                catch (Exception e)
                {
                    logger.warn("Error loading algorithm: " + processName, e);
                    infoLogger.warn("Error loading algorithm: " + processName + " ("
                        + StringUtils.chainExceptionMessages(e) + ")");
                }
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
}