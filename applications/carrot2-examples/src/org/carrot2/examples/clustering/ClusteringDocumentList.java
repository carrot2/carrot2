
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.clustering;

import java.util.*;

import org.carrot2.clustering.lingo.LinearTfIdfTermWeighting;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.ExampleUtils;
import org.carrot2.util.attribute.AttributeUtils;

/**
 * This example shows how to cluster a set of documents available as an {@link ArrayList}.
 * This setting is particularly useful for quick experiments with custom data for which
 * there is no corresponding {@link DocumentSource} implementation. For production use,
 * it's better to implement a {@link DocumentSource} for the custom document source, so
 * that e.g the {@link CachingController} can cache it, if needed.
 * 
 * @see ClusteringDataFromDocumentSources
 * @see UsingCachingController
 */
public class ClusteringDocumentList
{
    public static void main(String [] args)
    {
        /*
         * Prepare a Collection of {@link Document} instances. Every document SHOULD have
         * a unique URL (identifier), a title and a snippet (document content), but none
         * of these are obligatory.
         */
        final List<Document> documents = new ArrayList<Document>();
        for (final String [] element : documentContent)
        {
            documents.add(new Document(element[0], "", element[1]));
        }

        /*
         * We are clustering using a simple controller (no caching, one-time shot).
         */
        final SimpleController controller = new SimpleController();

        /*
         * All data for components (and between them) is passed using a Map. Place the
         * required attributes and tuning options in the map below before you start
         * processing. Each document source and algorithm comes with a set of attributes
         * that can be tweaked at runtime (during component initialization or processing
         * of every query). Refer to each component's documentation for details.
         */
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeNames.DOCUMENTS, documents);

        /*
         * We will cluster by URL components first. The algorithm that does this is called
         * ByUrlClusteringAlgorithm. It has no parameters.
         */
        ProcessingResult result = controller.process(attributes,
            ByUrlClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);

        /*
         * Now we will cluster the same documents using a more complex text clustering
         * algorithm: Lingo. Note that the process is essentially the same, but we will
         * set an algorithm parameter for term weighting to a non-default value to show
         * how it is done.
         */
        final Class<?> algorithm = LingoClusteringAlgorithm.class;

        attributes.put(AttributeUtils.getKey(algorithm, "termWeighting"),
            LinearTfIdfTermWeighting.class);

        /*
         * If you know what query generated the documents you're about to cluster, pass
         * the query to the algorithm, which will usually increase clustering quality.
         */
        attributes.put(AttributeNames.QUERY, "data mining");

        result = controller.process(attributes, algorithm);

        ExampleUtils.displayResults(result);

        /*
         * The ProcessingResult object contains everything that has been contributed to
         * the output Map of values. We can, for example, check if native libraries have
         * been used (Lingo uses native matrix libraries on supported platforms and
         * defaults to Java equivalents on all others).
         */
        Boolean nativeUsed = (Boolean) result.getAttributes().get(
            AttributeUtils.getKey(algorithm, "nativeMatrixUsed"));

