
/*
 * Copyright 2002 Dawid Weiss. All rights reserved.
 * Please refer to licence file in docs/legal/carrot2.LICENCE
 *
 * $Id$
 */

package com.paulodev.carrot.httpmultipage;

import gnu.regexp.REException;

import java.io.*;
import java.util.*;
import org.jdom.Element;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.util.common.StreamUtils;
import com.dawidweiss.carrot.util.jdom.JDOMHelper;
import com.dawidweiss.carrot.util.net.http.*;

/**
 * Submits a search query and reads as many pages as needed to reach the number of requested results.
 *
 * The result snippets are NOT yet extracted from the page, only an estimation of their number is taken
 * into account.
 */
public class HttpMultiPageReader
{
    private final static Logger log = Logger.getLogger(HttpMultiPageReader.class);

    protected final static String PAGE_INFO
            = "pageinfo";
    protected final static String EXPECTED_RESULTS_PER_PAGE
            = PAGE_INFO + "/results-per-page";
    protected final static String RESULTS_MATCHED
            = PAGE_INFO + "/number-of-matched-documents/regexpression";
    protected final static String NO_RESULTS_MARKER
            = PAGE_INFO + "/no-results-marker/regexpression";
    protected final static String RESULTS_PER_PAGE
            = PAGE_INFO+ "/expected-results-per-page";

    protected HTTPFormSubmitter submitter;
    protected FormParameters    queryParameters;


    /** It is ok to instantiate this class without arguments */
    public HttpMultiPageReader( HTTPFormSubmitter submitter, FormParameters queryParameters )
    {
        this.submitter = submitter;
        this.queryParameters = queryParameters;
    }


    public Enumeration getQueryResultsPages(String query, int resultsNeeded, String encoding, Element pageInfo)
            throws IOException, REException, Exception
    {
        String inputEncoding  = encoding;
        String outputEncoding = encoding;

        try {
            // load the first page of the results.
            Map mappings = new HashMap();
            mappings.put("query.string", query);
            mappings.put("query.startFrom", "0");

            InputStream pageInputStream = submitter.submit( queryParameters, mappings, outputEncoding );

            // load the page entirely.
            byte[] pageBytes = StreamUtils.readFully(pageInputStream);

            // find out how many results there is on the page.
            Vector         outputPages    = new Vector();
            int            resultsPerPage = Integer.parseInt(JDOMHelper.getElement(RESULTS_PER_PAGE, pageInfo).getText());

            InputStream pageStream;

            pageStream = new ByteArrayInputStream(pageBytes);

            int startFrom = 0;
            while (resultsNeeded > 0)
            {
                outputPages.add(pageStream);

                resultsNeeded = resultsNeeded - resultsPerPage;

                if (resultsNeeded > 0)
                {
                    // fetch next page of the results.
                    startFrom  += resultsPerPage;
                    mappings.put("query.startFrom", Integer.toString(startFrom));
                    pageStream = submitter.submit(queryParameters, mappings, outputEncoding);
                }
            }

            return outputPages.elements();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding.");
        }
    }

    /**
     * Returns an InputStream to the collated results of a Query
     * (pages are returned one after another if more than one
     * was needed to reach the required number of results).
     *
     * Some information about the service must be provided pageInfo XML object.
     * An example XML structure for pageInfo could look as shown below.
     *
     * <pre>
     * &lt;pageinfo&gt;
     *   &lt;expected-results-per-page&gt;50&lt;/expected-results-per-page&gt;
     *
     *   &lt;!-- actual number of results found on a results page.
     *        This property MUST be found for the snippets to
     *        be extracted --&gt;
     *   &lt;number-of-matched-documents&gt;
     *       &lt;regexpression&gt;
     *           &lt;match&gt;&lt;![CDATA[of about[^S]*S]]&gt;&lt;/match&gt;
     *           &lt;replace regexp="[^0123456789]*" with="" /&gt;
     *       &lt;/regexpression&gt;
     *   &lt;/number-of-matched-documents&gt;
     *
     *   &lt;!-- if no results number is found, the page is again checked whether
     *        it matched a 'no-results' page --&gt;
     *   &lt;no-results-marker&gt;
     *       &lt;regexpression&gt;
     *           &lt;match&gt;&lt;![CDATA[did not match any documents]]&gt;&lt;/match&gt;
     *       &lt;/regexpression&gt;
     *   &lt;/no-results-marker&gt;
     * &lt;/pageinfo&gt;
     * </pre>
     *
     * @param query  A query string passed to the service.
     * @param resultsNeeded  Number of results needed.
     * @param encoding Encoding of the output parameters and the input stream
     *                 (for pattern matching only, the stream is not altered).
     * @param pageInfo  A pageInfo XML DOM. Please refer to an example.
     * @return An input stream to the collated result pages.
     * @throws IOException Problems with reading/submitting the web page.
     * @throws REException Regular expression cannot be parsed.
     * @throws Exception If page-parsing problems occur.
     */

    public InputStream getQueryResults(String query, int resultsNeeded, String encoding, Element pageInfo)
            throws IOException, REException, Exception
    {
    Enumeration e = getQueryResultsPages(query, resultsNeeded, encoding, pageInfo);
    if (e != null)
            return new SequenceInputStream(e);
        else
            return null;
    }
}



