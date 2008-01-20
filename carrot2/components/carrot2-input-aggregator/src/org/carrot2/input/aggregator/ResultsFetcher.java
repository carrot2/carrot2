
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

package org.carrot2.input.aggregator;

import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.impl.ArrayOutputComponent;

/**
 * @author Stanislaw Osinski
 */
final class ResultsFetcher implements Runnable
{
    private static Logger log = Logger.getLogger(ResultsFetcher.class);

    private final LocalController localController;
    private final ResultsCollector resultsCollector;

    private final String processId;
    private final String query;

    private final int requestedResults;
    private final Map requestParameters;

    /**
     * After a fetches has been decoupled, it is no longer valid.
     */
    private boolean decoupled;

    /**
     * @param localController
     * @param resultsCollector
     * @param inputId
     * @param query
     * @param requestedResults
     */
    public ResultsFetcher(LocalController localController,
        ResultsCollector resultsCollector, String inputId, String query,
        Map masterRequestParameters, int requestedResults)
    {
        super();
        this.localController = localController;
        this.resultsCollector = resultsCollector;
        this.processId = inputId;
        this.query = query;
        this.requestedResults = requestedResults;
        this.requestParameters = new HashMap(masterRequestParameters);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        requestParameters.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
            new Integer(requestedResults));

        try
        {
            // This looks weird, but in theory (or while debugging :) it can
            // happen that the fetcher gets decoupled even before it's
            // started fetching results. In this case the localController
            // field will be null at this point.

            // [dw] No, it's not weird -- start() doesn't pass control to the
            // newly created thread (it doesn't have to), so your code in
            // aggregator
            // may execute before run() even starts. It is correct.
            synchronized (this)
            {
                if (decoupled)
                {
                    return;
                }
            }

            // This is the time-consuming bit
            final ProcessingResult result = localController.query(processId,
                query, requestParameters);

            synchronized (this)
            {
                if (!decoupled)
                {
                    resultsCollector
                        .addResults(((ArrayOutputComponent.Result) result
                            .getQueryResult()).documents, processId);
                }
            }
        }
        catch (MissingProcessException e)
        {
            // Let the collector proceed first
            pushEmptyResults();
            throw new RuntimeException(e);
        }
        catch (ProcessingException e)
        {
            // Need to check the type of the enclosed exception -- the
            // interrupted flag gets unset after the InterruptedException
            // gets caught
            if (!(e.getCause() instanceof InterruptedException))
            {
                pushEmptyResults();
                log.warn("Exception in input aggregator", e);
            }
        }
        catch (Exception e)
        {
            pushEmptyResults();
            log.warn("Exception in input aggregator", e);
        }
    }

    private void pushEmptyResults()
    {
        synchronized (this)
        {
            if (!decoupled)
            {
                resultsCollector.addResults(Collections.EMPTY_LIST, processId);
            }
        }
    }

    public void decouple()
    {
        synchronized (this)
        {
            decoupled = true;
        }
    }
}
