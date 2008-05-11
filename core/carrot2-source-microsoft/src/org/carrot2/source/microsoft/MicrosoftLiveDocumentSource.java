package org.carrot2.source.microsoft;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.*;
import org.apache.commons.lang.StringUtils;
import org.carrot2.util.attribute.*;

import com.microsoft.msnsearch.*;

/**
 * A {@link DocumentSource} fetching {@link Document}s (search results) from Microsoft
 * Live!.
 */
@Bindable
public final class MicrosoftLiveDocumentSource extends SearchEngine
{
    /** Application ID assigned to Carrot Search s.c. */
    public final static String CARROTSEARCH_APPID = "DE531D8A42139F590B253CADFAD7A86172F93B96";

    /** Logger for this class. */
    private final static Logger logger = Logger
        .getLogger(MicrosoftLiveDocumentSource.class);

    /*
     * Disable annoying "missing activation.jar" message from Axis.
     */
    static
    {
        Logger.getLogger("org.apache.axis.utils.JavaUtils").setLevel(Level.ERROR);
    }

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * Static executor for running search threads.
     */
    private final static ExecutorService executor = SearchEngine.createExecutorService(
        MAX_CONCURRENT_THREADS, MicrosoftLiveDocumentSource.class);

    /**
     * Microsoft-assigned application ID for querying the API.
     */
    @Init
    @Input
    @Attribute
    String appid = CARROTSEARCH_APPID;

    /**
     * Culture and language restriction.
     * 
     * @label Culture
     */
    @Input
    @Processing
    @Attribute
    CultureInfo culture = CultureInfo.ENGLISH_UNITED_STATES;

    /**
     * Safe search restriction (porn filter).
     * 
     * @label Safe Search
     */
    @Processing
    @Input
    @Attribute
    SafeSearch safeSearch = SafeSearch.MODERATE;

    /**
     * Microsoft Live! metadata.
     */
    private static final SearchEngineMetadata metadata = new SearchEngineMetadata(50, 1000); 

    /**
     * Run a single query.
     */
    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata, executor);
    }
    
    /**
     * Create a single page fetcher for the search range.
     */
    @Override
    protected final Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new Callable<SearchEngineResponse>()
        {
            public SearchEngineResponse call() throws Exception
            {
                statistics.incrPageRequestCount();
                return search(query, bucket.start, bucket.results);
            }
        };
    }

    /**
     * Run the actual single-page search against MSN.
     */
    private final SearchEngineResponse search(String query, int startAt,
        int totalResultsRequested)
    {
        final SearchRequest request = new SearchRequest(appid, // application id
            query, // query
            culture.cultureInfoCode, // culture info
            safeSearch.getSafeSearchOption(), // safe search options
            new String []
            {
                SearchFlagsNull._None, SearchFlagsNull._DisableLocationDetection,
            }, // search flags
            null, // location
            null // requests
        );

        final String [] searchFields = new String []
        {
            ResultFieldMaskNull._Url, ResultFieldMaskNull._Title,
            ResultFieldMaskNull._Description,
        };

        final int fetchSize = Math.min(totalResultsRequested, metadata.resultsPerPage);
        final SourceRequest sourceRequest = new SourceRequest(SourceType.Web, startAt,
            fetchSize, "", new String []
            {
                SortByTypeNull._Default
            }, // sort by field?
            searchFields, // result fields
            new String [] {}); // search tag filters

        request.setRequests(new SourceRequest []
        {
            sourceRequest
        });

        MSNSearchPortType service;
        try
        {
            service = new MSNSearchServiceLocator().getMSNSearchPort();
        }
        catch (Exception e)
        {
            throw new ProcessingException(
                "Could not initialize Microsoft Live! service.", e);
        }

        final SourceResponse [] responses;
        try
        {
            responses = service.search(new Search(request)).getResponse().getResponses();
        }
        catch (RemoteException e)
        {
            throw new ProcessingException("Microsoft Live! query failed.", e);
        }

        // Check for suspicious responses.
        if (responses.length != 1)
        {
            logger.warn("More than one response for a search: " + responses.length);
        }

        final SourceResponse response = responses[0];
        final Result [] searchResults = response.getResults();

        final SearchEngineResponse searchEngineResponse = new SearchEngineResponse();
        final ArrayList<Document> docs = searchEngineResponse.results;
        for (int j = 0; j < searchResults.length; j++)
        {
            String tmp = searchResults[j].getUrl();

            if (StringUtils.isEmpty(tmp))
            {
                logger.warn("Empty URL in search results.");
                continue;
            }

            final Document document = new Document();
            document.addField(Document.CONTENT_URL, tmp);

            tmp = StringEscapeUtils.unescapeHtml(searchResults[j].getTitle());
            if (!StringUtils.isEmpty(tmp))
            {
                document.addField(Document.TITLE, tmp);
            }

            tmp = StringEscapeUtils.unescapeHtml(searchResults[j].getDescription());
            if (!StringUtils.isEmpty(tmp))
            {
                document.addField(Document.SUMMARY, tmp);
            }

            docs.add(document);
        }

        searchEngineResponse.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
            (long) response.getTotal());

        return searchEngineResponse;
    }
}
