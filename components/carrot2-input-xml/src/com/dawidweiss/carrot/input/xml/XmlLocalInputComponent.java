/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.input.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * Implements a local input component that reads data
 * from an XML file, transforms it using XSLT, extracts
 * information from that file and pushes it to the successor
 * component.
 * 
 * <p>
 * Input XML file must be specified as an URL, File or InputStream object
 * in the 'source' parameter of the request context. Alternatively, a String object
 * can be provided. It is converted to an URL after substitution of parameters of
 * the form: <code>${parameter_name}</code>. Named parameters must be present in
 * the request context. <code>query</code> is replaced with the current query.
 * </p>
 * 
 * <p>
 * Input XSLT file must be specified as an URL, File or InputStream object
 * in the 'xslt' parameter of the request context.
 * </p>
 * 
 * <p>
 * Override this class and extract useful
 * information from the DOM tree in {todo} method to create
 * custom components. 
 * </p>
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class XmlLocalInputComponent extends
        LocalInputComponentBase implements RawDocumentsProducer {
    private static Logger log = Logger
            .getLogger(XmlLocalInputComponent.class);

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
            .asList(new Object[] { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
            .asList(new Object[] { RawDocumentsProducer.class }));

    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** Current request context */
    private RequestContext requestContext;

    /**
     * Creates a new instance of the component.
     */
    public XmlLocalInputComponent() {
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities() {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities() {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources() {
        super.flushResources();
        query = null;
        rawDocumentConsumer = null;
        requestContext = null;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
     */
    public void setNext(LocalComponent next) {
        super.setNext(next);
        if (next instanceof RawDocumentsConsumer) {
            rawDocumentConsumer = (RawDocumentsConsumer) next;
        } else {
            rawDocumentConsumer = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
            throws ProcessingException {
        super.startProcessing(requestContext);

        // Store the current context
        this.requestContext = requestContext;

        // See if the required attributes are present in the query
        // context:
        Map params = requestContext.getRequestParameters();
        
        resultDom( performQuery(params) );
    }
    
    /**
     * This is the actual workhorse. This method is also used by the remote component.
     */
    protected DocumentResult performQuery(Map params) throws ProcessingException {
        InputStream source = null;
        Object sourceOb = params.get("source");
        if (sourceOb == null) {
            throw new ProcessingException("source request parameter must be given (URL, File, InputStream or a String (converted to URL)).");
        }
        try {
            if (sourceOb instanceof InputStream) {
                source = (InputStream) sourceOb;
            } else if (sourceOb instanceof URL) {
                source = ((URL) sourceOb).openStream();
	        } else if (sourceOb instanceof File) {
                source = new BufferedInputStream(
                        new FileInputStream((File) sourceOb));
	        } else if (sourceOb instanceof String){
	            // Try if the sourceOb converts to an URL at all,
	            // substitute parts of the URL if needed
	            String stringifiedUrl = substituteParams((String) sourceOb, params);
	            try {
	                URL url = new URL(stringifiedUrl);
	                source = url.openStream();
	            } catch (MalformedURLException e) {
	                log.warn("Malformed URL: " + stringifiedUrl);
	                throw new ProcessingException("Malformed source URL: " + stringifiedUrl);
	            }
	        } else {
	            throw new ProcessingException("source must be an URL or a File: "
	                    + sourceOb);            
	        }
        } catch (IOException e) {
            throw new ProcessingException("Could not open stream for URL: "
                    + params.get("source"));
        }

        InputStream xslt = null;
        Object xsltOb = params.get("xslt");
        if (xsltOb == null) {
            throw new ProcessingException("xslt request parameter must be given (URL, File or InputStream).");
        }
        try {
            if (xsltOb instanceof InputStream) {
                xslt = (InputStream) xsltOb;
            } else if (xsltOb instanceof URL) {
                xslt = ((URL) xsltOb).openStream();
	        } else if (xsltOb instanceof File) {
                xslt = new BufferedInputStream(
                        new FileInputStream((File) xsltOb));
	        } else {
	            throw new ProcessingException("xslt must be an URL or a File: "
	                    + xsltOb);            
	        }
        } catch (IOException e) {
            throw new ProcessingException("Could not open stream for URL: "
                    + params.get("xslt"));
        }
        
        // we have an input and an xslt stylesheet. Perform transformation.
        TransformerFactory tf = TransformerFactory.newInstance(); 
        try {
            Transformer transformer = tf.newTransformer(new StreamSource(xslt));
            DocumentResult dom = new DocumentResult();
            transformer.transform(new StreamSource(source), dom);
            
            // we have the DOM. do something with the result
            return dom;
        } catch (TransformerConfigurationException e) {
            log.error(e);
            throw new ProcessingException("XSLT transformer configuration exception: " + e.toString());
        } catch (TransformerException e) {
            log.error(e);
            throw new ProcessingException("XSLT transformer exception: " + e.toString());
        }        
    }

    /**
     * This method is invoked when the source has been parsed and 
     * a dom4j DOM tree is ready for extraction of information.
     * 
     * By default, the transformed XML is considered to be consistent
     * with the Carrot2 search result XML specification.
     */
    protected void resultDom(DocumentResult dom) throws ProcessingException {
        pushAsLocalData( dom.getDocument().getRootElement() );
    }

    /**
     * Substitutes strings of the form: <code>${param_name}</code> with
     * values from the request parameters (only if param's name is a String).
     * 
     * An additional parameter is called <code>query</code> and is substituted
     * with a currently executed query.
     * 
     * @param url
     * @param requestParams
     * @return The url with parameters substituted with their values.
     */
    protected String substituteParams(String url, Map requestParams) {
        try {
	        int index = 0;
	        
	        StringBuffer buf = new StringBuffer(url);
	        
	        while ( (index = buf.indexOf("${", index)) >= 0) {
	            int lastIndex = buf.indexOf("}", index+2);
	            if (lastIndex < 0) {
	                // no ending '}', ignore.
	                log.warn("No ending '}' in the substitution parameter of the URL: "
	                        + url);
	                index = index + 2;
	                continue;
	            }
	            String paramName = buf.substring(index+2, lastIndex);
	            if ("query".equals(paramName)) {
	                String v = URLEncoder.encode(query, "UTF-8");
	                buf.replace(index, lastIndex+1, v);
	                index = index + v.length();
	            } else if (requestParams.containsKey(paramName)) {
	                try {
		                String value = URLEncoder.encode((String) requestParams.get(paramName), "UTF-8"); 
		                buf.replace(index, lastIndex+1, value);
		                index = index + value.length();
	                } catch (ClassCastException e) {
	                    // ignore.
	                }
	            } else {
	                // check if maybe there was a missing closing bracket?
	                int tmp = buf.indexOf("${", index + 1);
	                if (tmp >= 0 && tmp < lastIndex) {
	                    // yes.
	                    log.warn("Missing closing bracket?: " + paramName + 
	                            " (" + url + ")");
	                    index = index + 2;
	                    continue;
	                }
	                log.warn("No value to substitute: " + paramName
	                        	+ " (" + url + ")");
	                index = lastIndex;
	            }
	        }
	
	        return buf.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported?");
        }
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName() {
        return "Xml Input";
    }

    private void pushAsLocalData(Element root) throws ProcessingException {
        List documents = root.elements("document");

        // Pass the actual document count
        requestContext.getRequestParameters().put(
                LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
                new Integer(documents.size()));

        // Pass the query (if there was any)
        if (!query.equals("")) {
		    requestContext.getRequestParameters().put(
		            LocalInputComponent.PARAM_QUERY, query);
        }

        int id = 0;
        for (Iterator i = documents.iterator(); i.hasNext(); id++) {
            Element docElem = (Element) i.next();

            String url = docElem.elementText("url");
            String title = docElem.elementText("title");
            String snippet = docElem.elementText("snippet");

            RawDocument document = new RawDocumentSnippet(new Integer(id),
                    title, snippet, url, 0);
            this.rawDocumentConsumer.addDocument(document);
        }
    }
}