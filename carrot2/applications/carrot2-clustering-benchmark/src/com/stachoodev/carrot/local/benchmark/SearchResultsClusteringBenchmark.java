/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.local.benchmark;

import java.io.*;
import java.util.*;

import com.chilang.carrot.filter.cluster.local.*;
import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.dawidweiss.carrot.filter.langguesser.*;
import com.dawidweiss.carrot.filter.stc.local.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.stachoodev.carrot.filter.lingo.algorithm.*;
import com.stachoodev.carrot.filter.lingo.local.*;
import com.stachoodev.carrot.filter.normalizer.local.*;
import com.stachoodev.carrot.input.odp.local.*;
import com.stachoodev.carrot.local.benchmark.report.*;
import com.stachoodev.carrot.odp.*;
import com.stachoodev.carrot.output.local.metrics.*;
import com.stachoodev.matrix.factorization.*;
import com.stachoodev.matrix.factorization.seeding.*;

/**
 * Benchmarks search results clustering algorithms available in Carrot2 using
 * Open Directory Project data.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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
        queries = new LinkedHashMap();
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
        // 1244841 - web clustering

        warmUpQuery = "catid: 42951";
        queryMap.put("separation-level-1",
            "catid: 791558 905697 5843791 96938 26950 43585 468909");
        queryMap.put("separation-level-2",
            "catid: 337874 48435 171000 783469 289730 26975 210325");
        queryMap
            .put(
                "separation-level-3",
                "catid: 209353 592083 327 196267 240856 27078 303 27074 283016 27075 1139293 27073");
        queryMap.put("separation-level-4",
            "catid: 136387 504694 86425 96057 92335");
        queryMap.put("separation-level-5",
            "catid: 58029 449902 292594 115111 382391 110783 452210");
        queryMap.put("ai-nn-people", "catid: 354439 5809");
        queryMap.put("polska-level-1",
            "catid: 339598 570229 212801 32496 232622 114095 365576 870296");
        queryMap
            .put(
                "outlier-level-3",
                "catid: 209353 592083 327 196267 240856 27078 303 27074 283016 27075 1139293 27073 1244841");
    }

    /**
     * @param localController
     */
    private void addProcesses(LocalController localController) throws Exception
    {
        // ODP -> RoughKMeans -> Output
        LocalProcessBase roughKMeans = new LocalProcessBase("input.odp",
            "output.cluster-consumer", new String []
            { "filter.rough-k-means" }, "ODP -> RoughKMeans", "");
        localController.addProcess("rough-k-means", roughKMeans);

        // ODP -> Guesser -> STC -> Output
        LocalProcessBase stc = new LocalProcessBase("input.odp",
            "output.cluster-consumer", new String []
            { "filter.language-guesser", "filter.stc" },
            "ODP -> Language Guesser -> STC", "");
        localController.addProcess("stc", stc);

        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
        LocalProcessBase lingoNMF1 = new LocalProcessBase(
            "input.odp",
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.case-normalizer", "filter.lingo-nmf-1" },
            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMF-1",
            "");
        localController.addProcess("lingo-nmf-1", lingoNMF1);

        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
        LocalProcessBase lingoNMF3 = new LocalProcessBase(
            "input.odp",
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.case-normalizer", "filter.lingo-nmf-3" },
            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMF-3",
            "");
        localController.addProcess("lingo-nmf-3", lingoNMF3);

        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
        LocalProcessBase lingoNMFKM1 = new LocalProcessBase(
            "input.odp",
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.case-normalizer", "filter.lingo-nmf-km-1" },
            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMFKM-1",
            "");
        localController.addProcess("lingo-nmf-km-1", lingoNMFKM1);

        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
        LocalProcessBase lingoNMFKM3 = new LocalProcessBase(
            "input.odp",
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.case-normalizer", "filter.lingo-nmf-km-3" },
            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMFKM-3",
            "");
        localController.addProcess("lingo-nmf-km-3", lingoNMFKM3);

        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
        LocalProcessBase lingoLNMF1 = new LocalProcessBase(
            "input.odp",
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.case-normalizer", "filter.lingo-lnmf-1" },
            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoLNMF-1",
            "");
        localController.addProcess("lingo-lnmf-1", lingoLNMF1);

        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
        LocalProcessBase lingoLNMF3 = new LocalProcessBase(
            "input.odp",
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.case-normalizer", "filter.lingo-lnmf-3" },
            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoLNMF-3",
            "");
        localController.addProcess("lingo-lnmf-3", lingoLNMF3);

        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
        LocalProcessBase lingoKM = new LocalProcessBase(
            "input.odp",
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.case-normalizer", "filter.lingo-km-3" },
            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoKM-3",
            "");
        localController.addProcess("lingo-km-3", lingoKM);

        // ODP -> Guesser -> Tokenizer -> LingoSVD -> Output
        LocalProcessBase lingoSVD = new LocalProcessBase(
            "input.odp",
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.case-normalizer", "filter.lingo-svd-3" },
            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoSVD-3",
            "");
        localController.addProcess("lingo-svd-3", lingoSVD);
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

        // Case normalizer filter component
        LocalComponentFactory caseNormalizerFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new CaseNormalizerLocalFilterComponent();
            }
        };
        localController.addLocalComponentFactory("filter.case-normalizer",
            caseNormalizerFilterFactory);

        // Lingo NMF filter component
        LocalComponentFactory lingoNMF1FilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(1));
                return new LingoLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-nmf-1",
            lingoNMF1FilterFactory);

        // Lingo NMF filter component
        LocalComponentFactory lingoNMF3FilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
                NonnegativeMatrixFactorizationEDFactory matrixFactorizationFactory = new NonnegativeMatrixFactorizationEDFactory();
                matrixFactorizationFactory.setK(15);
                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
                    matrixFactorizationFactory);
                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));
                return new LingoLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-nmf-3",
            lingoNMF3FilterFactory);

        // Lingo NMF filter component
        LocalComponentFactory lingoNMFKM1FilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
                LocalNonnegativeMatrixFactorizationFactory matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
                KMeansSeedingStrategyFactory seeding = new KMeansSeedingStrategyFactory();
                seeding.setMaxIterations(3);
                matrixFactorizationFactory.setSeedingFactory(seeding);
                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
                    matrixFactorizationFactory);
                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(1));
                return new LingoLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-nmf-km-1",
            lingoNMFKM1FilterFactory);

        // Lingo NMF filter component
        LocalComponentFactory lingoNMFKM3FilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
                LocalNonnegativeMatrixFactorizationFactory matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
                KMeansSeedingStrategyFactory seeding = new KMeansSeedingStrategyFactory();
                seeding.setMaxIterations(3);
                matrixFactorizationFactory.setSeedingFactory(seeding);
                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
                    matrixFactorizationFactory);
                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));
                return new LingoLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-nmf-km-3",
            lingoNMFKM3FilterFactory);

        // Lingo KM filter component
        LocalComponentFactory lingoKMFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
                KMeansMatrixFactorizationFactory matrixFactorizationFactory = new KMeansMatrixFactorizationFactory();
                matrixFactorizationFactory.setOrdered(true);
                matrixFactorizationFactory.setK(15);
                matrixFactorizationFactory.setMaxIterations(25);
                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
                    matrixFactorizationFactory);
                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));
                return new LingoLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-km-3",
            lingoKMFilterFactory);

        // Lingo LNMF filter component
        LocalComponentFactory lingoLNMF1FilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
                LocalNonnegativeMatrixFactorizationFactory matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
                    matrixFactorizationFactory);
                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(1));
                return new LingoLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-lnmf-1",
            lingoLNMF1FilterFactory);

        // Lingo LNMF filter component
        LocalComponentFactory lingoLNMF3FilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
                LocalNonnegativeMatrixFactorizationFactory matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
                    matrixFactorizationFactory);
                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));
                return new LingoLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-lnmf-3",
            lingoLNMF3FilterFactory);

        // Lingo SVD filter component
        LocalComponentFactory lingoSVDFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
                PartialSingularValueDecompositionFactory matrixFactorizationFactory = new PartialSingularValueDecompositionFactory();
                matrixFactorizationFactory.setK(15);
                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
                    matrixFactorizationFactory);
                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));

                return new LingoLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-svd-3",
            lingoSVDFilterFactory);

        // STC filter component
        LocalComponentFactory stcFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new STCLocalFilterComponent();
            }
        };
        localController
            .addLocalComponentFactory("filter.stc", stcFilterFactory);

        // Rough KMeans clustering filter component
        LocalComponentFactory roughKMeansFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RoughKMeansLocalFilterComponent();
            }
        };
        localController.addLocalComponentFactory("filter.rough-k-means",
            roughKMeansFilterFactory);

        // Cluster consumer output component
        LocalComponentFactory clusterConsumerOutputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RawClustersMetricsLocalOutputComponent();
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

        //        LineNumberReader reader = new LineNumberReader(new
        // InputStreamReader(System.in));

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

                System.out.println(processId + ": " + queryId);

                // Execute the query
                long start = System.currentTimeMillis();
                ProcessingResult result = localController.query(processId,
                    query, new HashMap());
                long stop = System.currentTimeMillis();

                // Unwrap results
                List resultList = (List) result.getQueryResult();
                ProfiledRequestContext requestContext = (ProfiledRequestContext) result
                    .getRequestContext();
                List profiles = requestContext.getProfiles();

                List clusters = (List) resultList.get(0);
                Map metrics = (Map) resultList.get(1);

                // Contribute to the main report
                Map mainInfo = new LinkedHashMap();
                mainInfo.put("Process", processId);
                mainInfo.put("Query", queryId);
                mainInfo.put("Documents", requestContext.getRequestParameters()
                    .get(LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS));
                mainInfo.put("Total time", Long.toString(stop - start) + " ms");
                mainInfo.putAll(metrics);
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

                // Add profiles
                detailedReport.addList(profiles, "Execution profiles",
                    "profiles", "profile");

                // Serialize the detailed report
                detailedReport.serialize(new File(parent, processId + "-"
                    + queryId + ".xml"));
            }

            // It may be a good idea to GC a bit here
            System.gc();
            System.gc();
            System.gc();
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