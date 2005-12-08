package com.dawidweiss.carrot.input.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.dawidweiss.carrot.util.common.StreamUtils;
import com.dawidweiss.carrot.util.net.http.FormActionInfo;
import com.dawidweiss.carrot.util.net.http.FormParameters;
import com.dawidweiss.carrot.util.net.http.Parameter;

/**
 * A Yahoo Search Service.
 * 
 * @author Dawid Weiss
 */
public class YahooSearchService {
    private YahooSearchServiceDescriptor descriptor;

    public YahooSearchService(YahooSearchServiceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Searches Yahoo and retrieves a maximum of <code>requestedResults</code>
     * snippets. May throw an exception if service is no longer available.
     * 
     * @throws IOException If an I/O exception occurred. 
     */
    public YahooSearchResult [] query(final String query, 
            final int requestedResults) throws IOException {

        final ArrayList result = new ArrayList(requestedResults); 

        final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        final HttpClient client = new HttpClient(connectionManager);
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        final FormActionInfo formActionInfo = descriptor.getFormActionInfo();

        InputStream is = null; 

        int startFrom = 1;
        long resultsLeft = requestedResults;
        boolean firstPass = true;

        try {
            final YahooResponseHandler handler = new YahooResponseHandler(result);
            final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            final XMLReader reader = parser.getXMLReader();
            reader.setFeature("http://xml.org/sax/features/validation", false);
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setContentHandler(handler);

            while (true) {
                final long perQueryResults = Math.min(descriptor.getMaxResultsPerQuery(), resultsLeft);
                if (perQueryResults <= 0) {
                    break;
                }

                final HashMap mappedParameters = new HashMap();
                mappedParameters.put("query.string", query);
                mappedParameters.put("query.startFrom", Integer.toString(startFrom));
                mappedParameters.put("query.results", Long.toString(perQueryResults));

                // Convert from FormActionInfo/FormParameters to HttpClient. This could
                // be implemented in a nicer way perhaps.
                final String method = descriptor.getFormActionInfo().getMethod();
                final FormParameters parameters = descriptor.getFormParameters();
                final HttpMethodBase httpMethod;
                if ("GET".equalsIgnoreCase(method)) {
                    httpMethod = new GetMethod();
                } else {
                    httpMethod = new PostMethod();
                }
                final String url = formActionInfo.getServiceURL().toExternalForm();
                httpMethod.setURI(new URI(url, false));
                httpMethod.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");

                try {
                    HashMap httpHeaders = formActionInfo.getHttpHeaders();
                    for (Iterator i = httpHeaders.keySet().iterator(); i.hasNext();) {
                        String header = (String) i.next();
                        String value = (String) httpHeaders.get(header);
                        httpMethod.addRequestHeader(new Header(header, value));
                    }
                    httpMethod.addRequestHeader(new Header("Accept-Encoding", "gzip"));
    
                    // Now convert parameters.
                    ArrayList nameValues = new ArrayList();
                    for (Iterator i = parameters.getParametersIterator(); i.hasNext();) {
                        final Parameter p = (Parameter) i.next();
                        final String name = p.getName();
                        final Object value = p.getValue(mappedParameters);
                        if (value instanceof String) {
                            nameValues.add(new NameValuePair(name, (String) value));
                        } else {
                            throw new RuntimeException("Only String mapped parameters supported.");
                        }
                    }

                    if ("GET".equalsIgnoreCase(method)) {
                        final NameValuePair [] nameValueArray = (NameValuePair [])
                            nameValues.toArray(new NameValuePair[nameValues.size()]);
                        ((GetMethod) httpMethod).setQueryString(nameValueArray);
                    } else {
                        for (int i = 0; i < nameValues.size(); i++) {
                            ((PostMethod) httpMethod).addParameter((NameValuePair) nameValues.get(i));
                        }
                    }
                    
                    int statusCode = client.executeMethod(httpMethod);
                    if (statusCode == HttpStatus.SC_OK) {
                        is = httpMethod.getResponseBodyAsStream();
                        Header encoded = httpMethod.getResponseHeader("Content-Encoding");
                        if (encoded != null && "gzip".equals(encoded.getValue())) {
                            is = new GZIPInputStream(is);
                        }
                        reader.parse(new InputSource(is));
                        is.close();
                        is = null;
                    } else {
                        is = httpMethod.getResponseBodyAsStream();
                        Header encoded = httpMethod.getResponseHeader("Content-Encoding");
                        if (encoded != null && "gzip".equals(encoded.getValue())) {
                            is = new GZIPInputStream(is);
                        }
                        final byte [] message = StreamUtils.readFully(is);
                        throw new IOException("Yahoo returned HTTP Error: "
                                + statusCode + ", HTTP payload: "
                                + new String(message, "iso8859-1"));
                    }
                } finally {
                    httpMethod.releaseConnection();
                }
                
                if (handler.isErraneous()) {
                    throw new IOException("Yahoo service error: "
                            + handler.getErrorText());
                }

                if (handler.firstResultPosition != startFrom && handler.resultsReturned > 0) {
                    throw new IOException("Assertion failed: returned startFrom different.");
                }
                if (firstPass) {
                    // Correct the number of requested results to the maximum available.
                    resultsLeft = Math.min(requestedResults, handler.totalResults);
                    firstPass = false;
                } else {
                    if (handler.resultsReturned == 0) {
                        throw new IOException("Assertion failed: returned zero results?");
                    }
                }
                resultsLeft -= handler.resultsReturned;
                startFrom += handler.resultsReturned;
            }
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Problems setting up XML parser: " + e.toString(), e);
        } catch (SAXException e) {
            throw new RuntimeException("Problems parsing Yahoo output: " + e.toString(), e);
        } finally {
            connectionManager.shutdown();
        }

        return (YahooSearchResult []) result.toArray(new YahooSearchResult [result.size()]);
    }
}
