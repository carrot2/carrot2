/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.normalizer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.dawidweiss.carrot.core.local.LocalFilterComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalOutputComponent;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContextBase;
import com.dawidweiss.carrot.core.local.clustering.TokenizedDocument;
import com.dawidweiss.carrot.core.local.impl.DocumentsConsumerOutputComponent;
import com.dawidweiss.carrot.filter.langguesser.LanguageGuesserFactory;
import com.dawidweiss.carrot.filter.langguesser.RawDocumentLanguageDetection;
import com.dawidweiss.carrot.input.localcache.CachedQueriesStore;
import com.dawidweiss.carrot.input.localcache.RemoteCacheAccessLocalInputComponent;
import com.dawidweiss.carrot.util.tokenizer.SnippetTokenizerLocalFilterComponent;
import com.dawidweiss.carrot.util.tokenizer.languages.AllKnownLanguages;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SmartCaseNormalizerBenchmark
{
    /** */
    private String [] queries;

    /** */
    private LocalInputComponent localInputComponent;

    /** */
    private LocalOutputComponent localOutputComponent;

    /** */
    private LocalFilterComponent languageGuesser;

    /** */
    private LocalFilterComponent snippetTokenizer;

    /** */
    private SmartCaseNormalizer caseNormalizer;

    /**
     * @throws InstantiationException
     *  
     */
    public SmartCaseNormalizerBenchmark() throws InstantiationException
    {
        queries = new String []
        { "derbyshire", "wales", "south yorkshire", "sweat shop", "workforce",
         "periodicos", "violencia", "responsabilidad", "Económicas",
         "terrorismo", "miś", "urząd", "uczelnia", "nabór", "szkoła",
         "bureautique", "publicités", "productivité ", "Académie de Lyon",
         "Mobilité", "Entwicklung", "aufbereitung", "Zahnärzte", "Gesundheit",
         "zusammenarbeit", "werkgever", "cijfer", "scherpschutter", "voorkeur",
         "wederzijds", "relazione tecnica", "Pubblico Incanto",
         "costruzione della strada", "Procedura Aperta", "Aggiornamento" };

        CachedQueriesStore store = new CachedQueriesStore(
            new File(
                "components/carrot2-util-lang-recognizer-tools/src/com/stachoodev/carrot/util/recognizer/query-cache"));

        // Initialise the local components
        localInputComponent = new RemoteCacheAccessLocalInputComponent(store);
        languageGuesser = new RawDocumentLanguageDetection(
            LanguageGuesserFactory.getLanguageGuesser(Arrays
                .asList(AllKnownLanguages.getLanguageCodes())));
        languageGuesser.init(null);
        
        snippetTokenizer = new SnippetTokenizerLocalFilterComponent();
        snippetTokenizer.init(null);
        
        localOutputComponent = new DocumentsConsumerOutputComponent();
        localInputComponent.init(null);

        // The case normalizer under tests
        caseNormalizer = new SmartCaseNormalizer();
    }

    /**
     * @throws ProcessingException
     *  
     */
    public void runPerformance() throws ProcessingException
    {
        // Warm up
        System.out.println("Warming up...");
        processQueries(3);

        // Measure
        System.out.println("Testing ...");
        double performance = processQueries(7);

        System.out.println(performance + " snippets/s\n");
    }

    /**
     * @param queryStrings
     * @throws ProcessingException
     */
    private double processQueries(int repeat) throws ProcessingException
    {
        int documentsProcessed = 0;
        long time = 0;
        for (int j = 0; j < repeat; j++)
        {
            for (int i = 0; i < queries.length; i++)
            {
//                System.out.println("Query: " + queries[i]);
                String query = queries[i];
                localInputComponent.setQuery(query);
                localInputComponent.setNext(languageGuesser);
                languageGuesser.setNext(snippetTokenizer);
                snippetTokenizer.setNext(localOutputComponent);
                localInputComponent.startProcessing(new RequestContextBase(
                    null, new HashMap()));

                List tokenizedDocuments = (List) localOutputComponent.getResult();

                long start = System.currentTimeMillis();
                for (Iterator iter = tokenizedDocuments.iterator(); iter
                    .hasNext();)
                {
                    TokenizedDocument tokenizedDocument = (TokenizedDocument) iter.next();
                    caseNormalizer.addDocument(tokenizedDocument);
                }
                caseNormalizer.getNormalizedDocuments();
                long stop = System.currentTimeMillis();
                caseNormalizer.clear();

                time += stop - start;

                documentsProcessed += tokenizedDocuments.size();

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
     * @throws InstantiationException
     */
    public static void main(String [] args) throws ProcessingException,
        InterruptedException, IOException, InstantiationException
    {
        SmartCaseNormalizerBenchmark benchmark = new SmartCaseNormalizerBenchmark();

        benchmark.runPerformance();
    }
}