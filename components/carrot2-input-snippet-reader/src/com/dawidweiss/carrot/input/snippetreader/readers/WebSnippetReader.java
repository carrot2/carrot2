

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

package com.dawidweiss.carrot.input.snippetreader.readers;

import com.dawidweiss.carrot.input.snippetreader.extractors.regexp.*;
import com.dawidweiss.carrot.util.HTMLTextStripper;
import com.dawidweiss.carrot.util.common.StreamUtils;
import com.dawidweiss.carrot.util.common.StringUtils;
import com.dawidweiss.carrot.util.common.XMLSerializerHelper;
import com.dawidweiss.carrot.util.jdom.JDOMHelper;
import com.dawidweiss.carrot.util.net.http.FormActionInfo;
import com.dawidweiss.carrot.util.net.http.FormParameters;
import com.dawidweiss.carrot.util.net.http.HTTPFormSubmitter;

import org.apache.log4j.Logger;

import org.jdom.Element;

import gnu.regexp.RE;

import java.io.*;

import java.net.URL;

import java.util.Enumeration;
import java.util.Vector;


/**
 * Reads snippets from a Web search engine.
 */
public class WebSnippetReader {

    private static final Logger log = Logger.getLogger(WebSnippetReader.class);

    private Element config;

    private HttpMultiPageReader reader;

    private RegExpSnippetExtractor extractor;

    private String baseURL;

    private String relativeBaseURL;

    /**
     * Initializes this snippet reader to use some service. The configuration
     * is a JDOM XML structure.
     */
    public WebSnippetReader(Element configuration) throws Exception {
        config = configuration;

        URL serviceURL = new URL(JDOMHelper.getStringFromJDOM(
                    "/service/request/service#url", configuration, true));
        baseURL = serviceURL.getProtocol() + "://" + serviceURL.getHost() +
            ((serviceURL.getPort() == -1) ? "" : (":" + serviceURL.getPort())) +
            "/";
        relativeBaseURL = JDOMHelper.getStringFromJDOM("/service/request/service#url",
                configuration, true);

        if (relativeBaseURL.lastIndexOf('/') > 0) {
            relativeBaseURL = relativeBaseURL.substring(0,
                    relativeBaseURL.lastIndexOf('/') + 1);
        }

        log.debug("Base service URL: " + baseURL);
        log.debug("Base relative service URL: " + relativeBaseURL);

        FormActionInfo actionInfo = new FormActionInfo(JDOMHelper.getElement(
                    "/service/request", config));
        FormParameters queryParameters = new FormParameters(JDOMHelper.getElement(
                    "/service/request/parameters", config));
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

        reader = new HttpMultiPageReader(submitter, queryParameters);
        extractor = new RegExpSnippetExtractor(new SnippetDescription(
                    JDOMHelper.getElement("/service/response/snippet", config)));
    }

