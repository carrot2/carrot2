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
import org.carrot2.input.aggregator.AggregatorInput;
import org.carrot2.input.aggregator.AggregatorInputComponent;
import org.carrot2.input.msnapi.MsnApiInputComponent;
import org.carrot2.input.yahooapi.YahooApiInputComponent;

/**
 * <p>
 * This example shows how to use the {@link AggregatorInputComponent} and cluster the
 * results with Lingo.
 * </p>
 * <p>
 * We additionally demonstrate the capabilities of {@link LocalControllerBase} to
 * automatically discover and load components (without their explicit addition to the
 * controller). The referred components must have descriptors named the same as their
 * identifiers and present in the root of the classpath.
 * </p>
 * 
 * @see DirectDocumentFeedExample
 * @see Example
 */
public final class AggregatorInputExample
{
    /**
     * @param args Command line arguments are not used in this application.
     */
    public static void main(final String [] args)
    {
        try
        {
            //
            // Initialize local controller. Normally you'd run this only once
            // for an entire application (controller is thread safe).
            //
            final LocalController controller = initLocalController();

            // The query to execute
            final String query = "data mining";

            // Set up some custom parameters, including the number of results to fetch
            final HashMap params = new HashMap();
            params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "100");

            final ProcessingResult pResult = controller.query("aggregator-lingo", query,
                params);
            final ArrayOutputComponent.Result result = (ArrayOutputComponent.Result) pResult
                .getQueryResult();

            //
            // Once we have the buffered snippets and clusters, we can display
            // them somehow. We'll reuse the simple text-dumping method
            // available in {@link Example}.
            //
            Example.displayResults(result);
        }
        catch (Exception e)
        {
            // There shouldn't be any, but just in case.
            System.err.println("An exception occurred: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * In this method we put together a {@link LocalController}.
     * 
     * @return This method returns a fully configured, reusable instance a
     *         {@link LocalController}.
     */
    private static LocalController initLocalController() throws DuplicatedKeyException
    {
        final LocalControllerBase controller = new LocalControllerBase();

        //
        // Now it's time to create input, output and filters.
        //
        // To show an alternative to "manual" instantiation of components,
        // we will demonstrate the "auto-discovery" of components based
        // on their names. Lingo uses the following filters:
        //
        // filter-rawdocument-enumerator
        // filter-clustering-lingo-multi
        //
        // Instead of adding them to the controller here (which we could do),
        // we will refer to these components by name at the time
        // we add the process. The components will be loaded automatically.
        //
        // Note that we can mix autoloading with "manually" added factories --
        // below we'll "manually" add a factory for the aggregator input component.
        //
        controller.setComponentAutoload(true);

        //
        // Now we need to "manually" set up the input aggregator component.
        //
        LocalComponentFactory aggregatorInputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                // First, create the factories for all inputs we want to aggregate.
                // In this case we'll have two factories, but we could have more.
                
                // Factory for MSN input
                LocalComponentFactory msnFactory = new LocalComponentFactory()
                {
                    public LocalComponent getInstance()
                    {
                        return new MsnApiInputComponent();
                    }
                };

                // Factory for Yahoo! input
                LocalComponentFactory yahooFactory = new LocalComponentFactory()
                {
                    public LocalComponent getInstance()
                    {
                        return new YahooApiInputComponent();
                    }
                };

                // Finally, create the aggregator. We pass an array of classes describing
                // individual inputs being aggregated, see {@link AggregatorInput} for
                // the details.
                return new AggregatorInputComponent(new AggregatorInput [] {
                    new AggregatorInput("MSN", msnFactory, 3.0),
                    new AggregatorInput("Yahoo!", yahooFactory, 1.0),
                });
            }
        };
        
        controller.addLocalComponentFactory("input-aggregator", aggregatorInputFactory);

        //
        // In the final step, assemble a process from the above.
        //
        try
        {
            // These are the "filters" for our process.
            final String [] filters = new String []
            {
                "filter-rawdocument-enumerator", 
                "filter-clustering-lingo-multi",
            };
            controller.addProcess("aggregator-lingo", new LocalProcessBase("input-aggregator",
                "output-array", filters));
        }
        catch (InitializationException e)
        {
            // This exception is thrown during verification of the added component chain,
            // when a component cannot properly initialize for some reason. We don't
            // expect it here, so rethrow it as runtime exception.
            throw new RuntimeException(e);
        }
        catch (MissingComponentException e)
        {
            // If you give an identifier of a component for which factory has not been
            // added to the controller, you'll get this exception. Impossible in our
            // example.
            throw new RuntimeException(e);
        }

        return controller;
    }
}