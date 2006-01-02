
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

/**
 * This class receives callbacks from some RegExpSnipperReader object about
 * improperly formed snippets etc. Subclass this class and override methods
 * for which you want to receive notifications. If not overriden, methods do
 * nothing.
 *
 * @author Dawid Weiss
 */
public abstract class SnippetExtractorCallback {
    /**
     * Instantiate without any arguments.
     */
    public SnippetExtractorCallback() {
    }

    /**
     * Calls when a snippet has been recognized, but no title could be
     * extracted.
     */
    public void snippetHasNoTitle() {
    }

    /**
     * Calls when a snippet has been recognized, but no URL could be extracted.
     */
    public void snippetHasNoURL() {
    }

    /**
     * Called when a snippet has been recognized, and the summary could not be
     * extracted. The reader expects <code>true</code> if the empty summary is
     * considered to be o.k. and the snippet will be added to the result, or
     * <code>false</code> otherwise. The default implementation returns true
     * (allows empty summaries).
     *
     * @return true if the snippet is to be appended to the result with an
     *         empty summary.
     */
    public boolean acceptSnippetWithEmptySummary() {
        return true;
    }

    /**
     * If s is null, end of stream has been reached.
     *
     * @param s
     */
    public void snippetRecognized(SimpleSnippet s) {
    }

    /**
     * Called when a snippet region match has been found (before any analysis
     * of title/url/summary). This method may be left unimplemented - it is
     * mostly for debugging purposes.
     */
    public void entireSnippetRegionMatch(String matchedString, int streamStart,
        int streamEnd) {
    }
}
