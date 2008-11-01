package org.carrot2.webapp;

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

    static void initLoggers(String contextPath) throws IOException
    {
        final Logger queryLogger = Logger.getLogger(QueryProcessorServlet.QUERY_LOG_NAME);
        queryLogger.addAppender(getQueryLogAppender(contextPath));
        Logger.getRootLogger().addAppender(
            FileAppenderInitializationContextListener.getFullLogAppender(contextPath));
    }

    static FileAppender getFullLogAppender(String contextPath) throws IOException
    {
        final String logPrefix = getLogDirPrefix();
        final FileAppender appender = new FileAppender(new PatternLayout(
            "%d{ISO8601},[%p],[%t],%c,%m%n"), logPrefix + "/c2-"
            + getContextPathSegment(contextPath) + "-full.log", true);
        appender.setEncoding("UTF-8");
        return appender;
    }

    static FileAppender getQueryLogAppender(String contextPath) throws IOException
    {
        final String logPrefix = getLogDirPrefix();
        final FileAppender appender = new FileAppender(new PatternLayout(
            "%d{ISO8601},%m%n"), logPrefix + "/c2-" + getContextPathSegment(contextPath)
            + "-queries.log", true);
        appender.setEncoding("UTF-8");
        return appender;
    }

    private static String getLogDirPrefix()
    {
        final String catalinaHome = System.getProperty("catalina.home");
        return catalinaHome != null ? catalinaHome + "/logs" : "";
    }

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
