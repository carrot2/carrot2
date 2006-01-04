
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.input.snippetreader.extractors.regexp;

import org.dom4j.Element;


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
     */
    public SnippetDescription(Element snippetDescription) {
        this();
        initInstanceFromXML(snippetDescription);
    }

    /**
     * Returns a regular expression token matching an entire snippet.
     */
    public RegularExpression getSnippetMatch() {
        return snippetMatch;
    }

    /**
     * Returns a regular expression token matching the start of a title.
     */
    public RegularExpression getTitleStartMatch() {
        return titleStart;
    }

    /**
     * Returns a regular expression token matching the end of a title.
     */
    public RegularExpression getTitleEndMatch() {
        return titleEnd;
    }

    /**
     * Returns a regular expression token matching the start of an URL.
     */
    public RegularExpression getURLStartMatch() {
        return urlStart;
    }

    /**
     * Returns a regular expression token matching the end of an URL.
     */
    public RegularExpression getURLEndMatch() {
        return urlEnd;
    }

    /**
     * Returns a regular expression token matching the start of a summary.
     */
    public RegularExpression getSummaryStartMatch() {
        return summaryStart;
    }

    /**
     * Returns a regular expression token matching the end of a summary.
     */
    public RegularExpression getSummaryEndMatch() {
        return summaryEnd;
    }

    /**
     * Initializes this object using an XML Element. See descriptors in
     * the component's folder for examples.
     *
     * @param snippet The root Element of the description.
     */
    protected void initInstanceFromXML(Element snippet) {
        snippetMatch = new RegularExpression(snippet.element("match"));

        titleStart = new RegularExpression((Element) snippet.selectSingleNode("title/start"));
        titleEnd = new RegularExpression((Element) snippet.selectSingleNode("title/end"));
        urlStart = new RegularExpression((Element) snippet.selectSingleNode("url/start"));
        urlEnd = new RegularExpression((Element) snippet.selectSingleNode("url/end"));
        summaryStart = new RegularExpression((Element) snippet.selectSingleNode("summary/start"));
        summaryEnd = new RegularExpression((Element) snippet.selectSingleNode("summary/end"));

        valid = true;
    }
}
