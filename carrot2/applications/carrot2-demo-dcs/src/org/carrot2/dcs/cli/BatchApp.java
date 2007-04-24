
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

import java.io.*;
import java.util.*;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.carrot2.core.MissingProcessException;
import org.carrot2.dcs.*;

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

        final String processId = options.getOptionValue("algorithm");
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

                    ProcessingUtils.cluster(processId, context.getController(), getLogger(), inputStream, outputStream, false);
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
    }
}
