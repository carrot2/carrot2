
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.local.remoteadapters;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponentBase;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer;
import com.dawidweiss.carrot.util.common.XMLSerializerHelper;
import com.dawidweiss.carrot.util.net.http.FormActionInfo;
import com.dawidweiss.carrot.util.net.http.FormParameters;
import com.dawidweiss.carrot.util.net.http.HTTPFormSubmitter;
import com.dawidweiss.carrot.util.net.http.Parameter;


public class RemoteRawDocumentsProducerInputComponent
    extends LocalInputComponentBase
    implements RawDocumentsProducer, LocalInputComponent
{
    private final static Logger log = Logger.getLogger( RemoteRawDocumentsProducerInputComponent.class ); 
    
    /** The default number of results to retrieve from the input component. */
    private final static int DEFAULT_RESULTS_NUMBER = 100;

    private static final Set CAPABILITIES_COMPONENT =
        new HashSet(Arrays.asList(
            new Object [] {
                RawDocumentsProducer.class,
            }
        ));

    private URL remoteComponentUrl;
    private String query;
    
    private RawDocumentsConsumer rawDocumentConsumer;
    private com.dawidweiss.carrot.util.common.XMLSerializerHelper serializer
        = XMLSerializerHelper.getInstance(); 


    public RemoteRawDocumentsProducerInputComponent(URL remoteUrl)
    {
        this.remoteComponentUrl = remoteUrl;
    }
    
    /**
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        if (!(next instanceof RawDocumentsConsumer)) {
            log.warn("Successor component not an instance of: "
                + RawDocumentsConsumer.class.getName());
        } else {
            this.rawDocumentConsumer = (RawDocumentsConsumer) next;
        }
    }

    public void startProcessing(RequestContext requestContext) 
        throws ProcessingException
    {
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_QUERY, this.query);

        super.startProcessing(requestContext);

        if (query == null)
            return;

        // now query the remote input document.
        int resultsRequested = super.getIntFromRequestContext(
            requestContext, LocalInputComponent.PARAM_REQUESTED_RESULTS,
            DEFAULT_RESULTS_NUMBER);
        
        try {
            InputStream is = queryRemoteSource(this.query, resultsRequested);

            // now parse the input stream.
            SAXReader builder = new SAXReader();
            Element root = builder.read(
                new InputStreamReader(is, "UTF-8")).getRootElement();

            // unwrap the results
            if (!"searchresult".equals(root.getName())) {
                throw new ProcessingException("Search result XML not starting with 'searchresult' element.");
            }
            
            List children = root.elements();
            boolean debugUnknownAttrs = 
                log.isEnabledFor(org.apache.log4j.Level.DEBUG);
            for (Iterator i = children.iterator(); i.hasNext(); ) {
                Element elem = (Element) i.next();
                if ("document".equals( elem.getName() )) {
                    // pass raw document node.
                    if (rawDocumentConsumer != null)
                        rawDocumentConsumer.addDocument(
                            new RawDocumentElementWrapper(elem));
                } 
                else if ("group".equals(elem.getName())) {
                    
                } else {
                    if (debugUnknownAttrs) {
                        log.debug("Unknown element in the XML stream: "
                            + elem.getName());
                    }
                }
            }
        }
        catch (IOException e) {
            throw new ProcessingException("Could not query remote component atL "
                + this.remoteComponentUrl.toExternalForm(), e);
        }
        catch (DocumentException e)
        {
            throw new ProcessingException("Result XML parsing exception.", e);
        }
    }
    
    protected InputStream queryRemoteSource(String queryString, int resultsRequested)
        throws IOException
    {
        FormActionInfo actionInfo = new FormActionInfo(remoteComponentUrl, "post");
        FormParameters queryArgs = new FormParameters();
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

        StringBuffer buf = new StringBuffer();

        // assemble the query xml.
        buf.append("<query requested-results=\"");
        buf.append(resultsRequested);
        buf.append("\">");
        buf.append(serializer.toValidXmlText(queryString, false));
        buf.append("</query>");

        Parameter queryRequestXml = new Parameter(
                "carrot-request", buf.toString(), false
            );
        queryArgs.addParameter(queryRequestXml);

        InputStream data = submitter.submit(queryArgs, null, "UTF-8");

        if (data == null)
        {
            throw new IOException(
                "Server returned suspicious HTTP response code: ("
                + ((java.net.HttpURLConnection) submitter.getConnection()).getResponseCode() + ") "
                + ((java.net.HttpURLConnection) submitter.getConnection()).getResponseMessage()
            );
        }

        return data; 
    }



    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();

        // clean up.
        this.query = null;
        this.remoteComponentUrl = null;
        this.rawDocumentConsumer = null;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

}
