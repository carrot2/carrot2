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

package org.carrot2.webapp.serializers;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.carrot2.util.URLEncoding;
import org.carrot2.webapp.*;
import org.dom4j.*;
import org.dom4j.io.XMLWriter;

/**
 * A serializer for the main search page.
 *
 * @author Dawid Weiss
 */
public class XMLPageSerializer implements PageSerializer
{
    private final DocumentFactory factory = DocumentFactory.getInstance();
    private final String skinBase;
    private final String contextPath;
    private final String releaseInfo;
    private final ResourceBundle messages;

    public XMLPageSerializer(String contextPath, String stylesheetsBase,
        String releaseInfo, ResourceBundle messages)
    {
        this.contextPath = contextPath;
        this.skinBase = stylesheetsBase;
        this.releaseInfo = releaseInfo;
        this.messages = messages;
    }

    public String getContentType()
    {
        return Constants.MIME_XML_CHARSET_UTF;
    }

    public void writePage(final OutputStream os, final SearchSettings searchSettings,
        final SearchRequest searchRequest) throws IOException
    {
        final Writer writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);

        // Assume a plain request type.
        final Document doc = factory.createDocument();
        final Element root = factory.createElement("page");
        root.addAttribute("year", Integer.toString(Calendar.getInstance().get(
            Calendar.YEAR)));

        // We add '@' to inform xslt processor that the stylesheet
        // is webapp-relative (not fs-root relative); this way we can avoid
        // loopback connections from the xslt parser to the webapp container
        doc.add(factory.createProcessingInstruction("xml-stylesheet",
            "type=\"text/xsl\" href=\"@" + skinBase + "/page.xsl\""));

        doc.add(factory.createProcessingInstruction("skin-uri", contextPath + skinBase));
        doc.add(factory.createProcessingInstruction("context-path", contextPath));
        doc.add(factory.createProcessingInstruction("release-info", releaseInfo));

        doc.add(root);

        // Attach the meta information block.
        root.add(createMeta(factory, searchSettings, searchRequest));

