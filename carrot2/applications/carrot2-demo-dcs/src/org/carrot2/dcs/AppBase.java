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

import java.io.File;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.dcs.cli.BatchApp;
import org.carrot2.dcs.http.DCSApp;
import org.carrot2.util.StringUtils;

/**
 * A base for command-line invoked applications.
 * 
 * @see BatchApp
 * @see DCSApp
 */
public abstract class AppBase
{
    /**
     * Logger for the application.
     */
    private final Logger logger;

    /** Command line options. */
    protected final Options cliOptions = new Options();

    /** Application name. */
    private final String appName;
    
    /** Header text */
    protected final String header;

    /**
     * Initializing constructor.
     * 
     * @param appName Application name (used in help and logger naming).
     */
    protected AppBase(String appName)
    {
        this(appName, "");
    }

    /**
     * Initializing constructor.
     * 
     * @param appName Application name (used in help and logger naming).
     * @param header Header text to be appended before options help
     */
    protected AppBase(String appName, String header)
    {
        this.appName = appName;
        this.logger = Logger.getLogger(appName);
        this.header = header;
    }
    
    /**
     * Initializes application context.
     */
    protected final void go(String [] args)
    {
        initializeOptions(cliOptions);

        if (args.length == 0)
        {
            printHeader();
            printUsage();
            return;
        }

        final Parser parser = new GnuParser();
        final CommandLine line;
        try
        {
            line = parser.parse(cliOptions, args);
            try
            {
                go(line);
            }
            catch (ConfigurationException e)
            {
                getLogger().fatal(StringUtils.chainExceptionMessages(e));
            }
            catch (Throwable e)
            {
                getLogger().fatal("Unhandled program error occurred.", e);
            }
        }
        catch (MissingArgumentException e)
        {
            logger.log(Level.FATAL, "Provide the required argument for option "
                + e.getMessage());
            printUsage();
        }
        catch (MissingOptionException e)
        {
            logger.log(Level.FATAL, "Provide the required option " + e.getMessage());
            printUsage();
        }
        catch (UnrecognizedOptionException e)
        {
            logger.log(Level.FATAL, e.getMessage() + "\n");
            printUsage();
        }
        catch (ParseException exp)
        {
            logger.log(Level.FATAL, "Could not parse command line: " + exp.getMessage());
            printUsage();
        }
    }

    /**
     * Initializes the processing context.
     * 
     * @param descriptorsDir A directory with definitions of processes and descriptors.
     * @throws ConfigurationException if the configuration fails for some reason.
     */
    protected ControllerContext initializeContext(File descriptorsDir) throws ConfigurationException
    {
        logger.info("Initializing components.");

        try {
            final ControllerContext context = new ControllerContext();
    
            if (descriptorsDir.exists() && !descriptorsDir.isDirectory())
            {
                throw new Exception("Components directory not found: "
                    + descriptorsDir.getAbsolutePath());
            }
    
            context.initialize(descriptorsDir, logger);
    
            logger.info("Finished initializing components.");
            return context;
        }
        catch (Exception e)
        {
            throw new ConfigurationException("Could not initialize clustering algorithms. Inspect log files.", e);
        }
    }

    /**
     * Prints header text (if not blank).
     */
    protected void printHeader()
    {
        if (!StringUtils.isBlank(header))
        {
            System.out.println(header);
        }
    }
    
    /**
     * Prints usage (options).
     */
    protected void printUsage()
    {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getCommandName(), cliOptions, true);
    }

    /**
     * Override and write your stuff using command line options.
     */
    protected abstract void go(CommandLine line) throws Exception;

    /**
     * Override and initialize options.
     */
    protected abstract void initializeOptions(Options options);

    /**
     * Override to provide command line app name.
     * 
     * @return command line app name, eg. c2http
     */
    protected String getCommandName()
    {
        return this.appName;
    }

    /**
     * Returns the logger for this class.
     */
    protected final Logger getLogger()
    {
        return logger;
    }
}
