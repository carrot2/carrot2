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
 * TODO: support for query description?
 * TODO: option for turning on/off profiling for particular components?
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
        warmUpQuery = "catid: 42951";
//        queryMap.put("separation-level-1",
//            "catid: 791558 905697 5843791 96938 26950 43585 468909");
//        queryMap.put("separation-level-2",
//            "catid: 337874 48435 171000 783469 289730 26975 210325");
//        queryMap
//            .put(
//                "separation-level-3",
//                "catid: 209353 592083 327 196267 240856 27078 303 27074 283016 27075 1139293 27073");
//        queryMap.put("separation-level-4",
//            "catid: 136387 504694 86425 96057 92335");
//        queryMap.put("separation-level-5",
//            "catid: 58029 449902 292594 115111 382391 110783 452210");
//        queryMap.put("ai-nn-people", "catid: 354439 5809");
//        queryMap.put("polska-level-1",
//            "catid: 339598 570229 212801 32496 232622 114095 365576 870296");
//        queryMap
//            .put(
//                "outlier-level-3",
//                "catid: 209353 592083 327 196267 240856 27078 303 27074 283016 27075 1139293 27073 1244841");
       
        // Balanced size, separation level 1
        queryMap.put("cats-2-bal-t-sep-1", "catid: 365639 287192");
        queryMap.put("cats-3-bal-t-sep-1", "catid: 185352 7698 26775");
        queryMap.put("cats-4-bal-t-sep-1", "catid: 6253 58375 8568 26954");
        queryMap.put("cats-5-bal-t-sep-1", "catid: 41312 188623 7430 241 26948");
        queryMap.put("cats-6-bal-t-sep-1", "catid: 325060 5462 84469 41436 93022 792595");
        queryMap.put("cats-7-bal-t-sep-1", "catid: 899171 81121 98602 240716 468908 8830 27075");
        queryMap.put("cats-8-bal-t-sep-1", "catid: 254684 1270973 817696 92618 472689 221 27160 283374");
        
        // Unbalanced size (+/- 50%), separation level 1
        queryMap.put("cats-2-bal-f-sep-1", "catid: 69395 7210");
        queryMap.put("cats-3-bal-f-sep-1", "catid: 58083 472742 48499");
        queryMap.put("cats-4-bal-f-sep-1", "catid: 398551 435847 109413 41938");
        queryMap.put("cats-5-bal-f-sep-1", "catid: 280473 196121 185 119014 423185");
        queryMap.put("cats-6-bal-f-sep-1", "catid: 205549 7450 41300 6621 26841 505967");
        queryMap.put("cats-7-bal-f-sep-1", "catid: 276670 7207 472697 52215 5800938 41936 26963");
        queryMap.put("cats-8-bal-f-sep-1", "catid: 399658 280285 295 486788 26839 175622 129488 254784");

        // Balanced size, separation level 2
        queryMap.put("cats-2-bal-t-sep-2", "catid: 27082 282106");
        queryMap.put("cats-3-bal-t-sep-2", "catid: 198214 83564 58419");
        queryMap.put("cats-4-bal-t-sep-2", "catid: 8372 401 272162 58779");
        queryMap.put("cats-5-bal-t-sep-2", "catid: 58123 887744 5339 82155 437987");
        queryMap.put("cats-6-bal-t-sep-2", "catid: 69478 577433 26881 26946 106806 96023");
        queryMap.put("cats-7-bal-t-sep-2", "catid: 8421 40632 178528 140175 59800 350389 540280");
        queryMap.put("cats-8-bal-t-sep-2", "catid: 6122 4902 110507 133 452210 5431 589495 198647");

        // Unbalanced size (+/- 50%), separation level 2
        queryMap.put("cats-2-bal-f-sep-2", "catid: 6620 5307");
        queryMap.put("cats-3-bal-f-sep-2", "catid: 5822171 7460 7706");
        queryMap.put("cats-4-bal-f-sep-2", "catid: 198926 123154 349908 95762");
        queryMap.put("cats-5-bal-f-sep-2", "catid: 6378 6163 6055 452806 4918");
        queryMap.put("cats-6-bal-f-sep-2", "catid: 8888 220801 8090 707465 40022 43581");
        queryMap.put("cats-7-bal-f-sep-2", "catid: 7322 7455 53786 1257637 392525 8887 7606");
        queryMap.put("cats-8-bal-f-sep-2", "catid: 292999 317940 1157346 57931 4808 6142 4993 1243179");

        // Balanced size, separation level 3
        queryMap.put("cats-2-bal-t-sep-3", "catid: 48472 26621");
        queryMap.put("cats-3-bal-t-sep-3", "catid: 197724 5465 332889");
        queryMap.put("cats-4-bal-t-sep-3", "catid: 429194 397702 791675 5347");
        queryMap.put("cats-5-bal-t-sep-3", "catid: 145491 5877762 287353 69526 112582");
        queryMap.put("cats-6-bal-t-sep-3", "catid: 53264 812644 8955 52128 110835 8872");
        queryMap.put("cats-7-bal-t-sep-3", "catid: 128323 841187 320908 413656 791254 382727 392113");
        queryMap.put("cats-8-bal-t-sep-3", "catid: 108421 452582 6099 285920 124090 5451 459492 6072");

        // Unbalanced size (+/- 50%), separation level 3
        queryMap.put("cats-2-bal-f-sep-3", "catid: 59328 585483");
        queryMap.put("cats-3-bal-f-sep-3", "catid: 851854 472726 473433");
        queryMap.put("cats-4-bal-f-sep-3", "catid: 26914 388662 80718 1211428");
        queryMap.put("cats-5-bal-f-sep-3", "catid: 811341 287353 396899 80887 320");
        queryMap.put("cats-6-bal-f-sep-3", "catid: 559969 1274009 359908 7667 110037 51052");
        queryMap.put("cats-7-bal-f-sep-3", "catid: 170719 895110 4916 57507 5404 85675 4918");
        queryMap.put("cats-8-bal-f-sep-3", "catid: 592083 327 868131 196267 27078 303 240856 210326");

        // Unbalanced size, separation level 4
        queryMap.put("cats-2-bal-f-sep-4", "catid: 57882 6083");
        queryMap.put("cats-3-bal-f-sep-4", "catid: 185352 5779 463882");
        queryMap.put("cats-4-bal-f-sep-4", "catid: 178332 26974 223614 26962");
        queryMap.put("cats-5-bal-f-sep-4", "catid: 6524 530870 6476 5954 6012");
        queryMap.put("cats-6-bal-f-sep-4", "catid: 904305 200591 216495 43371 5871416 347409");
        queryMap.put("cats-7-bal-f-sep-4", "catid: 5878175 8345 80915 112077 8335 421910 8417");
        queryMap.put("cats-8-bal-f-sep-4", "catid: 6163 57964 6198 399658 80614 6244 6238 6181");

        // Unbalanced size, separation level 5
        queryMap.put("cats-2-bal-f-sep-5", "catid: 7700 215778");
        queryMap.put("cats-3-bal-f-sep-5", "catid: 5866 5867 5868");
        queryMap.put("cats-4-bal-f-sep-5", "catid: 116067 6474 122008 6472");
        queryMap.put("cats-5-bal-f-sep-5", "catid: 6523 142289 6524 107353 395536");
        queryMap.put("cats-6-bal-f-sep-5", "catid: 71085 5830352 793306 96550 71084 71111");
        queryMap.put("cats-7-bal-f-sep-5", "catid: 418653 418651 431700 116277 280883 139063 144344");
        queryMap.put("cats-8-bal-f-sep-5", "catid: 452210 504547 382391 58001 449575 531880 435644 420716");

        // Unbalanced size, separation level 6
        queryMap.put("cats-2-bal-f-sep-6", "catid: 58008 1174851");
        queryMap.put("cats-3-bal-f-sep-6", "catid: 335064 448461 448462");
        queryMap.put("cats-4-bal-f-sep-6", "catid: 432203 432201 432200 781635");
        queryMap.put("cats-5-bal-f-sep-6", "catid: 84491 57479 96503 110519 84479");
        queryMap.put("cats-6-bal-f-sep-6", "catid: 6296 6297 5815462 6298 6301 5815457");
        queryMap.put("cats-7-bal-f-sep-6", "catid: 1188811 6543 535289 533908 1140685 900626 1227509");
        queryMap.put("cats-8-bal-f-sep-6", "catid: 901623 392036 203972 58004 58031 110219 504547 906183");

        // A single outlier of different sizes
        queryMap.put("sep-3-on-1-os-100", "catid: 429194 397702 791675 5347 556802");
        queryMap.put("sep-3-on-1-os-50", "catid: 429194 397702 791675 5347 1257774");
        queryMap.put("sep-3-on-1-os-40", "catid: 429194 397702 791675 5347 783404");
        queryMap.put("sep-3-on-1-os-30", "catid: 429194 397702 791675 5347 94505");
        queryMap.put("sep-3-on-1-os-20", "catid: 429194 397702 791675 5347 1126382");
        queryMap.put("sep-3-on-1-os-15", "catid: 429194 397702 791675 5347 349824");
        queryMap.put("sep-3-on-1-os-10", "catid: 429194 397702 791675 5347 59498");

        // Two outliers of different sizes
        queryMap.put("sep-3-on-2-os-100", "catid: 145491 5877762 287353 69526 112582 7433 8417");
        queryMap.put("sep-3-on-2-os-50", "catid: 145491 5877762 287353 69526 112582 904279 783380");
        queryMap.put("sep-3-on-2-os-40", "catid: 145491 5877762 287353 69526 112582 921041 933088");
        queryMap.put("sep-3-on-2-os-30", "catid: 145491 5877762 287353 69526 112582 201009 8764");
        queryMap.put("sep-3-on-2-os-20", "catid: 145491 5877762 287353 69526 112582 276657 26619");
        queryMap.put("sep-3-on-2-os-15", "catid: 145491 5877762 287353 69526 112582 793986 209358");
        queryMap.put("sep-3-on-2-os-10", "catid: 145491 5877762 287353 69526 112582 568954 1131193");
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