
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

package org.carrot2.input.xml;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.impl.XmlStreamInputComponent;
import org.carrot2.core.impl.XmlStreamInputComponent.QueryResult;
import org.carrot2.util.StringUtils;
import org.carrot2.util.resources.Resource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.*;

/**
 * <p>Implements a local input component that reads data from an XML file, transforms it using 
 * XSLT, extracts information from the resulting XML DOM and pushes it to the successor component.</p>
 *
 * <p>There are two arguments to this component: an XML file and the XSLT stylesheet to process it with.
 * Values of these arguments can be passed to the component instance during initialization or at query
 * processing time (in the request context). The following data types are recognized: 
 * {@link String} (converted to an URL), {@link URL}, {@link File}, {@link InputStream} or
 * (preferred) {@link Resource}.
 *
 * <p><b>Note that arguments passed at initialization time take precedence over arguments passed
 * at query-time (die to potential security issues).</b>
 *
 * <h2>Argument names and substitution strings</h2>
 *
 * <p>The <code>source</code> parameter points to the input XML. If a {@link String} is given,
 * parameters from the request context can become part of the string before it is 
 * converted to an {@link URL}. Parameters in the <code>source</code> string are encoded in
 * the following form: <code>${parameter_name}</code>. Default substituted parameters are:
 * <ul>
 *  <li><code>query</code> is replaced with URL-escaped current query,</li>
 *  <li><code>results</code> is replaced with the number of requested results,</li>
 * </ul></p>
 * 
 * <p><b>Note that parameter substitution is a runtime feature only. Default source passed in
 * the constructor will be prefetched and will not have any substituted parameters.</b>
 * 
 * <p>The <code>xslt</code> parameter should point to an XSLT file (allowed data types are 
 * described above). Note that passing XSLT at query time is not a performance-wise decision
 * since the XSLT will not be cached in any way. There is one special value of this parameter:
 * setting it to <code>identity</code> will cause the input XML to be parsed directly, with
 * no XSLT transformation (identity transformer). This can be used for sources that
 * emit XMLs in the Carrot2 format and do not require transformation.
 * 
 * <p>The XSLT must not contain external dependencies or references (URI resolver is set to 
 * <code>null</code>).
 * 
 * @author Dawid Weiss
 * @author Paul Dlug (identity XSLT patch)
 * @version $Revision$
 */