        System.out.println("Native libraries used: " + nativeUsed);
    }

    /**
     * Documents to cluster.
     */
    private static final String [][] documentContent = new String [] []
    {
        {
            "Data Mining - Wikipedia", "http://en.wikipedia.org/wiki/Data_mining"
        },
        {
            "KD Nuggets", "http://www.kdnuggets.com/"
        },
        {
            "The Data Mine", "http://www.the-data-mine.com/"
        },
        {
            "DMG", "http://www.dmg.org/"
        },
        {
            "Two Crows: Data mining glossary", "http://www.twocrows.com/glossary.htm"
        },
        {
            "Jeff Ullman's Data Mining Lecture Notes",
            "http://www-db.stanford.edu/~ullman/mining/mining.html"
        },
        {
            "Thearling.com", "http://www.thearling.com/"
        },
        {
            "Data Mining", "http://www.eco.utexas.edu/~norman/BUS.FOR/course.mat/Alex"
        },
        {
            "CCSU - Data Mining", "http://www.ccsu.edu/datamining/resources.html"
        },
        {
            "Data Mining: Practical Machine Learning Tools and Techniques",
            "http://www.cs.waikato.ac.nz/~ml/weka/book.html"
        },
        {
            "Data Mining - Monografias.com",
            "http://www.monografias.com/trabajos/datamining/datamining.shtml"
        },
        {
            "Amazon.com: Data Mining: Books: Pieter Adriaans,Dolf Zantinge",
            "http://www.amazon.com/exec/obidos/tg/detail/-/0201403803?v=glance"
        },
        {
            "DMReview", "http://www.dmreview.com/"
        },
        {
            "Data Mining @ CCSU", "http://www.ccsu.edu/datamining"
        },
        {
            "What is Data Mining", "http://www.megaputer.com/dm/dm101.php3"
        },
        {
            "Electronic Statistics Textbook: Data Mining Techniques",
            "http://www.statsoft.com/textbook/stdatmin.html"
        },
        {
            "data mining - a definition from Whatis.com - see also: data miner, data analysis",
            "http://searchcrm.techtarget.com/sDefinition/0,,sid11_gci211901,00.html"
        },
        {
            "St@tServ - About Data Mining", "http://www.statserv.com/datamining.html"
        },
        {
            "DATA MINING 2005", "http://www.wessex.ac.uk/conferences/2005/data05"
        },
        {
            "Investor Home - Data Mining", "http://www.investorhome.com/mining.htm"
        },
        {
            "SAS | Data Mining and Text Mining",
            "http://www.sas.com/technologies/data_mining"
        },
        {
            "Data Mining Student Notes, QUB",
            "http://www.pcc.qub.ac.uk/tec/courses/datamining/stu_notes/dm_book_1.html"
        },
        {
            "Data Mining", "http://datamining.typepad.com/data_mining"
        },
        {
            "Two Crows Corporation", "http://www.twocrows.com/"
        },
        {
            "Statistical Data Mining Tutorials", "http://www.autonlab.org/tutorials"
        },
        {
            "Data Mining: An Introduction",
            "http://databases.about.com/library/weekly/aa100700a.htm"
        },
        {
            "Data Mining Project", "http://research.microsoft.com/dmx/datamining"
        },
        {
            "An Introduction to Data Mining",
            "http://www.thearling.com/text/dmwhite/dmwhite.htm"
        },
        {
            "Untangling Text Data Mining",
            "http://www.sims.berkeley.edu/~hearst/papers/acl99/acl99-tdm.html"
        },
        {
            "Data Mining Technologies", "http://www.data-mine.com/"
        },
        {
            "SQL Server Data Mining", "http://www.sqlserverdatamining.com/"
        },
        {
            "Data Warehousing Information Center", "http://www.dwinfocenter.org/"
        },
        {
            "ITworld.com - Data mining",
            "http://www.itworld.com/App/110/050805datamining"
        },
        {
            "IBM Research | Almaden Research Center | Computer Science",
            "http://www.almaden.ibm.com/cs/quest"
        },
        {
            "Data Mining and Discovery", "http://www.aaai.org/AITopics/html/mining.html"
        },
        {
            "Data Mining: An Overview", "http://www.fas.org/irp/crs/RL31798.pdf"
        },
        {
            "Data Mining", "http://www.gr-fx.com/graf-fx.htm"
        },
        {
            "Data Mining Benchmarking Association (DMBA)",
            "http://www.dmbenchmarking.com/"
        },
        {
            "Data Mining",
            "http://www.computerworld.com/databasetopics/businessintelligence/datamining"
        },
        {
            "National Center for Data Mining (NCDM) - University of Illinois at Chicago",
            "http://www.ncdm.uic.edu/"
        },
    };
}
