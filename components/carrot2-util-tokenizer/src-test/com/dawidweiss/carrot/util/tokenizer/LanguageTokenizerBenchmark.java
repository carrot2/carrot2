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
package com.dawidweiss.carrot.util.tokenizer;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.impl.*;
import com.dawidweiss.carrot.input.localcache.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LanguageTokenizerBenchmark
{
    /** */
    private Map queries;

    /** */
    private LocalInputComponent localInputComponent;

    /** */
    private LocalOutputComponent localOutputComponent;

    /** */
    private SnippetTokenizer snippetTokenizer;

    /**
     *  
     */
    public LanguageTokenizerBenchmark(boolean benchmarkLanguageTokenizers)
    {
        queries = new HashMap();
        queries.put("xx", Arrays
            .asList(new String []
            { "derbyshire", "wales", "south yorkshire", "sweat shop",
             "workforce" }));
        if (benchmarkLanguageTokenizers)
        {
            queries.put("en", Arrays.asList(new String []
            { "derbyshire", "wales", "south yorkshire", "sweat shop",
             "workforce" }));
            queries.put("es", Arrays.asList(new String []
            { "periodicos", "violencia", "responsabilidad", "Económicas",
             "terrorismo" }));
            queries.put("pl", Arrays.asList(new String []
            { "miś", "urząd", "uczelnia", "nabór", "szkoła" }));
            queries.put("fr", Arrays.asList(new String []
            { "bureautique", "publicités", "productivité ", "Académie de Lyon",
             "Mobilité" }));
            queries.put("de", Arrays.asList(new String []
            { "Entwicklung", "aufbereitung", "Zahnärzte", "Gesundheit",
             "zusammenarbeit" }));
            queries.put("nl", Arrays
                .asList(new String []
                { "werkgever", "cijfer", "scherpschutter", "voorkeur",
                 "wederzijds" }));
            queries
                .put("it", Arrays.asList(new String []
                { "relazione tecnica", "Pubblico Incanto",
                 "costruzione della strada", "Procedura Aperta",
                 "Aggiornamento" }));
        }

        CachedQueriesStore store = new CachedQueriesStore(
            new File(
                "components/carrot2-util-lang-recognizer-tools/src/com/stachoodev/carrot/util/recognizer/query-cache"));

        // Initialise the local components
        localInputComponent = new RemoteCacheAccessLocalInputComponent(store);
        localOutputComponent = new DocumentsConsumerOutputComponent();

        // Initialize snippet tokenizer
        snippetTokenizer = new SnippetTokenizer();
    }

    /**
     * @throws ProcessingException
     *  
     */
    public void runPerformance() throws ProcessingException
    {
        for (Iterator iter = queries.keySet().iterator(); iter.hasNext();)
        {
            String languageCode = (String) iter.next();
            List queryStrings = (List) queries.get(languageCode);

            System.out.println("Benchmarking language: " + languageCode);

            // Warm up
            System.out.println("Warming up...");
            processQueries(languageCode, queryStrings, 50);

            // Measure
            System.out.println("Testing ...");
            double performance = processQueries(languageCode, queryStrings, 400);

            System.out.println(performance + " snippets/s\n");
        }
    }

    /**
     * @param queryStrings
     * @throws ProcessingException
     */
    private double processQueries(String languageCode, List queryStrings,
        int repeat) throws ProcessingException
    {
        int documentsProcessed = 0;
        long time = 0;
        for (int j = 0; j < repeat; j++)
        {
            for (int i = 0; i < queryStrings.size(); i++)
            {
                String query = queryStrings.get(i).toString();
                localInputComponent.setQuery(query);
                localInputComponent.setNext(localOutputComponent);
                localInputComponent.startProcessing(new RequestContextBase(
                    null, new HashMap()));

                List rawDocuments = (List) localOutputComponent.getResult();

                for (Iterator iter = rawDocuments.iterator(); iter.hasNext();)
                {
                    RawDocument rawDocument = (RawDocument) iter.next();
                    rawDocument.setProperty(RawDocument.PROPERTY_LANGUAGE,
                        languageCode);
                }

                long start = System.currentTimeMillis();
                snippetTokenizer.tokenize(rawDocuments);
                long stop = System.currentTimeMillis();
                snippetTokenizer.clear();

                time += stop - start;

                documentsProcessed += rawDocuments.size();

                localInputComponent.flushResources();
            }
        }

        return documentsProcessed * 1000.0 / time;
    }

    /**
     * @param args
     * @throws ProcessingException
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String [] args) throws ProcessingException,
        InterruptedException, IOException
    {
        LanguageTokenizerBenchmark benchmark = new LanguageTokenizerBenchmark(false);

        benchmark.runPerformance();
    }
}