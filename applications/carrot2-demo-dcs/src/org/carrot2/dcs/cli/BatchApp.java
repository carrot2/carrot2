
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.log4j.Level;
import org.carrot2.core.MissingProcessException;
import org.carrot2.dcs.AppBase;
import org.carrot2.dcs.ConfigConstants;
import org.carrot2.dcs.ControllerContext;
import org.carrot2.dcs.ProcessingUtils;

/**
 * A command-line batch utility for processing XML documents with search results.
 */
public class BatchApp extends AppBase
{
    public BatchApp()
    {
        super("batch");
    }

    /**
     * Command line entry point (main).
     */
    public static void main(String [] args) throws MissingProcessException, Exception
    {
        new BatchApp().go(args);
    }

    /**
     * Command line entry point (after parsing arguments).
     */
    protected final void go(CommandLine options)
    {
        final File outputDir = (File) getOption(options, "output", null);
        final File descriptors = (File) getOption(options, "algorithms", new File("algorithms"));

        super.config.setDefaultValue(ConfigConstants.ATTR_CLUSTERS_ONLY, new Boolean(options.hasOption("co")));
        super.config.setDefaultValue(ConfigConstants.ATTR_DEFAULT_PROCESSID, options.getOptionValue("algorithm"));
        super.config.setDefaultValue(ConfigConstants.ATTR_OUTPUT_FORMAT, 
            options.hasOption("json") ? ControllerContext.RESULTS_TO_JSON :
            options.hasOption("xml") ? ControllerContext.RESULTS_TO_XML : null);

        final boolean verbose = options.hasOption("verbose");
        if (verbose)
        {
            logger.setLevel(Level.DEBUG);
        }

        if (outputDir != null && !outputDir.exists())
        {
            logger.info("Creating output folder: " + outputDir.getAbsolutePath());
            if (!outputDir.mkdirs())
            {
                logger.error("Could not create output folder: " + outputDir.getAbsolutePath());
                return;
            }
        }

        if (outputDir == null)
        {
            logger.warn("Output directory not specified, clustering without saving the result.");
        }

        // Initialize processes.
        final ControllerContext context;
        try
        {
            context = initializeContext(descriptors);
        }
        catch (Exception e)
        {
            getLogger().fatal("Could not initialize clustering algorithms. Inspect log files.", e);
            return;
        }

        final String processId = config.getString(ConfigConstants.ATTR_DEFAULT_PROCESSID);
        if (processId == null || !context.getController().getProcessIds().contains(processId))
        {
            if (processId == null)
            {
                getLogger().fatal("Provide the identifier of a clustering algorithm to use.");
            }
            else
            {
                getLogger().fatal("This clustering algorithm is not available: " + processId);
            }
            return;
        }

        // Collect files to process.
        final String [] unprocessed = options.getArgs();
        final ArrayList files = new ArrayList();

        for (int i = 0; i < unprocessed.length; i++)
        {
            final File file = new File(unprocessed[i]);
            if (!file.exists())
            {
                logger.warn("File does not exist, skipping: " + file);
                continue;
            }

            if (file.isDirectory())
            {
                final File [] subfiles = file.listFiles(new FileFilter()
                {
                    public boolean accept(File subfile)
                    {
                        return (subfile.isFile() && subfile.canRead());
                    }
                });
                files.addAll(Arrays.asList(subfiles));
            }
            else
            {
                files.add(file);
            }
        }

        // Run batch clustering.
        try
        {
            if (files.isEmpty())
            {
                getLogger().warn("Empty list of input files. Provide files and/or directories as program arguments.");
            }

            for (Iterator i = files.iterator(); i.hasNext();)
            {
                final File f = (File) i.next();
                logger.info("Processing file: " + f.getName());

                OutputStream outputStream = null;
                InputStream inputStream = null;
                try
                {
                    inputStream = new FileInputStream(f);
                    if (outputDir != null)
                    {
                        outputStream = new FileOutputStream(new File(outputDir, f.getName()));
                    }
                    else
                    {
                        outputStream = new ByteArrayOutputStream();
                    }

                    final String processName = config.getRequiredString(ConfigConstants.ATTR_DEFAULT_PROCESSID);
                    final String outputProcessName = null;
                    final boolean clustersOnly = config.getRequiredBoolean(ConfigConstants.ATTR_CLUSTERS_ONLY);
                    ProcessingUtils.cluster(context.getController(), getLogger(), inputStream, outputStream, processName, outputProcessName, clustersOnly);
                }
                catch (IOException e)
                {
                    if (outputStream != null) outputStream.close();
                    if (inputStream != null) inputStream.close();
                }
            }
        }
        catch (Exception e)
        {
            getLogger().fatal("Unhandled program error occurred.", e);
        }

        getLogger().info("Finished.");
    }

    /**
     * Print usage help.
     */
    protected void printUsage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getCommandName() + " [options] <file|dir> <file|dir> ...", cliOptions, false);
    }

    /**
     * Batch processor.
     */
    protected void initializeOptions(Options options)
    {
        final Option processName = new Option("algorithm", true, "Identifier of the algorithm used for clustering.");
        processName.setArgName("identifier");
        processName.setRequired(false);
        processName.setType(String.class);
        options.addOption(processName);

        final Option descriptors = new Option("descriptors", true, "Descriptors folder (algorithms).");
        descriptors.setArgName("path");
        descriptors.setRequired(false);
        descriptors.setType(File.class);
        options.addOption(descriptors);

        final Option output = new Option("output", true, "Directory for output files.");
        output.setArgName("path");
        output.setRequired(false);
        output.setType(File.class);
        options.addOption(output);

        final Option verbose = new Option("verbose", false, "Be more verbose.");
        options.addOption(verbose);

        final Option clustersOnly = new Option("co", false, "Skips input documents in the response.");
        clustersOnly.setLongOpt("clusters-only");
        clustersOnly.setRequired(false);
        options.addOption(clustersOnly);

        final OptionGroup outputFormats = new OptionGroup();
        // Add options to the group.
        {
            final Option xmlOutput = new Option("xml", false, "XML output format");
            outputFormats.addOption(xmlOutput);
            final Option jsonOutput = new Option("json", false, "JSON output format");
            outputFormats.addOption(jsonOutput);
        }
        outputFormats.setRequired(true);
        options.addOptionGroup(outputFormats);
    }
}
