
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.cli.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.core.ProcessingComponentDescriptor.ProcessingComponentDescriptorToId;
import org.carrot2.core.ProcessingComponentSuite;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.source.xml.XmlDocumentSource;
import org.carrot2.text.linguistic.DefaultLexicalDataFactory;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.ReflectionUtils;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.resource.DirLocator;
import org.carrot2.util.resource.FileResource;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;

import org.carrot2.shaded.guava.common.collect.ImmutableMap;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Carrot2 batch processing command line application.
 */
public class BatchApp
{
    private final Logger log = org.slf4j.LoggerFactory.getLogger("batch");

    @Option(name = "-v", aliases =
    {
        "--verbose"
    }, required = false, usage = "Print detailed messages")
    boolean verbose;

    @Option(name = "-o", aliases =
    {
        "--output-dir"
    }, required = false, metaVar = "DIR", usage = "Directory for output files")
    File outputDir = new File("output");

    enum Format
    {
        JSON, XML;
    }

    @Option(name = "-f", aliases =
    {
        "--output-format"
    }, required = false, usage = "Output format")
    Format outputFormat = Format.XML;

    @Option(name = "-d", aliases =
    {
        "--output-documents"
    }, required = false, usage = "Copies input documents on output")
    boolean outputDocuments = false;
    
    @Option(name = "-t", aliases =
    {
        "--output-attributes"
    }, required = false, usage = "Copies attribute values on ouput")
    boolean outputAttributes = false;

    @Option(name = "-a", aliases =
    {
        "--algorithm"
    }, required = false, metaVar = "ALGORITHM", usage = "Identifier or class name of the clustering algorithm to use, see below for the list")
    String algorithm;

    @Argument(metaVar = "INPUT", required = true, usage = "File in Carrot2 XML format or directory of files to cluster")
    List<File> inputFiles;

    int filesClusteredTotal = 0;
    int filesClusteredWithWarnings = 0;

    private ProcessingComponentSuite componentSuite;
    private List<ProcessingComponentDescriptor> algorithms;

    /**
     * Private constructor. Reads the available algorithms from the component suite.
     */
    private BatchApp() throws Exception
    {
        final File suitesDir = new File("suites");
        ResourceLookup suiteLookup = new ResourceLookup(
            new DirLocator(suitesDir));

        IResource suite = suiteLookup.getFirst("suite-batch.xml");
        if (suite == null)
            throw new RuntimeException(
                "Could not find suite-batch.xml in "
                    + suitesDir.getAbsolutePath());

        componentSuite = ProcessingComponentSuite.deserialize(suite, suiteLookup);

        algorithms = componentSuite.getAlgorithms();
        if (algorithms.isEmpty())
        {
            throw new RuntimeException(
                "Component suite does not contain any clustering algorithms.");
        }
    }

    /**
     * Processes all input.
     */
    private int process() throws Exception
    {
        final Controller controller = ControllerFactory.createPooling();
        final Map<String, Object> initAttributes = ImmutableMap.<String, Object> of(
            AttributeUtils.getKey(DefaultLexicalDataFactory.class, "resourceLookup"), 
            new ResourceLookup(new DirLocator("resources")));

        controller.init(
            initAttributes, 
            componentSuite.getComponentConfigurations());        

        // Prepare the algorithm
        if (StringUtils.isBlank(algorithm))
        {
            // Set the first algorithm as the default.
            algorithm = algorithms.get(0).getId();
        }
        else
        {
            // Check if the provided algorithm is valid
            try
            {
                ReflectionUtils.classForName(algorithm, false);
            }
            catch (ClassNotFoundException ignored)
            {
                // See if there's a corresponding algorithm in the suite
                final List<String> algorithmIds = Lists.transform(algorithms,
                    ProcessingComponentDescriptorToId.INSTANCE);
                if (!algorithmIds.contains(algorithm))
                {
                    log.warn("No such algorithm: " + algorithm
                        + ". Available algorithms: " + algorithmIds.toString());
                    return 20;
                }
            }
        }

        if (verbose)
        {
            log.info("Clustering with " + algorithm);
        }

        // Check if the output directory exists, create on if necessary
        if (!checkAndMakeDir(outputDir))
        {
            return 20;
        }

        // Process files in the order they were specified. For input directories,
        // a corresponding directory will be created on output.
        final long start = System.currentTimeMillis();
        for (File file : inputFiles)
        {
            try
            {
                process(file, outputDir, controller);
            }
            catch (Exception e)
            {
                processingWarning(file, e);
            }
        }

        log.info("Clustering of "
            + filesClusteredTotal
            + " files completed"
            + (filesClusteredWithWarnings > 0 ? " with " + filesClusteredWithWarnings
                + " warnings" : "") + " [" + (System.currentTimeMillis() - start)
            + " ms]");

        return filesClusteredWithWarnings == 0 ? 0 : 10;
    }

