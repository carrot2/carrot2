

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


import com.dawidweiss.carrot.controller.carrot2.*;
import com.dawidweiss.carrot.controller.carrot2.components.*;
import com.dawidweiss.carrot.controller.carrot2.guard.*;
import com.dawidweiss.carrot.controller.carrot2.process.cache.*;
import com.dawidweiss.carrot.controller.carrot2.process.scripted.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessDescriptor;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;
import org.apache.bsf.*;
import org.apache.log4j.*;
import org.put.util.net.http.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * This class represents a 'resolved' process definition in a BSF language.
 */
public class ResolvedScriptedProcess
    implements ProcessDefinition
{
    private static final Logger log = Logger.getLogger(ResolvedScriptedProcess.class);
    private ProcessDescriptor descriptor;
    private String script;
    private String language;
    private ComponentsLoader componentsLoader;
    private MockController mockExecutionInfo;

    public ResolvedScriptedProcess(ComponentsLoader componentsLoader, ProcessDescriptor descriptor)
        throws IllegalArgumentException
    {
        this.descriptor = descriptor;

        if (descriptor.getProcessingScript() == null)
        {
            throw new IllegalArgumentException("Processing script does not exist.");
        }

        this.script = descriptor.getProcessingScript().getContent();
        this.language = descriptor.getProcessingScript().getLanguage();

        if (org.apache.bsf.BSFManager.isLanguageRegistered(language) == false)
        {
            throw new IllegalArgumentException("Script language not supported: " + language);
        }

        try
        {
            mockExecutionInfo = analyzeUsedComponents();
        }
        catch (Throwable t)
        {
            throw new RuntimeException(
                "The script did not pass mock object test: "
                + org.put.util.exception.ExceptionHelper.getStackTrace(t)
            );
        }

        if (!mockExecutionInfo.getSentsResponse())
        {
            throw new RuntimeException(
                "Script does not send any response to diagnostic mock object invocation."
            );
        }

        this.componentsLoader = componentsLoader;
    }

    public void applyProcess(
        Query query, OutputStream output, Map requestParameters, Cache cache, QueryGuard queryGuard,
        HttpSession session, HttpServletRequest request, ServletContext context
    )
        throws Throwable
    {
        BSFManager manager = new BSFManager();

        // declare global beans.
        manager.declareBean(
            "query", new QueryBean(query),
            com.dawidweiss.carrot.controller.carrot2.process.scripted.Query.class
        );

        ControllerBean controller = new ControllerBean(
                output, cache, queryGuard, session, request, context
            );
        controller.setDoCacheInput(false);
        controller.setUseCachedInput(true);

        manager.declareBean(
            "controller", controller,
            com.dawidweiss.carrot.controller.carrot2.process.scripted.Controller.class
        );

        // execute the script.
        try
        {
            manager.exec(language, "Process script: " + descriptor.getId(), 0, 0, script);
        }
        catch (BSFException e)
        {
            if (e.getTargetException() != null)
            {
                throw e.getTargetException();
            }

            throw e;
        }
        finally
        {
            // close any opened input streams.
            controller.dispose();
        }
    }


    public String getInputComponentId()
    {
        List l = this.mockExecutionInfo.getUsedInputs();

        if (l.size() > 0)
        {
            return (String) l.get(0);
        }

        return null;
    }


    public String getId()
    {
        return descriptor.getId();
    }


    public boolean isScripted()
    {
        return true;
    }


    public String getDefaultDescription()
    {
        return descriptor.getDescription();
    }


    /**
     * For scripted processes this method is rather a heuristic. It may return false even if the
     * proces uses the component.
     */
    public boolean usesComponent(ComponentDescriptor descriptor)
    {
        switch (descriptor.getType().getType())
        {
            case ComponentType.INPUT_TYPE:
                return this.mockExecutionInfo.getUsedInputs().contains(descriptor.getId());

            case ComponentType.OUTPUT_TYPE:
            case ComponentType.FILTER_TYPE:

                if (this.script.indexOf('"' + descriptor.getId() + '"') >= 0)
                {
                    return true;
                }

                return false;

            default:
                throw new RuntimeException("Unknown component type: " + descriptor.getType());
        }
    }


    /**
     * Executes the script on mock objects and analyzes the components used/ syntax etc.
     */
    protected MockController analyzeUsedComponents()
        throws Throwable
    {
        BSFManager manager = new BSFManager();

        // declare global beans.
        manager.declareBean(
            "query", new MockQuery(),
            com.dawidweiss.carrot.controller.carrot2.process.scripted.Query.class
        );

        MockController controller = new MockController();
        manager.declareBean(
            "controller", controller,
            com.dawidweiss.carrot.controller.carrot2.process.scripted.Controller.class
        );

        // execute the script.
        try
        {
            manager.exec(language, "Process script: " + descriptor.getId(), 0, 0, script);
        }
        catch (BSFException e)
        {
            if (e.getTargetException() != null)
            {
                throw e.getTargetException();
            }

            throw e;
        }

        return controller;
    }

    private final boolean DUMP_INTERMEDIATE_STREAMS = false;
    private final File DUMPDIR = new File("h:\\dump");
    private final int [] counter = new int[1];

    /**
     * This class implements the Controller interface made available to BSF beans in charge of a
     * process.
     */
    private class ControllerBean
        implements com.dawidweiss.carrot.controller.carrot2.process.scripted.Controller
    {
        private final OutputStream output;
        private boolean cached;
        private boolean writeToCache;
        private Cache cache;
        private QueryGuard queryGuard;
        private List toDispose = new LinkedList();
        private HttpSession session;
        private HttpServletRequest request;
        private ServletContext context;

        public ControllerBean(
            OutputStream output, Cache cacher, QueryGuard queryGuard, HttpSession session,
            HttpServletRequest request, ServletContext context
        )
        {
            this.output = output;
            this.cache = cacher;
            this.queryGuard = queryGuard;
            this.session = session;
            this.request = request;
            this.context = context;
        }

        public void setDoCacheInput(boolean newValue)
        {
            this.writeToCache = newValue;
        }


        public boolean getDoCacheInput()
        {
            return this.writeToCache;
        }


        public void setUseCachedInput(boolean newValue)
        {
            this.cached = newValue;
        }


        public boolean getUseCachedInput()
        {
            return this.cached;
        }


        public InputStream invokeInputComponent(
            String componentId,
            com.dawidweiss.carrot.controller.carrot2.process.scripted.Query query
        )
            throws IOException, ComponentFailureException
        {
            return invokeInputComponent(componentId, query, null);
        }


        public InputStream invokeInputComponent(
            String componentId,
            com.dawidweiss.carrot.controller.carrot2.process.scripted.Query query,
            Map optionalParams
        )
            throws IOException, ComponentFailureException
        {
            log.debug("start: " + componentId);

            ComponentDescriptor component = componentsLoader.findComponent(componentId);

            FormActionInfo actionInfo = new FormActionInfo(
                    new URL(component.getServiceURL()), "post"
                );
            FormParameters queryArgs = new FormParameters();
            HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

            java.io.InputStream inputStream = null;

            com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query q = new com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query();

            try
            {
                q.setContent(query.getQuery());
                q.setRequestedResults(
                    (query.getNumberOfExceptedResults() == 0) ? 100
                                                              : query.getNumberOfExceptedResults()
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

                    if (writeToCache && (inputStream != null))
                    {
                        log.debug("Caching output for query.");
                        inputStream = cache.cacheInputFor(
                                inputStream, q, componentId, optionalParams
                            );
                    }
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
                        log.warn("Could not close input.");
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
                        log.warn("Could not close input.");
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
                QueryProcessor.generateNoOutputFailure(component, submitter);
            }

            if (DUMP_INTERMEDIATE_STREAMS)
            {
                inputStream = dumpStream(inputStream);
            }

            toDispose.add(inputStream);

            log.debug("finished: " + componentId);

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
            log.debug("Start: Invoking filter/output component: " + componentId);

            ComponentDescriptor component = componentsLoader.findComponent(componentId);

			if (component == null)
				throw new IOException("Could not find component of id: "
                    + componentId);

            FormActionInfo actionInfo = new FormActionInfo(
                    new URL(component.getServiceURL()), "post"
                );
            HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);
            FormParameters queryArgs = new FormParameters();

            addOptionalParams(optionalParams, queryArgs);
            queryArgs.addParameter(new Parameter("carrot-xchange-data", data, false));

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

            InputStream inputStream = submitter.submit(queryArgs, null, "UTF-8");

            if (inputStream == null)
            {
                QueryProcessor.generateNoOutputFailure(component, submitter);
            }

            if (DUMP_INTERMEDIATE_STREAMS)
            {
                Class t = inputStream.getClass();
                inputStream = dumpStream(inputStream);
            }

            toDispose.add(inputStream);

            log.debug("End: Invoking filter/output component: " + componentId);

            return inputStream;
        }


        private final InputStream dumpStream(InputStream is)
        {
            // this implementation dumps the input stream without any delays.
            byte [] bytes;

            try
            {
                bytes = org.put.util.io.FileHelper.readFully(is);

                synchronized (counter)
                {
                    try
                    {
                        OutputStream os = new FileOutputStream(
                                new File(DUMPDIR, "carrot_dump_" + counter[0])
                            );
                        log.debug("Dumping intermediate stream #" + counter[0]);
                        counter[0]++;
                        os.write(bytes);
                        os.close();
                    }
                    catch (FileNotFoundException e)
                    {
                        throw new RuntimeException("Cannot dump the debugging stream.");
                    }
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException("Stream IO error.");
            }

            return new ByteArrayInputStream(bytes);
        }
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

    private static class QueryBean
        implements com.dawidweiss.carrot.controller.carrot2.process.scripted.Query
    {
        private final transient Query query;

        public QueryBean(Query query)
        {
            this.query = query;
        }

        public final String getQuery()
        {
            return query.getContent();
        }


        public final int getNumberOfExceptedResults()
        {
            return query.getRequestedResults();
        }
    }
}
