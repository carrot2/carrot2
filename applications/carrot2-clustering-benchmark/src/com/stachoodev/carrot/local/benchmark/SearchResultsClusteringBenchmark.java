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
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.impl.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.dawidweiss.carrot.filter.langguesser.*;
import com.dawidweiss.carrot.filter.stc.local.*;
import com.dawidweiss.carrot.input.localcache.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.dawidweiss.carrot.util.tokenizer.languages.*;
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
    private Set queries;

    /** The warm-up query */
    private String [] warmUpQueries;

    /**
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public SearchResultsClusteringBenchmark(String odpIndexLocation)
        throws IOException, ClassNotFoundException, Exception
    {
//        ODPIndex.initialize(odpIndexLocation);

        // Prepare queries
        queries = new LinkedHashSet();
        addQueries(queries);

        // Prepare the controller and clustering algorithms
        localController = new ProfiledLocalController();

        addComponentFactories(localController);
        addProcesses(localController);
    }

    /**
     * @param querySet
     */
    private void addQueries(Set querySet)
    {
//        warmUpQueries = new String []
//                                    { "catid: 354439 5809",
//                                     "catid: 791558 905697 5843791 96938 26950 43585 468909",
//                                     "catid: 78928",
//                                     "catid: 339598 570229 212801 32496 232622 114095 365576 870296",
//                                     "catid: 337874 48435 171000 783469 289730 26975 210325" };
                                    
        warmUpQueries = new String []
        { 
         "data mining", "clustering", "george bush", "dawid weiss",
         "polski bank", "salsa", "clinton", "york tourist information", 
         "data mining", "clustering", "george bush", "dawid weiss",
         "polski bank", "salsa", "clinton", "york tourist information", 
         };
                                    
//        queryMap.put("ai-nn-people", "catid: 354439 5809");
//		queryMap.put("polska-level-1",
//            "catid: 339598 570229 212801 32496 232622 114095 365576 870296");
//        queryMap
//            .put(
//                "outlier-level-3",
//                "catid: 209353 592083 327 196267 240856 27078 303 27074 283016 27075 1139293 27073 1244841");

        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/6f67758f7770b321", "politechnika poznańska"));
        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/3b20bcbe3147a5d8", "ronnie snooker 100snip"));
        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/7363b71742a5f026", "ronnie snooker 200snip"));
        
        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/9539b3f9f86226db", "sheffield 50snip"));
        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/e15c0492391c553e", "sheffield 100snip"));
        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/e236a2ef413a7722", "sheffield 150snip"));
        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/47c2e5af97110a75", "sheffield 200snip"));
        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/3b1e1d61dee9b46", "data mining 200snip"));
        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/608f996e428748b1", "carrot2 200snip"));
        querySet.add(ODPQuery.createLocalCache("file:applications/carrot2-clustering-benchmark/etc/query-cache/3751e4974ee9c29d", "part of speech tagging java 200snip"));
        querySet.add(ODPQuery.createLocalCache("data mining"));
        querySet.add(ODPQuery.createLocalCache("clustering"));
        querySet.add(ODPQuery.createLocalCache("george bush"));
        querySet.add(ODPQuery.createLocalCache("dawid weiss"));
        querySet.add(ODPQuery.createLocalCache("polski bank"));
        querySet.add(ODPQuery.createLocalCache("salsa"));
        querySet.add(ODPQuery.createLocalCache("clinton"));
        querySet.add(ODPQuery.createLocalCache("york tourist information"));
        querySet.add(ODPQuery.createLocalCache("poznań university of technology"));
        querySet.add(ODPQuery.createLocalCache("hostels poland"));
        querySet.add(ODPQuery.createLocalCache("jimmy white"));
        querySet.add(ODPQuery.createLocalCache("apache"));
        querySet.add(ODPQuery.createLocalCache("apache junction"));
        querySet.add(ODPQuery.createLocalCache("eclipse"));
        querySet.add(ODPQuery.createLocalCache("CSIDC"));
        querySet.add(ODPQuery.createLocalCache("cornwall uk"));
        querySet.add(ODPQuery.createLocalCache("IEEE"));
        querySet.add(ODPQuery.createLocalCache("sheffield"));
        querySet.add(ODPQuery.createLocalCache("snooker"));
        querySet.add(ODPQuery.createLocalCache("carrot2"));
        querySet.add(ODPQuery.createLocalCache("search results clustering"));
        querySet.add(ODPQuery.createLocalCache("search results clustering algorithm"));
        
      
//        // Balanced size, separation level 1
//        querySet.add(ODPQuery.createSeparationTest(1, true, "catid: 365639 287192"));
//        querySet.add(ODPQuery.createSeparationTest(1, true, "catid: 185352 7698 26775"));
//        querySet.add(ODPQuery.createSeparationTest(1, true, "catid: 6253 58375 8568 26954"));
//        querySet.add(ODPQuery.createSeparationTest(1, true, "catid: 41312 188623 7430 241 26948"));
//        querySet.add(ODPQuery.createSeparationTest(1, true, "catid: 325060 5462 84469 41436 93022 792595"));
//        querySet.add(ODPQuery.createSeparationTest(1, true, "catid: 899171 81121 98602 240716 468908 8830 27075"));
//        querySet.add(ODPQuery.createSeparationTest(1, true, "catid: 254684 1270973 817696 92618 472689 221 27160 283374"));
//
//        // Unbalanced size (+/- 50%), separation level 1
//        querySet.add(ODPQuery.createSeparationTest(1, false, "catid: 69395 7210"));
//        querySet.add(ODPQuery.createSeparationTest(1, false, "catid: 58083 472742 48499"));
//        querySet.add(ODPQuery.createSeparationTest(1, false, "catid: 398551 435847 109413 41938"));
//        querySet.add(ODPQuery.createSeparationTest(1, false, "catid: 280473 196121 185 119014 423185"));
//        querySet.add(ODPQuery.createSeparationTest(1, false, "catid: 205549 7450 41300 6621 26841 505967"));
//        querySet.add(ODPQuery.createSeparationTest(1, false, "catid: 276670 7207 472697 52215 5800938 41936 26963"));
//        querySet.add(ODPQuery.createSeparationTest(1, false, "catid: 399658 280285 295 486788 26839 175622 129488 254784"));
//
//        // Balanced size, separation level 2
//        querySet.add(ODPQuery.createSeparationTest(2, true, "catid: 27082 282106"));
//        querySet.add(ODPQuery.createSeparationTest(2, true, "catid: 198214 83564 58419"));
//        querySet.add(ODPQuery.createSeparationTest(2, true, "catid: 8372 401 272162 58779"));
//        querySet.add(ODPQuery.createSeparationTest(2, true, "catid: 58123 887744 5339 82155 437987"));
//        querySet.add(ODPQuery.createSeparationTest(2, true, "catid: 69478 577433 26881 26946 106806 96023"));
//        querySet.add(ODPQuery.createSeparationTest(2, true, "catid: 8421 40632 178528 140175 59800 350389 540280"));
//        querySet.add(ODPQuery.createSeparationTest(2, true, "catid: 6122 4902 110507 133 452210 5431 589495 198647"));
//
//        // Unbalanced size (+/- 50%), separation level 2
//        querySet.add(ODPQuery.createSeparationTest(2, false, "catid: 6620 5307"));
//        querySet.add(ODPQuery.createSeparationTest(2, false, "catid: 5822171 7460 7706"));
//        querySet.add(ODPQuery.createSeparationTest(2, false, "catid: 198926 123154 349908 95762"));
//        querySet.add(ODPQuery.createSeparationTest(2, false, "catid: 6378 6163 6055 452806 4918"));
//        querySet.add(ODPQuery.createSeparationTest(2, false, "catid: 8888 220801 8090 707465 40022 43581"));
//        querySet.add(ODPQuery.createSeparationTest(2, false, "catid: 7322 7455 53786 1257637 392525 8887 7606"));
//        querySet.add(ODPQuery.createSeparationTest(2, false, "catid: 292999 317940 1157346 57931 4808 6142 4993 1243179"));
//
//        // Balanced size, separation level 3
//        querySet.add(ODPQuery.createSeparationTest(3, true, "catid: 48472 26621"));
//        querySet.add(ODPQuery.createSeparationTest(3, true, "catid: 197724 5465 332889"));
//        querySet.add(ODPQuery.createSeparationTest(3, true, "catid: 429194 397702 791675 5347"));
//        querySet.add(ODPQuery.createSeparationTest(3, true, "catid: 145491 5877762 287353 69526 112582"));
//        querySet.add(ODPQuery.createSeparationTest(3, true, "catid: 53264 812644 8955 52128 110835 8872"));
//        querySet.add(ODPQuery.createSeparationTest(3, true, "catid: 128323 841187 320908 413656 791254 382727 392113"));
//        querySet.add(ODPQuery.createSeparationTest(3, true, "catid: 108421 452582 6099 285920 124090 5451 459492 6072"));
//
//        // Unbalanced size (+/- 50%), separation level 3
//        querySet.add(ODPQuery.createSeparationTest(3, false, "catid: 59328 585483"));
//        querySet.add(ODPQuery.createSeparationTest(3, false, "catid: 851854 472726 473433"));
//        querySet.add(ODPQuery.createSeparationTest(3, false, "catid: 26914 388662 80718 1211428"));
//        querySet.add(ODPQuery.createSeparationTest(3, false, "catid: 811341 287353 396899 80887 320"));
//        querySet.add(ODPQuery.createSeparationTest(3, false, "catid: 559969 1274009 359908 7667 110037 51052"));
//        querySet.add(ODPQuery.createSeparationTest(3, false, "catid: 170719 895110 4916 57507 5404 85675 4918"));
//        querySet.add(ODPQuery.createSeparationTest(3, false, "catid: 592083 327 868131 196267 27078 303 240856 210326"));
//
//        // Unbalanced size, separation level 4
//        querySet.add(ODPQuery.createSeparationTest(4, false, "catid: 57882 6083"));
//        querySet.add(ODPQuery.createSeparationTest(4, false, "catid: 185352 5779 463882"));
//        querySet.add(ODPQuery.createSeparationTest(4, false, "catid: 178332 26974 223614 26962"));
//        querySet.add(ODPQuery.createSeparationTest(4, false, "catid: 6524 530870 6476 5954 6012"));
//        querySet.add(ODPQuery.createSeparationTest(4, false, "catid: 904305 200591 216495 43371 5871416 347409"));
//        querySet.add(ODPQuery.createSeparationTest(4, false, "catid: 5878175 8345 80915 112077 8335 421910 8417"));
//        querySet.add(ODPQuery.createSeparationTest(4, false, "catid: 6163 57964 6198 399658 80614 6244 6238 6181"));
//
//        // Unbalanced size, separation level 5
//        querySet.add(ODPQuery.createSeparationTest(5, false, "catid: 7700 215778"));
//        querySet.add(ODPQuery.createSeparationTest(5, false, "catid: 5866 5867 5868"));
//        querySet.add(ODPQuery.createSeparationTest(5, false, "catid: 116067 6474 122008 6472"));
//        querySet.add(ODPQuery.createSeparationTest(5, false, "catid: 6523 142289 6524 107353 395536"));
//        querySet.add(ODPQuery.createSeparationTest(5, false, "catid: 71085 5830352 793306 96550 71084 71111"));
//        querySet.add(ODPQuery.createSeparationTest(5, false, "catid: 418653 418651 431700 116277 280883 139063 144344"));
//        querySet.add(ODPQuery.createSeparationTest(5, false, "catid: 452210 504547 382391 58001 449575 531880 435644 420716"));
//
//        // Unbalanced size, separation level 6
//        querySet.add(ODPQuery.createSeparationTest(6, false, "catid: 58008 1174851"));
//        querySet.add(ODPQuery.createSeparationTest(6, false, "catid: 335064 448461 448462"));
//        querySet.add(ODPQuery.createSeparationTest(6, false, "catid: 432203 432201 432200 781635"));
//        querySet.add(ODPQuery.createSeparationTest(6, false, "catid: 84491 57479 96503 110519 84479"));
//        querySet.add(ODPQuery.createSeparationTest(6, false, "catid: 6296 6297 5815462 6298 6301 5815457"));
//        querySet.add(ODPQuery.createSeparationTest(6, false, "catid: 1188811 6543 535289 533908 1140685 900626 1227509"));
//        querySet.add(ODPQuery.createSeparationTest(6, false, "catid: 901623 392036 203972 58004 58031 110219 504547 906183"));
//
//        // A single outlier of different sizes
//        querySet.add(ODPQuery.createOutlierTest(3, 1, 100, "catid: 429194 397702 791675 5347 556802"));
//        querySet.add(ODPQuery.createOutlierTest(3, 1, 50, "catid: 429194 397702 791675 5347 1257774"));
//        querySet.add(ODPQuery.createOutlierTest(3, 1, 40, "catid: 429194 397702 791675 5347 783404"));
//        querySet.add(ODPQuery.createOutlierTest(3, 1, 30, "catid: 429194 397702 791675 5347 94505"));
//        querySet.add(ODPQuery.createOutlierTest(3, 1, 20, "catid: 429194 397702 791675 5347 1126382"));
//        querySet.add(ODPQuery.createOutlierTest(3, 1, 15, "catid: 429194 397702 791675 5347 349824"));
//        querySet.add(ODPQuery.createOutlierTest(3, 1, 10, "catid: 429194 397702 791675 5347 59498"));
//
//        // Two outliers of different sizes
//        querySet.add(ODPQuery.createOutlierTest(3, 2, 100, "catid: 145491 5877762 287353 69526 112582 7433 8417"));
//        querySet.add(ODPQuery.createOutlierTest(3, 2, 50, "catid: 145491 5877762 287353 69526 112582 904279 783380"));
//        querySet.add(ODPQuery.createOutlierTest(3, 2, 40, "catid: 145491 5877762 287353 69526 112582 921041 933088"));
//        querySet.add(ODPQuery.createOutlierTest(3, 2, 30, "catid: 145491 5877762 287353 69526 112582 201009 8764"));
//        querySet.add(ODPQuery.createOutlierTest(3, 2, 20, "catid: 145491 5877762 287353 69526 112582 276657 26619"));
//        querySet.add(ODPQuery.createOutlierTest(3, 2, 15, "catid: 145491 5877762 287353 69526 112582 793986 209358"));
//        querySet.add(ODPQuery.createOutlierTest(3, 2, 10, "catid: 145491 5877762 287353 69526 112582 568954 1131193"));

        // Performance test - will take A WHILE
        for(int i = 0; false && i < 10; i++)
        {
	        // 50 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536"));
	        
	        // 100 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724 5007"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734 58083"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938 4831"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888 403109"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536 231738"));
	        
	        // 150 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724 5007 88529"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734 58083 185352"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938 4831 5867041"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888 403109 453843"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536 231738 487242"));
	        
	        // 200 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724 5007 88529 5867041"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734 58083 185352 299143"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938 4831 5867041 43543"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888 403109 453843 363083"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536 231738 487242 299143"));
	        
	        // 250 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724 5007 88529 5867041 818536"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734 58083 185352 299143 5007"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938 4831 5867041 43543 185352"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888 403109 453843 363083 4831"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536 231738 487242 299143 403109"));
	        
	        // 300 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724 5007 88529 5867041 818536 1121492"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734 58083 185352 299143 5007 271547"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938 4831 5867041 43543 185352 108888"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888 403109 453843 363083 4831 304224"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536 231738 487242 299143 403109 202114"));
	        
	        // 350 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724 5007 88529 5867041 818536 1121492 1243317"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734 58083 185352 299143 5007 271547 88529"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938 4831 5867041 43543 185352 108888 453843"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888 403109 453843 363083 4831 304224 7698"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536 231738 487242 299143 403109 202114 29944"));
	        
	        // 400 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724 5007 88529 5867041 818536 1121492 1243317 58083"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734 58083 185352 299143 5007 271547 88529 231738"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938 4831 5867041 43543 185352 108888 453843 5854"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888 403109 453843 363083 4831 304224 7698 299143"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536 231738 487242 299143 403109 202114 29944 7321"));
	        
	        // 450 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724 5007 88529 5867041 818536 1121492 1243317 58083 8603"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734 58083 185352 299143 5007 271547 88529 231738 550048"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938 4831 5867041 43543 185352 108888 453843 5854 425150"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888 403109 453843 363083 4831 304224 7698 299143 200101"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536 231738 487242 299143 403109 202114 29944 7321 844985"));
	        
	        // 500 snippets
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55724 5007 88529 5867041 818536 1121492 1243317 58083 8603 166565"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 55734 58083 185352 299143 5007 271547 88529 231738 550048 111107"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 41938 4831 5867041 43543 185352 108888 453843 5854 425150 700343"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 108888 403109 453843 363083 4831 304224 7698 299143 200101 41938"));
	        querySet.add(ODPQuery.createPerformanceTest("catid: 818536 231738 487242 299143 403109 202114 29944 7321 844985 4831"));
        }
    }

    /**
     * @param localController
     */
    private void addProcesses(LocalController localController) throws Exception
    {
//        // ODP -> RoughKMeans -> Output
//        LocalProcessBase roughKMeans = new LocalProcessBase("input.odp",
//            "output.cluster-consumer", new String []
//            { "filter.rough-k-means" }, "ODP -> RoughKMeans", "");
//        localController.addProcess("rough-k-means", roughKMeans);
//
//        // ODP -> Guesser -> STC -> Output
//        LocalProcessBase stc = new LocalProcessBase("input.odp",
//            "output.cluster-consumer", new String []
//            { "filter.language-guesser", "filter.stc" },
//            "ODP -> Language Guesser -> STC", "");
//        localController.addProcess("stc", stc);
//
//        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
//        LocalProcessBase lingoNMF1 = new LocalProcessBase(
//            "input.odp",
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-nmf-1" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMF-1",
//            "");
//        localController.addProcess("lingo-nmf-1", lingoNMF1);
//
//        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
//        LocalProcessBase lingoNMF2 = new LocalProcessBase(
//            "input.odp",
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-nmf-2" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMF-2",
//            "");
//        localController.addProcess("lingo-nmf-2", lingoNMF2);

//        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
//        LocalProcessBase lingoNMF3 = new LocalProcessBase(
//            "input.odp",    
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-nmf-3" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMF-3",
//            "");
//        localController.addProcess("lingo-nmf-3", lingoNMF3);

        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
        LocalProcessBase lingoNMF3 = new LocalProcessBase(
            "input.local-cache",    
            "output.cluster-consumer",
            new String []
            { "filter.language-guesser", "filter.tokenizer",
             "filter.case-normalizer", "filter.lingo-nmf-3" },
            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMF-3",
            "");
        localController.addProcess("lingo-nmf-3-cache", lingoNMF3);

//        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
//        LocalProcessBase lingoNMFKL3 = new LocalProcessBase(
//            "input.odp",
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-nmf-kl-3" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMFKL-3",
//            "");
//        localController.addProcess("lingo-nmf-kl-3", lingoNMFKL3);
//
//        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
//        LocalProcessBase lingoNMFKM1 = new LocalProcessBase(
//            "input.odp",
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-nmf-km-1" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMFKM-1",
//            "");
//        localController.addProcess("lingo-nmf-km-1", lingoNMFKM1);
//
//        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
//        LocalProcessBase lingoNMFKM3 = new LocalProcessBase(
//            "input.odp",
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-nmf-km-3" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMFKM-3",
//            "");
//        localController.addProcess("lingo-nmf-km-3", lingoNMFKM3);
//
//        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
//        LocalProcessBase lingoLNMF1 = new LocalProcessBase(
//            "input.odp",
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-lnmf-1" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoLNMF-1",
//            "");
//        localController.addProcess("lingo-lnmf-1", lingoLNMF1);
//
//        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
//        LocalProcessBase lingoLNMF3 = new LocalProcessBase(
//            "input.odp",
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-lnmf-3" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoLNMF-3",
//            "");
//        localController.addProcess("lingo-lnmf-3", lingoLNMF3);
//
//        // ODP -> Guesser -> Tokenizer -> LingoNMF -> Output
//        LocalProcessBase lingoKM = new LocalProcessBase(
//            "input.odp",
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-km-3" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoKM-3",
//            "");
//        localController.addProcess("lingo-km-3", lingoKM);
//
//        // ODP -> Guesser -> Tokenizer -> LingoSVD -> Output
//        LocalProcessBase lingoSVD = new LocalProcessBase(
//            "input.odp",
//            "output.cluster-consumer",
//            new String []
//            { "filter.language-guesser", "filter.tokenizer",
//             "filter.case-normalizer", "filter.lingo-svd-3" },
//            "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoSVD-3",
//            "");
//        localController.addProcess("lingo-svd-3", lingoSVD);
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

        // Cached input component
        LocalComponentFactory localCacheInputFactory = new LocalComponentFactoryBase()
        {
            private CachedQueriesStore store = new CachedQueriesStore(
                new File(
                    "D:\\Dev\\Eclipse\\workspace\\carrot2\\applications\\carrot2-clustering-benchmark\\etc\\query-cache"));
            
            public LocalComponent getInstance()
            {
                return new RemoteCacheAccessLocalInputComponent(store);
            }
        };
        localController.addLocalComponentFactory("input.local-cache", localCacheInputFactory);

        // Language guesser component
        LocalComponentFactory languageGuesserFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RawDocumentLanguageDetection(LanguageGuesserFactory
                    .getLanguageGuesser(AllKnownLanguages.getLanguageCodes()));
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

//        // Lingo NMF filter component
//        LocalComponentFactory lingoNMF1FilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                Map parameters = new HashMap();
//                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(1));
//                return new LingoReloadedLocalFilterComponent(parameters);
//            }
//        };
//        localController.addLocalComponentFactory("filter.lingo-nmf-1",
//            lingoNMF1FilterFactory);
//
//        // Lingo NMF filter component
//        LocalComponentFactory lingoNMF2FilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                Map parameters = new HashMap();
//                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(2));
//                return new LingoReloadedLocalFilterComponent(parameters);
//            }
//        };
//        localController.addLocalComponentFactory("filter.lingo-nmf-2",
//            lingoNMF2FilterFactory);

        // Lingo NMF filter component
        LocalComponentFactory lingoNMF3FilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
//                NonnegativeMatrixFactorizationEDFactory matrixFactorizationFactory = new NonnegativeMatrixFactorizationEDFactory();
//                matrixFactorizationFactory.setK(20);
//                parameters.put(LingoWeb.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
//                    matrixFactorizationFactory);
//                parameters.put(LingoWeb.PARAMETER_QUALITY_LEVEL, new Integer(3));
                return new LingoWebLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-nmf-3",
            lingoNMF3FilterFactory);

