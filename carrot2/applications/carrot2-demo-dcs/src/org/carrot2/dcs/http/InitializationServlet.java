
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

package org.carrot2.dcs.http;

import java.io.File;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.carrot2.dcs.*;

/**
 * Initialization of context parameters in case they are not available (when running in stand-alone mode, context
 * parameters are passed to the context directly).
 */
public final class InitializationServlet extends HttpServlet
{
    /**
     * This key is used to pass initialized instance of
     * (instance of {@link AppConfig}) to the interested servlets
     * (via {@link ServletContext}).
     * 
     * <p>It may also be the case (when running in command-line mode
     * using JETTY) that the configuration is already initialized and present
     * in the servlet context. In such case, this servlet does nothing.
     */
    public static final String ATTR_APPCONFIG = "dcs.app-config";

    /**
     * Perform initialization.
     */
    public void init() throws ServletException
    {
        AppConfig config = (AppConfig) getServletContext().getAttribute(InitializationServlet.ATTR_APPCONFIG);
        if (config != null)
        {
            // Already initialized (JETTY startup).
            config.consoleLogger.info("Console mode, skipping configuration in web.xml.");
            return;
        }

        final HashMap processingOptions = new HashMap();

        // Initialize console logger (hardcoded prefix).
        final Logger dcsLogger = Logger.getLogger("dcs-webapp");

        // Initialize controller context.
        final ControllerContext context = initializeControllerContext(dcsLogger);

        // Initialize default process. If not found, try the first available process.
        processingOptions.put(
            ProcessingOptionNames.ATTR_PROCESSID, 
            initializeDefaultProcess(context, dcsLogger));

        // Initialize other options.
        for (Enumeration e = getInitParameterNames(); e.hasMoreElements();)
        {
            final String paramName = (String) e.nextElement();
            if (ProcessingOptionNames.ATTR_PROCESSID.equals(paramName))
            {
                continue;
            }

            processingOptions.put(paramName, getInitParameter(paramName));
        }

        final ServletContext servletContext = getServletContext();
        servletContext.setAttribute(InitializationServlet.ATTR_APPCONFIG, 
            new AppConfig(context, dcsLogger, processingOptions));
    }

    /**
     * Initialize the controller context. 
     */
    private ControllerContext initializeControllerContext(Logger dcsLogger)
        throws ServletException
    {
        final ControllerContext context = new ControllerContext();
        final File descriptorsDir = new File(getServletContext().getRealPath("algorithms"));
        if (descriptorsDir.exists() && !descriptorsDir.isDirectory())
        {
            final String message = "Components directory not found: " + descriptorsDir.getAbsolutePath();
            dcsLogger.fatal(message);
            throw new ServletException(message);
        }
        context.initialize(descriptorsDir, dcsLogger);
        return context;
    }

    /**
     * Initialize the default process. 
     */
    private String initializeDefaultProcess(ControllerContext context, Logger dcsLogger)
        throws ServletException
    {
        String processId = getInitParameter(ProcessingOptionNames.ATTR_PROCESSID);
        if (processId == null)
        {
            dcsLogger.warn("No " + ProcessingOptionNames.ATTR_PROCESSID 
                + " init parameter specified. Taking the first available algorithm.");

            final List processIds = context.getProcessIds();
            if (processIds.size() == 0)
            {
                final String message = "No algorithms available.";
                dcsLogger.error(message);
                throw new ServletException(message);
            }
            else
            {
                processId = (String) processIds.get(0);
            }
        }
        dcsLogger.debug("Default algorithm set to: " + processId);
        
        return processId;
    }
}
