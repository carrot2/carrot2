
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

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.carrot2.core.*;
import org.carrot2.core.impl.*;
import org.carrot2.filter.lingo.local.*;
import org.carrot2.input.lucene.*;

/**
 * <p>This is an example of using the Carrot<sup>2</sup> API to cluster
 * search results aquired from a local
 * <a href="http://lucene.apache.org/">Lucene</a> index.</p>
 *
 * <p>This example is a continuation of the API introduction available
 * in the {@link Example} class, present in this project.</p>
 *
 * <p>This tutorial starts in the {@link #main(String[])} method.</p>
 *
 * @see Example
 *
 * @author Dawid Weiss
 */
public final class LuceneExample {


    /**
     * <h1>Using Carrot<sup>2</sup> and Lucene</h1>
     *
     * <h2>Introduction</h2>
     *
     * <p>In this example we will configure and run a few queries against
     * an index created and searched with Java Information Retrieval
     * library <a href="http://lucene.apache.org/">Lucene</a>. You should
     * be familiar with how Lucene creates indexes and how it works. Corresponding
     * documentation is available in that project.</p>
     *
     * <h2>Preparation</h2>
     *
     * <p>Further on we assume that a Lucene index is compiled and available for
     * searches. Carrot<sup>2</sup> uses Lucene 2.x JAR internally, so the index
     * should be compatible. In addition to that, we will need <b>three fields</b>,
     * corresponding to the information Carrot<sup>2</sup> utilizes:</p>
     * <ul>
     *  <li>a field for <b>url</b> of a document,<li>
     *  <li>a field for the <b>title</b> of a document,</li>
     *  <li>a field for the <b>snippet</b> of a document, or in other words, a textual summary of document's contents.</li>
     * <ul>
     *
     * <p>If any of the above fields are not present in your index, you can simulate
     * them programmatically (only the <code>url</code> field is obligatory, but it also
     * can be simulated). This is a somewhat advanced technique and we will further assume
     * that each of the above fields is available in your index.</p>
     *
     * <h2>Assembling a controller and putting it to work</h2>
     *
     * <p>Practically the only difference between the example shown in {@link Example} class
     * and now is how we initialize the {@link LocalController} component. We will create
     * an input component searching for documents in our index ({@link LuceneLocalInputComponent})
     * and link it with the Lingo clustering algorithm. This is shown in {@link #initLocalController()}
     * method.</p>
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
            final String query = "your query";

            /*
             * You can provide additional parameters (e.g. the number of results
             * to fetch from the source) through the Map below.
             */
            final Map parameters = new HashMap();
            parameters.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "150");

            final ProcessingResult pResult = controller.query("lucene-lingo", query, parameters);
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
     * <p>In this method we put together a {@link LocalController}. Note that
     * the Lucene input component requires a configuration object. The factory
     * provides this object, so the configuration is permanent for the entire
     * lifecycle of a component. Alternatively, you could provide a different
     * Lucene configuration at query-time, setting a {@link RequestContext}
     * parameter of value {@link LuceneLocalInputComponentFactoryConfig} under a key
     * {@link LuceneLocalInputComponent#LUCENE_CONFIG}.</p>
     *
     * @param indexLocation A folder where your index is present. Note that
     *      certain index parameters are hardcoded in this method, so you need
     *      to modify them as well.
     *
     * @return This method returns a fully configured, reusable instance
     *      a {@link LocalController}.
     */
    private static LocalController initLocalController() throws DuplicatedKeyException {
        final LocalController controller = new LocalControllerBase();

        //
        // Collect the information required for Lucene input component - location of
        // the index, searched fields and fields that map to URL, title and snippet
        // of {@link RawDocument}s to be clustered.
        //

        // Place your index location in this variable.
        File indexLocation = null;

        // Create a Searcher. Note that the same searcher is used
        // in case multiple queries are run through Carrot<sup>2</sup>.
        final IndexReader indexReader;
        try {
            if (indexLocation == null) {
                throw new IOException("Initialize indexLocation first.");
            }
            indexReader = IndexReader.open(indexLocation);
        } catch (IOException e) {
            throw new RuntimeException("Lucene index not present at" +
                    " location: " + indexLocation, e);
        }
        final Searcher searcher = new IndexSearcher(indexReader);

        // Create an Analyzer. This must be the same analyzer as the one
        // used to create your index. We use a standard analyzer here.
        final Analyzer analyzer = new StandardAnalyzer();

        // Define your field configuration here. Search fields are the
        // fields used to retrieve matching documents when you query
        // Lucene through Carrot<sup>2</sup>. Title, URL and summary
        // fields are used for retriving data to be clustered (the URL
        // field is used for document identification, actually).
        final String [] searchFields = {"summary", "title"};
        final String urlField = "url";
        final String titleField = "title";
        final String summaryField = "summary";

        final LuceneLocalInputComponentConfig luceneConfig =
            new LuceneLocalInputComponentConfig(
                new LuceneLocalInputComponentFactoryConfig(searchFields, titleField,
                summaryField, urlField), searcher, analyzer);

        //
        // Create Lucene input component factory.
        //
        final LocalComponentFactory input = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new LuceneLocalInputComponent(luceneConfig);
            }
        };

        // add lucene input as 'lucene-myindex'
        controller.addLocalComponentFactory("lucene-myindex", input);


        //
        // Now it's time to create filters. We will use Lingo clustering
        // component.
        //
        final LocalComponentFactory lingo = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                // we will use the defaults here, see {@link Example}
                // for more verbose configuration.
                return new LingoLocalFilterComponent();
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
            controller.addProcess("lucene-lingo",
                    new LocalProcessBase("lucene-myindex", "buffer", new String [] {"lingo-classic"}));
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