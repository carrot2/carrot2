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
    /** Command-line options */
    private CliOptions opts;

    public BatchApp()
    {
        super("batch");
    }
    
    public BatchApp(String header)
    {
        super("batch", header);
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
    protected final void go(CommandLine options) throws Exception
    {
        // Toggle verbose mode.
        if (options.hasOption(opts.verbose.getOpt())) getLogger().setLevel(Level.DEBUG);

        // Collect files to process.
        final ArrayList files = collectInputFiles(options.getArgs());
        if (files.isEmpty())
        {
            throw new ConfigurationException(
                "Empty list of input files. Provide files and/or directories " +
                "as program arguments.");
        }

        // Initialize the controller context.
        final ControllerContext context = initializeContext(
            (File) CliOptions.getOption(options, opts.descriptorsDir, new File("descriptors")));

        final HashMap processingOptions = new HashMap();
        processingOptions.put(
            ProcessingOptionNames.ATTR_PROCESSID, 
            opts.parseProcessIdOption(options, context));
        processingOptions.put(
            ProcessingOptionNames.ATTR_OUTPUT_FORMAT, 
            opts.parseOutputFormat(options));
        processingOptions.put(
            ProcessingOptionNames.ATTR_CLUSTERS_ONLY, 
            Boolean.toString(options.hasOption(opts.clustersOnly.getOpt())));

        // Initialize the output folder.
        final File outputDir = getOutputDirectory(options);

        // Run batch clustering.
        for (Iterator i = files.iterator(); i.hasNext();)
        {
            final File f = (File) i.next();
            getLogger().info("Processing file: " + f.getName());

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

                ProcessingUtils.cluster(context.getController(), getLogger(),
                    inputStream, outputStream, processingOptions);
            }
            catch (IOException e)
            {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
            }
        }

        getLogger().info("Finished.");
    }

    /**
     * Collects input files to be processed.
     */
    private ArrayList collectInputFiles(String [] unprocessed)
    {
        final ArrayList files = new ArrayList(unprocessed.length);
        for (int i = 0; i < unprocessed.length; i++)
        {
            final File file = new File(unprocessed[i]);
            if (!file.exists())
            {
                getLogger().warn("File does not exist, skipping: " + file);
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
        return files;
    }

    /**
     * Creates or tests the output folder.
     */
    private File getOutputDirectory(CommandLine options)
    {
        final File outputDir = (File) CliOptions.getOption(options, opts.outputFolder,
            null);
        if (outputDir != null)
        {
            if (!outputDir.exists())
            {
                getLogger().info("Creating output folder: " + outputDir.getAbsolutePath());
                if (!outputDir.mkdirs())
                {
                    final String message = "Could not create output folder: "
                        + outputDir.getAbsolutePath();
                    getLogger().error(message);
                    throw new RuntimeException(message);
                }
            }
        }
        else
        {
            getLogger().warn("Output directory not specified, clustering without saving the result.");
        }

        return outputDir;
    }

    /**
     * Print usage help.
     */
    protected void printUsage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getCommandName() + " [options] <INPUT>, <INPUT>...\n\n"
            + "INPUT can be a file or a directory.\n"
            + "The available options are described below.",
            cliOptions, false);
    }

    /**
     * Batch processor.
     */
    protected void initializeOptions(Options options)
    {
        opts = new CliOptions();

        CliOptions.addAll(options, new Object []
        {
            opts.descriptorsDir, opts.processName, opts.outputFormat, opts.clustersOnly,
            opts.verbose, opts.outputFolder
        });
    }
}
