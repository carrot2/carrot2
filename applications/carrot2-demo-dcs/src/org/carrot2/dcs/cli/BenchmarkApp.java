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

package org.carrot2.dcs.cli;

import java.io.File;
import java.util.*;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.impl.ArrayInputComponent;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.dcs.*;
import org.carrot2.util.PerformanceLogger;
import org.carrot2.util.StringUtils;

/**
 * A command-line benchmarking utility.
 */
public class BenchmarkApp extends AppBase
{
    /** Command-line options */
    private CliOptions opts;

    /** Benchmark stats. */
    private Logger statsLogger = Logger.getLogger("benchmark-results");

    public BenchmarkApp()
    {
        super("benchmark");
    }

    /**
     * Command line entry point (main).
     */
    public static void main(String [] args) throws MissingProcessException, Exception
    {
        new BenchmarkApp().go(args);
    }

    /**
     * Command line entry point (after parsing arguments).
     */
    protected final void go(CommandLine options) throws Exception
    {
        // Toggle verbose mode.
        if (options.hasOption(opts.verbose.getOpt())) getLogger().setLevel(Level.DEBUG);

        if (options.getArgs().length > 0) 
        {
            throw new ConfigurationException("Unrecognized options: "
                + StringUtils.toString(Arrays.asList(options.getArgs()), ", "));
        }

        // Initialize the controller context.
        final ControllerContext context = initializeContext(
            (File) CliOptions.getOption(options, opts.descriptorsDir, new File("descriptors")));

        // Determine input component IDs and build fake processes for these inputs.
        final String [] inputIds = options.getOptionValues(opts.benchmarkInputs.getOpt());
        final String [] inputs = createInputProcesses(inputIds, context.getController());

        // Determine algorithm IDs
        final String [] algorithms = opts.parseBenchmarkAlgorithmsOption(options, context);

        // Input sizes
        final int [] requestSizes = opts.parseBenchmarkResultsOption(options);

        // Queries
        final String [] queries = options.getOptionValues(opts.benchmarkQueries.getOpt());

        // Other options
        final int rounds = ((Integer) CliOptions.getOption(
            options, opts.benchmarkRounds, Integer.valueOf("20"))).intValue();
        final int warmup = ((Integer) CliOptions.getOption(
            options, opts.benchmarkWarmupRounds, Integer.valueOf("5"))).intValue();
        final boolean cacheInput = !options.hasOption(opts.benchmarkCacheInput.getOpt());

        final HashMap requestProperties = new HashMap();
        final PerformanceLogger plogger = new PerformanceLogger(Level.DEBUG, getLogger());

        // Loop over all possibilities.
        for (int sizeIndex = 0; sizeIndex < requestSizes.length; sizeIndex++)
        {
            final int requestSize = requestSizes[sizeIndex];

            for (int inputIndex = 0; inputIndex < inputs.length; inputIndex++)
            {
                final String input = inputs[inputIndex];

                for (int queryIndex = 0; queryIndex < queries.length; queryIndex++)
                {
                    final String query = queries[queryIndex];

                    // Fetch data from the input.
                    ArrayOutputComponent.Result inputData = null;

                    for (int algoIndex = 0; algoIndex < algorithms.length; algoIndex++)
                    {
                        final String algorithm = algorithms[algoIndex];

                        for (int round = 0; round < rounds + warmup; round++)
                        {
                            if (inputData == null || !cacheInput)
                            {
                                requestProperties.put(
                                    LocalInputComponent.PARAM_REQUESTED_RESULTS, 
                                    new Integer(requestSize));

                                plogger.start("Fetching");
                                inputData = (ArrayOutputComponent.Result) context.getController().query(
                                        createInternalName(input), query, 
                                        new HashMap(requestProperties)).getQueryResult();
                                
                                final String logMsg = "results=;" + requestSize 
                                    + ";input=;" + input + ";query=;" + query; 
                                final long duration = plogger.end(Level.INFO, logMsg);
                                statsLogger.info("fetching;" + logMsg + ";duration=;" + duration);
                            }

                            final HashMap tmp = new HashMap(requestProperties);
                            tmp.put(ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS, inputData.documents);
                            tmp.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, 
                                Integer.toString(inputData.documents.size()));

                            plogger.start("Clustering");
                            context.getController().query(algorithm, query, tmp).getQueryResult();
                            
                            final String logMsg = "results=;" + requestSize 
                                + ";input=;" + input + ";query=;" + query + ";algorithm=;" 
                                + algorithm + ";warmup=;" + (round < warmup);
                            final long duration = plogger.end(Level.INFO, logMsg);
                            statsLogger.info("clustering;" + logMsg + ";duration=;" + duration);
                        }
                    }
                }
            }
        }

        getLogger().info("Finished.");
    }

    /**
     * 
     */
    private String [] createInputProcesses(String [] inputs, LocalController controller)
    {
        final String [] NO_FILTERS = new String [0];
        final ArrayList processIds = new ArrayList(inputs.length);
        final List existingProcesses = controller.getProcessIds();

        for (int i = 0; i < inputs.length; i++)
        {
            final String inputComponent = inputs[i];

            if (existingProcesses.contains(inputComponent))
            {
                processIds.add(inputComponent);
                getLogger().warn("Input process with this name already exists, no wrapping: "
                    + inputComponent);
            }
            else
            {
                Throwable t = null;
                try
                {
                    final String internalProcessName = createInternalName(inputComponent);
                    controller.addProcess(internalProcessName, 
                        new LocalProcessBase(inputComponent, "output-demo-webapp", NO_FILTERS));
                    processIds.add(inputComponent);
                }
                catch (InitializationException e)
                {
                    t = e;
                }
                catch (MissingComponentException e)
                {
                    t = e;
                }
                catch (DuplicatedKeyException e)
                {
                    t = e;
                }
                if (t != null) 
                {
                    getLogger().warn("Skipping input component: "
                        + inputComponent + ": " + StringUtils.chainExceptionMessages(t));
                }
            }
        }
        return (String []) processIds.toArray(new String [processIds.size()]);
    }

    /**
     * 
     */
    private String createInternalName(String input)
    {
        return ".internal." + input;
    }

    /**
     * Print usage help.
     */
    protected void printUsage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getCommandName() + " [options] run\n"
            + "The available options are described below.",
            cliOptions, false);
    }

    /**
     * Benchmark processor options.
     */
    protected void initializeOptions(Options options)
    {
        opts = new CliOptions();

        CliOptions.addAll(options, new Object []
        {
            opts.descriptorsDir, 
            opts.verbose,
            opts.benchmarkInputs,
            opts.benchmarkAlgorithms,
            opts.benchmarkCacheInput,
            opts.benchmarkQueries,
            opts.benchmarkResults,
            opts.benchmarkRounds,
            opts.benchmarkWarmupRounds,
        });
    }
}
