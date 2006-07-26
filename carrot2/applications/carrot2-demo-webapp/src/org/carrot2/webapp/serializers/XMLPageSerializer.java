
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.serializers;

import java.io.*;
import java.util.*;

import org.carrot2.webapp.*;
import org.carrot2.webapp.SearchSettings.SearchRequest;
import org.dom4j.*;
import org.dom4j.io.XMLWriter;

import com.dawidweiss.carrot.util.net.URLEncoding;

/**
 * A serializer for main search page.
 * 
 * @author Dawid Weiss
 */
public class XMLPageSerializer implements PageSerializer {
    private static final String QUERY_SERVLET_PATH = "/search";
    private final DocumentFactory factory = DocumentFactory.getInstance();
    private final String skinBase;
    private final String contextPath;

    public XMLPageSerializer(String contextPath, String stylesheetsBase) {
        this.contextPath = contextPath;
        this.skinBase = stylesheetsBase;
    }

    public String getContentType() {
        return Constants.MIME_XML_CHARSET_UTF;
    }

    public void writePage(final OutputStream os, 
            final SearchSettings searchSettings,
            final SearchRequest searchRequest)
        throws IOException
    {
        final Writer writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);

        // Assume a plain request type.
        final Document doc = factory.createDocument();
        final Element root = factory.createElement("page");

        // We add '@' to inform xslt processor that the stylesheet
        // is webapp-relative (not fs-root relative); this way we can avoid
        // loopback connections from the xslt parser to the webapp container
        doc.add(factory.createProcessingInstruction(
                "xml-stylesheet", "type=\"text/xsl\" href=\"@" + 
                skinBase + "/page.xsl\""));

        doc.add(factory.createProcessingInstruction("skin-uri", 
                contextPath + skinBase));
        doc.add(factory.createProcessingInstruction("context-path", 
                contextPath));

        doc.add(root);

        // Attach the meta information block.
        root.add(createMeta(factory, searchSettings, searchRequest));

        // Output the result.
        final XMLWriter xmlwriter = new XMLWriter(writer);
        try {
            xmlwriter.write(doc);
        } finally {
            xmlwriter.flush();
        }
    }

    /**
     * Create the meta information block containing input search tabs,
     * algorithms and other info required to construct the final HTML page.   
     */
    private final Element createMeta(final DocumentFactory factory, 
            final SearchSettings searchSettings,
            final SearchRequest searchRequest) throws UnsupportedEncodingException {
        final Element meta = factory.createElement("meta");

        // Emit action URLs
        final Element actionUrls = meta.addElement("action-urls");
        actionUrls.addElement("new-search").setText(QUERY_SERVLET_PATH);
        final String uri = QUERY_SERVLET_PATH + "?"
            + QueryProcessorServlet.PARAM_Q + "=" + URLEncoding.encode(searchRequest.query, "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_INPUT + "=" + URLEncoding.encode(searchRequest.getInputTab().getShortName(), "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_ALG + "=" + URLEncoding.encode(searchRequest.getAlgorithm().getShortName(), "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_SIZE + "=" + searchRequest.getInputSize();
        actionUrls.addElement("query-docs").setText(uri + "&type=d");
        actionUrls.addElement("query-clusters").setText(uri + "&type=c");

        // Emit interface strings, TODO: depending on the input locale?
        final Element strings = meta.addElement("strings");
        strings.addElement("search").setText("Search");

        // Emit input search tabs
        final Element tabs = meta.addElement("tabs");
        tabs.addAttribute("form-element", QueryProcessorServlet.PARAM_INPUT);
        final List inputTabs = searchSettings.getInputTabs();
        final int maxTab = inputTabs.size();
        for (int i = 0; i < maxTab; i++) {
            final TabSearchInput inputTab = (TabSearchInput) inputTabs.get(i);
            final Element tab = tabs.addElement("tab");
            tab.addAttribute("id", inputTab.getShortName());
            if (searchRequest.inputTabIndex == i) {
                tab.addAttribute("selected", "selected");
            }
            tab.addElement("short").setText(inputTab.getShortName());
            tab.addElement("long").setText(inputTab.getLongDescription());
            
            for (Iterator j = inputTab.getOtherProperties().entrySet().iterator(); j.hasNext();) {
                final Map.Entry entry = (Map.Entry) j.next();
                final Element propElem = tab.addElement("property");
                propElem.addAttribute("key", (String) entry.getKey());
                propElem.addAttribute("value", (String) entry.getValue());
            }
            
            // Add example queries urls
            final String exampleQueriesString = ((String) inputTab.getOtherProperties().get(
                "tab.exampleQueries"));
            if (exampleQueriesString != null)
            {
                final String [] exampleQueries = exampleQueriesString.split("\\|");
                final Element queriesElement = tab.addElement("example-queries");
                for (int j = 0; j < exampleQueries.length; j++)
                {
                    final Element query = queriesElement.addElement("example-query");
                    final String url = QUERY_SERVLET_PATH + "?"
                        + QueryProcessorServlet.PARAM_Q + "="
                        + URLEncoding.encode(exampleQueries[j], "UTF-8")
                        + "&" + QueryProcessorServlet.PARAM_INPUT + "="
                        + URLEncoding.encode(inputTab.getShortName(), "UTF-8");
                    query.addAttribute("url", url);
                    query.setText(exampleQueries[j]);
                }
            }
        }

        // Emit algorithms
        final Element algorithms = meta.addElement("algorithms");
        algorithms.addAttribute("form-element", QueryProcessorServlet.PARAM_ALG);
        final List algorithmsList = searchSettings.getAlgorithms();
        final int maxAlg = algorithmsList.size();
        for (int i = 0; i < maxAlg; i++) {
            final TabAlgorithm algo = (TabAlgorithm) algorithmsList.get(i);
            final Element algoElem = algorithms.addElement("alg");
            algoElem.addAttribute("id", algo.getShortName());
            if (searchRequest.algorithmIndex == i) {
                algoElem.addAttribute("selected", "selected");
            }
            algoElem.addElement("short").setText(algo.getShortName());
            algoElem.addElement("long").setText(algo.getLongDescription());
        }

        // Emit allowed search sizes.
        final Element qsizes = meta.addElement("query-sizes");
        final int [] allowedInputSizes = searchSettings.getAllowedInputSizes();
        qsizes.addAttribute("form-element", QueryProcessorServlet.PARAM_SIZE);
        for (int i = 0; i < allowedInputSizes.length; i++) {
            final Element sizeElem = qsizes.addElement("size");
            sizeElem.addAttribute("id", Integer.toString(allowedInputSizes[i]));
            sizeElem.setText(Integer.toString(allowedInputSizes[i]));
            if (searchRequest.inputSizeIndex == i) {
                sizeElem.addAttribute("selected", "selected");
            }
        }
        
        // Pass other request parameters.
        final Element args = meta.addElement("request-arguments");
        for (Iterator i = searchRequest.getRequestArguments().entrySet().iterator(); i.hasNext();) {
            final Map.Entry entry = (Map.Entry) i.next();
            final String [] values = (String []) entry.getValue();
            final Element arg = args.addElement("arg");
            arg.addAttribute("name", (String) entry.getKey());
            for (int j = 0; j < values.length; j++) {
                arg.addElement("value").setText(values[j]);
            }
        }

        // And finally, emit the query
        meta.addElement("query").setText(searchRequest.query);

        return meta;
    }
}