public class XmlLocalInputComponent 
    extends LocalInputComponentBase 
    implements RawDocumentsProducer
{
    private static Logger log = Logger.getLogger(XmlLocalInputComponent.class);

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
            .asList(new Object[] { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
            .asList(new Object[] { RawDocumentsProducer.class }));

    /** Name of the input XML parameter. */
    public static final String PARAM_SOURCE_XML = "source";
    
    /** Name of the input XSLT parameter. */
    public static final String PARAM_XSLT = "xslt";

    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** Current request context */
    private RequestContext requestContext;

    /**
     * URI resolver. Does nothing. 
     */
    private final static URIResolver uriResolver = new URIResolver() {
        public Source resolve(String href, String base) throws TransformerException
        {
            return null;
        }
    };

    /** Default query encoding. */
    protected static final String DEFAULT_QUERY_ENCODING = "UTF-8";

    /** Default XML (for the transformation). */
    protected final Document defaultXML;

    /** Precompiled default XSLT. */
    protected final Templates defaultXSLT;

    /** Query encoding. */
    protected final String queryEncoding;

    /** XSLT transformer factory. */
    private final TransformerFactory tFactory; 

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
        this.queryEncoding = queryEncoding;

        this.tFactory = TransformerFactory.newInstance();
        this.tFactory.setURIResolver(uriResolver);

        try {
            if (xml != null) {
                final SAXReader xmlReader = new SAXReader();
                
                // Check for parameters in case source is a string. Parameters
                // are not valid at initialization time.
                if (xml instanceof String && ((String) xml).indexOf("${") >= 0) {
                    throw new RuntimeException("Substitutable parameters are not valid " +
                            "at initialization time: " + xml);
                }

                defaultXML = xmlReader.read(resolveArgument(xml));
            } else {
                defaultXML = null;
            }
    
            if (xslt != null) {
                defaultXSLT = prepareTemplate(resolveArgument(xslt));
            } else {
                defaultXSLT = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Illegal arguments or I/O error.", e);
        }
    }

    /**
     * 
     */
    public void startProcessing(RequestContext requestContext)
            throws ProcessingException {
        super.startProcessing(requestContext);

        // Store the current context
        this.requestContext = requestContext;

        // See if the required attributes are present in the query context:
        resultDom(performQuery(requestContext.getRequestParameters()));
    }

    /**
     * Transform the input to a Document.
     */
    private final Document performQuery(Map params) 
        throws ProcessingException
    {
        try {
            final int requestedResults = getRequestedResults(params);

            // Determine the source.
            final Document inputXML;
            if (this.defaultXML != null) {
                inputXML = this.defaultXML;
            } else {
                final Object sourceArg = params.get(PARAM_SOURCE_XML);
                if (sourceArg == null) {
                    throw new ProcessingException("Parameter missing: " + PARAM_SOURCE_XML);
                }
                final SAXReader xmlReader = new SAXReader();
                inputXML = xmlReader.read(
                    resolveArgumentWithSubstitution(
                        sourceArg, query, requestedResults, params));
            }
    
            // Determine the XSLT.
            final Transformer t;
            if (this.defaultXSLT != null) {
                // transform with the default precompiled template.
                t = this.defaultXSLT.newTransformer();
            } else {
                final Object xsltArg = params.get(PARAM_XSLT);
    
                if (xsltArg == null) {
                    throw new ProcessingException("Parameter missing: " + PARAM_XSLT);
                }
    
                if ("identity".equals(xsltArg)) {
                    // special case, just pass to output.
                    return inputXML;
                } else {
                    // transform with ad-hoc XSLT.
                    t = this.tFactory.newTransformer(
                        new StreamSource(resolveArgument(xsltArg)));
                }
            }

            // Register request attributes as parameters for the transformer.
            for (Iterator i = params.keySet().iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                Object value = params.get(key);
                if (value instanceof String) {
                    t.setParameter(key, value);
                }
            }

            final DocumentResult dom = new DocumentResult();
            t.transform(new DocumentSource(inputXML), dom);

            return dom.getDocument();
        } catch (Exception e) {
            throw new ProcessingException("Could not process query.", e);
        }
    }

    /**
     * <p>This method is invoked when the source has been parsed and 
     * a dom4j DOM tree is ready for extraction of information.
     * 
     * <p>By default, the transformed XML is considered to be consistent
     * with the Carrot2 search result XML specification.
     */
    protected void resultDom(Document doc) throws ProcessingException {
        pushAsLocalData(doc.getRootElement());
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
	        final StringBuffer buf = new StringBuffer(url);
	        
	        while ( (index = buf.indexOf("${", index)) >= 0) {
	            int lastIndex = buf.indexOf("}", index+2);
	            if (lastIndex < 0) {
	                // no ending '}', ignore.
	                log.warn("No ending '}' in the substitution parameter of the URL: " + url);
	                index = index + 2;
	                continue;
	            }
	            final String paramName = buf.substring(index+2, lastIndex);
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
		                String value = URLEncoder.encode(
                            (String) requestParams.get(paramName), queryEncoding); 
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
            throw new RuntimeException("Encoding not supported: " + queryEncoding);
        }
    }

    /*
     * @see org.carrot2.core.LocalComponent#getName()
     */
    public String getName() {
        return "Xml Input";
    }

    /** */
    public void setNext(LocalComponent next) {
        super.setNext(next);

        if (next instanceof RawDocumentsConsumer) {
            rawDocumentConsumer = (RawDocumentsConsumer) next;
        } else {
            rawDocumentConsumer = null;
        }
    }
    
    /** */
    public void setQuery(String query) {
        this.query = query;
    }

    /** */
    public Set getComponentCapabilities() {
        return COMPONENT_CAPABILITIES;
    }

    /** */
    public Set getRequiredSuccessorCapabilities() {
        return SUCCESSOR_CAPABILITIES;
    }

    /** */
    public void flushResources() {
        super.flushResources();

        query = null;
        rawDocumentConsumer = null;
        requestContext = null;
    }

    /**
     * Push data from the XML result as a sequence of {@link RawDocument}s.
     */
    private void pushAsLocalData(Element root) throws ProcessingException {
        final QueryResult queryResult = XmlStreamInputComponent.extractQueryResult(root, -1);
    
        // Pass the actual document count
        requestContext.getRequestParameters().put(
                LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
                new Integer(queryResult.rawDocuments.size()));
    
        // Pass the query (if there was any)
        if (!StringUtils.isBlank(queryResult.query)) {
    	    requestContext.getRequestParameters().put(
    	            LocalInputComponent.PARAM_QUERY, queryResult.query);
        }
    
        for (Iterator i = queryResult.rawDocuments.iterator(); i.hasNext();)
        {
            final RawDocument document = (RawDocument) i.next();
            this.rawDocumentConsumer.addDocument(document);
        }
    }

    /**
     * Resolve the type of the input argument and
     * convert it to an {@link InputStream}.
     */
    private InputStream resolveArgument(Object source)
        throws IOException
    {
        if (source instanceof InputStream)
        {
            return (InputStream) source;
        }
    
        if (source instanceof String)
        {
            // fall through to the URL recognition block.
            try {
                source = new URL((String) source);
            } catch (MalformedURLException e) {
                log.warn("Malformed source URL: " + source);
                throw new IOException("Malformed source URL: " + source);
            }
        }
    
        if (source instanceof URL)
        {
            return  ((URL) source).openStream();
        }
    
        if (source instanceof File)
        {
            return new BufferedInputStream(new FileInputStream((File) source));
        }

        if (source instanceof Resource)
        {
            return ((Resource) source).open();
        }
    
        throw new IOException("Argument type not recognized: " + source);
    }

    /**
     * Resolve the type of the input argument, substituting parameters
     * in case it is a {@link String}.
     */
    private InputStream resolveArgumentWithSubstitution(
        Object source, String query, int requestedResults, Map parameters)
        throws IOException
    {
        if (source instanceof String)
        {
            // Substitute parameters and convert the String to an URL.
            source = substituteParams(
                (String) source, query, parameters, requestedResults, queryEncoding);
        }
    
        return resolveArgument(source);
    }

    /**
     * Precompile XSLT stylesheet. May throw runtime exceptions if the stylesheet
     * is corrupted.
     */
    private Templates prepareTemplate(InputStream stream)
    {
        if (!tFactory.getFeature(SAXSource.FEATURE)
                || !tFactory.getFeature(SAXResult.FEATURE)) {
            throw new RuntimeException(
                    "Required source types not supported by the Transformer Factory.");
        }

        if (!tFactory.getFeature(SAXResult.FEATURE)
                || !tFactory.getFeature(StreamResult.FEATURE)) {
            throw new RuntimeException(
                    "Required result types not supported by the Transformer Factory.");
        }

        if (!(tFactory instanceof SAXTransformerFactory)) {
            throw new RuntimeException(
                    "TransformerFactory not an instance of SAXTransformerFactory");
        }

        tFactory.setErrorListener(new ErrorListener() {
            public void warning(TransformerException exception) throws TransformerException {
                throw exception;
            }

            public void error(TransformerException exception) throws TransformerException {
                throw exception;
            }

            public void fatalError(TransformerException exception) throws TransformerException {
                throw exception;
            }
        });

        try
        {
            return tFactory.newTemplates(new StreamSource(stream));
        }
        catch (TransformerConfigurationException e)
        {
            throw new RuntimeException("Could not compile stylesheet.", e);
        }
    }
}