//        // Lingo NMF-KL filter component
//        LocalComponentFactory lingoNMFKL3FilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                Map parameters = new HashMap();
//                NonnegativeMatrixFactorizationKLFactory matrixFactorizationFactory = new NonnegativeMatrixFactorizationKLFactory();
//                matrixFactorizationFactory.setK(15);
//                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
//                    matrixFactorizationFactory);
//                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));
//                return new LingoReloadedLocalFilterComponent(parameters);
//            }
//        };
//        localController.addLocalComponentFactory("filter.lingo-nmf-kl-3",
//            lingoNMFKL3FilterFactory);
//
//        // Lingo NMF filter component
//        LocalComponentFactory lingoNMFKM1FilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                Map parameters = new HashMap();
//                LocalNonnegativeMatrixFactorizationFactory matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
//                KMeansSeedingStrategyFactory seeding = new KMeansSeedingStrategyFactory();
//                seeding.setMaxIterations(3);
//                matrixFactorizationFactory.setSeedingFactory(seeding);
//                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
//                    matrixFactorizationFactory);
//                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(1));
//                return new LingoReloadedLocalFilterComponent(parameters);
//            }
//        };
//        localController.addLocalComponentFactory("filter.lingo-nmf-km-1",
//            lingoNMFKM1FilterFactory);
//
//        // Lingo NMF filter component
//        LocalComponentFactory lingoNMFKM3FilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                Map parameters = new HashMap();
//                LocalNonnegativeMatrixFactorizationFactory matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
//                KMeansSeedingStrategyFactory seeding = new KMeansSeedingStrategyFactory();
//                seeding.setMaxIterations(3);
//                matrixFactorizationFactory.setSeedingFactory(seeding);
//                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
//                    matrixFactorizationFactory);
//                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));
//                return new LingoReloadedLocalFilterComponent(parameters);
//            }
//        };
//        localController.addLocalComponentFactory("filter.lingo-nmf-km-3",
//            lingoNMFKM3FilterFactory);
//
//        // Lingo KM filter component
//        LocalComponentFactory lingoKMFilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                Map parameters = new HashMap();
//                KMeansMatrixFactorizationFactory matrixFactorizationFactory = new KMeansMatrixFactorizationFactory();
//                matrixFactorizationFactory.setOrdered(true);
//                matrixFactorizationFactory.setK(15);
//                matrixFactorizationFactory.setMaxIterations(25);
//                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
//                    matrixFactorizationFactory);
//                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));
//                return new LingoReloadedLocalFilterComponent(parameters);
//            }
//        };
//        localController.addLocalComponentFactory("filter.lingo-km-3",
//            lingoKMFilterFactory);
//
//        // Lingo LNMF filter component
//        LocalComponentFactory lingoLNMF1FilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                Map parameters = new HashMap();
//                LocalNonnegativeMatrixFactorizationFactory matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
//                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
//                    matrixFactorizationFactory);
//                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(1));
//                return new LingoReloadedLocalFilterComponent(parameters);
//            }
//        };
//        localController.addLocalComponentFactory("filter.lingo-lnmf-1",
//            lingoLNMF1FilterFactory);
//
//        // Lingo LNMF filter component
//        LocalComponentFactory lingoLNMF3FilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                Map parameters = new HashMap();
//                LocalNonnegativeMatrixFactorizationFactory matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
//                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
//                    matrixFactorizationFactory);
//                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));
//                return new LingoReloadedLocalFilterComponent(parameters);
//            }
//        };
//        localController.addLocalComponentFactory("filter.lingo-lnmf-3",
//            lingoLNMF3FilterFactory);
//
//        // Lingo SVD filter component
//        LocalComponentFactory lingoSVDFilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                Map parameters = new HashMap();
//                PartialSingularValueDecompositionFactory matrixFactorizationFactory = new PartialSingularValueDecompositionFactory();
//                matrixFactorizationFactory.setK(15);
//                parameters.put(Lingo.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
//                    matrixFactorizationFactory);
//                parameters.put(Lingo.PARAMETER_QUALITY_LEVEL, new Integer(3));
//
//                return new LingoReloadedLocalFilterComponent(parameters);
//            }
//        };
//        localController.addLocalComponentFactory("filter.lingo-svd-3",
//            lingoSVDFilterFactory);
//
//        // STC filter component
//        LocalComponentFactory stcFilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                return new STCLocalFilterComponent();
//            }
//        };
//        localController
//            .addLocalComponentFactory("filter.stc", stcFilterFactory);
//
//        // Rough KMeans clustering filter component
//        LocalComponentFactory roughKMeansFilterFactory = new LocalComponentFactoryBase()
//        {
//            public LocalComponent getInstance()
//            {
//                return new RoughKMeansLocalFilterComponent();
//            }
//        };
//        localController.addLocalComponentFactory("filter.rough-k-means",
//            roughKMeansFilterFactory);

        // Cluster metrics output component
        LocalComponentFactory clusterMetricsOutputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RawClustersMetricsLocalOutputComponent();
            }
        };
        localController.addLocalComponentFactory("output.cluster-metrics",
            clusterMetricsOutputFactory);

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
            for (int i = 0; i < warmUpQueries.length; i++)
            {
                localController.query(processId, warmUpQueries[i], new HashMap());
            }

            // Execute each query
            for (Iterator queriesIter = queries.iterator(); queriesIter
                .hasNext();)
            {
                ODPQuery query = (ODPQuery) queriesIter.next();
                String queryId = query.toString();

                System.out.println(processId + ": " + queryId);

                // Execute the query
                long start = System.currentTimeMillis();
                ProcessingResult result = localController.query(processId,
                    query.getQueryText(), new HashMap());
                long stop = System.currentTimeMillis();

                // Unwrap results
                List resultList = (List) result.getQueryResult();
                ProfiledRequestContext requestContext = (ProfiledRequestContext) result
                    .getRequestContext();
                List profiles = requestContext.getProfiles();

//                List clusters = (List) resultList.get(0);
//                Map metrics = (Map) resultList.get(1);
                List clusters = resultList;
                Map metrics = new HashMap();
                
                // Contribute to the main report
                Map mainInfo = new LinkedHashMap();
                mainInfo.put("Process", processId);
                query.addToMap(mainInfo);
                mainInfo.put("Documents", requestContext.getRequestParameters()
                    .get(LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS));
                int nonJunkClusters = 0;
                for (Iterator iterator = clusters.iterator(); iterator
                    .hasNext();)
                {
                    RawCluster cluster = (RawCluster) iterator.next();
                    if (cluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) == null)
                    {
                        nonJunkClusters++;
                    }
                }
                mainInfo.put("Clusters", new Integer(nonJunkClusters));
                mainInfo.put("Total Time", Long.toString(stop - start) + " ms");
                long filterTime = 0;
                for (int p = 1; p < profiles.size() - 1; p++)
                {
                    filterTime += ((Profile) profiles.get(p))
                        .getTotalTimeElapsed();
                }
                mainInfo.put("Filter Time", Long.toString(filterTime) + " ms");
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
        info.put("Queries", Integer.toString(queries.size()));
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