    /**
     * Writes a Carrot2 XML stream to the given Writer.
     */
    public void getSnippetsAsCarrot2XML(final Writer outputStream,
        String query, final int snippetsNeeded) throws Exception {
        outputStream.write("<searchresult>\n");

        // output the optional query tag at the beginning of the document.
        outputStream.write("<query requested-results=\"" + snippetsNeeded +
            "\"><![CDATA[" + query + "]]></query>\n");

        try {
            String encoding;

            if ((encoding = JDOMHelper.getStringFromJDOM(
                            "/service/request#encoding", config, false)) == null) {
                encoding = "iso8859-1";
            }

            InputStream is = reader.getQueryResults(query, snippetsNeeded,
                    encoding,
                    JDOMHelper.getElement("/service/response/pageinfo", config));

            if (is != null) {
                final float warnLevel = Float.parseFloat(JDOMHelper.getStringFromJDOM(
                            "/service/response/pageinfo/warn-when-below",
                            config, true));

                extractor.extractSnippets(new InputStreamReader(is,
                        JDOMHelper.getStringFromJDOM(
                            "/service/response#encoding", config, false)),
                    new SnippetExtractorCallback() {
                        int notitle = 0;
                        int nourl = 0;
                        int nosummary = 0;
                        int recognized = 0;
                        HTMLTextStripper htmlStripper = HTMLTextStripper.getInstance();
                        XMLSerializerHelper xmlSerializer = XMLSerializerHelper.getInstance();

                        public void snippetHasNoTitle() {
                            notitle++;
                        }

                        public void snippetHasNoURL() {
                            nourl++;
                        }

                        public boolean acceptSnippetWithEmptySummary() {
                            nosummary++;

                            return true;
                        }

                        public void snippetRecognized(SimpleSnippet s) {
                            if (s == null) {
                                if (recognized < (snippetsNeeded * warnLevel)) {
                                    // Issue a warning.
                                    log.warn("Only " + recognized + " out of " +
                                        snippetsNeeded +
                                        " results were extracted.");
                                }
                            } else {
                                try {
                                    recognized++;
                                    outputStream.write("<document id=\"" +
                                        recognized + "\">\n\t<title>");
                                    xmlSerializer.writeValidXmlText(outputStream,
                                        htmlStripper.htmlToText(s.getTitle()),
                                        false);
                                    outputStream.write("</title>\n");

                                    outputStream.write("\t<url><![CDATA[");

                                    String docUrl = s.getDocumentURL();

                                    if (docUrl.startsWith("/")) {
                                        outputStream.write(baseURL);
                                        outputStream.write(docUrl);
                                    } else if (docUrl.indexOf(':') < 0) {
                                        outputStream.write(relativeBaseURL);
                                        outputStream.write(docUrl);
                                    } else {
                                        outputStream.write(docUrl);
                                    }

                                    outputStream.write("]]></url>\n");

                                    if (s.getSummary() != null) {
                                        outputStream.write("\t<snippet>");
                                        xmlSerializer.writeValidXmlText(outputStream,
                                            htmlStripper.htmlToText(
                                                s.getSummary()), false);

                                        outputStream.write("</snippet>\n");
                                    }

                                    outputStream.write("</document>\n");
                                } catch (IOException e) {
                                    throw new RuntimeException(
                                        "IOException when saving result.");
                                }
                            }
                        }
                    });
                is.close();
            }

            outputStream.write("</searchresult>\n");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Retrieves snippets for a query.
     */
    public final Vector getSnippets(String query, final int snippetsNeeded) 
    	throws Exception {
        return getSnippets(query, snippetsNeeded, null);
    }

    /**
     * Retrieves snippets for a query.
     */
    public Vector getSnippets(String query, final int snippetsNeeded, Vector out_RawPages)
        throws Exception {
        final Vector res = new Vector();

        try {
            String encoding;

            if ((encoding = JDOMHelper.getStringFromJDOM(
                            "/service/request#encoding", config, false)) == null) {
                encoding = "iso8859-1";
            }

            Enumeration pgs = reader.getQueryResultsPages(query, snippetsNeeded,
                    encoding,
                    JDOMHelper.getElement("/service/response/pageinfo", config));

            if (out_RawPages != null) {
		        Vector newPgs = new Vector();
		        Vector pages = new Vector();
		        while (pgs.hasMoreElements()) {
		        	InputStream is = (InputStream) pgs.nextElement();
		        	byte [] buf = StreamUtils.readFully(is);
		        	String page = new String(buf, "UTF-8");
		        	page = page.replaceFirst("<[Hh][Ee][Aa][Dd]>", "<head><base href='" + baseURL + "'>");
		        	pages.add(page);

		        	newPgs.add(new ByteArrayInputStream(buf));
		        }
		        pgs = newPgs.elements();
            }

            InputStream is = new SequenceInputStream(pgs);

            if (is == null) {
                return res;
            }

            final float warnLevel = Float.parseFloat(JDOMHelper.getStringFromJDOM(
                        "/service/response/pageinfo/warn-when-below", config,
                        true));

            extractor.extractSnippets(new InputStreamReader(is,
                    JDOMHelper.getStringFromJDOM("/service/response#encoding",
                        config, false)),
                new SnippetExtractorCallback() {
                    int notitle = 0;
                    int nourl = 0;
                    int nosummary = 0;
                    int recognized = 0;

                    public void snippetHasNoTitle() {
                        notitle++;
                    }

                    public void snippetHasNoURL() {
                        nourl++;
                    }

                    public boolean acceptSnippetWithEmptySummary() {
                        nosummary++;

                        return true;
                    }

                    public void snippetRecognized(SimpleSnippet s) {
                        if (s == null) {
                            if (recognized < (snippetsNeeded * warnLevel)) {
                                // Issue a warning.
                                log.warn("Only " + recognized + " out of " +
                                    snippetsNeeded +
                                    " results were extracted.");
                            }
                        } else {
                            recognized++;
                            res.add(StringUtils.removeMarkup(
                                    StringUtils.entitiesToCharacters(
                                        s.getTitle(), false)));
                            res.add(s.getDocumentURL());
                            res.add((s.getSummary() == null) ? ""
                                                             : StringUtils.removeMarkup(
                                    StringUtils.entitiesToCharacters(
                                        s.getSummary(), false)));
                        }
                    }
                });

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return res;
    }

    /**
     * Retrieves a HTML source of a query, highlights matches.
     */
    public String getHighlightedMatches(String query, int snippetsNeeded)
        throws Exception {
        final StringBuffer tokenizedStream = new StringBuffer();

        try {
            String encoding;

            if ((encoding = JDOMHelper.getStringFromJDOM(
                            "/service/request#encoding", config, false)) == null) {
                encoding = "iso8859-1";
            }

            InputStream is = reader.getQueryResults(query, 80, encoding,
                    JDOMHelper.getElement("/service/response/pageinfo", config));

            byte[] fullInput;

            if (is == null) {
                fullInput = reader.getFirstResultsPage(query, 80, encoding,
                        JDOMHelper.getElement("/service/response/pageinfo",
                            config));
            } else {
                fullInput = StreamUtils.readFullyAndCloseInput(is);
            }

            final String fullInputString = new String(fullInput,
                    JDOMHelper.getStringFromJDOM("/service/response#encoding",
                        config, false));

            extractor.extractSnippets(new StringReader(fullInputString),
                new SnippetExtractorCallback() {
                    int currentIndex = 0;

                    public void entireSnippetRegionMatch(String matchedString,
                        int streamStart, int streamEnd) {
                        // find 'out-of-token' space
                        int is = streamStart;
                        int ie = streamEnd;

                        is = adjustLeft(fullInputString, streamStart);
                        ie = adjustRight(fullInputString, streamEnd);

                        tokenizedStream.append(fullInputString.substring(
                                currentIndex, is));
                        tokenizedStream.append(
                            "<span style=\"background-color: yellow;\"><font color=red>[</font>");
                        tokenizedStream.append(fullInputString.substring(is, ie));
                        tokenizedStream.append(
                            "<font color=red>]</font></span>");
                        currentIndex = ie;
                    }

                    public void snippetRecognized(SimpleSnippet s) {
                        if (s == null) {
                            tokenizedStream.append(fullInputString.substring(
                                    currentIndex));
                        } else {
                            tokenizedStream.append(
                                "<br><span style=\"background-color: red;\"><font color=white>[snippet recognized]</font></span><br>");
                            tokenizedStream.append(
                                "<span style=\"background-color: lightgrey;\"><font color=black><pre>\n");
                            tokenizedStream.append("Title: &gt;" +
                                StringUtils.removeMarkup(s.getTitle()) +
                                "&lt;\n");
                            tokenizedStream.append("URL  : &gt;" +
                                s.getDocumentURL() + "&lt;\n");
                            tokenizedStream.append("Desc : &gt;" +
                                StringUtils.removeMarkup(s.getSummary()) +
                                "&lt;\n");
                            tokenizedStream.append("\n</pre></span>");
                        }
                    }

                    public boolean acceptSnippetWithEmptySummary() {
                        tokenizedStream.append(
                            "<span style=\"background-color: red;\"><font color=white>[NS]</font></span>");

                        return true;
                    }

                    public void snippetHasNoTitle() {
                        tokenizedStream.append(
                            "<span style=\"background-color: red;\"><font color=white>[NT]</font></span>");
                    }

                    public void snippetHasNoURL() {
                        tokenizedStream.append(
                            "<span style=\"background-color: red;\"><font color=white>[NURL]</font></span>");
                    }

                    int adjustLeft(String s, int index) {
                        int outoftoken = s.substring(0, index + 1).lastIndexOf("><",
                                index);

                        if (outoftoken != -1) {
                            if (fullInputString.charAt(outoftoken) == '<') {
                                index = outoftoken - 1;
                            }
                        }

                        return index;
                    }

                    int adjustRight(String s, int index) {
                        int outoftoken = s.indexOf("><", index);

                        if (outoftoken != -1) {
                            if (fullInputString.charAt(outoftoken) == '>') {
                                index = outoftoken + 1;
                            }
                        }

                        return index;
                    }
                });
        } catch (Throwable e) {
	        StringWriter sw = new StringWriter();
	        PrintWriter  pw = new PrintWriter( sw );
	
	        e.printStackTrace( pw );
	
	        pw.close();
	        String stTrace = sw.toString();

            tokenizedStream.setLength(0);
            tokenizedStream.append("<html><body>An exception occurred.<br><b>" +
                e.toString() + "</b><br><pre>" + stTrace + "</pre></body></html>");
        }

        // remove scripts.
        String output = tokenizedStream.toString();
        String[] removeTokens = {
            "<script.*?</script>", "onLoad=[^> \t]*", "onClick=[^> \t]*"
        };

        for (int i = 0; i < removeTokens.length; i++) {
            RE removeMatches = new RE(removeTokens[i],
                    RE.REG_ICASE | RE.REG_DOT_NEWLINE);
            output = removeMatches.substituteAll(output, "");
        }

        return output;
    }

    /**
     * Retrieves the source of a query.
     */
    public String getQuerySource(String query, int snippetsNeeded)
        throws Exception {
        StringBuffer stream = new StringBuffer();
        byte[] fullInput;

        try {
            String encoding;

            if ((encoding = JDOMHelper.getStringFromJDOM(
                            "/service/request#encoding", config, false)) == null) {
                encoding = "iso8859-1";
            }

            InputStream is = null;

            try {
                is = reader.getQueryResults(query, 80, encoding,
                        JDOMHelper.getElement("/service/response/pageinfo",
                            config));
            } catch (Exception e1) {
            }

            if (is == null) {
                fullInput = reader.getFirstResultsPage(query, 80, encoding,
                        JDOMHelper.getElement("/service/response/pageinfo",
                            config));
            } else {
                fullInput = StreamUtils.readFullyAndCloseInput(is);
            }

            stream.append(new String(fullInput,
                    JDOMHelper.getStringFromJDOM("/service/response#encoding",
                        config, true)));
        } catch (Throwable e) {
            stream.setLength(0);
            stream.append("<html><body>An exception occurred.<br><b>" +
                e.toString() + "</b><br><pre>" + "</pre></body></html>");
        }

        return stream.toString();
    }
}