    /**
     * Files a processing warning.
     */
    private void processingWarning(File processedFile, Exception e)
    {
        filesClusteredWithWarnings++;
        final String message = "Failed to process "
            + (processedFile.isDirectory() ? "directory" : "file") + ": "
            + processedFile.getAbsolutePath();
        if (verbose)
        {
            log.warn(message, e);
        }
        else
        {
            log.warn(message);
        }
    }

    /**
     * Checks if a directory exists and attempts to create one.
     * 
     * @return <code>false</code> if the directory cannot be created
     */
    private boolean checkAndMakeDir(final File dir)
    {
        if (dir.exists())
        {
            if (!dir.isDirectory())
            {
                log.warn("Output directory: " + dir.getAbsolutePath()
                    + " exists, but is not a directory");
                return false;
            }
        }
        else
        {
            if (!dir.mkdirs())
            {
                log.warn("Failed to create output directory: " + dir.getAbsolutePath());
                return false;
            }
        }

        return true;
    }

    /**
     * Processes an individual file or a directory.
     */
    private void process(File fileOrDirectory, File currentOutputDir,
        Controller controller) throws Exception
    {
        if (!fileOrDirectory.exists())
        {
            log.warn("File " + fileOrDirectory.getAbsolutePath() + " does not exist");
            return;
        }

        final String fileName = fileOrDirectory.getName();
        if (fileOrDirectory.isDirectory())
        {
            final File newCurrentOutputDir = new File(currentOutputDir, fileName);
            if (checkAndMakeDir(newCurrentOutputDir))
            {
                for (File fileOrDir : fileOrDirectory.listFiles())
                {
                    try
                    {
                        process(fileOrDir, newCurrentOutputDir, controller);
                    }
                    catch (Exception e)
                    {
                        processingWarning(fileOrDir, e);
                    }
                }
            }
        }
        else
        {
            filesClusteredTotal++;

            final Map<String, Object> attributes = Maps.newHashMap();
            attributes.put("XmlDocumentSource.xml", new FileResource(fileOrDirectory));
            final ProcessingResult result = controller.process(attributes,
                XmlDocumentSource.class.getName(), algorithm);

            // Stick to UTF-8 encoding on the output.
            final String outputFileName = 
                Format.JSON.equals(outputFormat) && fileName.endsWith(".xml") 
                ? fileName.substring(0, fileName .length() - 4) + ".json" : fileName;

            final OutputStream stream = new FileOutputStream(
                new File(currentOutputDir, outputFileName));
            try
            {
                if (Format.JSON.equals(outputFormat))
                {
                    Writer w = new OutputStreamWriter(stream, "UTF-8");
                    result.serializeJson(w, null, outputDocuments, true, outputAttributes);
                    w.flush();
                }
                else
                {
                    result.serialize(stream, outputDocuments, true, outputAttributes);
                }
            }
            finally
            {
                CloseableUtils.close(stream);
            }

            log.info("Clustering " + fileOrDirectory.getAbsolutePath() + " ["
                + result.getAttribute(AttributeNames.PROCESSING_TIME_TOTAL) + "ms]");
        }
    }

    public static void main(String [] args) throws Exception
    {
        final BatchApp batch = new BatchApp();

        final CmdLineParser parser = new CmdLineParser(batch);
        parser.setUsageWidth(80);

        try
        {
            parser.parseArgument(args);
            System.exit(batch.process());
        }
        catch (CmdLineException e)
        {
            System.out.print("Usage: batch");
            parser.printSingleLineUsage(System.out);
            System.out.println();
            parser.printUsage(System.out);

            System.out.println("\n" + e.getMessage());

            final List<String> algorithmIds = Lists.transform(batch.algorithms,
                ProcessingComponentDescriptorToId.INSTANCE);
            System.out.println("\nAvailable algorithms: " + algorithmIds.toString());
        }
    }
}
