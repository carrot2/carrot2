
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
import org.carrot2.util.StringUtils;

/**
 * Shared methods for adding and parsing command line {@link Option}s.
 */
public final class CliOptions
{
    public final Option processName;
    public final Option descriptorsDir;
    public final Option outputFolder;

    public final Option verbose;
    public final Option clustersOnly;

    public final Option outputFormat;

    public final Option port;

    /**
     *
     */
    public CliOptions()
    {
        processName = createProcessName();
        descriptorsDir = createDescriptorsDir();
        outputFolder = createOutputFolder();
        verbose = createVerbose();
        clustersOnly = createClustersOnly();
        outputFormat = createOutputFormats();
        port = createPortOption();
    }

    private Option createPortOption()
    {
        final Option option = new Option("port", true, "Socket number to bind to.");
        option.setArgName("number");
        option.setRequired(true);
        option.setType(Number.class);
        return option;
    }

    /**
     * Select the output format.
     */
    private Option createOutputFormats()
    {
        final Option option = new Option("f", true,
            "Select default output format. Allowed values: json, xml.");
        option.setArgName("format");
        option.setLongOpt("output-format");
        option.setRequired(false);

        return option;
    }

    /**
     * Enables verbose logging.
     */
    private Option createClustersOnly()
    {
        final Option options = new Option("co", false,
            "Skip repeating input documents in the clustered output.");
        options.setLongOpt("clusters-only");
        options.setRequired(false);
        return options;
    }

    /**
     * Enables verbose logging.
     */
    private Option createVerbose()
    {
        final Option option = new Option("v", false, "Be more verbose.");
        option.setLongOpt("verbose");
        return option;
    }

    /**
     * Selection of the folder where component/algorithm descriptors are placed.
     */
    private Option createOutputFolder()
    {
        final Option option = new Option("o", true, "Folder for output files.");
        option.setLongOpt("output-dir");
        option.setArgName("path");
        option.setRequired(false);
        option.setType(File.class);
        return option;
    }

    /**
     * Selection of the folder where component/algorithm descriptors are placed.
     */
    private Option createDescriptorsDir()
    {
        final Option option = new Option("d", true,
            "Folder with component and process descriptors.");
        option.setLongOpt("descriptors-dir");
        option.setArgName("path");
        option.setRequired(false);
        option.setType(File.class);
        return option;
    }

    /**
     * Selection of the default algorithm.
     */
    private Option createProcessName()
    {
        final Option option = new Option("p", true,
            "Identifier of the default process (algorithm) used for clustering.");
        option.setLongOpt("process");
        option.setArgName("identifier");
        option.setRequired(false);
        option.setType(String.class);
        return option;
    }

    /**
     * Return the option's value or, if undefined, the default value.
     */
    public static Object getOption(CommandLine options, Option option, Object defaultValue)
    {
        if (options.hasOption(option.getOpt()))
        {
            return options.getOptionObject(option.getOpt());
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Adds all {@link Option}s and {@link OptionGroup}s from <code>array</code> to
     * <code>option</code>.
     */
    public static void addAll(Options options, Object [] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            final Object o = array[i];
            if (o instanceof OptionGroup)
            {
                options.addOptionGroup((OptionGroup) o);
            }
            else if (o instanceof Option)
            {
                options.addOption((Option) o);
            }
            else
            {
                throw new IllegalArgumentException("Option or OptionGroup required: "
                    + o.getClass());
            }
        }
    }

    /**
     * Parses the content of {@link #processName} option and throws a
     * {@link ConfigurationException} if the process is not present in the
     * {@link ControllerContext} or if it is <code>null</code>.
     */
    public String parseProcessIdOption(CommandLine options, ControllerContext context)
        throws ConfigurationException
    {
        final String avString = "The following " + "processes are available: "
            + StringUtils.toString(context.getProcessIds(), ", ");

        final String processId = (String) CliOptions.getOption(options, processName,
            context.getDefaultProcessId());
        
        if (processId == null)
        {
            throw new ConfigurationException(
                "Provide a valid name of the default process. " + avString);
        }

        if (!context.getController().getProcessIds().contains(processId))
        {
            throw new ConfigurationException("Process does not exist: " + processId
                + ". " + avString);
        }

        return processId;
    }

    /**
     * Parses the content of {@link #outputFormat} option and throws a
     * {@link ConfigurationException} if the output format is not valid.
     */
    public String parseOutputFormat(CommandLine options) 
        throws ConfigurationException
    {
        String outputFormat = options.getOptionValue(this.outputFormat.getOpt());
        if (outputFormat == null)
        {
            outputFormat = "xml";
        }
        try
        {
            ControllerContext.getOutputProcessId(outputFormat);
        }
        catch (IllegalArgumentException e)
        {
            throw new ConfigurationException("This output format is not available: "
                + outputFormat);
        }
        return outputFormat;
    }
}
