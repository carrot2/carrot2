package org.carrot2.webapp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.*;

/**
 * Initializes file appenders to save logs to files named after the context path. Works
 * only if the container supports Servlet API 2.5.
 */
public class FileAppenderInitializationContextListener implements ServletContextListener
{
    static final String LOGGER_APPENDERS_INITIALIZED = "carrot2.webapp.servlet25api";

    public void contextDestroyed(ServletContextEvent event)
    {
    }

    public void contextInitialized(ServletContextEvent event)
    {
        // If the container has Servlet 2.5 API, init loggers here.
        // Otherwise, loggers will need to be initialized on the first request.
        final ServletContext servletContext = event.getServletContext();
        servletContext.setAttribute(LOGGER_APPENDERS_INITIALIZED, false);
        try
        {
            final Method method = ServletContext.class.getMethod("getContextPath");
            final String contextPath = (String) method.invoke(servletContext);
            initLoggers(contextPath);
            servletContext.setAttribute(LOGGER_APPENDERS_INITIALIZED, true);
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            // No servlet 2.5 api, ignore
        }
    }

    /*
     * 
     */
    static void initLoggers(String contextPath) throws IOException
    {
        final File logDir = getLogDir();
        if (logDir == null)
        {
            // No log dir.
            return;
        }

        try
        {
            final String contextPathSegment = getContextPathSegment(contextPath);
    
            final File queryLog = new File(logDir, "c2-" + contextPathSegment
                + "-queries.log");
            final Logger queryLogger = Logger.getLogger(QueryProcessorServlet.QUERY_LOG_NAME);
            queryLogger.addAppender(getQueryLogAppender(queryLog));
    
            final File fullLog = new File(logDir, "c2-" + contextPathSegment + "-full.log");
            Logger.getRootLogger().addAppender(getFullLogAppender(fullLog));
        }
        catch (IOException e)
        {
            Logger.getRootLogger().warn("Could not initialize custom appenders.", e);
        }
    }

    /*
     * 
     */
    private static FileAppender getFullLogAppender(File logFile) throws IOException
    {
        final FileAppender appender = new FileAppender(new PatternLayout(
            "%d{ISO8601},[%p],[%t],%c,%m%n"), logFile.getAbsolutePath(), true);
        appender.setEncoding("UTF-8");
        return appender;
    }

    /*
     * 
     */
    private static FileAppender getQueryLogAppender(File logFile) throws IOException
    {
        final FileAppender appender = new FileAppender(new PatternLayout(
            "%d{ISO8601},%m%n"), logFile.getAbsolutePath(), true);
        appender.setEncoding("UTF-8");
        return appender;
    }

    /**
     * @return Returns the log directory to dump custom logs to. If null is returned, no
     *         log dir is defined and custom appenders should not be attached.
     */
    private static File getLogDir()
    {
        /*
         * Check for custom override of logs location.
         */
        final String customLogFolder = System.getProperty("carrot2.logs");
        if (!StringUtils.isEmpty(customLogFolder))
        {
            File logDir = new File(customLogFolder);
            if (logDir.isDirectory())
            {
                return logDir;
            }
        }

        /*
         * Check for catalina's (Tomcat) default log folder.
         */
        final String catalinaHome = System.getProperty("catalina.home");
        if (!StringUtils.isEmpty(catalinaHome) && new File(catalinaHome).isDirectory())
        {
            final File logDir = new File(new File(catalinaHome), "logs");
            if (logDir.isDirectory())
            {
                return logDir;
            }
        }

        /*
         * If null is returned, no log dir is defined and custom appenders should not be
         * attached.
         */
        return null;
    }

    /*
     * 
     */
    private static String getContextPathSegment(String contextPath)
    {
        if (StringUtils.isBlank(contextPath))
        {
            contextPath = "root";
        }
        contextPath = contextPath.replaceAll("[^a-zA-Z0-9\\-]", "");
        return contextPath;
    }
}
