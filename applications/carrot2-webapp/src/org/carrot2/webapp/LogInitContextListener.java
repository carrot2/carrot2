
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map.Entry;

import javax.servlet.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.*;

import org.carrot2.shaded.guava.common.collect.*;

/**
 * Initializes file appenders to save logs to files named after the context path. Works
 * only if the container supports Servlet API 2.5.
 */
public class LogInitContextListener implements ServletContextListener
{
    /**
     * An instance of this class will save itself in the application context under this
     * identifier.
     */
    public static final String CONTEXT_ID = LogInitContextListener.class.getName();

    /**
     * Any created {@link Appender}s.
     */
    private Multimap<Logger, Appender> appenders = ArrayListMultimap.create();

    /**
     * Callback hook from the application container. Initialize logging appenders
     * immediately or defer initialization until possible.
     */
    public void contextInitialized(ServletContextEvent event)
    {
        final ServletContext servletContext = event.getServletContext();
        if (servletContext.getAttribute(CONTEXT_ID) != null)
        {
            // Only one instance needed.
            return;
        }

        /*
         * If the container has Servlet 2.5 API, init loggers immediately. Otherwise, save
         * itself to the context and wait for {@link QueryProcessorServlet} to perform
         * deferred initialization.
         */
        try
        {
            final Method method = ServletContext.class.getMethod("getContextPath");
            final String contextPath = (String) method.invoke(servletContext);
            addAppenders(contextPath);
        }
        catch (RuntimeException e)
        {
            // Rethrow runtime exceptions, if any.
            throw e;
        }
        catch (Exception e)
        {
            /*
             * No servlet 2.5 API (getContextPath), save in the context for deferred
             * initialization.
             */
            servletContext.setAttribute(CONTEXT_ID, this);
        }
    }

    /**
     * Callback hook from the application container. Cleanup any created appenders and
     * unregister them from their loggers.
     */
    public void contextDestroyed(ServletContextEvent event)
    {
        for (Entry<Logger, Collection<Appender>> e : appenders.asMap().entrySet())
        {
            final Logger logger = e.getKey();
            for (Appender a : e.getValue())
            {
                logger.removeAppender(a);
                a.close();
            }
        }
        appenders.clear();
    }

    /**
     * Add appenders based on the provided context path.
     */
    void addAppenders(String contextPath)
    {
        final File logDir = getLogDir();
        if (logDir == null)
        {
            // No log dir.
            return;
        }

        try
        {
            final String contextPathName = getContextPathSegment(contextPath);

            addAppender(new File(logDir, "c2-" + contextPathName + "-queries.log"),
                Logger.getLogger(QueryProcessorServlet.QUERY_LOG_NAME), "%d{ISO8601},%m%n");

            addAppender(new File(logDir, "c2-" + contextPathName + "-full.log"), Logger
                .getRootLogger(), "%d{ISO8601},[%p],[%t],%c,%m%n");
        }
        catch (IOException e)
        {
            Logger.getRootLogger().warn("Could not initialize custom appenders.", e);
        }
    }

    /*
     * 
     */
    private void addAppender(File file, Logger logger, String pattern) throws IOException
    {
        final FileAppender appender = new DailyRollingFileAppender(new PatternLayout(pattern), file
            .getAbsolutePath(), "'.'yyyy-MM-dd");
        appender.setEncoding("UTF-8");
        appender.setAppend(true);

        logger.addAppender(appender);
        appenders.put(logger, appender);
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
        final String catalinaBase = System.getProperty("catalina.base");
        if (!StringUtils.isEmpty(catalinaBase) && new File(catalinaBase).isDirectory())
        {
            final File logDir = new File(new File(catalinaBase), "logs");
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
