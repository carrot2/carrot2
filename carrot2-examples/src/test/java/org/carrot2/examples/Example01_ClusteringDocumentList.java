
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples;

import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm2;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.*;
import org.junit.Test;

/**
 * This example shows how to cluster a set of documents available as an {@link List}.
 */
public class Example01_ClusteringDocumentList
{
    /* A few example documents, normally you would need at least 200 for reasonable clusters. */
    private final String [][] data = new String [][] {
        {
            "http://en.wikipedia.org/wiki/Data_mining",
            "Data mining - Wikipedia, the free encyclopedia",
            "Article about knowledge-discovery in databases (KDD), the practice of automatically searching large stores of data for patterns."
        },

        {
            "http://www.ccsu.edu/datamining/resources.html",
            "CCSU - Data Mining",
            "A collection of Data Mining links edited by the Central Connecticut State University ... Graduate Certificate Program. Data Mining Resources. Resources. Groups ..."
        },

        {
            "http://www.kdnuggets.com/",
            "KDnuggets: Data Mining, Web Mining, and Knowledge Discovery",
            "Newsletter on the data mining and knowledge industries, offering information on data mining, knowledge discovery, text mining, and web mining software, courses, jobs, publications, and meetings."
        },

        {
            "http://en.wikipedia.org/wiki/Data-mining",
            "Data mining - Wikipedia, the free encyclopedia",
            "Data mining is considered a subfield within the Computer Science field of knowledge discovery. ... claim to perform \"data mining\" by automating the creation ..."
        },

        {
            "http://www.anderson.ucla.edu/faculty/jason.frand/teacher/technologies/palace/datamining.htm",
            "Data Mining: What is Data Mining?",
            "Outlines what knowledge discovery, the process of analyzing data from different perspectives and summarizing it into useful information, can do and how it works."
        },
    };

    @Test
    public void clusterWithLingoAlgorithm()
    {
        /* Prepare input documents */
        final List<Document> documents = Arrays.stream(data)
            .map(row -> new Document(row[1], row[2], row[0]))
            .collect(Collectors.toList());

        /* A controller to manage the processing pipeline. */
        final Controller controller = ControllerFactory.createPooling();

        /*
         * Perform clustering by topic using the Lingo algorithm. Lingo can
         * take advantage of the original query, so we provide it along with the documents.
         */

        final List<Cluster> clustersByTopic =
            controller.process(documents, "data mining", LingoClusteringAlgorithm.class).getClusters();

        ConsoleFormatter.displayClusters(clustersByTopic);
    }

    @Test
    public void clusterWithStcAlgorithm()
    {
        /* Prepare input documents */
        final List<Document> documents = Arrays.stream(data)
            .map(row -> new Document(row[1], row[2], row[0]))
            .collect(Collectors.toList());

        /* A controller to manage the processing pipeline. */
        final Controller controller = ControllerFactory.createPooling();

        /*
         * Perform clustering by topic using the STC algorithm.
         */
        final List<Cluster> clustersByTopic =
            controller.process(documents, "data mining", STCClusteringAlgorithm.class).getClusters();

        ConsoleFormatter.displayClusters(clustersByTopic);
    }


    @Test
    public void clusterWithLingo2Algorithm()
    {
        ServiceLoader<LanguageCode> load = ServiceLoader.load(LanguageCode.class);
        load.iterator();

        // StemmingComponentProvider p;
        // Set<String> langs = p.supportedLanguageCodes();
        // IStemmer stemmer = p.provide(String lang);

        IStemmer stemmer = new DefaultStemmerFactory().getStemmer(LanguageCode.ENGLISH);
        ITokenizer tokenizer = new DefaultTokenizerFactory().getTokenizer(LanguageCode.ENGLISH);
        ILexicalData lexicalData = new DefaultLexicalDataFactory().getLexicalData(LanguageCode.ENGLISH);

        LingoClusteringAlgorithm2 alg = new LingoClusteringAlgorithm2(tokenizer, stemmer, lexicalData);

        Stream<Document> docStream = Arrays.asList(new Document("title", "content")).stream();
        List<Cluster> clusters = alg.cluster(docStream);

        ConsoleFormatter.displayClusters(clusters);
    }
}
