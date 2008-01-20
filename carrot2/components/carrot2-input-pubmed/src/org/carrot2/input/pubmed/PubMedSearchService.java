
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

package org.carrot2.input.pubmed;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.xml.parsers.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.log4j.*;
import org.carrot2.core.*;
import org.carrot2.util.*;
import org.xml.sax.*;


/**
 * Performs searches on the PubMed database using its on-line e-utilities:
 * http://eutils.ncbi.nlm.nih.gov/entrez/query/static/eutils_help.html
 * 
 * @author Stanislaw Osinski
 */
public class PubMedSearchService
{
    private final static Logger log = Logger
            .getLogger(PubMedSearchService.class);

    /** URLs for the PubMed service */
    public static final String E_SEARCH_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi";
    public static final String E_FETCH_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi";


    /**
     * Searches PubMed and feeds the consumer with a maximum of
     * <code>requestedResults</code> search results. Throws {@link IOException}
     * in case of problems with the PubMed service.
     */
    public void query(final String query, final int requestedResults,
            PubMedSearchResultConsumer consumer)
        throws IOException
    {
        final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        final HttpClient client = new HttpClient(connectionManager);
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        try {
            getPubMedAbstracts(getPubMedIds(query, requestedResults, client),
                    client, consumer);
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException("Problems setting up XML parser: "
                    + e.toString(), e);
        }
        catch (SAXException e) {
            throw new IOException("Problems parsing PubMed response: "
                    + e.getMessage());
        }
        catch (FactoryConfigurationError e) {
            throw new RuntimeException("Problems setting up XML parser: "
                    + e.toString(), e);
        }
        catch (Exception e) {
            throw new IOException("Problems fetching results from PubMed: "
                    + e.getMessage());
        }
        finally {
            connectionManager.shutdown();
        }
    }


    /**
     * Searches PubMed and returns a maximum of <code>requestedResults</code>
     * search results. Throws {@link IOException} in case of problems with the
     * PubMed service.
     */
    public PubMedSearchResult[] query(final String query,
            final int requestedResults)
        throws IOException
    {
        final List results = new ArrayList(requestedResults);

        query(query, requestedResults, new PubMedSearchResultConsumer() {
            public void add(PubMedSearchResult result)
                throws ProcessingException
            {
                results.add(result);
            }
        });

        return (PubMedSearchResult[])results
                .toArray(new PubMedSearchResult[results.size()]);
    }


    /**
     * Gets PubMed entry ids matching the query.
     */
    private List getPubMedIds(final String query, final int requestedResults,
            final HttpClient client)
        throws Exception
    {
        // Set-up parser and handler for ESearch first
        SAXParser searchParser;
        searchParser = SAXParserFactory.newInstance().newSAXParser();
        final XMLReader reader = searchParser.getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);

        PubMedSearchHandler searchHandler = new PubMedSearchHandler();
        reader.setContentHandler(searchHandler);

        // GET method headers and parameters
        GetMethod httpMethod = new GetMethod();
        try {
            InputStream is = null;
            httpMethod.setURI(new URI(E_SEARCH_URL, false));
            httpMethod.setRequestHeader("Content-type",
                    "application/x-www-form-urlencoded; charset=UTF-8");
            httpMethod.addRequestHeader(new Header("Accept-Encoding", "gzip"));
            NameValuePair[] params = new NameValuePair[] {
                    new NameValuePair("db", "pubmed"),
                    new NameValuePair("term", query),
                    new NameValuePair("retmax", Integer
                            .toString(requestedResults)),
                    new NameValuePair("usehistory", "n") };
            httpMethod.setQueryString(params);

            // Get document IDs
            log.debug("Querying PubMed: " + httpMethod.getURI());
            int statusCode = client.executeMethod(httpMethod);
            if (statusCode == HttpStatus.SC_OK
                    || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE
                    || statusCode == HttpStatus.SC_BAD_REQUEST) {
                is = httpMethod.getResponseBodyAsStream();
                Header encoded = httpMethod
                        .getResponseHeader("Content-Encoding");
                if (encoded != null && "gzip".equals(encoded.getValue())) {
                    is = new GZIPInputStream(is);
                }
                reader.parse(new InputSource(is));
                is.close();
                is = null;
            }
            else {
                is = httpMethod.getResponseBodyAsStream();
                Header encoded = httpMethod
                        .getResponseHeader("Content-Encoding");
                if (encoded != null && "gzip".equals(encoded.getValue())) {
                    is = new GZIPInputStream(is);
                }
                final byte[] message = StreamUtils.readFully(is);
                throw new IOException("PubMed returned HTTP Error: "
                        + statusCode + ", HTTP payload: "
                        + new String(message, "iso8859-1"));
            }
        }
        finally {
            httpMethod.releaseConnection();
        }

        List primaryIds = searchHandler.getPubMedPrimaryIds();
        return primaryIds;
    }


    /**
     * Gets PubMed abstracts corresponding to the provided ids.
     */
    private void getPubMedAbstracts(List ids, final HttpClient client,
            PubMedSearchResultConsumer consumer)
        throws Exception
    {
        // Set-up parser and handler for ESearch first
        SAXParser searchParser;
        searchParser = SAXParserFactory.newInstance().newSAXParser();
        final XMLReader reader = searchParser.getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);

        PubMedFetchHandler fetchHandler = new PubMedFetchHandler(consumer);
        reader.setContentHandler(fetchHandler);

        // GET method headers and parameters
        GetMethod httpMethod = new GetMethod();
        try {
            InputStream is = null;
            httpMethod.setURI(new URI(E_FETCH_URL, false));
            httpMethod.setRequestHeader("Content-type",
                    "application/x-www-form-urlencoded; charset=UTF-8");
            httpMethod.addRequestHeader(new Header("Accept-Encoding", "gzip"));
            NameValuePair[] params = new NameValuePair[] {
                    new NameValuePair("db", "pubmed"),
                    new NameValuePair("id", getIdsString(ids)),
                    new NameValuePair("retmode", "xml"),
                    new NameValuePair("rettype", "abstract"), };
            httpMethod.setQueryString(params);

            // Get document contents
            // No URL logging here, as the url can get really long
            int statusCode = client.executeMethod(httpMethod);
            if (statusCode == HttpStatus.SC_OK
                    || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE
                    || statusCode == HttpStatus.SC_BAD_REQUEST) {
                is = httpMethod.getResponseBodyAsStream();
                Header encoded = httpMethod
                        .getResponseHeader("Content-Encoding");
                if (encoded != null && "gzip".equals(encoded.getValue())) {
                    is = new GZIPInputStream(is);
                }
                reader.parse(new InputSource(is));
                is.close();
                is = null;
            }
            else {
                is = httpMethod.getResponseBodyAsStream();
                Header encoded = httpMethod
                        .getResponseHeader("Content-Encoding");
                if (encoded != null && "gzip".equals(encoded.getValue())) {
                    is = new GZIPInputStream(is);
                }
                final byte[] message = StreamUtils.readFully(is);
                throw new IOException("PubMed returned HTTP Error: "
                        + statusCode + ", HTTP payload: "
                        + new String(message, "iso8859-1"));
            }
        }
        finally {
            httpMethod.releaseConnection();
        }
    }


    private String getIdsString(List ids)
    {
        StringBuffer buf = new StringBuffer();
        for (Iterator it = ids.iterator(); it.hasNext();) {
            String id = (String)it.next();
            buf.append(id);
            if (it.hasNext()) {
                buf.append(",");
            }
        }

        return buf.toString();
    }
}
