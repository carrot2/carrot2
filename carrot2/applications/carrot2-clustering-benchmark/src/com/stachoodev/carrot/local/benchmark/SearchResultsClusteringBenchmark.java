/*
 * SearchResultsClusteringBenchmark.java
 * 
 * Created on 2004-06-29
 */
package com.stachoodev.carrot.local.benchmark;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.impl.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.dawidweiss.carrot.filter.langguesser.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.stachoodev.carrot.filter.lingo.algorithm.*;
import com.stachoodev.carrot.filter.lingo.local.*;
import com.stachoodev.carrot.input.odp.local.*;
import com.stachoodev.carrot.local.benchmark.report.*;
import com.stachoodev.carrot.odp.*;
import com.stachoodev.matrix.factorization.*;

/**
 * Benchmarks search results clustering algorithms available in Carrot2 using
 * Open Directory Project data.
 * 
 * @author stachoo
 */
public class SearchResultsClusteringBenchmark
{
    /** Local controller */
    private LocalController localController;

    /** A map of queries (values) and their ids (keys) */
    private Map queries;

    /** The warm-up query */
    private String warmUpQuery;

    /**
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public SearchResultsClusteringBenchmark(String odpIndexLocation)
        throws IOException, ClassNotFoundException, Exception
    {
        ODPIndex.initialize(odpIndexLocation);

        // Prepare queries
        queries = new HashMap();
        addQueries(queries);

        // Prepare the controller and clustering algorithms
        localController = new ProfiledLocalController();

        addComponentFactories(localController);
        addProcesses(localController);
    }

    /**
     * @param queryMap
     */
    private void addQueries(Map queryMap)
    {
        warmUpQuery = "catid: 42951 6118 557980 4812";
        queryMap.put("computers", "catid: 6083 909542 6142 57728");
    }

    /**
     * @param localController
     */
    private void addProcesses(LocalController localController) throws Exception
    {
        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
        LocalProcessBase lingoNMF = new LocalProcessBase(
            "input.odp",
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer", "filter.lingo-nmf" },
            "ODP -> Language Guesser -> Tokenizer -> LingoNMF", "");
        localController.addProcess("lingo-nmf", lingoNMF);

        // ODP -> Guesser -> Tokenizer -> LingoLNMF -> Output
        LocalProcessBase lingoLNMF = new LocalProcessBase("input.odp",
            "output.cluster-consumer", new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.lingo-lnmf" },
            "ODP -> Language Guesser -> Tokenizer -> LingoLNMF", "");
        localController.addProcess("lingo-lnmf", lingoLNMF);
    }