        // Output the result.
        final XMLWriter xmlwriter = new XMLWriter(writer);
        try
        {
            xmlwriter.write(doc);
        }
        finally
        {
            xmlwriter.flush();
        }
    }

    /**
     * Create the meta information block containing input search tabs, algorithms and
     * other info required to construct the final HTML page.
     */
    private final Element createMeta(final DocumentFactory factory,
        final SearchSettings searchSettings, final SearchRequest searchRequest)
        throws UnsupportedEncodingException
    {
        final Element meta = factory.createElement("meta");

        // Emit action URLs
        final Element actionUrls = meta.addElement("action-urls");
        final String uri = QueryProcessorServlet.PARAM_Q + "="
            + URLEncoding.encode(searchRequest.query, "UTF-8") + "&"
            + QueryProcessorServlet.PARAM_INPUT + "="
            + URLEncoding.encode(searchRequest.getInputTab().getShortName(), "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_ALG + "="
            + URLEncoding.encode(searchRequest.getAlgorithm().getShortName(), "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_SIZE + "=" + searchRequest.getInputSize();
        String queryStringExtension = getQueryStringExtension(searchRequest);
        actionUrls.addElement("query-docs").setText(
            uri + queryStringExtension + "&type=d");
        actionUrls.addElement("query-clusters").setText(
            uri + queryStringExtension + "&type=c");

        // Emit interface strings, TODO: depending on the input locale?
        emitMessageStrings(meta);

        // Emit input search tabs
        final Element tabs = meta.addElement("tabs");
        tabs.addAttribute("form-element", QueryProcessorServlet.PARAM_INPUT);
        final List inputTabs = searchSettings.getInputTabs();
        final int maxTab = inputTabs.size();
        final Set userTabs = extractUserTabs(searchRequest.cookies, inputTabs); // ordered
        for (int i = 0; i < maxTab; i++)
        {
            final TabSearchInput inputTab = (TabSearchInput) inputTabs.get(i);
            final Element tab = tabs.addElement("tab");

            tab.addAttribute("id", inputTab.getShortName());
            tab.addElement("short").setText(inputTab.getShortName());
            tab.addElement("long").setText(inputTab.getLongDescription());

            for (Iterator j = inputTab.getOtherProperties().entrySet().iterator(); j
                .hasNext();)
            {
                final Map.Entry entry = (Map.Entry) j.next();
                if (entry.getValue() != null)
                {
                    final Element propElem = tab.addElement("property");
                    propElem.addAttribute("key", (String) entry.getKey());
                    addTextWithLinks(propElem, entry.getValue().toString());
                }
            }

            // Add example queries urls
            final String exampleQueriesString = ((String) inputTab.getOtherProperties()
                .get("tab.exampleQueries"));
            if (exampleQueriesString != null)
            {
                final String [] exampleQueries = exampleQueriesString.split("\\|");
                final Element queriesElement = tab.addElement("example-queries");
                for (int j = 0; j < exampleQueries.length; j++)
                {
                    final Element query = queriesElement.addElement("example-query");
                    final String url = QueryProcessorServlet.PARAM_Q + "="
                        + URLEncoding.encode(exampleQueries[j], "UTF-8") + "&"
                        + QueryProcessorServlet.PARAM_INPUT + "="
                        + URLEncoding.encode(inputTab.getShortName(), "UTF-8");
                    query.addAttribute("url", url);
                    query.setText(exampleQueries[j]);
                }
            }
        }

        // Emit user tab order
        final Element userTabsElement = meta.addElement("user-tabs");
        boolean selectedTabAdded = false;
        for (Iterator it = userTabs.iterator(); it.hasNext();)
        {
            String tabId = (String) it.next();
            Element userTabElement = userTabsElement.addElement("user-tab");
            userTabElement.addAttribute("id", tabId);
            if (searchRequest.getInputTab().getShortName().equals(tabId))
            {
                userTabElement.addAttribute("selected", "true");
                selectedTabAdded = true;
            }
        }
        if (!selectedTabAdded)
        {
            Element userTabElement = userTabsElement.addElement("user-tab").addAttribute(
                "selected", "true");
            userTabElement.addAttribute("id", searchRequest.getInputTab().getShortName());
        }

        // Emit algorithms
        final Element algorithms = meta.addElement("algorithms");
        algorithms.addAttribute("form-element", QueryProcessorServlet.PARAM_ALG);
        final List algorithmsList = searchSettings.getAlgorithms();
        final int maxAlg = algorithmsList.size();
        for (int i = 0; i < maxAlg; i++)
        {
            final TabAlgorithm algo = (TabAlgorithm) algorithmsList.get(i);
            final Element algoElem = algorithms.addElement("alg");
            algoElem.addAttribute("id", algo.getShortName());
            if (searchRequest.algorithmIndex == i)
            {
                algoElem.addAttribute("selected", "selected");
            }
            algoElem.addElement("short").setText(algo.getShortName());
            if (algo.getLongDescription() != null)
            {
                algoElem.addElement("long").setText(algo.getLongDescription());
            }
        }

        // Emit allowed search sizes.
        final Element qsizes = meta.addElement("query-sizes");
        final int [] allowedInputSizes = searchSettings.getAllowedInputSizes();
        qsizes.addAttribute("form-element", QueryProcessorServlet.PARAM_SIZE);
        for (int i = 0; i < allowedInputSizes.length; i++)
        {
            final Element sizeElem = qsizes.addElement("size");
            sizeElem.addAttribute("id", Integer.toString(allowedInputSizes[i]));
            sizeElem.setText(Integer.toString(allowedInputSizes[i]));
            if (searchRequest.inputSizeIndex == i)
            {
                sizeElem.addAttribute("selected", "selected");
            }
        }

        // Pass other request parameters.
        final Element args = meta.addElement("request-arguments");
        for (Iterator i = searchRequest.getRequestArguments().entrySet().iterator(); i
            .hasNext();)
        {
            final Map.Entry entry = (Map.Entry) i.next();
            final String [] values = (String []) entry.getValue();
            final Element arg = args.addElement("arg");
            arg.addAttribute("name", (String) entry.getKey());
            for (int j = 0; j < values.length; j++)
            {
                arg.addElement("value").setText(values[j]);
            }
        }

        // And finally, emit the query
        meta.addElement("query").setText(searchRequest.query);
        meta.addElement("query-escaped").setText(escapeForJavascriptString(searchRequest.query));
        if (searchRequest.expandedQuery != null)
        {
            meta.addElement("expanded-query").setText(searchRequest.expandedQuery);
        }

        return meta;
    }
    
    private String escapeForJavascriptString(String text)
    {
        return text.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    }

    private String getQueryStringExtension(SearchRequest searchRequest)
        throws UnsupportedEncodingException
    {
        Map extraRequestOpts = searchRequest.extraRequestOpts;
        StringBuffer result = new StringBuffer();

        for (Iterator it = extraRequestOpts.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            result.append("&");
            result.append(entry.getKey());
            result.append("=");
            result.append(URLEncoding.encode((String) entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private Set extractUserTabs(Map cookies, List inputs)
    {
        String tabOrder = (String) cookies.get(Constants.COOKIE_TAB_ORDER);
        Set result = new LinkedHashSet();
        if (tabOrder != null && tabOrder.length() > 0)
        {
            String [] tabs = tabOrder.split(":");
            for (int i = 0; i < tabs.length; i++)
            {
                result.add(tabs[i]);
            }
        }
        else
        {
            for (Iterator it = inputs.iterator(); it.hasNext();)
            {
                TabSearchInput input = (TabSearchInput) it.next();
                if (!input.isHidden())
                {
                    result.add(input.getShortName());
                }
            }
        }
        return result;
    }

    private void emitMessageStrings(final Element meta)
    {
        final Element strings = meta.addElement("strings");
        for (Enumeration it = messages.getKeys(); it.hasMoreElements();)
        {
            String key = (String) it.nextElement();
            strings.addElement(key).setText(messages.getString(key));
        }
    }

    private void addTextWithLinks(Element element, String text)
    {
        Matcher matcher = Pattern.compile("(<<(.*)>>)").matcher(text);

        int lastMatchedIndex = 0;

        while (matcher.find())
        {
            element.addText(text.substring(lastMatchedIndex, matcher.start()));
            Element a = element.addElement("a");
            a.setText(matcher.group(2));
            a.addAttribute("href", "http://" + matcher.group(2));
            lastMatchedIndex = matcher.end();
        }

        element.addText(text.substring(lastMatchedIndex));
    }
}