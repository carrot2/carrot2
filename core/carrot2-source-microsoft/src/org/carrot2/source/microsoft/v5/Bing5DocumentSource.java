
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft.v5;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.CommonAttributes;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.shaded.guava.common.base.Strings;
import org.carrot2.shaded.guava.common.util.concurrent.RateLimiter;
import org.carrot2.source.MultipageSearchEngine;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.SimpleSearchEngine;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.NotBlank;
import org.carrot2.util.httpclient.HttpRedirectStrategy;
import org.carrot2.util.httpclient.HttpUtils;

/**
 * A base {@link IDocumentSource} sending requests to Bing Search API V5.
 * 
 * <p>Important: there are limits for free use of the above API (beyond which it is a
 * paid service).
 * 
 * @see "https://msdn.microsoft.com/en-us/library/mt604056.aspx"
 */
@Bindable(prefix = "Bing5DocumentSource", inherit = CommonAttributes.class)
public class Bing5DocumentSource extends MultipageSearchEngine
{
    /**
     * System property name for passing Bing API key.
     * 
     * You can also override the key per-controller or request 
     * via init or runtime attributes.
     */
    public static final String SYSPROP_BING5_API = "bing5.key";

    /** Web search specific metadata. */
    final static MultipageSearchEngineMetadata METADATA = new MultipageSearchEngineMetadata(50, 950);

    /**
     * REST endpoint.
     */
    private final static String SERVICE_URL = "https://api.cognitive.microsoft.com/bing/v5.0/search";

    /**
     * Default timeout.
     */
    private static final int BING_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);

    /**
     * As per Bing's official guidelines, limit the rate to a maximum of 5 requests per second.
     */
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(3);

    /**
     * The API key used to authenticate requests. You will have to provide your own API key.
     * There is a free monthly grace request limit.
     *
     * <p>By default takes the system property's value under key: <code>bing5.key</code>.</p>
     */
    @Init
    @Processing
    @Input
    @Attribute
    @Label("Application API key")
    @Level(AttributeLevel.BASIC)
    @Group(SERVICE)
    @Required
    @NotBlank
    public String apiKey = System.getProperty(SYSPROP_BING5_API);

    /**
     * Search type filter.
     */
    @Input
    @Processing
    @Attribute
    @Label("Search source type")
    @Level(AttributeLevel.BASIC)
    @Group(SimpleSearchEngine.SERVICE)
    public SourceType sourceType = SourceType.WEBPAGES;

    /**
     * Site restriction to return value under a given URL. Example:
     * <tt>http://www.wikipedia.org</tt> or simply <tt>wikipedia.org</tt>.
     */
    @Processing
    @Input
    @Attribute
    @Label("Site restriction")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.FILTERING)        
    public String site;

    /**
     * Language and country/region information for the request.
     */
    @Input
    @Processing
    @Attribute
    @Label("Market")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.FILTERING)
    public MarketOption market = MarketOption.ENGLISH_UNITED_STATES;

    /**
     * Adult search restriction (porn filter).
     */
    @Processing
    @Input
    @Attribute
    @Label("Safe search")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.FILTERING)
    public AdultOption adult;

    /**
     * HTTP redirect response strategy (follow or throw an error).
     */
    @Input
    @Processing
    @Attribute
    @Label("HTTP redirect strategy")
    @Level(AttributeLevel.MEDIUM)
    @Group(SimpleSearchEngine.SERVICE)
    @Internal
    public HttpRedirectStrategy redirectStrategy = HttpRedirectStrategy.NO_REDIRECTS; 

    /**
     * Respect official guidelines concerning rate limits. If set to false,
     * rate limits are not observed.
     */
    @Input
    @Processing
    @Attribute
    @Label("Respect request rate limits")
    @Level(AttributeLevel.ADVANCED)
    @Group(SimpleSearchEngine.SERVICE)
    public boolean respectRateLimits = true; 
    
    public Bing5DocumentSource() {
    }
    
    @Override
    public void process() throws ProcessingException {
      process(METADATA, getSharedExecutor(10, this.getClass()));
    }

    @Override
    protected void process(MultipageSearchEngineMetadata metadata, ExecutorService executor) throws ProcessingException
    {
        if (Strings.isNullOrEmpty(apiKey)) {
            throw new ProcessingException("Bing V5 API requires a key. See "
                + Bing5DocumentSource.class.getSimpleName() + " class documentation.");
        }
        super.process(metadata, executor);
    }

    /**
     * Create a single page fetcher for the search range.
     */
    @Override
    protected final Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            public SearchEngineResponse search() throws Exception
            {
                return doSearch(query, bucket.start, bucket.results);
            }
        };
    }

    /**
     * Run a single request to Bing API V5.
     */
    private final SearchEngineResponse doSearch(String query, int startAt, int totalResultsRequested)
        throws Exception
    {
        if (respectRateLimits) {
          RATE_LIMITER.acquire();
        }

        if (!Strings.isNullOrEmpty(site)) {
            query = Strings.nullToEmpty(query) + " site:" + site;
        }

        final ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("q", query == null ? "" : query.trim()));
        params.add(new BasicNameValuePair("offset", Integer.toString(startAt)));
        params.add(new BasicNameValuePair("count", Integer.toString(totalResultsRequested)));

        // Restrict search source type.
        params.add(new BasicNameValuePair("responseFilter", sourceType.responseFilter()));

        if (market != null) {
          params.add(new BasicNameValuePair("mkt", market.marketCode));
        }

        if (adult != null) {
          params.add(new BasicNameValuePair("safeSearch", adult.name()));
        }

        // Disable hit highlighting.
        params.add(new BasicNameValuePair("textDecorations", "false"));
        // params.add(new BasicNameValuePair("textFormat", "Raw"));

        List<Header> headers = Arrays.<Header> asList(
            new BasicHeader("Ocp-Apim-Subscription-Key", apiKey));

        HttpUtils.Response response = null;
