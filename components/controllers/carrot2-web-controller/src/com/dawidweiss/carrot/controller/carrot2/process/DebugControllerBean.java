

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


package com.dawidweiss.carrot.controller.carrot2.process;


import com.dawidweiss.carrot.controller.carrot2.QueryProcessor;
import com.dawidweiss.carrot.controller.carrot2.components.ComponentsLoader;
import com.dawidweiss.carrot.controller.carrot2.guard.GuardVetoException;
import com.dawidweiss.carrot.controller.carrot2.guard.QueryGuard;
import com.dawidweiss.carrot.controller.carrot2.process.cache.Cache;
import com.dawidweiss.carrot.controller.carrot2.process.scripted.ComponentFailureException;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor;
import org.apache.log4j.Logger;
import org.put.util.io.FileHelper;
import org.put.util.net.http.FormActionInfo;
import org.put.util.net.http.FormParameters;
import org.put.util.net.http.HTTPFormSubmitter;
import org.put.util.net.http.Parameter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class implements the Controller interface made available to BSF beans in charge of a
 * process.
 */
class DebugControllerBean
    implements com.dawidweiss.carrot.controller.carrot2.process.scripted.Controller
{
    private DebugQueryExecutionInfo info;
	final static Logger log = Logger.getLogger(ControllerBean.class);
    private final OutputStream output;
    private boolean cached;
    private boolean writeToCache;
    private Cache cache;
    private QueryGuard queryGuard;
    private List toDispose = new LinkedList();
    private HttpSession session;
    private HttpServletRequest request;
    private ServletContext context;
    private ComponentsLoader componentsLoader;

    public DebugControllerBean(
        OutputStream output, Cache cacher, QueryGuard queryGuard, HttpSession session,
        HttpServletRequest request, ServletContext context, ComponentsLoader componentsLoader
    )
    {
        this.output = output;
        this.cache = cacher;
        this.queryGuard = queryGuard;
        this.session = session;
        this.request = request;
        this.context = context;
        this.componentsLoader = componentsLoader;
        this.info = new DebugQueryExecutionInfo(log);
        info.startCapturingLog4j();
        log.debug("Starting debug query execution.");
    }

    public void setDoCacheInput(boolean newValue)
    {
        this.writeToCache = newValue;
        log.debug("Do Cache input set to: " + newValue);
    }
    
    
    public DebugQueryExecutionInfo getDebugInfo()
    {
        return info;
    }


    public boolean getDoCacheInput()
    {
        return this.writeToCache;
    }


    public void setUseCachedInput(boolean newValue)
    {
        this.cached = newValue;
        log.debug("Use Cached input set to: " + newValue);
    }


    public boolean getUseCachedInput()
    {
        return this.cached;
    }


    public InputStream invokeInputComponent(
        String componentId, com.dawidweiss.carrot.controller.carrot2.process.scripted.Query query
    )
        throws IOException, ComponentFailureException
    {
        return invokeInputComponent(componentId, query, null);
    }


    public InputStream invokeInputComponent(
        String componentId, com.dawidweiss.carrot.controller.carrot2.process.scripted.Query query,
        Map optionalParams
    )
        throws IOException, ComponentFailureException
    {
        info.startComponent(componentId);
        log.info("Invoking input component: " + componentId + " with query: " + query.getQuery() + " (expected results: "
            + query.getNumberOfExpectedResults() + "), params: " + optionalParams);

        ComponentDescriptor component = componentsLoader.findComponent(componentId);

        FormActionInfo actionInfo = new FormActionInfo(new URL(component.getServiceURL()), "post");
        FormParameters queryArgs = new FormParameters();
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

        java.io.InputStream inputStream = null;

        com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query q = new com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query();

        try
        {
            q.setContent(query.getQuery());
            q.setRequestedResults(
                (query.getNumberOfExpectedResults() == 0) ? 100
                                                          : query.getNumberOfExpectedResults()
            );

            if (this.cached)
            {
                inputStream = cache.getInputFor(q, componentId, optionalParams);
                log.debug("Using cache: " + ((inputStream == null) ? "no"
                                                                   : "yes")
                );
            }

            if (inputStream == null)
            {
                // nah, no cached input... check with the guard and query input component
                if (queryGuard != null)
                {
                    String permission;

                    if (
                        (permission = queryGuard.allowInputComponent(
                                    q, component, session, request, context
                                )) != null
                    )
                    {
                        throw new GuardVetoException(component, "guard." + permission);
                    }
                }

                StringWriter sw = new StringWriter();
                q.marshal(sw);
                log.debug("Sending query: " + sw.toString());

                Parameter queryRequestXml = new Parameter(
                        "carrot-request", sw.getBuffer().toString(), false
                    );
                addOptionalParams(optionalParams, queryArgs);
                queryArgs.addParameter(queryRequestXml);
                inputStream = submitter.submit(queryArgs, null, "UTF-8");
            }
        }
        catch (GuardVetoException ex)
        {
            // close the input stream
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    log.error("Could not close input.");
                }
            }

            throw ex;
        }
        catch (Exception ex)
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    log.error("Could not close input.");
                }
            }

            log.error("Could not process the query.", ex);
            throw new ComponentFailureException(
                component,
                "Could not process query because of the following reason: " + ex.toString()
            );
        }

        if (inputStream == null)
        {
            log.info("No output from component? Analyzing response headers...");
			try {
				QueryProcessor.generateNoOutputFailure(component, submitter);
			} catch (ComponentFailureException e1) {
                log.error("Response headers analysis.", e1);
                throw e1;
			}
        }

        // read the input entirely.
        try
        {
            byte [] bytes = FileHelper.readFullyAndCloseInput(inputStream);
            info.addStreamInfo("Data received from input component: " + componentId, bytes);
            inputStream = new ByteArrayInputStream( bytes );
        }
        catch (IOException e)
        {
            log.error("I/O error reading input component's response.");
            throw e;
        }

        log.info("Finished reading from: " + componentId);
        return inputStream;
    }


    public InputStream invokeFilterComponent(String componentId, InputStream data)
        throws IOException, ComponentFailureException
    {
        return invokeFilterOrOutputComponent(componentId, data, null);
    }


    public InputStream invokeFilterComponent(
        String componentId, InputStream data, Map optionalParams
    )
        throws IOException, ComponentFailureException
    {
        return invokeFilterOrOutputComponent(componentId, data, optionalParams);
    }


    public InputStream invokeOutputComponent(String componentId, InputStream data)
        throws IOException, ComponentFailureException
    {
        return invokeFilterOrOutputComponent(componentId, data, null);
    }


    public InputStream invokeOutputComponent(
        String componentId, InputStream data, Map optionalParams
    )
        throws IOException, ComponentFailureException
    {
        return invokeFilterOrOutputComponent(componentId, data, optionalParams);
    }


    public void sendResponse(InputStream data)
        throws IOException
    {
        byte [] buffer = new byte[8000];
        int i;

        while ((i = data.read(buffer)) > 0)
        {
            this.output.write(buffer, 0, i);
        }
    }


    public void dispose()
    {
        for (Iterator i = toDispose.iterator(); i.hasNext();)
        {
            InputStream is = (InputStream) i.next();

            try
            {
                is.close();
            }
            catch (IOException e)
            {
                log.warn("Cannot dispose of input stream: " + e.toString());
            }
        }

        toDispose.clear();
    }


    private final InputStream invokeFilterOrOutputComponent(
        String componentId, InputStream data, Map optionalParams
    )
        throws IOException, ComponentFailureException
    {
        info.startComponent(componentId);
        log.info("Invoking filter/output component: " + componentId + ", params: " + optionalParams);

        ComponentDescriptor component = componentsLoader.findComponent(componentId);

        if (component == null)
        {
            throw new IOException("Could not find component of id: " + componentId);
        }

        FormActionInfo actionInfo = new FormActionInfo(new URL(component.getServiceURL()), "post");
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);
        FormParameters queryArgs = new FormParameters();

        addOptionalParams(optionalParams, queryArgs);
        queryArgs.addParameter(new Parameter("carrot-xchange-data", data, false));

        if (queryGuard != null)
        {
            String permission;

            if (
                (permission = queryGuard.allowFilterComponent(component, session, request, context)) != null
            )
            {
                throw new GuardVetoException(component, "guard." + permission);
            }
        }

        InputStream inputStream = submitter.submit(queryArgs, null, "UTF-8");

        if (inputStream == null)
        {
            log.warn("No output from component? Analyzing response headers...");
            try {
                QueryProcessor.generateNoOutputFailure(component, submitter);
            } catch (ComponentFailureException e1) {
                log.error("Response headers analysis.", e1);
                throw e1;
            }
        }

        // read the input entirely.
        try
        {
            byte [] bytes = FileHelper.readFullyAndCloseInput(inputStream);
            info.addStreamInfo("Data received from filter/output component: " + componentId, bytes);
            inputStream = new ByteArrayInputStream( bytes );
        }
        catch (IOException e)
        {
            log.error("I/O error reading input component's response.");
            throw e;
        }

        log.info("End: Invoking filter/output component: " + componentId);
        return inputStream;
    }


    private final void addOptionalParams(Map optionalParams, FormParameters queryArgs)
    {
        if (optionalParams != null)
        {
            for (Iterator i = optionalParams.keySet().iterator(); i.hasNext();)
            {
                Object key = i.next();
                Object value = optionalParams.get(key);

                if (value instanceof Object [])
                {
                    Object [] values = (Object []) value;

                    for (int j = 0; j < values.length; j++)
                    {
                        queryArgs.addParameter(
                            new Parameter(key.toString(), values[j].toString(), false)
                        );
                    }
                }
                else
                {
                    queryArgs.addParameter(new Parameter(key.toString(), value.toString(), false));
                }
            }
        }
    }

    
}
