
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

package org.carrot2.dcs.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.carrot2.dcs.AppConfig;
import org.carrot2.dcs.ControllerContext;

/**
 * A superclass for processor servlets (handling initialization).
 * 
 * @author Dawid Weiss
 */
public abstract class AbstractProcessorServlet extends HttpServlet
{
    /**
     * Local logger.
     */
    protected Logger logger = Logger.getLogger(this.getClass());

    /**
     * DCS logger.
     */
    protected Logger dcsLogger;

    /**
     * Controller context for processing requests.
     */
    protected ControllerContext context;

    /**
     * Application configuration object.
     */
    protected AppConfig config;

    /**
     * Indicates if the servlet was initialized properly.
     */
    private boolean initialized;

    /**
     * 
     */
    public void init() throws ServletException
    {
        super.init();

        config = (AppConfig) getServletContext().getAttribute(
            InitializationServlet.ATTR_APPCONFIG);
        if (config == null)
        {
            final String message = "Expected an configuration object in the servlet context.";
            logger.error(message);
            return;
        }
    
        this.dcsLogger = config.getConsoleLogger();
        this.context = config.getControllerContext();
    
        initialized = true;
    }

    /**
     * @return Returns <code>true</code> if this servlet was successfully initialized
     * (all the required parameters were available in the context).
     */
    protected final boolean isInitialized()
    {
        return this.initialized;
    }
}