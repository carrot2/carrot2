
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
import java.util.List;

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

    public final Option benchmarkInputs;
    public final Option benchmarkAlgorithms;
    public final Option benchmarkResults;
    public final Option benchmarkQueries;
    public final Option benchmarkRounds;
    public final Option benchmarkWarmupRounds;
    public final Option benchmarkCacheInput;
    public final Option benchmarkXMLFolder;

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
        benchmarkInputs = createBenchmarkInputs();
        benchmarkAlgorithms = createBenchmarkAlgorithms();
        benchmarkResults = createBenchmarkResults();
        benchmarkQueries = createBenchmarkQueries();
        benchmarkRounds = createBenchmarkRounds();
        benchmarkWarmupRounds = createBenchmarkWarmupRounds();
        benchmarkCacheInput = createCacheInput();
        benchmarkXMLFolder = createBenchmarkXMLFolder();
    }

    /**
     * 
     */
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
     * 
     */
    private Option createBenchmarkXMLFolder()
    {
        final Option option = new Option("f", true,
            "Folder with input XMLs.");
        option.setLongOpt("inputs-dir");
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
     * 
     */
    private Option createBenchmarkInputs()
    {
        final Option option = new Option("i", true, "List of IDs of input components.");
        option.setLongOpt("inputs");
        option.setArgName("id1 id2...");
        option.setRequired(false);
        option.setArgs(Option.UNLIMITED_VALUES);
        option.setType(String[].class);
        return option;
    }

    /**
     * 
     */
    private Option createBenchmarkAlgorithms()
    {
        final Option option = new Option("a", true, "List of IDs of algorithms (default: all available).");
        option.setLongOpt("algorithms");
        option.setArgName("id1 id2...");
        option.setRequired(false);
        option.setArgs(Option.UNLIMITED_VALUES);
        option.setType(String[].class);
        return option;
    }

    /**
     * 
     */
    private Option createBenchmarkResults()
    {
        final Option option = new Option("s", true, "List of request sizes (default: 50 100 150).");
        option.setLongOpt("request-sizes");
        option.setArgName("size1 size2...");
        option.setRequired(false);
        option.setArgs(Option.UNLIMITED_VALUES);
        option.setType(String[].class);
        return option;
    }

    private Option createCacheInput()
    {
        final Option option = new Option("nc", false, "Always refetch data from input sources.");
        option.setLongOpt("no-cache");
        option.setRequired(false);
        return option;
    }

    private Option createBenchmarkWarmupRounds()
    {
        final Option option = new Option("w", true, "Number of warmup rounds (default: 5).");
        option.setLongOpt("warmup-rounds");
        option.setArgName("integer");
        option.setRequired(false);
        option.setType(Number.class);
        return option;
    }

    private Option createBenchmarkRounds()
    {
        final Option option = new Option("r", true, "Number of benchmark rounds (default: 20).");
        option.setLongOpt("rounds");
        option.setArgName("integer");
        option.setRequired(false);
        option.setType(Number.class);
        return option;
    }

    private Option createBenchmarkQueries()
    {
        final Option option = new Option("q", true, "List of queries.");
        option.setLongOpt("queries");
        option.setArgName("query1 query2...");
        option.setRequired(false);
        option.setArgs(Option.UNLIMITED_VALUES);
        option.setType(String[].class);
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

    /**
     * 
     */
    public String [] parseBenchmarkAlgorithmsOption(CommandLine options, ControllerContext context)
        throws ConfigurationException
    {
        final List processIds = context.getProcessIds();

        if (!options.hasOption(benchmarkAlgorithms.getOpt()))
        {
            return (String[]) processIds.toArray(new String[processIds.size()]);
        }

        final String [] algorithms = options.getOptionValues(benchmarkAlgorithms.getOpt());
        for (int i = 0; i < algorithms.length; i++)
        {
            if (!context.getController().getProcessIds().contains(algorithms[i]))
            {
                final String avString = "The following " + "processes are available: "
                    + StringUtils.toString(context.getProcessIds(), ", ");
                throw new ConfigurationException("Process does not exist: " 
                    + algorithms[i] + ". " + avString);
            }
        }

        return algorithms;
    }

    /**
     * 
     */
    public int [] parseBenchmarkResultsOption(CommandLine options) 
        throws ConfigurationException
    {
        if (!options.hasOption(benchmarkResults.getOpt()))
        {
            return new int [] {50, 100, 150};
        }

        final String [] sizes = options.getOptionValues(benchmarkResults.getOpt());
        final int [] outValue = new int[sizes.length];
        for (int i = 0; i < sizes.length; i++)
        {
            try
            {
                outValue[i] = Integer.parseInt(sizes[i]);
                if (outValue[i] <= 0 || outValue[i] > 1000) 
                {
                    throw new NumberFormatException();
                }
            }
            catch (NumberFormatException e)
            {
                throw new ConfigurationException("Input size must be within [1, 1000]: "
                    + sizes[i]);
            }
        }
        return outValue;
    }
}
