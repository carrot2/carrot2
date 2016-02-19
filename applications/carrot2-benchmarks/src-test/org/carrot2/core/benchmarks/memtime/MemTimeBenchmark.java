
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

package org.carrot2.core.benchmarks.memtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IProcessingComponent;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.CloseableUtils;
import org.junit.BeforeClass;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Compute approximate memory and time characteristic for a given algorithm and input.
 */
@SuppressWarnings("unused")
public class MemTimeBenchmark
{
    /**
     * Due to class renames (LanguageCode changed its package) the logged XML files don't
     * deserialize properly. This is a stub wrapper that only reads documents.
     */
    @Root(name = "searchresult", strict = false)
    public static class ResponseWrapper
    {
        @ElementList(inline = true, required = false)
        public List<Document> documents;
    }

    /**
     * A list of input documents. These documents should be some real-life input snippets
     * (preferably short, because longer input is simulated by concatenating documents
     * together).
     */
    protected static ArrayList<Document> documents = new ArrayList<Document>();

    /**
     * Directory with input XML files. Files should be organized using file name
     * convention:
     * 
     * <pre>
     * response - xxxxx.xml
     * </pre>
     * 
     * where <code>xxxxx</code> is a sequential number starting from 0.
     */
    private static File inputFilesDir;

    /**
     * Folder for the output log files.
     */
    private static File outputFilesDir;

    /**
     * Maximum number of input files to read from disk.
     */
    private static int MAX_FILES;

    /**
     * Minimum documents to cluster.
     */
    protected static int MIN;

    /**
     * Maximum documents to cluster.
     */
    protected static int MAX;

    /**
     * Increment step for the documents to cluster range.
     */
    protected static int STEP;

    /**
     * The controller used to drive the clustering process.
     */
    private static Controller controller;

    /**
     * Override defaults with system properties.
     */
    public static void overrideDefaults()
    {
        inputFilesDir = new File(System.getProperty("inputFilesDir", "input"));
        outputFilesDir = new File(System.getProperty("outputFilesDir", "tmp"));
        MAX_FILES = Integer.parseInt(System.getProperty("MAX_FILES", "200"));
        MIN = Integer.parseInt(System.getProperty("MIN", "100"));
        MAX = Integer.parseInt(System.getProperty("MAX", "20000"));
        STEP = Integer.parseInt(System.getProperty("STEP", "100"));
    }

    /**
     * Populate {@link #documents}.
     */
    public static void readData() throws Exception
    {
        Persister p = new Persister();
        for (int i = 0; i < MAX_FILES; i++)
        {
            String fileName = String.format("response-%05d.xml", i);
            ResponseWrapper w = p.read(ResponseWrapper.class, new File(inputFilesDir,
                fileName));

            if (w.documents == null) continue;
            for (Document d : w.documents)
            {
                documents.add(d);
            }
        }
    }

    /**
     * Dump JVM info to the output folder.
     */
    private static void dumpJVMInfo() throws Exception
    {
        String [] properties =
        {
            "java.runtime.name", "java.vm.version", "java.vm.vendor", "java.vm.name",
            "java.vm.specification.name", "java.runtime.version", "os.arch",
            "java.vm.specification.vendor", "os.name", "java.specification.name",
            "sun.management.compiler", "os.version", "java.specification.version",
            "java.vm.specification.version", "sun.arch.data.model",
            "java.specification.vendor", "java.vm.info", "java.version", "java.vendor",
            "sun.cpu.isalist",
        };
        Arrays.sort(properties);

        File output = new File(outputFilesDir, "jvm.log");
        Writer w = null;
        try
        {
            w = new OutputStreamWriter(new FileOutputStream(output), "UTF-8");
            w.write("Benchmark executed at: " + new Date() + "\n\n");

            for (String prop : properties)
            {
                w.write(prop + "=" + System.getProperty(prop, "n/a") + "\n");
            }
            w.write("processors=" + Runtime.getRuntime().availableProcessors() + "\n");
            w.write("\n");
        }
        finally
        {
            CloseableUtils.close(w);
        }
    }

