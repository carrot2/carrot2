/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Stanislaw Osinski, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package org.carrot2.benchmark;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.impl.*;
import org.carrot2.core.impl.ArrayOutputComponent.Result;

/**
 * @author Stanislaw Osinski
 */
public class ClusteringBenchmark
{
    /** Info/debug logger */
    private static final Logger log = Logger
        .getLogger(ClusteringBenchmark.class);

    /** Manages components and processes */
    private BenchmarkContext context;

    /** The number of clustering algorithm warm-up runs */
    private int warmUpRuns = 20;

    /** Input warmup parameters */
    private boolean inputWarmup = true;
    private String inputWarmupQuery = "amiga";
    private int inputWarmupResults = 200;

    private String [] queries = new String []
    {
        "data mining",
        "clustering",
        "chips",
        "clinton",
        "apple",
        "test",
        "salsa",
        "baseball",
        "london",
        "tuning",

        "washington",
        "school",
        "computer",
        "internet",
        "java",
        "php",
        "windows",
        "text",
        "tiger",
        "panther",

        "radio",
        "music",
        "film",
        "arts"
    };

    private int [] resultCounts = new int []
    {
        50, 100, 150, 200, 250, 300, 350, 400
    };

    public void init()
    {
        log.info("Initializing...");
        context = new BenchmarkContext();
        try
        {
            context.initialize();
        }
        catch (Exception e)
        {
            System.out.println("Could not initialize benchmark context: "
                + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void go()
    {
        log.info("Warming up...");
        warmup();
        log.info("Running...");
        run();
        log.info("Finished.");
    }

    private void warmup()
    {
        // Input warmup
        if (inputWarmup)
        {
            run("lucene", "lingo", inputWarmupQuery, inputWarmupResults);
        }

        // Clustering algorithm warmup
        Map parameters = new HashMap();
        parameters.put(XmlDirInputComponent.XML_DIR, new File("queries"));

        for (int i = 0; i < warmUpRuns; i++)
        {
            executeQuery("warmup-lingo", "data-mining.xml", parameters);
        }
    }

    private void run()
    {
        int resultCountIndex = 0;
        for (int i = 0; i < queries.length; i++)
        {
            run("lucene", "lingo", queries[i], resultCounts[resultCountIndex]);

            resultCountIndex = (resultCountIndex + 1) % resultCounts.length;
        }
    }

    private void run(String inputId, String algorithmId, String query,
        int results)
    {
        long start, inputTime, clusteringTime;

        // Get input documents
        Map inputParameters = new HashMap();
        inputParameters.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
            new Integer(results));

        start = System.currentTimeMillis();
        ProcessingResult inputResult = executeQuery("input-" + inputId, query,
            inputParameters);
        inputTime = System.currentTimeMillis() - start;

        ArrayOutputComponent.Result input = (Result) inputResult
            .getQueryResult();

        // Run clustering
        Map benchmarkParameters = new HashMap();
        benchmarkParameters.put(
            RawDocumentsProducerLocalInputComponent.PARAM_SOURCE_RAW_DOCUMENTS,
            input.documents);
        benchmarkParameters.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
            new Integer(results));

        start = System.currentTimeMillis();
        executeQuery("benchmark-" + algorithmId, query, benchmarkParameters);
        clusteringTime = System.currentTimeMillis() - start;

        // Processing times
        System.out.println(inputId + ";" + algorithmId + ";"
            + input.documents.size() + ";input;" + inputTime);
        System.out.println(inputId + ";" + algorithmId + ";"
            + input.documents.size() + ";clustering;" + clusteringTime);
    }

    private ProcessingResult executeQuery(String processId, String query,
        Map inputParameters)
    {
        try
        {
            LocalControllerBase controller = context.getController();
            return controller.query(processId, query, inputParameters);
        }
        catch (Exception e)
        {
            System.out.println("Could not execute query: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * @param args
     */
    public static void main(String [] args)
    {
        ClusteringBenchmark benchmark = new ClusteringBenchmark();
        benchmark.init();
        benchmark.go();
    }
}
