
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
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.carrot2.dcs.Config;
import org.carrot2.dcs.ConfigConstants;
import org.carrot2.dcs.ControllerContext;

/**
 * Initialization of context parameters in case they are not available (when running in stand-alone mode, context
 * parameters are passed to the context directly).
 */
public final class InitializationServlet extends HttpServlet
{
    /**
     * Application configuration (instance of {@link Config}. This key is used
     * to pass the configuration to the servlets (via servlet context).
     */
    public static final String ATTR_APPCONFIG = "dcs.app-config";

    /**
     * Perform initialization.
     */
    public void init() throws ServletException
    {
        Config config = (Config) getServletContext().getAttribute(InitializationServlet.ATTR_APPCONFIG);
        if (config == null)
        {
            config = new Config();
        }

        Logger dcsLogger = (Logger) config.getValue(ConfigConstants.ATTR_DCS_LOGGER);
        if (dcsLogger == null)
        {
            dcsLogger = Logger.getLogger("dcs-webapp");
            config.setDefaultValue(ConfigConstants.ATTR_DCS_LOGGER, dcsLogger);
        }

        if (!config.hasValue(ConfigConstants.ATTR_CONTROLLER_CONTEXT))
        {
            final File descriptorsDir = new File(getServletContext().getRealPath("algorithms"));
            final ControllerContext context = new ControllerContext();
            if (descriptorsDir.exists() && !descriptorsDir.isDirectory())
            {
                final String message = "Components directory not found: " + descriptorsDir.getAbsolutePath();
                dcsLogger.fatal(message);
                throw new ServletException(message);
            }

            context.initialize(descriptorsDir, dcsLogger);
            config.setDefaultValue(ConfigConstants.ATTR_CONTROLLER_CONTEXT, context);
        }

        if (!config.hasValue(ConfigConstants.ATTR_DEFAULT_PROCESSID))
        {
            // Look for init. parameter. If not found, try the first available process.
            String processId = getInitParameter(ConfigConstants.ATTR_DEFAULT_PROCESSID);
            if (processId == null)
            {
                dcsLogger.warn("No " + ConfigConstants.ATTR_DEFAULT_PROCESSID 
                    + " init parameter specified. Taking the first available algorithm.");

                final ControllerContext ctx = (ControllerContext) getServletContext()
                    .getAttribute(ConfigConstants.ATTR_CONTROLLER_CONTEXT);
                final List processIds = ctx.getController().getProcessIds();
                for (Iterator i = processIds.iterator(); i.hasNext();)
                {
                    final String id = (String) i.next();
                    if (id.startsWith(".internal"))
                    {
                        continue;
                    }

                    processId = id;
                    break;
                }

                if (processIds == null)
                {
                    final String message = "No algorithms available.";
                    dcsLogger.error(message);
                    throw new ServletException(message);
                }
            }
            config.setDefaultValue(ConfigConstants.ATTR_DEFAULT_PROCESSID, processId);
            dcsLogger.debug("Default algorithm set to: " + processId);
        }

        if (!config.hasValue(ConfigConstants.ATTR_CLUSTERS_ONLY))
        {
            final String clustersOnly = getInitParameter(ConfigConstants.ATTR_CLUSTERS_ONLY);
            config.setDefaultValue(ConfigConstants.ATTR_CLUSTERS_ONLY, Boolean.valueOf(clustersOnly));
            dcsLogger.debug("Clusters only switch (init parameter): " + clustersOnly);
        }

        if (!config.hasValue(ConfigConstants.ATTR_OUTPUT_FORMAT))
        {
            final String outputFormatName = getInitParameter(ConfigConstants.ATTR_OUTPUT_FORMAT);
            final String outputProcessName;
            if ("xml".equals(outputFormatName))
            {
                outputProcessName = ControllerContext.RESULTS_TO_XML;
            }
            else if ("json".equals(outputFormatName))
            {
                outputProcessName = ControllerContext.RESULTS_TO_JSON;
            }
            else
            {
                dcsLogger.warn("Request format invalid: " + outputFormatName);
                return;
            }
            config.setDefaultValue(ConfigConstants.ATTR_OUTPUT_FORMAT, outputProcessName);
        }
        
        final ServletContext servletContext = getServletContext();
        servletContext.setAttribute(InitializationServlet.ATTR_APPCONFIG, config);
    }
}