    /**
     * Initialize static data.
     */
    @BeforeClass
    public static void initStaticData() throws Exception
    {
        overrideDefaults();
        readData();
        dumpJVMInfo();
        controller = ControllerFactory.createPooling();
    }

    /**
     * Perform the time/memory evaluation for a single algorithm.
     */
    protected void evalShortDocs(String resultPrefix,
        Class<? extends IProcessingComponent> algorithm, int MIN, int MAX, int STEP)
    {
        final Logger logger = LoggerFactory.getLogger(resultPrefix);

        File output = new File(outputFilesDir, resultPrefix + ".log");
        Writer w = null;
        int docs = 0;
        try
        {
            w = new OutputStreamWriter(new FileOutputStream(output), "UTF-8");

            String header = "docs size[MB] time[s] mem[MB]";
            w.write(header + "\n");
            logger.info(header);

            for (docs = MIN; docs < Math.min(MAX + 1, documents.size()); docs += STEP)
            {
                memClean();
                memPeak();

                final long start = now();
                final HashMap<String, Object> attributes = Maps.newHashMap();
                final List<Document> inputList = documents.subList(0, Math.min(docs,
                    documents.size()));
                attributes.put(AttributeNames.DOCUMENTS, inputList);

                // luceneIndex(inputList);
                controller.process(attributes, algorithm);
                final long end = now();

                final double memUsedMB = memPeak() / (1024 * 1024.0);
                final double timeSecs = (end - start) / 1000.0;
                final double mbLength = countByteLength(inputList) / (1024 * 1024.0);
                final int docsCount = inputList.size();

                final String logLine = String.format(Locale.ENGLISH, "%d %.2f %.2f %.2f",
                    docsCount, mbLength, timeSecs, memUsedMB);

                logger.info(logLine);
                w.write(logLine + "\n");
                w.flush();
            }
        }
        catch (OutOfMemoryError e)
        {
            logger.warn("OOM at: " + docs);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            CloseableUtils.close(w);
        }
    }

    /**
     * Index documents in-memory using Lucene.
     */
    private void luceneIndex(List<Document> inputList)
    {
        try
        {
            Directory dir = new RAMDirectory();
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            IndexWriter w = new IndexWriter(dir, config);

            for (Document d : inputList)
            {
                final org.apache.lucene.document.Document nd = new org.apache.lucene.document.Document();
                nd.add(new TextField("title", StringUtils.defaultIfEmpty(d.getTitle(), ""), Store.NO));
                nd.add(new TextField("snippet", StringUtils.defaultIfEmpty(d.getSummary(), ""), Store.NO));
                w.addDocument(nd);
            }

            w.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Count the overall length of the input (titles and snippets). The length is
     * expressed in characters.
     */
    private static long countByteLength(List<Document> inputList)
    {
        long length = 0;
        for (Document d : inputList)
        {
            if (d.getTitle() != null) length += d.getTitle().length();
            if (d.getSummary() != null) length += d.getSummary().length();
        }
        return length;
    }

    /**
     * @return Return {@link System#currentTimeMillis()}.
     */
    private static long now()
    {
        return System.currentTimeMillis();
    }

    /**
     * Best-effort attempt to force {@link System#gc()}.
     */
    private static void memClean()
    {
        System.gc();
        System.gc();
        Thread.yield();
    }

    /**
     * Return the peak number of bytes used (all memory pools) and reset the peak usage.
     */
    private static long memPeak()
    {
        long peak = 0;
        for (MemoryPoolMXBean b : ManagementFactory.getMemoryPoolMXBeans())
        {
            peak += b.getPeakUsage().getUsed();
            b.resetPeakUsage();
        }

        return peak;
    }
}
