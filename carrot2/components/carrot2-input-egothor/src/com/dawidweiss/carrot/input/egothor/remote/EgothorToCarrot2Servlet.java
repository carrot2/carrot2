
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.input.egothor.remote;

import org.egothor.data.Hit;
import org.egothor.data.QueryResponse;

import org.egothor.indexer.html2.HTMLMetadata;

import org.egothor.util.Snippy;

import org.egothor.warrior.Normalizator;

import org.egothor.web.contexts.ContextManager;
import org.egothor.web.contexts.SearchContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * A servlet that can execute queries in Carrot2 input query XML format against
 * one of the predefined search contexts. The results are returned as an XML
 * stream in Carrot2 search results format (that can be processed using other
 * Carrot2-compatible components). You can use the following POST parameters
 * to customize the behavior of this class:
 * 
 * <ul>
 * <li>
 * <code>search-context</code> - the name of a search context to use. If not
 * present, default search context is used.
 * </li>
 * <li>
 * <code>max-context-sentences</code> - how many context phrases should be
 * printed?
 * </li>
 * <li>
 * <code>window-size</code> - window size for snippet generator.
 * </li>
 * </ul>
 * 
 * <p>
 * More info about Carrot2: <a
 * href="http://www.cs.put.poznan.pl/dweiss/carrot">http://www.cs.put.poznan.pl/dweiss/carrot</a>
 * </p>
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class EgothorToCarrot2Servlet extends HttpServlet {
    /**
     * Default number of requested results, if not overriden by a request's
     * parameter.
     */
    private final static int DEFAULT_REQUESTED_RESULTS = 100;

    /**
     * JDOM's document builder used to parse the input stream.
     */
    private DocumentBuilder builder;

    /**
     * Builders factory.
     */
    private DocumentBuilderFactory factory;

    /**
     * Window size for snippet generator. Override this with 'window-size' init
     * parameter of the servlet.
     */
    private int windowSize = 7;

    /**
     * How many context pieces should be printed? Override this with
     * 'max-context-sentences' init parameter of the servlet.
     */
    private int maxContextSentences = 3;

    /**
     * How many results should be returned in case no request is specified?
     */
    private int defaultResultsNumber = DEFAULT_REQUESTED_RESULTS;

    /**
     * @param string The string to be analyzed.
     *
     * @return Returns <code>true</code> if the <code>string</code> parameter
     *         contains characters other than matching a regular expression
     *         <code>\ \n\t\r</code> and is not <code>null</code>.
     */
    private final boolean containsNonSpaceChars(String string) {
        if (string == null) {
            return false;
        }

        int len = string.length();

        // in pessimistic case we have to traverse all chars, but such case
        // will be very rare and we avoid creating new objects, which is a
        // big gain.
        for (int i = 0; i < len; i++) {
            switch (string.charAt(i)) {
            case ' ':
            case '\t':
            case '\n':
            case '\r':

                continue;

            default:
                return true;
            }
        }

        return false;
    }

    /**
     * Removes markup from a word. Markup is understood as a sequence of
     * characters between &lt; and &gt; characters.
     *
     * @param word The word from which markup is to be removed.
     *
     * @return <code>word</code> with all markup removed.
     */
    private final String removeMarkup(String word) {
        final char[] chars = word.toCharArray();
        int i = 0;
        int j = 0;

        while (i < chars.length) {
            if (chars[i] == '<') {
                // skip until '>'
                while ((i < chars.length) && (chars[i] != '>')) {
                    i++;
                }

                if (i < chars.length) {
                    i++;
                }

                continue;
            }

            chars[j] = chars[i];
            i++;
            j++;
        }

        if (i == j) {
            return word;
        } else {
            return new String(chars, 0, j);
        }
    }

    /**
     * Initialize the servlet.
     *
     * @param config Servlet configuration passed from the servlet container.
     *
     * @exception ServletException If initialization exception occurred.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            this.maxContextSentences = Integer.parseInt(config.getInitParameter(
                        "max-context-sentences"));
        } catch (Exception e) {
            // don't care if we can't parse it or find it.
        }

        try {
            this.windowSize = Integer.parseInt(config.getInitParameter(
                        "window-size"));
        } catch (Exception e) {
            // don't care if we can't parse it or find it.
        }

        try {
            this.defaultResultsNumber = Integer.parseInt(config.getInitParameter(
                        "default-results-number"));
        } catch (Exception e) {
            // don't care if we can't parse it or find it.
        }

        factory = DocumentBuilderFactory.newInstance();

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ServletException("Could not load an XML parser.", e);
        }
    }

    /**
     * We ignore GET requests by default. Just print some info about the
     * component.
     */
    protected void doGet(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
            "Use HTTP POST method to send Carrot2 queries.");
    }

    /**
     * Process HTTP POST request assuming it contains Carrot2 query XML.
     *
     * @param request HTTP request object passed from the servlet container.
     * @param response HTTP response object passed from the servlet container.
     */
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        // first check if we have a context manager servlet instance
        ContextManager contextManagerOb = null;

        try {
            contextManagerOb = (ContextManager) super.getServletContext()
                                                     .getAttribute("contextManagerInstance");

            if (contextManagerOb == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Context manager instance unavailable.");

                return;
            }
        } catch (ClassCastException ce) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Context manager not an instance of org.egothor.web.contexts.ContextManager.");

            return;
        }

        // force input encoding if not specified.
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }

        // now check that the request is a valid Carrot2 data.
        String c2data = request.getParameter("carrot-request");

        if (c2data == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "POST request must contain 'carrot-request' parameter.");

            return;
        }

        Document queryRequest = null;

        // TODO: this is synchronized on a single object because builders
        // are not fail-safe. Maybe a pool would be more efficient.. on the
        // other hand... the gain may be insignificant.
        synchronized (builder) {
            try {
                queryRequest = builder.parse(new ByteArrayInputStream(
                            c2data.getBytes("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                throw new ServletException(
                    "Fatal: UTF-8 not supported on the server JRE.");
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Could not read or parse Carrot2 query request.");

                return;
            }
        }

        Element queryElement = queryRequest.getDocumentElement();

        if (!"query".equals(queryElement.getNodeName())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Bad query XML: root element not a 'query': " +
                queryElement.getNodeName());

            return;
        }

        int requestedResults = DEFAULT_REQUESTED_RESULTS;

        try {
            requestedResults = Integer.parseInt(queryElement.getAttribute(
                        "requested-results"));
        } catch (NumberFormatException e) {
            // ignore exceptions.
        }

        queryElement.normalize();

        NodeList textNodes = queryElement.getChildNodes();

        if (textNodes.getLength() <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Bad query XML: no text in 'query' element?");

            return;
        }

        if (org.w3c.dom.Node.TEXT_NODE != textNodes.item(0).getNodeType()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Bad query XML: 'query' should have no subelements.");

            return;
        }

        String query = textNodes.item(0).getNodeValue();

        // now check that there is a search context available...
        String useSearchContext = request.getParameter("search-context");
        SearchContext searchContext = null;

        if (useSearchContext != null) {
            searchContext = contextManagerOb.getSearchContext(useSearchContext);
        }

        if (searchContext == null) {
            searchContext = contextManagerOb.getDefaultSearchContext();
        }

        log("Accepted Carrot2 query (requested results: " + requestedResults +
            ", context: " + searchContext.getName() + "): " + query);

        // Pass the query to Egothor's search context...
        if ((searchContext == null) || !searchContext.isConfigured()) {
            log("Search context not configured or usable: " +
                searchContext.getId());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Search context not configured or usable: " +
                searchContext.getId());

            return;
        }

        // and flush the result as Carrot2 output stream.
        response.setContentType("text/xml");

        OutputStream os = response.getOutputStream();
        Writer out = new OutputStreamWriter(os, "UTF-8");

        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        out.write("<searchresult>\n");
        out.write("<query requested-results=\"" + requestedResults + "\">");
        Normalizator.print(out, query);
        out.write("</query>\n\n");

        try {
            String queryExtension = searchContext.getQueryExtensionForRequest(request.getParameterMap());
            QueryResponse qr = searchContext.executeQuery(0, requestedResults,
                    query, queryExtension, 0);

            Enumeration hits = qr.getEnumeration();
            String[] tokens = qr.queryTokens();

            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = removeMarkup(tokens[i]);
            }

            Snippy snipp = new Snippy(tokens) {
                    public String snip(String text, String delim, int window,
                        int max_hits) {
                        return removeMarkup(super.snip(removeMarkup(text),
                                delim, windowSize, maxContextSentences));
                    }
                };

            for (int i = 0; (i < requestedResults) && hits.hasMoreElements();
                    i++) {
                Hit hs = (Hit) hits.nextElement();
                HTMLMetadata metaData = new HTMLMetadata(hs.getMeta());

                // only consider these hits that accompanied by some metadata
                if (metaData != null) {
                    out.write("<document id=\"" + (i + 1) + "\">\n");

                    String tmp = metaData.getTitle();
                    out.write("<title>");

                    if (containsNonSpaceChars(tmp)) {
                        Normalizator.print(out, tmp);
                    } else {
                        Normalizator.print(out, metaData.getLocation());
                    }

                    out.write("</title>\n");

                    tmp = metaData.getLocation();
                    out.write("<url><![CDATA[");

                    // we hope here the url won't contain ']]>' sequence.
                    out.write(tmp);
                    out.write("]]></url>\n");

                    tmp = metaData.getContent(snipp, 7);

                    if (containsNonSpaceChars(tmp)) {
                        out.write("<snippet>");
                        Normalizator.print(out, tmp);
                        out.write("</snippet>\n");
                    }

                    out.write("</document>\n\n");
                }
            }

            out.write("</searchresult>\n");
            out.flush();
        } catch (Exception processingException) {
            log("Processing exception.", processingException);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Processing exception: " + processingException.toString());

            return;
        }
    }
}
