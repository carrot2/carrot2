
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;


import java.io.*;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletConfig;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;


/**
 * A shared Log4j initialization class. The default constructor is not public, please use factory
 * method <code>getLog4jStarter</code> to acquire objects of this class.
 */
public class Log4jStarter
{
    /** Singleton instance of Log4jStarter */
    private static Log4jStarter log4jStarter;

    /** Performs static initialization of Log4j at startup. */
    static
    {
        getLog4jStarter();
    }

    private Log4jStarter()
    {
    }

    /**
     * Call this class to acquire an instance of Log4jStarter.
     *
     * @return Always a valid Log4jStarter object.
     */
    public static Log4jStarter getLog4jStarter()
    {
        synchronized (Log4jStarter.class)
        {
            if (log4jStarter != null)
            {
                Logger.getLogger(Log4jStarter.class).debug("Log4jStarter reused.");
            }
            else
            {
                log4jStarter = new Log4jStarter();

                // perform default initialization
                log4jStarter.initializeLog4j();

                // and attempt to override it with the properties user
                // might have defined in tomcat's /conf/log4j.properties
                String containerBase = System.getProperty("resin.home");
                if (containerBase == null) {
                    // try another choice:
                    containerBase = System.getProperty("catalina.base");
                    if (containerBase == null) {
                        // try another choice:
                        containerBase = System.getProperty("resin.home");
                    }
                }

                if (containerBase != null)
                {
                    File confFile = new File(
                            containerBase, "conf" + File.separator + "log4j.properties"
                        );

                    if (confFile.exists() && confFile.canRead())
                    {
                        Properties p = new Properties();
                        InputStream is = null;

                        try
                        {
                            is = new FileInputStream(confFile);
                            p.load(is);
                            convertRelativeLog4jProperties(p, new File(containerBase));
                            log4jStarter.initializeLog4j(p);

                            Logger.getLogger(Log4jStarter.class).info(
                                "Common lib configured log4j."
                            );
                        }
                        catch (IOException e)
                        {
                            System.err.println(
                                "[Carrot2 common lib] Error reading log4j configuration: "
                                + e.toString()
                            );
                        }
                        finally
                        {
                            if (is != null)
                            {
                                try
                                {
                                    is.close();
                                }
                                catch (IOException e)
                                {
                                }
                            }

                            ;
                        }
                    }
                }
            }

            // as for now it doesn't really matter much, so we'll just return a new
            // object every time. In the future maybe a singleton should be used?
            return log4jStarter;
        }
    }


    /**
     * Performs "standard" initialization of log4j. As for now, disables all logging.
     */
    public synchronized void initializeLog4j()
    {
        if (Logger.getRootLogger().getAllAppenders().hasMoreElements() == false)
        {
            org.apache.log4j.BasicConfigurator.configure(
                new AppenderSkeleton()
                {
                    protected void append(LoggingEvent event)
                    {
                        // do nothing.
                    }


                    public void close()
                    {
                    }


                    public boolean requiresLayout()
                    {
                        return true;
                    }
                }
            );
        }
    }


    /**
     * Performs custom initialization of Log4j based on a property file.
     *
     * @param p Properties which should be used to (re)initialize log4j.
     */
    public synchronized void initializeLog4j(Properties p)
    {
        // configure log4j.
        PropertyConfigurator.configure(p);
    }


