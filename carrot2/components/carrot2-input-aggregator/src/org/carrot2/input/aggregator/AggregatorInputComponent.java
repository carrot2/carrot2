
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

package org.carrot2.input.aggregator;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.impl.ArrayOutputComponent;

/**
 * <p>
 * This is an input component that aggregates results from a number of other
 * input components. In the constructor you can specify an array of factories of
 * input components to be aggregated. Optionally, you can set the maximum amount
 * of time the aggregator component will wait for all the individual inputs to
 * deliver the results.
 *
 * <p>
 * Limitations:
 *
 * <ul>
 * <li>For the time being, <b>this component does not perform query translation</b>.
 * This means that you might be getting strange results in case of advanced or
 * non-standard query syntax.</li>
 * </ul>
 *
 * This component was donated to the Carrot2 project by deepVertical.
 *
 * @author Stanislaw Osinski
 */
public class AggregatorInputComponent extends LocalInputComponentBase implements
    ResultsCollector, RawDocumentsProducer
{
    /**
     * The default input timeout (in miliseconds, 10 seconds).
     */
    public static final int DEFAULT_INPUT_TIMEOUT = 10 * 1000;

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = toSet(RawDocumentsConsumer.class);

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = toSet(RawDocumentsProducer.class);

    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current {@link RawDocumentsConsumer} to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** Input timeout */
    private int timeout = DEFAULT_INPUT_TIMEOUT;

    /** Aggregated input components */
    private AggregatorInput [] inputs;

    /** An internal controller that manages fetching. */
    private LocalController internalController;

    /** References to running results fetchers */
    private ResultsFetcher [] fetchers;
    private Thread [] fetcherThreads;

    /** Counts the running fetchers */
    private int runningFetchers;

    /** Collects the results from individual inputs */
    private Map allResults;

    /** An engine for merging search results */
    private ResultsMerger resultsMerger;

    private double weightSum;

    /**
     * Creates the aggregator input component.
     *
     * @param inputComponentFactories an array of {@link AggregatorInput}s
     *            describing the document sources to be aggregated
     */
    public AggregatorInputComponent(AggregatorInput [] inputs)
    {
        this(inputs, DEFAULT_INPUT_TIMEOUT);
    }

    /**
     * Creates the aggregator input component.
     *
     * @param inputComponentFactories an array of {@link AggregatorInput}s
     *            describing the document sources to be aggregated
     * @param timeout the maximum amount of time the aggregator component will
     *            wait for all the individual inputs to deliver the results
     */
    public AggregatorInputComponent(AggregatorInput [] inputs, int timeout)
    {
        this.inputs = inputs;
        this.timeout = timeout;
        this.resultsMerger = new SimpleResultsMerger();

        for (int i = 0; i < inputs.length; i++)
        {
            weightSum += inputs[i].weight;
        }
    }

    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawDocumentConsumer = (RawDocumentsConsumer) next;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.carrot2.core.LocalInputComponentBase#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        super.endProcessing();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.carrot2.core.LocalInputComponentBase#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();

        decoupleFetchers();
        rawDocumentConsumer = null;
        allResults = null;
        query = null;
        fetchers = null;
        fetcherThreads = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.carrot2.core.LocalInputComponentBase#processingErrorOccurred()
     */
    public void processingErrorOccurred()
    {
        super.processingErrorOccurred();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.carrot2.core.LocalInputComponentBase#startProcessing(org.carrot2.core.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);

        final int resultsRequested = super.getIntFromRequestContext(
            requestContext, LocalInputComponent.PARAM_REQUESTED_RESULTS, 100);

        allResults = new HashMap();

        // Spawn a thread for each input. The assumption that
        // threads construction is cheap seems to be reasonable.
        runningFetchers = inputs.length;
        fetchers = new ResultsFetcher [runningFetchers];
        fetcherThreads = new Thread [runningFetchers];
        for (int i = 0; i < inputs.length; i++)
        {
            fetchers[i] = new ResultsFetcher(internalController, this,
                inputs[i].inputId, query,
                requestContext.getRequestParameters(), (int) (resultsRequested
                    * inputs[i].weight / weightSum));
            fetcherThreads[i] = new Thread(fetchers[i]);
            fetcherThreads[i].start();
        }

        // Spawn a thread measuring the timeout and interrupting
        // fetchers if needed.
        final Thread timeoutThread = new Thread()
        {
            public void run()
            {
                try
                {
                    // Sleep until timeout, unless interrupted before the
                    // deadline. Another option is to replace sleep() with
                    // wait(), but due to spurious wake-ups and problems with
                    // time measuring in this case, we'll stick with sleep()
                    // for the time being.
                    Thread.sleep(timeout);

                    synchronized (AggregatorInputComponent.this)
                    {
                        if (runningFetchers == 0)
                        {
                            // All fetchers finished. Exit without a timeout.
                            return;
                        }
                        else
                        {
                            // Timeout occurred.
                            timeout();
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    // Do nothing.
                }
            }
        };
        timeoutThread.start();

        try
        {
            synchronized (this)
            {
                while (runningFetchers > 0)
                {
                    wait();
                }

                // Interrupt the timeout thread.
                if (timeoutThread.isAlive())
                {
                    timeoutThread.interrupt();
                }
            }
        }
        catch (InterruptedException e)
        {
            return;
        }

        // Decouple all fetchers
        decoupleFetchers();

        // Now merge and push the results.
        pushResults();
    }

    private void decoupleFetchers()
    {
        for (int i = 0; i < fetchers.length; i++)
        {
            fetchers[i].decouple();
        }
    }

    /**
     * Timeout running fetchers.
     */
    private void timeout()
    {
        synchronized (this)
        {
            if (runningFetchers > 0)
            {
                runningFetchers = 0;

                // Interrupt the alive threads
                for (int i = 0; i < fetcherThreads.length; i++)
                {
                    if (fetcherThreads[i].isAlive())
                    {
                        fetcherThreads[i].interrupt();
                    }
                }
            }
            this.notifyAll();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.carrot2.input.aggregator.ResultsCollector#addResults(java.util.List,
     *      java.lang.String)
     */
    public void addResults(List results, String inputId)
    {
        synchronized (this)
        {
            allResults.put(inputId, results);
            runningFetchers--;
            notifyAll();
        }
    }

    /**
     *
     */
    private void pushResults() throws ProcessingException
    {
        List mergedResults = resultsMerger.mergeResults(allResults, inputs);
        for (Iterator it = mergedResults.iterator(); it.hasNext();)
        {
            RawDocument document = (RawDocument) it.next();
            rawDocumentConsumer.addDocument(document);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.carrot2.core.LocalComponentBase#init(org.carrot2.core.LocalControllerContext)
     */
    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);

        // Create controller
        internalController = new LocalControllerBase();

        try
        {
            // Register input component factories
            for (int i = 0; i < inputs.length; i++)
            {
                internalController.addLocalComponentFactory(inputs[i].inputId,
                    inputs[i].inputFactory);
            }

            // Register array output component factory
            internalController.addLocalComponentFactory("array-output",
                new LocalComponentFactory()
                {
                    public LocalComponent getInstance()
                    {
                        return new ArrayOutputComponent();
                    }
                });

            // Register processes for querying inputs
            for (int i = 0; i < inputs.length; i++)
            {
                internalController.addProcess(inputs[i].inputId,
                    new LocalProcessBase(inputs[i].inputId, "array-output",
                        new String [0]));
            }
        }
        catch (DuplicatedKeyException e)
        {
            throw new InstantiationException(e.getMessage());
        }
        catch (InitializationException e)
        {
            throw new InstantiationException(e.getMessage());
        }
        catch (MissingComponentException e)
        {
            throw new InstantiationException(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.carrot2.core.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.carrot2.core.LocalComponentBase#getName()
     */
    public String getName()
    {
        return "Input Aggregator";
    }
}
