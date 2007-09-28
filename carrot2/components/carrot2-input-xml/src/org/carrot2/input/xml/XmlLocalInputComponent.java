
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

package org.carrot2.input.xml;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.SAXReader;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;

/**
 * <p>Implements a local input component that reads data from an XML file, transforms it using 
 * XSLT, extracts information from the resulting file and pushes it to the successor component.</p>
 * 
 * <p>Input XML file must be specified as an {@link URL}, {@link File} or {@link InputStream} object
 * in the <code>source</code> parameter of the request context or in the constructor. 
 * Alternatively, a {@link String} object can be provided - it is converted to an URL after 
 * substitution of parameters of the form: <code>${parameter_name}</code>. Named parameters must be present in
 * the request context.</p>
 * 
 * <p>Default substituted parameters are:
 * <ul>
 *  <li><code>query</code> is replaced with URL-escaped current query,</li>
 *  <li><code>results</code> is replaced with the number of requested results,</li>
 * </ul></p>
 * 
 * <p>
 * Input XSLT file must be specified as an URL, File or InputStream object
 * in the 'xslt' parameter of the request context. Alternatively, the 'xslt'
 * parameter may contain the String "identity" in which case the 'source'
 * will be parsed directly and returned. This is used for components that
 * directly output XML in the carrot2 format and do not require transformation.
 * </p>
 * 
 * <p>
 * Override this class and extract useful
 * information from the DOM tree in {todo} method to create
 * custom components. 
 * </p>
 * 
 * @author Dawid Weiss
 * @author Paul Dlug (identity XSLT patch)
 * @version $Revision$
 */
public class XmlLocalInputComponent extends
        LocalInputComponentBase implements RawDocumentsProducer, URIResolver {

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
     * Default query encoding: {@value} 
     */
    protected static final String DEFAULT_QUERY_ENCODING = "UTF-8";
    
    protected final Object defaultXml;
    protected final Object defaultXslt;
    protected final String queryEncoding;

    public static final String PARAM_SOURCE_XML = "source";
    public static final String PARAM_XSLT = "xslt";

    /**
     * Creates a new instance of the component.
     */
    public XmlLocalInputComponent() {
        this(null, null);
    }

    /**
     * Creates a new instance of the component with provided XSLT and XML and
     * UTF-8 query encoding.
     */
    public XmlLocalInputComponent(Object xml, Object xslt) {
        this(xml, xslt, DEFAULT_QUERY_ENCODING);
    }
    
    /**
     * Creates a new instance of the component with default XSLT and XML.
     */
    public XmlLocalInputComponent(Object xml, Object xslt, String queryEncoding) {
        this.defaultXml = xml;
        this.defaultXslt = xslt;
        this.queryEncoding = queryEncoding;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Set getComponentCapabilities() {
        return COMPONENT_CAPABILITIES;
    }

    public Set getRequiredSuccessorCapabilities() {
        return SUCCESSOR_CAPABILITIES;
    }

    public void flushResources() {
        super.flushResources();
        query = null;
        rawDocumentConsumer = null;
        requestContext = null;
    }

    public void setNext(LocalComponent next) {
        super.setNext(next);
        if (next instanceof RawDocumentsConsumer) {
            rawDocumentConsumer = (RawDocumentsConsumer) next;
        } else {
            rawDocumentConsumer = null;
        }
    }

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
    protected Document performQuery(Map params) throws ProcessingException {
        int requestedResults = getRequestedResults(params);

        InputStream source = null;
        Object sourceOb = params.get(PARAM_SOURCE_XML);
        if (sourceOb == null) {
            if (defaultXml == null) {
                throw new ProcessingException("source request parameter must be given (URL, File, InputStream or a String (converted to URL)).");
            } else {
                sourceOb = defaultXml;
            }
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
	            String stringifiedUrl = substituteParams((String) sourceOb, query,
                    params, requestedResults, queryEncoding);
                log.debug("Transformed URL: " + stringifiedUrl);
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
                    + sourceOb);
        }

        InputStream xslt = null;
        Object xsltOb = params.get(PARAM_XSLT);
        if (xsltOb == null) {
            if (defaultXslt == null) {
                throw new ProcessingException("xslt request parameter must be given (URL, File, InputStream or identity).");
            } else {
                xsltOb = defaultXslt;
            }
        }

        if ("identity".equals(xsltOb)) {
            try {
                SAXReader xmlReader = new SAXReader();
                final Document doc = xmlReader.read(source);
                return doc;
            } catch (DocumentException e) {
                throw new ProcessingException("SAXReader parsing exception: " + e.toString());
            }
        }

        try {
            if (xsltOb instanceof InputStream) {
                xslt = (InputStream) xsltOb;
            } else if (xsltOb instanceof URL) {
                xslt = ((URL) xsltOb).openStream();
	        } else if (xsltOb instanceof File) {
                xslt = new BufferedInputStream(
                        new FileInputStream((File) xsltOb));
	        } else if (xsltOb instanceof String)
	        {
	            // Try to parse string as url
                try {
                    URL url = new URL((String)xsltOb);
                    xslt = url.openStream();
                } catch (MalformedURLException e) {
                    throw new ProcessingException("Malformed source URL: " + xsltOb);
                }
	        }
	        else {
	            throw new ProcessingException("xslt must be an URL or a File: "
	                    + xsltOb);            
	        }
        } catch (IOException e) {
            throw new ProcessingException("Could not open stream for URL: " + xsltOb);
        }
        
        // we have an input and an xslt stylesheet. Perform transformation.
        TransformerFactory tf = TransformerFactory.newInstance(); 
        tf.setURIResolver(this);
        try {
            Transformer transformer = tf.newTransformer(new StreamSource(xslt));
            transformer.setURIResolver(this);
            
            // Register request attributes as parameters for the transformer.
            for (Iterator i = params.keySet().iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                Object value = params.get(key);
                if (value instanceof String) {
                    transformer.setParameter(key, value);
                }
            }
            
            DocumentResult dom = new DocumentResult();
            transformer.transform(new StreamSource(source), dom);
            
            // we have the DOM. do something with the result
            return dom.getDocument();
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
    protected void resultDom(Document doc) throws ProcessingException {
        pushAsLocalData( doc.getRootElement() );
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
    protected static String substituteParams(String url, String query, Map requestParams,
        int requestedResults, String queryEncoding)
    {
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
                if ("results".equals(paramName)) {
                    final String res = Integer.toString(requestedResults);
                    buf.replace(index, lastIndex+1, res);
                    index = index + res.length();
                } else if ("query".equals(paramName)) {
	                String v = URLEncoder.encode(query, queryEncoding);
	                buf.replace(index, lastIndex+1, v);
	                index = index + v.length();
	            } else if (requestParams.containsKey(paramName)) {
	                try {
		                String value = URLEncoder.encode((String) requestParams.get(paramName), DEFAULT_QUERY_ENCODING); 
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
     * @see org.carrot2.core.LocalComponent#getName()
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

            if (docElem.element("sources") != null)
            {
                List sources = docElem.element("sources").elements();
                String [] sourcesArray = new String [sources.size()];
                int j = 0;
                for (Iterator it = sources.iterator(); it.hasNext(); j++)
                {
                    Element sourceElement = (Element) it.next();
                    sourcesArray[j] = sourceElement.getText();
                }
                document.setProperty(RawDocument.PROPERTY_SOURCES, sourcesArray);
            }

            this.rawDocumentConsumer.addDocument(document);
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
     */
    public Source resolve(String href, String base) throws TransformerException
    {
        return null;
    }
}
