package org.carrot2.source.etools;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.xml.transform.Templates;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.*;
import org.carrot2.source.xml.XmlDocumentSourceHelper;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.resource.ClassResource;

/**
 * A Carrot2 input component for the eTools service (http://www.etools.ch). For commercial
 * licensing of the eTools feed, please e-mail: contact@comcepta.com.
 */
@Bindable
public class EToolsDocumentSource extends SearchEngine
{
    /**
     * Base URL for the eTools service
     * 
     * @label Service URL
     * @level Advanced
     */
    @Input
    @Processing
    @Attribute
    public String serviceUrlBase = "http://www.etools.ch/partnerSearch.do";

    /**
     * Enumeration for countries supported by {@link EToolsDocumentSource}, see
     * {@link EToolsDocumentSource#country}.
     */
    public enum Country
    {
        ALL("web"), SWITZERLAND("CH"), LICHTENSTEIN("LI"), GERMANY("DE"), AUSTRIA("AT"), FRANCE(
            "FR"), ITALY("IT"), SPAIN("ES"), GREAT_BRITAIN("GB");

        private String code;

        private Country(String code)
        {
            this.code = code;
        }

        @Override
        public String toString()
        {
            return StringUtils.capitalize(name().toLowerCase());
        }

        public String getCode()
        {
            return code;
        }
    }

    /**
     * Determines the country of origin for the returned search results.
     * 
     * @label Country
     * @level Medium
     */
    @Input
    @Processing
    @Attribute
    public Country country = Country.ALL;

    /**
     * Enumeration for languages supported by {@link EToolsDocumentSource}, see
     * {@link EToolsDocumentSource#language}.
     */
    public enum Language
    {
        ALL("all"), GERMAN("de"), ENGLISH("en"), FRENCH("fr"), ITALIAN("it"), SPANISH(
            "es");

        private String code;

        private Language(String code)
        {
            this.code = code;
        }

        @Override
        public String toString()
        {
            return StringUtils.capitalize(name().toLowerCase());
        }

        public String getCode()
        {
            return code;
        }
    }

    /**
     * Determines the language of the returned search results.
     * 
     * @label Language
     * @level Basic
     */
    @Input
    @Processing
    @Attribute
    public Language language = Language.ENGLISH;

    /**
     * Maximum time in milliseconds to wait for all data sources to return results.
     * 
     * @label Timeout
     * @level Advanced
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 0)
    public int timeout = 4000;

    /**
     * Determines which data sources to search.
     * 
     * @label Data sources
     * @level Medium
     */
    @Input
    @Processing
    @Attribute
    public DataSources dataSources = DataSources.ALL;

    /**
     * Enumeration for the data sources modes supported by {@link EToolsDocumentSource},
     * see {@link EToolsDocumentSource#dataSources}.
     */
    public enum DataSources
    {
        /**
         * All eTools data sources will be searched.
         */
        ALL("all"),

        /**
         * Five fastest eTools data sources at the moment will be searched.
         */
        FASTEST("fastest");

        private String code;

        private DataSources(String code)
        {
            this.code = code;
        }

        @Override
        public String toString()
        {
            return StringUtils.capitalize(code);
        }

        public String getCode()
        {
            return code;
        }
    }

    /**
     * Determines whether offensive content will be excluded from the returned results.
     * 
     * @label Safe search
     * @level Basic
     */
    @Input
    @Processing
    @Attribute
    public boolean safeSearch = false;

    /**
     * eTools partner identifier. If you have commercial arrangements with eTools, specify
     * your partner id here.
     * 
     * @label Partner
     * @level Advanced
     */
    @Input
    @Processing
    @Attribute
    @Internal
    public String partnerId = "Carrot2";

    /** Some constants for calculation of request parameters */
    private static final int MAX_DATA_SOURCE_RESULTS = 40;
    private static final int FASTEST_SOURCES_COUNT = 5;
    private static final int ALL_SOURCES_COUNT = 10;

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * Static executor for running search threads.
     */
    private final static ExecutorService executor = SearchEngine.createExecutorService(
        MAX_CONCURRENT_THREADS, EToolsDocumentSource.class);

    /** eTools XML to Carrot2 XML XSLT */
    private final Templates eTools2Carrot2Xslt;

    /** A helper class that groups common functionality for XML/XSLT based data sources. */
    private final XmlDocumentSourceHelper xmlDocumentSourceHelper = new XmlDocumentSourceHelper();

    /**
     * Creates an {@link EToolsDocumentSource} with the default values of attributes.
     */
    public EToolsDocumentSource()
    {
        eTools2Carrot2Xslt = xmlDocumentSourceHelper.loadXslt(new ClassResource(
            EToolsDocumentSource.class, "etools-to-c2.xsl"));
    }

    @Override
    public void process() throws ProcessingException
    {
        // ETools returns all results at once, but it's still useful to use
        // the SearchEngine class for statistics and common attributes
        super.process(new SearchEngineMetadata(results, results), executor);
    }

    @Override
    protected Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            public SearchEngineResponse search() throws Exception
            {
                // Ignore buckets, SearchEngine is configured to perform one request
                final URL serviceURL = new URL(buildServiceUrl());

                final ProcessingResult processingResult = xmlDocumentSourceHelper
                    .loadProcessingResult(serviceURL.openStream(), 
                        eTools2Carrot2Xslt, null);

                final SearchEngineResponse response = new SearchEngineResponse();
                final List<Document> documents = processingResult.getDocuments();
                if (documents != null)
                {
                    response.results.addAll(documents);
                    response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                        (long) documents.size());
                }
                else
                {
                    response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, 0L);
                }

                return response;
            }
        };
    }

    /**
     * Builds the service url for the specific query and attributes.
     */
    private String buildServiceUrl()
    {
        String urlBase = serviceUrlBase;
        if (urlBase.endsWith("/"))
        {
            urlBase = urlBase.substring(0, urlBase.length() - 1);
        }

        return urlBase + "?partner=" + partnerId + "&query="
            + org.carrot2.util.StringUtils.urlEncodeWrapException(query, "UTF-8")
            + "&dataSourceResults=" + Integer.toString(getDataSourceResultsCount())
            + "&maxRecords=" + results + "&language=" + language.getCode() + "&timeout="
            + Integer.toString(timeout) + "&dataSources=" + dataSources.getCode()
            + "&safeSearch=" + Boolean.toString(safeSearch) + "&country="
            + country.getCode();
    }

    /**
     * Returns the number of results per data source, estimated based on the total
     * requested results.
     * 
     * @param params
     */
    int getDataSourceResultsCount()
    {
        int sources = DataSources.ALL.equals(dataSources) ? ALL_SOURCES_COUNT
            : FASTEST_SOURCES_COUNT;

        if (results == 0)
        {
            return 0;
        }

        int rawDataSourceResults = results / sources;
        return Math.min(((rawDataSourceResults + 9) / 10 + 1) * 10,
            MAX_DATA_SOURCE_RESULTS);
    }
}
