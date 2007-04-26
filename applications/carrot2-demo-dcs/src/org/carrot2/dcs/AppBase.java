
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

/**
 * A base for command-line invoked applications.
 */
public abstract class AppBase
{
    /**
     * Logger for the application.
     */
    protected final Logger logger;

    /** Command line options. */
    protected final Options cliOptions = new Options();

    /** Application name. */
    private final String appName;

    /** Application configuration. */
    protected final Config config = new Config();
    
    /**
     * Initializing constructor.
     * 
     * @param appName Application name (used in help and logger naming).
     */
    protected AppBase(String appName)
    {
        this.appName = appName;
        this.logger = Logger.getLogger(appName);
    }

    /**
     * Initializes application context.
     */
    protected final void go(String [] args)
    {
        initializeOptions(cliOptions);

        if (args.length == 0)
        {
            printUsage();
            return;
        }

        final Parser parser = new GnuParser();
        final CommandLine line;
        try
        {
            line = parser.parse(cliOptions, args);
            go(line);
        }
        catch (MissingArgumentException e)
        {
            logger.log(Level.FATAL, "Provide the required argument for option " + e.getMessage());
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
     * @throws Exception if the configuration fails for some reason.
     */
    protected ControllerContext initializeContext(File descriptorsDir) throws Exception
    {
        logger.info("Initializing components.");

        final ControllerContext context = new ControllerContext();

        if (descriptorsDir.exists() && !descriptorsDir.isDirectory())
        {
            throw new Exception("Components directory not found: " + descriptorsDir.getAbsolutePath());
        }

        context.initialize(descriptorsDir, logger);

        logger.info("Finished initializing components.");
        return context;
    }

    /**
     * Prints usage (options).
     */
    protected void printUsage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getCommandName(), cliOptions, true);
    }

    /**
     * Override and write your stuff using command line options.
     */
    protected abstract void go(CommandLine line);

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

    /**
     * Get an option, if defined, or return the default value.
     */
    protected final Object getOption(CommandLine options, String optionName, Object defaultValue)
    {
        if (options.hasOption(optionName))
        {
            return options.getOptionObject(optionName);
        }
        else
        {
            return defaultValue;
        }
    }
}
