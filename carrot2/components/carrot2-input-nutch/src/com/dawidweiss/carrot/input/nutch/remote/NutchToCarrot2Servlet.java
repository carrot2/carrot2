
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

package com.dawidweiss.carrot.input.nutch.remote;

import com.dawidweiss.carrot.util.common.StringUtils;
import com.dawidweiss.carrot.util.common.XMLSerializerHelper;

import net.nutch.searcher.Hit;
import net.nutch.searcher.HitDetails;
import net.nutch.searcher.Hits;
import net.nutch.searcher.NutchBean;
import net.nutch.searcher.Query;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Nutch search engine adapter that accepts queries in  Carrot<sup>2</sup>
 * format and returns an XML result acceptable for clustering filters.  The
 * servlet can be configured by passing servlet parameters to it (in
 * <tt>web.xml</tt> file). Current parameters include:
 * 
 * <dl>
 * <dt>
 * <tt>default-results-number</tt>
 * </dt>
 * <dd>
 * The default number of returned results if not stated explicitly in the query
 * request XML.
 * </dd>
 * </dl>
 * 
 *
 * @author Dawid Weiss
 */
public class NutchToCarrot2Servlet extends HttpServlet {
    /**
     * The default number of returned results if not stated explicitly in the
     * query.
     */
    private final static int DEFAULT_REQUESTED_RESULTS = 100;

    /**
     * XML builder instance to parse XML requests.
     */
    private DocumentBuilder builder;

    /**
     * How many results should be returned in case no request size is specified
     * in the query?
     */
    private int defaultResultsNumber = DEFAULT_REQUESTED_RESULTS;

    /**
     * Initialize the servlet, acquire XML parser instance.
     *
     * @param config ServletConfig object passed from the container.
     *
     * @exception ServletException Thrown for some reason the servlet cannot be
     *            initialized.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            this.defaultResultsNumber = Integer.parseInt(config.getInitParameter(
                        "default-results-number"));
        } catch (Exception e) {
            // don't care if we can't parse it or find it.
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ServletException("Could not load an XML parser.", e);
        }
    }

    /**
     * We ignore GET requests by default. Just print some info about the
     * component.
     *
     * @param request A HTTP request (not used).
     * @param response A HTTP response (not used).
     *
     * @exception ServletException See superclass.
     * @exception IOException See superclass.
     */
    protected void doGet(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
            "Use HTTP POST method to send Carrot2 queries.");
    }

    /**
     * Process HTTP POST request assuming it contains Carrot2 query XML, format
     * and return the matching search results as Carrot2 XML data stream.
     *
     * @param request A HTTP request (not used).
     * @param response A HTTP response (not used).
     *
     * @exception ServletException See superclass.
     * @exception IOException See superclass.
     */
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        // force input encoding on the input stream if not specified.
        // Carrot2 requests should be in UTF-8.
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        } else {
            if (!"UTF-8".equalsIgnoreCase(request.getCharacterEncoding())) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Carrot2 requests must be UTF-8 encoded.");

                return;
            }
        }

        // Now check that the request is a valid Carrot2 data.
        // We allow the container to parse all of POST parameters by doing
        // a call to getParameter -- this is fine as most requests will be short
        // anyway, but DoS attacks are easy with this approach.  
        String c2data = request.getParameter("carrot-request");

        if (c2data == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "POST request must contain 'carrot-request' parameter.");

            return;
        }

        // Parse the Carrot2 XML request.
        //
        // This is synchronized on a single object because builders
        // are not fail-safe. Maybe a pool would be more efficient.. on the
        // other hand... the gain may be insignificant.
        Document queryRequest = null;

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

        // do some sanity checks and retrieve the request parameters.
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
            // ignore exceptions on parsing requested results.
            log("Could not parse requested results size: " +
                queryElement.getAttribute("requested-results"));
        }

        // assemble the query. The string buffer should probably
        // also be reused, just like the parser above...
        StringBuffer queryBuffer = new StringBuffer(20);
        NodeList queryNodes = queryElement.getChildNodes();
        int max = queryNodes.getLength();

        for (int i = 0; i < max; i++) {
            Node node = queryNodes.item(i);

            switch (node.getNodeType()) {
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                queryBuffer.append(node.getNodeValue());

                break;

            case Node.COMMENT_NODE:

                continue;

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Unrecognized node type in query XML: " +
                    node.getNodeType() + " (" + node.toString() + ")");
            }
        }

        // the serializer is not thread-safe, but could also be pooled, just
        // just as the objects above.
        XMLSerializerHelper xmlSerializer = XMLSerializerHelper.getInstance();

        // Perform a Nutch search with the acquired query.
        NutchBean nutchBean = NutchBean.get(super.getServletContext());
        Query query = Query.parse(queryBuffer.toString());
        Hits hits = nutchBean.search(query, requestedResults);
        int length = (int) Math.min(hits.getTotal(), requestedResults);
        Hit[] show = hits.getHits(0, length);
        HitDetails[] details = nutchBean.getDetails(show);

        // Flush the result as Carrot2 output stream.
        response.setContentType("text/xml");

        OutputStream os = response.getOutputStream();
        Writer out = new OutputStreamWriter(os, "UTF-8");

        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        out.write("<searchresult>\n");
        out.write("<query requested-results=\"");
        out.write(Integer.toString(requestedResults));
        out.write("\">");
        xmlSerializer.writeValidXmlText(out, queryBuffer.toString(), false);
        out.write("</query>\n\n");

        // Should summarizer be used in thread-safe mode like this?
        for (int i = 0; i < length; i++) {
            HitDetails detail = details[i];
            String title = StringUtils.entitiesToCharacters(detail.getValue(
                        "title"), false);
            String url = detail.getValue("url");

            // use url for docs w/o title
            if ((title == null) || (title.length() == 0)) {
                title = url;
            }

            // emit document id.
            out.write("<document id=\"");
            out.write(Integer.toString(i));
            out.write("\">\n");

            // emit the title.
            out.write("<title>");
            xmlSerializer.writeValidXmlText(out, title, false);
            out.write("</title>\n");

            // emit the URL.          
            out.write("<url><![CDATA[");

            // we hope here the url won't contain ']]>' sequence.
            out.write(url);
            out.write("]]></url>\n");

            // emit the summary (if exists)
            final String snippet = StringUtils.removeMarkup(StringUtils.entitiesToCharacters(
                    nutchBean.getSummary(detail, query), false));
            
            if ((snippet != null) && (snippet.length() > 0)) {
                out.write("<snippet>");
                xmlSerializer.writeValidXmlText(out, snippet, false);
                out.write("</snippet>\n");
            }

            out.write("</document>\n\n");
        }

        out.write("</searchresult>\n");
        out.flush();
    }
}