    /**
     * Performs custom initialization of Log4j based on property file embedded in a web
     * application. A valid ServletConfig object should be passed to this method, where the web
     * application descriptor file (<code>web.xml</code>) defines an input parameter for this
     * servlet named <code>log4j.properties</code>, whose value is translated to a
     * context-relative path and defines a file where properties will be read from. For example,
     * the following section of web.xml file:
     * <pre>
     * <servlet-name>Carrot2 Init Servlet</servlet-name>
     * <servlet-class>com.dawidweiss.carrot.controller.carrot2.Carrot2InitServlet</servlet-class>
     * <init-param>
     *   <param-name>log4j.properties</param-name>
     *   <param-value>/WEB-INF/myapplication-log4j.properties</param-value>
     * </init-param>
     * </pre>
     * defines that log4j properties will be read from
     * <code>{web-application-dir}/WEB-INF/myapplication-log4j.properties</code>. NOTE: all
     * properties starting with a token "TRANSLATE_CONTEXT::" will be replaced with real
     * filesystem application path. For example:
     * <code>log4j.appender.FILE.File=TRANSLATE_CONTEXT::/WEB-INF/carrot2.log</code>  could be
     * rewritten to:
     * <code>log4j.appender.FILE.File=f:/tomcat/webapps/mywebapp/WEB-INF/carrot2.log</code> If
     * initialization parameter log4j.properties is not defined, default initialization of log4j
     * is performed. If the file cannot be read or does not exist, default initialization is
     * performed.
     *
     * @param servletConfig A valid ServletConfig object
     */
    public synchronized void initializeLog4j(ServletConfig servletConfig)
    {
        String init;

        if (
            (servletConfig == null)
                || ((init = servletConfig.getInitParameter("log4j.properties")) == null)
        )
        {
            initializeLog4j();
            Logger.getLogger(this.getClass()).warn(
                "Log4j.properties parameter not defined for servlet: "
                + servletConfig.getServletName()
            );

            return;
        }

        File props = new File(servletConfig.getServletContext().getRealPath(init));

        if (!props.canRead())
        {
            initializeLog4j();
            Logger.getLogger(this.getClass()).error(
                "Log4j.properties file cannot be read from: " + props.getAbsolutePath()
            );

            return;
        }

        Properties p = null;

        try
        {
            p = new Properties();
            p.load(new FileInputStream(props));
        }
        catch (IOException ex)
        {
            initializeLog4j();
            Logger.getLogger(this.getClass()).error(
                "Log4j.properties caused an exception when reading: " + props.getAbsolutePath(), ex
            );

            return;
        }

        for (Iterator i = p.keySet().iterator(); i.hasNext();)
        {
            String key = (String) i.next();

            if (p.getProperty(key).startsWith("TRANSLATE_CONTEXT::"))
            {
                p.setProperty(
                    key,
                    servletConfig.getServletContext().getRealPath(
                        p.getProperty(key).substring("TRANSLATE_CONTEXT::".length())
                    )
                );
            }
            else if (p.getProperty(key).startsWith("TEMP::"))
            {
                String temp = System.getProperty("java.io.tmpdir");
                File ftemp = null;

                if (temp != null)
                {
                    ftemp = new File(temp);
                }
                else
                {
                    File tempReplacement = new File(
                            servletConfig.getServletContext().getRealPath(".")
                        );

                    if (tempReplacement.isDirectory())
                    {
                        ftemp = new File(tempReplacement, "temp");
                        ftemp.mkdir();
                    }
                }

                if (ftemp.isDirectory())
                {
                    ftemp = new File(ftemp, p.getProperty(key).substring("TEMP::".length()));
                    p.setProperty(key, ftemp.getAbsolutePath());
                }
                else
                {
                    throw new java.lang.IllegalArgumentException(
                        "Illegal log4j property value: " + p.getProperty(key)
                        + " cannot find TEMP folder."
                    );
                }
            }
        }

        initializeLog4j(p);
    }


    // --------------------------------------------------------- private section
    private static void convertRelativeLog4jProperties(Properties p, File relativeTo)
    {
        if (relativeTo.isFile())
        {
            relativeTo = relativeTo.getParentFile();
        }

        for (Iterator i = p.keySet().iterator(); i.hasNext();)
        {
            String key = (String) i.next();

            if (p.getProperty(key).startsWith("TRANSLATE_CONTEXT::"))
            {
                String relative = p.getProperty(key).substring("TRANSLATE_CONTEXT::".length());
                File absoluteFile = new File(
                        relativeTo.getAbsolutePath() + relative.replace('/', File.separatorChar)
                    );
                p.setProperty(key, absoluteFile.getAbsolutePath());
            }
        }
    }
}
