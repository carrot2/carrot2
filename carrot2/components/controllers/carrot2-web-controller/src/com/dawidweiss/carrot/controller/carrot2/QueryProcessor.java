

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.controller.carrot2;


import com.dawidweiss.carrot.controller.carrot2.guard.*;
import com.dawidweiss.carrot.controller.carrot2.process.*;
import com.dawidweiss.carrot.controller.carrot2.process.cache.Cache;
import com.dawidweiss.carrot.controller.carrot2.process.scripted.ComponentFailureException;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.*;
import org.apache.log4j.Logger;
import org.put.util.net.http.*;
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

    public QueryProcessor(Cache cache, QueryGuard guard)
    {
        this.cache = cache;
        this.queryGuard = guard;
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

    /**
     * Processes a query using the given Process and query parameters.
     */
    public ProcessingResultHolder process(
        ProcessDefinition process, Query query, Writer output, HttpSession session,
        HttpServletRequest request, ServletContext context
    )
    {
        HTTPFormSubmitter submitter = null;
        ComponentDescriptor component = null;

        try
        {
            log.debug(
                "Applying process: " + process.getId() + " to query: ["
                + ((query.getContent().length() > 200) ? query.getContent().substring(0, 199)
                                                       : query.getContent()) + "]"
            );

            if (process instanceof ResolvedProcessingChain)
            {
                ResolvedProcessingChain processingChain = (ResolvedProcessingChain) process;

                // Submit initial request to the Input Component.
                component = processingChain.getInputComponent();

                FormActionInfo actionInfo;
                FormParameters queryArgs;

                InputStream inputStream = null;

                if ((inputStream = this.cache.getInputFor(query, component.getId(), null)) != null)
                {
                    log.debug("Using cached query.");
                }
                else
                {
                    actionInfo = new FormActionInfo(new URL(component.getServiceURL()), "post");
                    queryArgs = new FormParameters();
                    submitter = new HTTPFormSubmitter(actionInfo);

                    StringWriter sw = new StringWriter();
                    query.marshal(sw);

                    Parameter queryRequestXml = new Parameter(
                            "carrot-request", sw.getBuffer().toString(), false
                        );

                    queryArgs.addParameter(queryRequestXml);

                    if (queryGuard != null)
                    {
                        String permission;

                        if (
                            (permission = queryGuard.allowInputComponent(
                                        query, component, session, request, context
                                    )) != null
                        )
                        {
                            throw new GuardVetoException(component, "guard." + permission);
                        }
                    }

                    inputStream = submitter.submit(queryArgs, null, "UTF-8");
                }

                if (inputStream == null)
                {
                    generateNoOutputFailure(component, submitter);
                }

                // Do the filters chain now.
                ComponentDescriptor [] filters = processingChain.getFilterComponents();

                for (int i = 0; i < filters.length; i++)
                {
                    component = filters[i];
                    actionInfo = new FormActionInfo(new URL(component.getServiceURL()), "post");
                    queryArgs = new FormParameters();
                    queryArgs.addParameter(
                        new Parameter("carrot-xchange-data", inputStream, false)
                    );
                    submitter = new HTTPFormSubmitter(actionInfo);

                    if (queryGuard != null)
                    {
                        String permission;

                        if (
                            (permission = queryGuard.allowFilterComponent(
                                        component, session, request, context
                                    )) != null
                        )
                        {
                            throw new GuardVetoException(component, "guard." + permission);
                        }
                    }

                    inputStream = submitter.submit(queryArgs, null, "UTF-8");

                    if (inputStream == null)
                    {
                        generateNoOutputFailure(component, submitter);
                    }
                }

                // do the output component
                component = processingChain.getOutputComponent();
                actionInfo = new FormActionInfo(new URL(component.getServiceURL()), "post");
                queryArgs = new FormParameters();
                queryArgs.addParameter(
                    new Parameter(
                        "carrot-xchange-data", new InputStreamReader(inputStream, "UTF-8"), false
                    )
                );
                submitter = new HTTPFormSubmitter(actionInfo);
                inputStream = submitter.submit(queryArgs, null, "UTF-8");

                if (inputStream == null)
                {
                    generateNoOutputFailure(component, submitter);
                }

                output.write(
                    new String(
                        org.put.util.io.FileHelper.readFullyAndCloseInput(inputStream), "UTF-8"
                    )
                );
            }
            else if (process instanceof ResolvedScriptedProcess)
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Map requestParams = new HashMap();
                ((ResolvedScriptedProcess) process).applyProcess(
                    query, os, requestParams, cache, queryGuard, session, request, context
                );

                // TODO: nasty - caching is not necessary here, it should
                // be implemented as a direct stream copy.
                output.write(new String(os.toByteArray(), "UTF-8"));
            }
            else
            {
                throw new RuntimeException(
                    "Process type not supported." + process.getClass().getName()
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
}