retry:
        do {
          response = HttpUtils.doGET(
                  SERVICE_URL, 
                  params, 
                  headers, 
                  /* user */ null, 
                  /* pwd */ null, 
                  BING_TIMEOUT, 
                  redirectStrategy.value());

          if (response.status == 429) {
            for (String [] header : response.headers) {
              if ("Retry-After".equalsIgnoreCase(header[0])) {
                long secs = Long.parseLong(header[1]);
                if (secs <= 5) {
                  Thread.sleep(TimeUnit.SECONDS.toMillis(secs));
                  continue retry;
                } else {
                  // We'd have to wait too long, key saturated or something else is wrong.
                  // break out.
                }
              }
            }
          }

          // Always break
          break;
        } while (true);

        BingResponse parsed;
        InputStream is = response.getPayloadAsStream();
        try {
          parsed = BingResponse.parse(is);
        } finally {
          is.close();
        }

        if (parsed instanceof SearchResponse) {
          SearchResponse searchResponse = (SearchResponse) parsed;

          SearchEngineResponse ser = new SearchEngineResponse();
          ser.metadata.put(SearchEngineResponse.COMPRESSION_KEY, response.compression);

          process(searchResponse, ser);
          
          if (market != null) {
            LanguageCode languageCode = market.toLanguageCode();
            for (Document doc : ser.results) {
              doc.setLanguage(languageCode);
            }
          }

          return ser;
        } else if (parsed instanceof ErrorResponse) {
          throw new IOException(((ErrorResponse) parsed).errors.get(0).message);
        } else if (parsed instanceof UnstructuredResponse) {
          throw new IOException(((UnstructuredResponse) parsed).message);
        } else {
          throw new RuntimeException("Unreachable.");
        }
    }

    private void process(SearchResponse searchResponse, SearchEngineResponse ser) {
      if (searchResponse.webPages != null) {
        ser.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, searchResponse.webPages.totalEstimatedMatches);
        
        for (SearchResponse.WebPages.Result r : searchResponse.webPages.value) {
          Document doc = new Document(r.name, r.snippet, r.url);
          if (r.displayUrl != null) {
            doc.setField(Document.CLICK_URL, r.displayUrl);
          }
          ser.results.add(doc);
        }
      }

      if (searchResponse.news != null) {
        for (SearchResponse.News.Result r : searchResponse.news.value) {
          Document doc = new Document(r.name, r.description, r.url);
          if (r.image != null && r.image.thumbnail != null) {
            doc.setField(Document.THUMBNAIL_URL, r.image.thumbnail.contentUrl);
          }
          if (r.provider != null) {
            ArrayList<String> sources = new ArrayList<>();
            for (SearchResponse.News.Result.Organization o : r.provider) {
              sources.add(o.name);
            }
            doc.setField(Document.SOURCES, sources);
          }
          ser.results.add(doc);
        }
      }
      
      if (searchResponse.images != null) {
        for (SearchResponse.Images.Result r : searchResponse.images.value) {
          Document doc = new Document(r.name, null, r.hostPageDisplayUrl);
          if (r.thumbnailUrl != null) {
            doc.setField(Document.THUMBNAIL_URL, r.thumbnailUrl);
          }
          ser.results.add(doc);
        }
      }
    }
}
