
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.apiexample;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.RawDocumentSnippet;
import org.carrot2.core.impl.*;
import org.carrot2.filter.lingo.local.EnglishLingoLocalFilterComponent;

/**
 * <p>This is an example of using the Carrot<sup>2</sup> API to cluster
 * search results that are readily available, e.g. as an array of {@link String}s. 
 * 
 * <p>This example is a continuation of the API introduction available
 * in the {@link Example} class, present in this project.</p>
 * 
 * <p>This tutorial starts in the {@link #main(String[])} method.</p>
 *
 * @see Example
 *
 * @author Dawid Weiss
 * @author Stanislaw Osinski
 */
public final class DirectDocumentFeedExample {


    /**
     * <h1>Using Carrot<sup>2</sup> with direct document feeding</h1>
     * 
     * <h2>Introduction</h2>
     * 
     * <p>
     * In this example we will show how to apply Carrot<sup>2</sup> clustering
     * to documents available e.g. as arrays of {@link String}s.
     * </p>
     * 
     * <h2>Assembling a controller and putting it to work</h2>
     * 
     * <p>
     * The difference between the example shown in {@link Example} class and
     * this one is in how we initialize the {@link LocalController} component.
     * Here we will create an input component that passes given list of
     * documents down the Carrot<sup>2</sup> chain and link it with the
     * Lingo clustering algorithm. This is shown in
     * {@link #initLocalController()} method.
     * </p>
     * 
     * @param args Command line arguments are not used in this application.
     */
    public static void main(final String [] args) {
        try {
            /*
             * Initialize local controller. Normally you'd run this only once
             * for an entire application (controller is thread safe).
             */
            final LocalController controller = initLocalController();

            /*
             * Once we have a controller we can run queries. Change the query
             * to something that is relevant to the data in your index.
             */
            
            // Data for clustering, containing documents consisting of 
            // titles and bodies of documents.
            String [][] documents = new String [] [] {
                { "Data Mining - Wikipedia", "http://en.wikipedia.org/wiki/Data_mining" },
                { "KD Nuggets", "http://www.kdnuggets.com/" },
                { "The Data Mine", "http://www.the-data-mine.com/" },
                { "DMG", "http://www.dmg.org/" },
                { "Two Crows: Data mining glossary", "http://www.twocrows.com/glossary.htm" },
                { "Jeff Ullman's Data Mining Lecture Notes", "http://www-db.stanford.edu/~ullman/mining/mining.html" },
                { "Thearling.com", "http://www.thearling.com/" },
                { "Data Mining", "http://www.eco.utexas.edu/~norman/BUS.FOR/course.mat/Alex" },
                { "CCSU - Data Mining", "http://www.ccsu.edu/datamining/resources.html" },
                { "Data Mining: Practical Machine Learning Tools and Techniques", "http://www.cs.waikato.ac.nz/~ml/weka/book.html" },
                { "Data Mining - Monografias.com", "http://www.monografias.com/trabajos/datamining/datamining.shtml" },
                { "Amazon.com: Data Mining: Books: Pieter Adriaans,Dolf Zantinge", "http://www.amazon.com/exec/obidos/tg/detail/-/0201403803?v=glance" },
                { "DMReview", "http://www.dmreview.com/" },
                { "Data Mining @ CCSU", "http://www.ccsu.edu/datamining" },
                { "What is Data Mining", "http://www.megaputer.com/dm/dm101.php3" },
                { "Electronic Statistics Textbook: Data Mining Techniques", "http://www.statsoft.com/textbook/stdatmin.html" },
                { "data mining - a definition from Whatis.com - see also: data miner, data analysis", "http://searchcrm.techtarget.com/sDefinition/0,,sid11_gci211901,00.html" },
                { "St@tServ - About Data Mining", "http://www.statserv.com/datamining.html" },
                { "DATA MINING 2005", "http://www.wessex.ac.uk/conferences/2005/data05" },
                { "Investor Home - Data Mining", "http://www.investorhome.com/mining.htm" },
                { "SAS | Data Mining and Text Mining", "http://www.sas.com/technologies/data_mining" },
                { "Data Mining Student Notes, QUB", "http://www.pcc.qub.ac.uk/tec/courses/datamining/stu_notes/dm_book_1.html" },
                { "Data Mining", "http://datamining.typepad.com/data_mining" },
                { "Two Crows Corporation", "http://www.twocrows.com/" },
                { "Statistical Data Mining Tutorials", "http://www.autonlab.org/tutorials" },
                { "Data Mining: An Introduction", "http://databases.about.com/library/weekly/aa100700a.htm" },
                { "Data Mining Project", "http://research.microsoft.com/dmx/datamining" },
                { "An Introduction to Data Mining", "http://www.thearling.com/text/dmwhite/dmwhite.htm" },
                { "Untangling Text Data Mining", "http://www.sims.berkeley.edu/~hearst/papers/acl99/acl99-tdm.html" },
                { "Data Mining Technologies", "http://www.data-mine.com/" },
                { "SQL Server Data Mining", "http://www.sqlserverdatamining.com/" },
                { "Data Warehousing Information Center", "http://www.dwinfocenter.org/" },
                { "ITworld.com - Data mining", "http://www.itworld.com/App/110/050805datamining" },
                { "IBM Research | Almaden Research Center | Computer Science", "http://www.almaden.ibm.com/cs/quest" },
                { "Data Mining and Discovery", "http://www.aaai.org/AITopics/html/mining.html" },
                { "Data Mining: An Overview", "http://www.fas.org/irp/crs/RL31798.pdf" },
                { "Data Mining", "http://www.gr-fx.com/graf-fx.htm" },
                { "Data Mining Benchmarking Association (DMBA)", "http://www.dmbenchmarking.com/" },
                { "Data Mining", "http://www.computerworld.com/databasetopics/businessintelligence/datamining" },
                { "National Center for Data Mining (NCDM) - University of Illinois at Chicago", "http://www.ncdm.uic.edu/" },
            };
            
            // Although the query will not be used to fetch any data, if the data
            // that you're submitting for clustering is a response to some
            // search engine-like query, please provide it, as the clustering
            // algrithm may use it to improve the clustering quality.
            final String query = "data mining";
            
            // The documents are provided for clustering in the 
            // PARAM_SOURCE_RAW_DOCUMENTS parameter, which should point to
            // a List of RawDocuments.
            List documentList = new ArrayList(documents.length);
            for (int i = 0; i < documents.length; i++)
            {
                documentList.add(new RawDocumentSnippet(
                    new Integer(i),  // unique id of the document, can be a plain sequence id
                    documents[i][0], // document title
                    documents[i][1], // document body
                    "dummy://" + i,  // URL (not required for clustering)
                    0.0f)            // document score, can be 0.0 
                );
            }
            
            final HashMap params = new HashMap();
            params.put(
                ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS,
                    documentList);
            final ProcessingResult pResult = controller.query("direct-feed-lingo", query, params);
            final ArrayOutputComponent.Result result = (ArrayOutputComponent.Result) pResult.getQueryResult();

            /*
             * Once we have the buffered snippets and clusters, we can display
             * them somehow. We'll reuse the simple text-dumping method
             * available in {@link Example}.
             */
            Example.displayResults(result);
        } catch (Exception e) {
            // There shouldn't be any, but just in case.
            System.err.println("An exception occurred: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * <p>In this method we put together a {@link LocalController}.
     *
     * @return This method returns a fully configured, reusable instance
     *      a {@link LocalController}.
     */
    private static LocalController initLocalController() throws DuplicatedKeyException {
        final LocalController controller = new LocalControllerBase();

        //
        // Create direct document feed input component factory. The documents
        // that that this component will feed will be provided at clustering
        // request time.
        //
        final LocalComponentFactory input = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new ArrayInputComponent();
            }
        };
        
        // add direct document feed input as 'input-direct'
        controller.addLocalComponentFactory("input-direct", input);


        //
        // Now it's time to create filters. We will use Lingo clustering
        // component. 
        //
        final LocalComponentFactory lingo = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                // we will use the defaults here, see {@link Example}
                // for more verbose configuration.
                return new EnglishLingoLocalFilterComponent();
            }
        };

        // add the clustering component as "lingo-classic"
        controller.addLocalComponentFactory("lingo-classic", lingo);

        
        //
        // Finally, create a result-catcher component
        //
        final LocalComponentFactory output = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new ArrayOutputComponent();
            }
        };

        // add the output component as "buffer"
        controller.addLocalComponentFactory("buffer", output);

        
        //
        // In the final step, assemble a process from the above.
        //
        try {
            controller.addProcess("direct-feed-lingo", 
                    new LocalProcessBase("input-direct", "buffer", new String [] {"lingo-classic"}));
        } catch (InitializationException e) {
            // This exception is thrown during verification of the added component chain,
            // when a component cannot properly initialize for some reason. We don't 
            // expect it here, so rethrow it as runtime exception.
            throw new RuntimeException(e);
        } catch (MissingComponentException e) {
            // If you give an identifier of a component for which factory has not been
            // added to the controller, you'll get this exception. Impossible in our
            // example.
            throw new RuntimeException(e);
        }

        return controller;
    }
}