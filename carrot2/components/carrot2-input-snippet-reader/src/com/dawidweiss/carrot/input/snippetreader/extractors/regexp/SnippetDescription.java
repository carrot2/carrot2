
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.input.snippetreader.extractors.regexp;

import org.jdom.Element;

import com.dawidweiss.carrot.util.jdom.JDOMHelper;


/**
 * Objects of this class describe how to extract a single snippet from some
 * string using regular expressions.
 *
 * @author Dawid Weiss
 */
public class SnippetDescription {
    protected boolean valid;

    protected RegularExpression snippetMatch;

    protected RegularExpression titleStart;
    protected RegularExpression titleEnd;

    protected RegularExpression urlStart;
    protected RegularExpression urlEnd;

    protected RegularExpression summaryStart;
    protected RegularExpression summaryEnd;

    /**
     * Use factory methods to acquire objects of this class.
     */
    protected SnippetDescription() {
        valid = false;
    }

    /**
     * Initialize using an XML Element.
     *
     * @param snippetDescription
     *
     * @throws WrappedException
     */
    public SnippetDescription(Element snippetDescription) {
        this();
        initInstanceFromXML(snippetDescription);
    }

    /**
     * Returns a regular expression token matching an entire snippet.
     *
     * @return
     */
    public RegularExpression getSnippetMatch() {
        return snippetMatch;
    }

    /**
     * Returns a regular expression token matching the start of a title.
     *
     * @return
     */
    public RegularExpression getTitleStartMatch() {
        return titleStart;
    }

    /**
     * Returns a regular expression token matching the end of a title.
     *
     * @return
     */
    public RegularExpression getTitleEndMatch() {
        return titleEnd;
    }

    /**
     * Returns a regular expression token matching the start of an URL.
     *
     * @return
     */
    public RegularExpression getURLStartMatch() {
        return urlStart;
    }

    /**
     * Returns a regular expression token matching the end of an URL.
     *
     * @return
     */
    public RegularExpression getURLEndMatch() {
        return urlEnd;
    }

    /**
     * Returns a regular expression token matching the start of a summary.
     *
     * @return
     */
    public RegularExpression getSummaryStartMatch() {
        return summaryStart;
    }

    /**
     * Returns a regular expression token matching the end of a summary.
     *
     * @return
     */
    public RegularExpression getSummaryEndMatch() {
        return summaryEnd;
    }

    /**
     * Initializes this object using an XML Element. The DOM structure must
     * contain at least the following elements (this is an example of wrapping
     * Google).
     * <pre>
     *   &lt;!-- test file snippet extraction --&gt;
     *   &lt;snippet&gt;
     *       &lt;!-- this should match the entire snippet --&gt;
     *       &lt;match consumer="false"&gt;
     *           &lt;regexp&gt;&lt;![CDATA[&lt;dt&gt;.*?&lt;/dt&gt;\n&lt;dd&gt;.*?&lt;br&gt;.*?&lt;span.*?&lt;br&gt;&lt;/dd&gt;]]&gt;&lt;/regexp&gt;
     *       &lt;/match&gt;
     *       &lt;!-- This will cut out only the title.
     *            start and end matches give indexes in a snippet string, which are
     *            then used to cut out a substring.
     *         --&gt;
     *       &lt;title&gt;
     *           &lt;start consume="true"&gt;
     *               &lt;regexp&gt;&lt;![CDATA[&lt;a.*?&lt;b&gt;]]&gt;&lt;/regexp&gt;
     *           &lt;/start&gt;
     *           &lt;end   consume="true"&gt;
     *               &lt;regexp&gt;&lt;![CDATA[&lt;/b&gt;]]&gt;&lt;/regexp&gt;
     *           &lt;/end&gt;
     *       &lt;/title&gt;
     * 
     *       &lt;!-- This will cut out the url of the snippet.
     *         --&gt;
     *       &lt;url&gt;
     *           &lt;start consume="true"&gt;
     *               &lt;regexp&gt;&lt;![CDATA[href="]]&gt;&lt;/regexp&gt;
     *           &lt;/start&gt;
     *           &lt;end   consume="true"&gt;
     *               &lt;regexp&gt;&lt;![CDATA["]]&gt;&lt;/regexp&gt;
     *           &lt;/end&gt;
     *       &lt;/url&gt;
     * 
     *       &lt;!-- This will cut out the summary of a snippet.
     *         --&gt;
     *       &lt;summary&gt;
     *           &lt;start consume="true"&gt;
     *               &lt;regexp&gt;&lt;![CDATA[&lt;dd&gt;]]&gt;&lt;/regexp&gt;
     *           &lt;/start&gt;
     *           &lt;end   consume="true"&gt;
     *               &lt;regexp&gt;&lt;![CDATA[&lt;br&gt;]]&gt;&lt;/regexp&gt;
     *           &lt;/end&gt;
     *       &lt;/summary&gt;
     *   &lt;/snippet&gt;
     * </pre>
     *
     * @param snippet The root Element of the description.
     *
     * @throws WrappedException
     */
    protected void initInstanceFromXML(Element snippet) {
        final String SNIPPET_NODE = "snippet";
        final String SNIPPET_MATCH_ALL = SNIPPET_NODE + "/match";
        final String TITLE_START = SNIPPET_NODE + "/title/start";
        final String TITLE_END = SNIPPET_NODE + "/title/end";
        final String URL_START = SNIPPET_NODE + "/url/start";
        final String URL_END = SNIPPET_NODE + "/url/end";
        final String SUMMARY_START = SNIPPET_NODE + "/summary/start";
        final String SUMMARY_END = SNIPPET_NODE + "/summary/end";

        try {
            snippetMatch = new RegularExpression(JDOMHelper.getElement(
                        SNIPPET_MATCH_ALL, snippet));
            titleStart = new RegularExpression(JDOMHelper.getElement(
                        TITLE_START, snippet));
            titleEnd = new RegularExpression(JDOMHelper.getElement(TITLE_END,
                        snippet));
            urlStart = new RegularExpression(JDOMHelper.getElement(URL_START,
                        snippet));
            urlEnd = new RegularExpression(JDOMHelper.getElement(URL_END,
                        snippet));
            summaryStart = new RegularExpression(JDOMHelper.getElement(
                        SUMMARY_START, snippet));
            summaryEnd = new RegularExpression(JDOMHelper.getElement(
                        SUMMARY_END, snippet));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not create SnippetDescription: some parameters missing.",
                e);
        }

        valid = true;
    }
}