    /**
     * @param localController
     */
    private void addComponentFactories(LocalController localController)
    {
        // ODP input component
        LocalComponentFactory odpInputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new ODPLocalInputComponent();
            }
        };
        localController.addLocalComponentFactory("input.odp", odpInputFactory);

        // Language guesser component
        LocalComponentFactory languageGuesserFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RawDocumentLanguageDetection(LanguageGuesserFactory
                    .getLanguageGuesser());
            }
        };
        localController.addLocalComponentFactory("filter.language-guesser",
            languageGuesserFilterFactory);

        // Tokenizer filter component
        LocalComponentFactory snippetTokenizerFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new SnippetTokenizerLocalFilterComponent();
            }
        };
        localController.addLocalComponentFactory("filter.tokenizer",
            snippetTokenizerFilterFactory);

        // Lingo NMF filter component
        LocalComponentFactory lingoNMFFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new LingoLocalFilterComponent();
            }
        };
        localController.addLocalComponentFactory("filter.lingo-nmf",
            lingoNMFFilterFactory);

        // Lingo LNMF filter component
        LocalComponentFactory lingoLNMFFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
                LocalNonnegativeMatrixFactorizationFactory matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
                matrixFactorizationFactory.setOrdered(true);
                matrixFactorizationFactory.setK(20);
                matrixFactorizationFactory.setMaxIterations(15);
                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
                    matrixFactorizationFactory);

                return new LingoLocalFilterComponent();
            }
        };
        localController.addLocalComponentFactory("filter.lingo-lnmf",
            lingoLNMFFilterFactory);

        // Cluster consumer output component
        LocalComponentFactory clusterConsumerOutputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RawClustersConsumerLocalOutputComponent();
            }
        };
        localController.addLocalComponentFactory("output.cluster-consumer",
            clusterConsumerOutputFactory);
    }

    /**
     * @param outputDirectory
     * @throws Exception
     * @throws MissingProcessException
     *  
     */
    public void runBenchmarks(String outputDirectory)
        throws MissingProcessException, Exception
    {
        // Where to save the XML reports
        File parent = new File(outputDirectory);
        while (!parent.isDirectory())
        {
            parent = parent.getParentFile();
        }

        // Initialize the main report
        XMLReport mainReport = new XMLReport("report");
        List results = new ArrayList();
        long mainStart = System.currentTimeMillis();

        // For each registered process
        List processNames = localController.getProcessIds();
        for (Iterator iter = processNames.iterator(); iter.hasNext();)
        {
            String processId = (String) iter.next();

            // Let the algorithm process one query to 'warm up' before we
            // start timing. This is to reduce the influence of initialisation
            // of JITs, caches, etc. on timings.
            localController.query(processId, warmUpQuery, new HashMap());

            // Execute each query
            for (Iterator queryIdIter = queries.keySet().iterator(); queryIdIter
                .hasNext();)
            {
                String queryId = (String) queryIdIter.next();
                String query = (String) queries.get(queryId);

                // Execute the query
                long start = System.currentTimeMillis();
                ProcessingResult result = localController.query(processId,
                    query, new HashMap());
                long stop = System.currentTimeMillis();

                // Unwrap results
                List clusters = (List) result.getQueryResult();
                ProfiledRequestContext requestContext = (ProfiledRequestContext) result
                    .getRequestContext();
                List profiles = requestContext.getProfiles();

                // Contribute to the main report
                Map mainInfo = new LinkedHashMap();
                mainInfo.put("Process", processId);
                mainInfo.put("Query", queryId);
                mainInfo.put("Documents", requestContext.getRequestParameters()
                    .get(LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS));
                mainInfo.put("Total time", Long.toString(stop - start) + " ms");
                results.add(mainInfo);

                // Create the detailed report
                XMLReport detailedReport = new XMLReport("report");

                // Add main info
                Map info = new LinkedHashMap();
                info.put("Process", localController.getProcessName(processId));
                info.put("Query", queryId);
                info.put("Documents", requestContext.getRequestParameters()
                    .get(LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS));
                for (Iterator profilesIter = profiles.iterator(); profilesIter
                    .hasNext();)
                {
                    Profile profile = (Profile) profilesIter.next();
                    info.put(profile.getComponentName(), Long.toString(profile
                        .getTotalTimeElapsed())
                        + " ms");
                }
                info.put("Total time", Long.toString(stop - start) + " ms");
                detailedReport.addMap(info, "General information", "info",
                    "entry", "key");

                // Add clusters
                detailedReport.addList(clusters, "Clustering results",
                    "raw-clusters", "raw-cluster");

                // Serialize the detailed report
                detailedReport.serialize(new File(parent, processId + "-"
                    + queryId + ".xml"));
            }
        }
        long mainStop = System.currentTimeMillis();

        // Add some general info
        Map info = new LinkedHashMap();
        info.put("Date", new Date().toString());
        info.put("Processes", Integer.toString(localController.getProcessIds()
            .size()));
        info.put("Queries", Integer.toString(queries.keySet().size()));
        info.put("Total time", Long.toString(mainStop - mainStart) + " ms");
        mainReport.addMap(info, "General information", "info", "entry", "key");

        // Add a list of process ids
        mainReport.addList(localController.getProcessIds(), null, "processes",
            "process");

        // Add the results and serialize
        mainReport.addList(results, "All results", "results", "result");
        mainReport.serialize(new File(parent, "report.xml"));
    }

    /**
     * @param args
     */
    public static void main(String [] args) throws Exception
    {
        String odpIndexLocation = System.getProperty("odp.index.dir");
        String reportDir = System.getProperty("report.dir");

        if (odpIndexLocation == null)
        {
            System.err
                .println("A path to an ODP index must be provided in the odp.index.dir system property");
            return;
        }

        if (reportDir == null)
        {
            System.err
                .println("A directory path for benchmark reports must be provided in the report.dir system property");
            return;
        }

        SearchResultsClusteringBenchmark benchmark;
        benchmark = new SearchResultsClusteringBenchmark(odpIndexLocation);

        benchmark.runBenchmarks(reportDir);
    }
}