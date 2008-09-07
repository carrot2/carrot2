package org.carrot2.source.google;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.apache.commons.httpclient.Header;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.*;
import org.carrot2.util.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.httpclient.HttpUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonNode;
import org.codehaus.jackson.map.JsonTypeMapper;

import com.google.common.collect.Maps;

/**
 * A {@link DocumentSource} fetching search results from Google JSON API. Please note that
 * this document source cannot deliver more than 32 search results.
 * 
 * @see http://code.google.com/apis/ajaxsearch/documentation/#fonje
 */
@Bindable
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

    /**
     * Static executor for running search threads.
     */
    private final static ExecutorService executor = ExecutorServiceUtils
        .createExecutorService(MAX_CONCURRENT_THREADS, GoogleDocumentSource.class);

    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata, executor);
    }

    @Override
    protected Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            public SearchEngineResponse search() throws Exception
            {
                final String serviceURL = buildServiceUrl(bucket.start);
                final SearchEngineResponse response = new SearchEngineResponse();

                InputStream stream = null;
                try
                {
                    final Map<String, Object> status = Maps.newHashMap();
                    stream = HttpUtils.openGzipHttpStream(serviceURL, status, new Header(
                        "Referer", referer));

                    final JsonParser jsonParser = new JsonFactory()
                        .createJsonParser(stream);
                    final JsonTypeMapper mapper = new JsonTypeMapper();
                    final JsonNode root = mapper.read(jsonParser);
                    final JsonNode responseData = root.getFieldValue("responseData");
                    final JsonNode resultsArray = responseData.getFieldValue("results");

                    if (resultsArray != null)
                    {
                        final Iterator<JsonNode> results = resultsArray.getElements();

                        for (; results.hasNext();)
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
                    response.metadata.put(SearchEngineResponse.COMPRESSION_KEY, status
                        .get(HttpUtils.STATUS_COMPRESSION_USED));
                }
                finally
                {
                    CloseableUtils.close(stream);
                }
                return response;
            }
        };
    }

    @Override
    protected void afterFetch(SearchEngineResponse response)
    {
        clean(response, keepHighlights, Document.TITLE, Document.SUMMARY);
    }

    private String buildServiceUrl(int start)
    {
        return serviceUrl + "?v=1.0&rsz=large&start=" + start + "&key="
            + StringUtils.urlEncodeWrapException(apiKey, "UTF-8") + "&q="
            + StringUtils.urlEncodeWrapException(query, "UTF-8");
    }
}
