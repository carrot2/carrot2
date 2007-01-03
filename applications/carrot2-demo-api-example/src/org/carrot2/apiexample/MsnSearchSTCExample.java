
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

import java.util.HashMap;

import org.carrot2.core.*;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.filter.stc.StcConstants;

/**
 * <p>This example shows how to query MSN Search and cluster
 * the results using STC.</p> 
 * 
 * <p>We additionally demonstrate the capabilities of {@link LocalControllerBase}
 * to automatically discover and load components (without their explicit
 * addition to the controller). The referred components must have descriptors
 * named the same as their identifiers and present in the root of the classpath.</p> 
 * 
 * @see DirectDocumentFeedExample
 * @see Example
 *
 * @author Dawid Weiss
 */
public final class MsnSearchSTCExample {
    /**
     * @param args Command line arguments are not used in this application.
     */
    public static void main(final String [] args) {
        try {
            /*
             * Initialize local controller. Normally you'd run this only once
             * for an entire application (controller is thread safe).
             */
            final LocalController controller = initLocalController();
            
            // Although the query will not be used to fetch any data, if the data
            // that you're submitting for clustering is a response to some
            // search engine-like query, please provide it, as the clustering
            // algrithm may use it to improve the clustering quality.
            final String query = "data mining";

            // Set up some custom parameters.
            final HashMap params = new HashMap();
            params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "200");

            // Set an STC parameter (see the browser application for more 
            // and tuning knobs). Here, we limit the output to 10 clusters.
            params.put(StcConstants.MAX_CLUSTERS, "10");

            final ProcessingResult pResult = controller.query("direct-feed-stc", query, params);
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
        final LocalControllerBase controller = new LocalControllerBase();

        //
        // Now it's time to create input, output and filters. 
        // We will use STC clustering component, MSN Search as the input
        // and an array as the output.
        //
        // To show an alternative to "manual" instantiation of components,
        // we will demonstrate the "auto-discovery" of components based
        // on their names. STC uses the following filters:
        //
        // filter-rawdocument-enumerator
        // filter-langset-en
        // filter-tokenizer
        // filter-smart-case-normalizer
        // filter-clustering-stc
        //
        // Instead of adding them to the controller here (which we could do),
        // we will refer to these components by name at the time
        // we add the process. The components will be loaded automatically.
        //
        controller.setComponentAutoload(true);

        //
        // In the final step, assemble a process from the above.
        //
        try {
            // These are the "filters" for our process.
            final String [] filters = new String [] {
                "filter-rawdocument-enumerator",
                "filter-langset-en",
                "filter-tokenizer",
                "filter-smart-case-normalizer",
                "filter-clustering-stc",
            };
            controller.addProcess("direct-feed-stc", 
                    new LocalProcessBase("input-msnapi", "output-array", filters));
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