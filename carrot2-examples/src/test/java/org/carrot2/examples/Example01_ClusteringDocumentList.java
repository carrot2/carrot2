
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
import java.util.stream.Collectors;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.junit.Test;

/**
 * This example shows how to cluster a set of documents available as an {@link List}.
 */
public class Example01_ClusteringDocumentList
{
    // [[[start:clustering-document-list]]]
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
    public void clusterByUrl()
    {
        /* [[[start:clustering-document-list-intro]]]
         * 
         * <div>
         * <p>
         * The easiest way to get started with Carrot2 is to cluster a collection
         * of {@link org.carrot2.core.Document}s. Each document can consist of:
         * </p>
         * 
         * <ul>
         * <li>document content: a query-in-context snippet, document abstract or full text,</li>
         * <li>document title: optional, some clustering algorithms give more weight to document titles,</li>
         * <li>document URL: optional, used by the {@link org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm}, 
         * ignored by other algorithms.</li>
         * </ul>
         * 
         * <p>
         * To make the example short, the code shown below clusters only 5 documents. Use
         * at least 20 to get reasonable clusters. If you have access to the query that generated
         * the documents being clustered, you should also provide it to Carrot2 to get better clusters.
         * </p>
         * </div>
         * 
         * [[[end:clustering-document-list-intro]]]
         */

        // [[[start:clustering-document-list]]]
        /* Prepare input documents */
        final List<Document> documents = Arrays.stream(data)
            .map(row -> new Document(row[1], row[2], row[0]))
            .collect(Collectors.toList());

        /* A controller to manage the processing pipeline. */
        final Controller controller = ControllerFactory.createPooling();

        /* Perform clustering by domain. In this case query is not useful, hence it is null. */
        final List<Cluster> clustersByDomain =
            controller.process(documents, null, ByUrlClusteringAlgorithm.class).getClusters();

        ConsoleFormatter.displayClusters(clustersByDomain);
        // [[[end:clustering-document-list]]]
    }

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
}
