
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

/**
 * A basic implementation of Snippet interface.
 */
public class SimpleSnippet {
    protected String title;
    protected String summary;
    protected String url;

    /**
     * Creates a snippet.
     */
    public SimpleSnippet(String title, String url, String summary) {
        this.title = title;
        this.url = url;
        this.summary = summary;

        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
    }

    /**
     * Retrieve this snippet's title string. The string may contain HTML markup
     * and entities, and may be null.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieve this snippet's summary string. The string may contain HTML
     * markup and entities and may be null.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Retrieve an URL to a document this snippet references to. The string
     * should be a valid URL, however this is not guaranteed. It may NOT be
     * null.
     */
    public String getDocumentURL() {
        return url;
    }

    /**
     * Tells whether the snippet has a summary.
     */
    public boolean hasSummary() {
        return summary != null;
    }

    /**
     * Displays this snippet as a String.
     */
    public String toString() {
        StringBuffer p = new StringBuffer();
        p.append("[URL:");

        if (url != null) {
            for (int i = 0; i < url.length(); i++) {
                if (url.charAt(i) != '\n') {
                    p.append(url.charAt(i));
                }
            }
        } else {
            p.append("<null>");
        }

        p.append(":TITLE:");

        if (title != null) {
            for (int i = 0; i < title.length(); i++) {
                if (title.charAt(i) != '\n') {
                    p.append(title.charAt(i));
                }
            }
        } else {
            p.append("<null>");
        }

        p.append(":SNIPPET:");

        if (summary != null) {
            for (int i = 0; i < summary.length(); i++) {
                if (summary.charAt(i) != '\n') {
                    p.append(summary.charAt(i));
                }
            }
        } else {
            p.append("<null>");
        }

        p.append("]");

        return p.toString();
    }
}
