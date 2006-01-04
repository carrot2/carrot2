
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

import gnu.regexp.*;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;


/**
 * Extracts snippets from an InputStream.
 *
 * @author Dawid Weiss
 */
public class RegExpSnippetExtractor {
    private final static Logger log = Logger.getLogger(RegExpSnippetExtractor.class);

    /**
     * The snippet description this object operates on.
     */
    private final SnippetDescription snippetDescription;

    /**
     * Prevent instantiation with no arguments
     *
     * @param snippetDescription
     */
    public RegExpSnippetExtractor(SnippetDescription snippetDescription) {
        this.snippetDescription = snippetDescription;
    }

    /**
     * Processes an input stream and extracts snippets. <b>Closes the input
     * reader on exit (even if an exception has been thrown)</b>
     *
     * @param inputCharacterStream An input stream to be parsed.
     * @param callback A callback object, which will be notified during the
     *        parsing of the input stream.
     *
     * @throws InterruptedException
     * @throws REException
     */
    public void extractSnippets(Reader inputCharacterStream,
        SnippetExtractorCallback callback)
        throws REException, InterruptedException {
        if (callback == null) {
            callback = new SnippetExtractorCallback() {
                    };
        }

        try {
            // Initialize regular expression matching rules.
            RE snippetRazor = new RE(snippetDescription.getSnippetMatch()
                                                       .getRegExp(),
                    RE.REG_DOT_NEWLINE);
            RE titleStartMarker = new RE(snippetDescription.getTitleStartMatch()
                                                           .getRegExp(),
                    RE.REG_DOT_NEWLINE);
            RE titleEndMarker = new RE(snippetDescription.getTitleEndMatch()
                                                         .getRegExp(),
                    RE.REG_DOT_NEWLINE);
            RE URLStartMarker = new RE(snippetDescription.getURLStartMatch()
                                                         .getRegExp(),
                    RE.REG_DOT_NEWLINE);
            RE URLEndMarker = new RE(snippetDescription.getURLEndMatch()
                                                       .getRegExp(),
                    RE.REG_DOT_NEWLINE);
            RE summaryStartMarker = new RE(snippetDescription.getSummaryStartMatch()
                                                             .getRegExp(),
                    RE.REG_DOT_NEWLINE);
            RE summaryEndMarker = new RE(snippetDescription.getSummaryEndMatch()
                                                           .getRegExp(),
                    RE.REG_DOT_NEWLINE);

            // read input stream and parse snippets until the input stream ends.
            REMatchEnumeration matches = snippetRazor.getMatchEnumeration(inputCharacterStream);

            while (matches.hasMoreMatches()) {
                REMatch matchingElement = matches.nextMatch();
                String fullSnippet = matchingElement.toString();

                // find the title. A title is mandatory for a snippet.
                String title;

                title = extractSubExpression(fullSnippet, titleStartMarker,
                        titleEndMarker,
                        snippetDescription.getTitleStartMatch().isConsumeToken(),
                        snippetDescription.getTitleEndMatch().isConsumeToken());

                callback.entireSnippetRegionMatch(fullSnippet,
                    matchingElement.getStartIndex(),
                    matchingElement.getEndIndex());

                if (title == null) {
                    // invalid snippet: no title found.
                    callback.snippetHasNoTitle();
                    continue;
                }

                // find the url. URL is also mandatory.
                String url;

                url = extractSubExpression(fullSnippet, URLStartMarker,
                        URLEndMarker,
                        snippetDescription.getURLStartMatch().isConsumeToken(),
                        snippetDescription.getURLEndMatch().isConsumeToken());

                if (url == null) {
                    log.debug("No URL matched in snippet: " + fullSnippet);

                    // invalid snippet: no url found.
                    callback.snippetHasNoURL();
                    continue;
                }

                // find the summary
                String summary;

                summary = extractSubExpression(fullSnippet, summaryStartMarker,
                        summaryEndMarker,
                        snippetDescription.getSummaryStartMatch()
                                          .isConsumeToken(),
                        snippetDescription.getSummaryEndMatch().isConsumeToken());

                if (summary == null) {
                    // check whether an empty summary should be allowed or not.
                    if (callback.acceptSnippetWithEmptySummary() == false) {
                        continue;
                    }
                }

                SimpleSnippet snippetOb = new SimpleSnippet(title, url, summary);

                callback.snippetRecognized(snippetOb);
            }

            // mark the end-of-stream for the readers.
            callback.snippetRecognized(null);
        } finally {
            if (inputCharacterStream != null) {
                try {
                    inputCharacterStream.close();
                } catch (IOException e) {
                    /**
                     * @todo add log entry here?
                     */
                }
            }
        }
    }

    /**
     * Finds a subexpression in a string.
     *
     * @param expression A full string from which a subexpression is to be
     *        extracted.
     * @param startMarker A regular expression the subexpression begins with
     * @param endMarker A regular expression the subexpression ends with
     * @param consumeStart Consume the regular expression at the start of
     *        subexpression?
     * @param consumeEnd Consume the regular expression at the end of
     *        subexpression?
     */
    protected String extractSubExpression(String expression, RE startMarker,
        RE endMarker, boolean consumeStart, boolean consumeEnd) {
        REMatch start;
        REMatch end;

        start = startMarker.getMatch(expression);

        if (start == null) {
            // no start token: no subexpression found.
            return null;
        }

        end = endMarker.getMatch(expression, start.getEndIndex());

        if (end == null) {
            // no end token: no subexpression found.
            return null;
        }

        int subExprStart;
        int subExprEnd;

        subExprStart = (consumeStart ? start.getEndIndex() : start.getEndIndex());
        subExprEnd = (consumeEnd ? end.getStartIndex() : end.getEndIndex());

        return expression.substring(subExprStart, subExprEnd);
    }
}
