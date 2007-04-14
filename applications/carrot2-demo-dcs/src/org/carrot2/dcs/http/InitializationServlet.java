package org.carrot2.dcs.http;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.carrot2.dcs.ControllerContext;

/**
 * Initialization of context parameters in case they are not available (when running in stand-alone mode, context
 * parameters are passed to the context directly).
 */
public final class InitializationServlet extends HttpServlet
{
    /**
     * Perform initialization.
     */
    public void init() throws ServletException
    {
        final ServletContext servletContext = getServletContext();

        Logger dcsLogger;
        // Check for DCS logger. If not defined, define it.
        if ((dcsLogger = (Logger) servletContext.getAttribute(ServletContextConstants.ATTR_DCS_LOGGER)) == null)
        {
            dcsLogger = Logger.getLogger("dcs-webapp");
            servletContext.setAttribute(ServletContextConstants.ATTR_DCS_LOGGER, dcsLogger);
        }

        if (null == servletContext.getAttribute(ServletContextConstants.ATTR_CONTROLLER_CONTEXT))
        {
            final File descriptorsDir = new File(servletContext.getRealPath("algorithms"));
            final ControllerContext context = new ControllerContext();
            if (descriptorsDir.exists() && !descriptorsDir.isDirectory())
            {
                final String message = "Components directory not found: " + descriptorsDir.getAbsolutePath();
                dcsLogger.fatal(message);
                throw new ServletException(message);
            }

            context.initialize(descriptorsDir, dcsLogger);
            servletContext.setAttribute(ServletContextConstants.ATTR_CONTROLLER_CONTEXT, context);
        }

        if (null == servletContext.getAttribute(ServletContextConstants.ATTR_DEFAULT_PROCESSID))
        {
            // Look for init. parameter. If not found, try the first available process.
            String processId = getInitParameter("algorithm");
            if (processId == null)
            {
                dcsLogger.warn("No 'algorithm' init parameter specified. Taking the first available process.");

                final ControllerContext ctx = (ControllerContext) servletContext
                    .getAttribute(ServletContextConstants.ATTR_CONTROLLER_CONTEXT);
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
            servletContext.setAttribute(ServletContextConstants.ATTR_DEFAULT_PROCESSID, processId);
            dcsLogger.debug("Default algorithm set to: " + processId);
        }
    }
}
