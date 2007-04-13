package org.carrot2.dcs.cli;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.carrot2.core.*;
import org.carrot2.core.impl.*;
import org.carrot2.dcs.AppBase;
import org.carrot2.dcs.ControllerContext;
import org.carrot2.util.PerformanceLogger;
import org.carrot2.util.StringUtils;

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
                cluster(processId, context.getController(), f, outputDir);
            }
        }
        catch (Exception e)
        {
            getLogger().fatal("Unhandled program error occurred.", e);
        }
        
        getLogger().info("Finished.");
    }

    /**
     * Run clustering for input files.
     */
    private void cluster(String processName, LocalController controller, File inputFile, File outputDir)
        throws Exception
    {
        // First collect input documents from the file.
        InputStream input = null;
        OutputStream output = null;

        final PerformanceLogger plogger = new PerformanceLogger(Level.DEBUG, this.getLogger());
        ArrayOutputComponent.Result result;
        try
        {
            getLogger().info("Processing file: " + inputFile.getName());

            plogger.start("Processing " + inputFile.getName());

            input = new FileInputStream(inputFile);

            // Phase 1 -- read the XML
            plogger.start("Reading XML");

            final HashMap requestProperties = new HashMap();
            requestProperties.put(XmlStreamInputComponent.XML_STREAM, input);
            result = (ArrayOutputComponent.Result) controller.query(ControllerContext.STREAM_TO_RAWDOCS, "n/a",
                requestProperties).getQueryResult();

            final List documents = result.documents;
            final String query = (String) requestProperties.get(LocalInputComponent.PARAM_QUERY);

            plogger.end("Documents: " + documents.size() + ", query: " + query);

            // Phase 2 -- cluster documents
            plogger.start("Clustering");

            requestProperties.clear();
            requestProperties.put(ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS, documents);
            requestProperties.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, Integer.toString(documents.size()));

            result = (ArrayOutputComponent.Result) controller.query(processName, query, requestProperties)
                .getQueryResult();
            final List clusters = result.clusters;

            plogger.end();

            // Phase 3 -- save the result or emit it somehow.
            plogger.start("Saving result");

            final File outputFile = new File(outputDir, inputFile.getName());
            if (outputDir != null)
            {
                output = new FileOutputStream(outputFile);
            }
            else
            {
                output = new ByteArrayOutputStream();
            }

            requestProperties.clear();
            requestProperties.put(ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS, documents);
            requestProperties.put(ArrayInputComponent.PARAM_SOURCE_RAW_CLUSTERS, clusters);
            requestProperties.put(SaveXmlFilterComponent.PARAM_OUTPUT_STREAM, output);
            requestProperties.put(SaveXmlFilterComponent.PARAM_SAVE_CLUSTERS, Boolean.TRUE);
            controller.query(ControllerContext.RESULTS_TO_XML, query, requestProperties);

            plogger.end(outputDir != null ? outputFile.getAbsolutePath() : "serialization only (no output file)");
        }
        catch (IOException e)
        {
            logger.warn("Failed to process: " + inputFile + " " + StringUtils.chainExceptionMessages(e));
            logger.debug("Failed to process: " + inputFile + " (full stack)", e);
        }
        finally
        {
            plogger.reset();
            if (input != null) input.close();
            if (output != null) output.close();
        }
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
