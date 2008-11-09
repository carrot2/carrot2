
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.google;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Callable;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.NameValuePair;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.httpclient.HttpUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonNode;
import org.codehaus.jackson.map.JsonTypeMapper;

/**
 * A {@link IDocumentSource} fetching search results from Google JSON API. Please note that
 * this document source cannot deliver more than 32 search results.
 * 
 * @see http://code.google.com/apis/ajaxsearch/documentation/#fonje
 */
@Bindable(prefix = "GoogleDocumentSource")
public class GoogleDocumentSource extends MultipageSearchEngine
{
    /**
     * Service URL.
     */
    @Input
    @Processing
    @Internal
    @Attribute
    public String serviceUrl = "http://ajax.googleapis.com/ajax/services/search/web";

    /**
     * Request Referer Header. Please do not use the default value when deploying this
     * component in production environments. Instead, put the URL to your application
     * here.
     * 
     * @level Advanced
     */
    @Input
    @Processing
    @Internal
    @Attribute
    public String referer = "http://www.carrot2.org";

    /**
     * Keep query word highlighting. Google by default highlights query words in
     * snippets using the bold HTML tag. Set this attribute to <code>true</code> to keep
     * these highlights.
     */
    @Input
    @Processing
    @Attribute
    public boolean keepHighlights = false;

    /**
     * Google API Key. Please do not use the default key when deploying this component in
     * production environments. Instead, apply for your own key.
     * 
     * @see http://code.google.com/apis/ajaxsearch/signup.html
     */
    public String apiKey = "ABQIAAAA_XmITjrzoipJYoBApAgGJhS8yIvkL4-1sNwOJWkV7nbkjq_Z_BQW0-uzOh5lKXRtEXQDTGbzIEz06Q";

    /**
     * Google search metadata.
     */
    static final MultipageSearchEngineMetadata metadata = new MultipageSearchEngineMetadata(
        8, 32);

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata, getSharedExecutor(MAX_CONCURRENT_THREADS, getClass()));
    }

    @Override
    protected Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            public SearchEngineResponse search() throws Exception
            {
                final SearchEngineResponse response = new SearchEngineResponse();
                final NameValuePair [] queryParams = new NameValuePair [] {
                    new NameValuePair("v", "1.0"),
                    new NameValuePair("rsz", "large"),
                    new NameValuePair("start", Integer.toString(bucket.start)),
                    new NameValuePair("key", apiKey),
                    new NameValuePair("q", query),
                };
                final Header [] headers = new Header [] {
                    new Header("Referer", referer),
                };

                final HttpUtils.Response httpResp = HttpUtils.doGET(serviceUrl,
                    Arrays.asList(queryParams), Arrays.asList(headers));

                final JsonParser jsonParser = new JsonFactory().createJsonParser(httpResp.getPayloadAsStream());
                final JsonTypeMapper mapper = new JsonTypeMapper();
                final JsonNode root = mapper.read(jsonParser);
                final JsonNode responseData = root.getFieldValue("responseData");
                final JsonNode resultsArray = responseData.getFieldValue("results");

                if (resultsArray != null)
                {
                    final Iterator<JsonNode> results = resultsArray.getElements();
                    while (results.hasNext())
                    {
                        final JsonNode result = results.next();
                        final Document document = new Document(result.getFieldValue(
                            "titleNoFormatting").getTextValue(), result
                            .getFieldValue("content").getTextValue(), result
                            .getFieldValue("url").getTextValue());
                        response.results.add(document);
                    }

                }
                final JsonNode cursor = responseData.getFieldValue("cursor")
                    .getFieldValue("estimatedResultCount");
                if (cursor != null)
                {
                    response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                        Long.parseLong(cursor.getTextValue()));
                }
                else
                {
                    response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, 0L);
                }

                response.metadata.put(SearchEngineResponse.COMPRESSION_KEY, httpResp.compression);

                return response;
            }
        };
    }

    @Override
    protected void afterFetch(SearchEngineResponse response)
    {
        clean(response, keepHighlights, Document.TITLE, Document.SUMMARY);
    }
}
