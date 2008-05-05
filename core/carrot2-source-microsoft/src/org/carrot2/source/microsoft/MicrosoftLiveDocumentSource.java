package org.carrot2.source.microsoft;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.*;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.source.*;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.*;

import com.google.common.base.Predicate;
import com.microsoft.msnsearch.*;

/**
 * A {@link DocumentSource} fetching {@link Document}s (search results) from Microsoft
 * Live!.
 */
@Bindable
public final class MicrosoftLiveDocumentSource extends SearchEngine
{
    /** Logger for this class. */
    final static Logger logger = Logger.getLogger(MicrosoftLiveDocumentSource.class);

    /** Application ID assigned to Carrot Search s.c. */
    public final static String CARROTSEARCH_APPID = "DE531D8A42139F590B253CADFAD7A86172F93B96";

    /**
     * Maximum concurrent threads to Microsoft Live! API from all instances of this
     * component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * Static executor for running search threads to Microsoft Live!. You can set the
     * number of concurrent requests from <b>all</b> instances of this component here.
     */
    private final static ExecutorService executor = Executors.newFixedThreadPool(
        MAX_CONCURRENT_THREADS,
        contextClassLoaderThreadFactory(MicrosoftLiveDocumentSource.class
            .getClassLoader()));

    /**
     * Microsoft-assigned application ID for querying the API.
     */
    @Init
    @Input
    @Attribute
    String appid = CARROTSEARCH_APPID;

    @Processing
    @Input
    @Attribute(key = AttributeNames.START)
    int start = 0;

    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    int results = 100;

    @Processing
    @Input
    @Attribute(key = AttributeNames.QUERY)
    @Required
    String query;

    @SuppressWarnings("unused")
    @Processing
    @Output
    @Attribute(key = AttributeNames.RESULTS_TOTAL)
    long resultsTotal;

    /**
     * Culture and language restriction.
     * 
     * @label Culture
     */
    @Input
    @Processing
    @Attribute
    @Required
    CultureInfo culture = CultureInfo.ENGLISH_UNITED_STATES;

    /**
     * Safe search restriction (porn filter).
     * 
     * @label Safe Search
     */
    @Processing
    @Input
    @Attribute
    @Required
    SafeSearch safeSearch = SafeSearch.MODERATE;

    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    Collection<Document> documents;

    /**
     * Maximum number of results returned per page.
     * 
     * @label Results Per Page
     * @level Advanced
     */
    @Init
    @Input
    @Attribute
    public int resultsPerPage = 50;

    /**
     * Maximum index of reachable result.
     * 
     * @label Maximum Result Index
     * @level Advanced
     */
    @Init
    @Input
    @Attribute
    public int maxResultIndex = 1000;

    /*
     * Disable annoying "missing activation.jar" message from Axis.
     */
    static
    {
        Logger.getLogger("org.apache.axis.utils.JavaUtils").setLevel(Level.ERROR);
    }

    /**
     * Run a request against Live! API.
     */
    @Override
    public void process() throws ProcessingException
    {
        final SearchEngineResponse [] responses = runQuery(query, start, results,
            maxResultIndex, resultsPerPage, executor);

        if (responses.length > 0)
        {
            // Collect documents from the responses.
            documents = new ArrayList<Document>(Math.min(results, maxResultIndex));
            collectDocuments(documents, responses);

            // Filter out duplicated URLs.
            final Iterator<Document> i = documents.iterator();
            final Predicate<Document> p = new UniqueFieldPredicate(Document.CONTENT_URL);
            while (i.hasNext())
            {
                if (!p.apply(i.next()))
                {
                    i.remove();
                }
            }

            resultsTotal = responses[0].getResultsTotal();
        }
        else
        {
            documents = Collections.<Document> emptyList();
            resultsTotal = 0;
        }
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
                return search(query, bucket.start, bucket.results);
            }
        };
    }

    /**
     * Run the actual search against MSN.
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

        final int fetchSize = Math.min(totalResultsRequested, resultsPerPage);
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
