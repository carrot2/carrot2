

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.remote.controller;


import com.dawidweiss.carrot.remote.controller.guard.*;
import com.dawidweiss.carrot.remote.controller.process.*;
import com.dawidweiss.carrot.remote.controller.cache.Cache;
import com.dawidweiss.carrot.remote.controller.process.scripted.ComponentFailureException;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.*;

import org.apache.log4j.Logger;
import com.dawidweiss.carrot.util.net.http.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.HttpServletResponse;


/**
 * An implementation of QueryProcessor interface, in addition binding all the components available
 * to STRUTS.
 */
public class QueryProcessor
{
    private final Logger log = Logger.getLogger(QueryProcessor.class);
    private Cache cache;
    private QueryGuard queryGuard;
    private boolean fullDebugInfo;
    private RequestHistory requestHistory;
    private boolean useHistory = true;


    /**
     * Create a new QueryProcessor with a given cache and query guard.
     */
    public QueryProcessor(Cache cache, QueryGuard guard)
    {
        this.cache = cache;
        this.queryGuard = guard;
    }
    
    
    /**
     * Sets request history memory.
     */
    public void setRequestHistory(RequestHistory history)
    {
        if (this.requestHistory != null)
            throw new IllegalStateException("Request history can be set only once.");
        this.requestHistory = history;
    }
    
    
    /**
     * Returns request history object or null.
     */
    public RequestHistory getRequestHistory() {
        return requestHistory;
    }
    
	public void setUseHistory(boolean useHistory) {
		log.debug("Using history of queries: " + useHistory);
		this.useHistory = useHistory;
	}


    /**
     * This is for debugging purposes only.
     */
    public void setUseCacheOnly(boolean cacheOnly)
    {
        log.debug("Using cache only: " + cacheOnly);

        if (cacheOnly)
        {
            if (queryGuard instanceof CacheOnlyGuard)
            {
                return;
            }

            queryGuard = new CacheOnlyGuard(queryGuard);
        }
        else
        {
            if (queryGuard instanceof CacheOnlyGuard)
            {
                queryGuard = ((CacheOnlyGuard) queryGuard).previous;
            }
        }

        log.debug("Active guard: " + queryGuard);
    }


    /**
     * Processes a query using the given Process and query parameters.
     */
    public ProcessingResultHolder process(
        ProcessDefinition process, Query query, Writer output, HttpSession session,
        HttpServletRequest request, ServletContext context
    )
    {
        try
        {
            log.debug(
                "Applying process: " + process.getId() + " to query: ["
                + ((query.getContent().length() > 200) ? query.getContent().substring(0, 199)
                                                       : query.getContent()) + "]"
            );

            if (process instanceof ResolvedScriptedProcess)
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Map requestParams = new HashMap();
                ((ResolvedScriptedProcess) process).applyProcess(
                    query, os, requestParams, cache, queryGuard, session, request, context
                );

                // TODO: nasty - caching is not necessary here, it should
                // be implemented as a direct stream copy.
                output.write(new String(os.toByteArray(), "UTF-8"));

                // handle request history.
                if (useHistory && requestHistory != null && !process.isHidden())
                    requestHistory.push(query, process);
            }
            else
            {
                throw new RuntimeException(
                    "Process type not supported: " + process.getClass().getName()
                );
            }
        }
        catch (GuardVetoException e)
        {
            log.warn("Guard veto exception: " + e.getMessage());

            ProcessingResultHolder result = new ProcessingResultHolder();
            result.setErraneous(true);
            result.addException(e);

            return result;
        }
        catch (Throwable e)
        {
            log.warn("Error processing query.", e);

            // if full-debug on, re-run the query and log all the information carefully.
            if (fullDebugInfo)
            {
                if (process instanceof ResolvedScriptedProcess)
                {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    Map requestParams = new HashMap();
                    DebugQueryExecutionInfo debugInfo = ((ResolvedScriptedProcess) process).applyProcessWithDebug(
                        query, os, requestParams, cache, queryGuard, session, request, context
                    );

                    // TODO: nasty - caching is not necessary here, it should
                    // be implemented as a direct stream copy.
                    ProcessingResultHolder result = new ProcessingResultHolder();
                    result.setErraneous(true);
                    result.addException(e);
                    result.setDebugInfo( debugInfo );
                    return result;
                }
            }

            ProcessingResultHolder result = new ProcessingResultHolder();
            result.setErraneous(true);
            result.addException(e);
            return result;
        }

        return null;
    }


    public static void generateNoOutputFailure(
        ComponentDescriptor component, HTTPFormSubmitter submitter
    )
        throws ComponentFailureException
    {
        HttpURLConnection connection = (java.net.HttpURLConnection) submitter.getConnection();

        try
        {
            if (connection.getResponseCode() != HttpServletResponse.SC_OK)
            {
                throw new ComponentFailureException(
                    component,
                    "Suspicious component response: (" + connection.getResponseCode() + ") "
                    + connection.getResponseMessage()
                );
            }
        }
        catch (java.io.FileNotFoundException e)
        {
            // JDK BUG.
            throw new ComponentFailureException(
                component,
                "Syspicious component response: (JDK bug prevents analysis of HTTP header): "
                + connection.getHeaderField(0)
            );
        }
        catch (ClassCastException e)
        {
            throw new ComponentFailureException(
                component, "No output from component. Reason unknown."
            );
        }
        catch (IOException e)
        {
            throw new ComponentFailureException(
                component, "No output from component. Reason unknown."
            );
        }
    }


    /**
     * If set to <code>true</code>, the query processor will re-run erroneous queries and assemble
     * a detailed error report. This operation is slowing down the server significantly as all
     * queries must be executed separately and twice.
     */
    public void setFullDebugInfo(boolean value)
    {
        this.fullDebugInfo = value;
    }

    private static class CacheOnlyGuard
        implements QueryGuard
    {
        QueryGuard previous;

        public CacheOnlyGuard(QueryGuard previous)
        {
            this.previous = previous;
        }

        public String allowInputComponent(
            Query q, ComponentDescriptor component, HttpSession session, HttpServletRequest request,
            ServletContext context
        )
        {
            return "cache-only-guard";
        }


        public String allowFilterComponent(
            ComponentDescriptor component, HttpSession session, HttpServletRequest request,
            ServletContext context
        )
        {
            if (previous == null)
            {
                return null;
            }
            else
            {
                return previous.allowFilterComponent(component, session, request, context);
            }
        }


        public String toString()
        {
            return "CacheOnlyGuard [" + previous + "]";
        }
    }
}
