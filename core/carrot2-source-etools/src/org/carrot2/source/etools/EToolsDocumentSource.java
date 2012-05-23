
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.etools;

import java.util.Collections;
import java.util.Map;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.*;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.xml.RemoteXmlSimpleSearchEngineBase;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.IResource;

import com.google.common.collect.Maps;

/**
 * A Carrot2 input component for the eTools service (http://www.etools.ch). For commercial
 * licensing of the eTools feed, please e-mail: <code>contact@comcepta.com</code>.
 */
@Bindable(prefix = "EToolsDocumentSource")
public class EToolsDocumentSource extends RemoteXmlSimpleSearchEngineBase
{
    /**
     * Base URL for the eTools service
     */
    @Input
    @Processing
    @Internal
    @Attribute
    @Label("Service URL")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)
    public String serviceUrlBase = "http://www.etools.ch/partnerSearch.do";

    /**
     * Enumeration for countries supported by {@link EToolsDocumentSource}, see
     * {@link EToolsDocumentSource#country}.
     */
    public enum Country
    {
        ALL("web"), 
        AUSTRIA("AT"), 
        FRANCE("FR"), 
        GERMANY("DE"), 
        GREAT_BRITAIN("GB"),
        ITALY("IT"), 
        LICHTENSTEIN("LI"), 
        SPAIN("ES"),
        SWITZERLAND("CH"); 

        private String code;

        private Country(String code)
        {
            this.code = code;
        }

        @Override
        public String toString()
        {
            return StringUtils.identifierToHumanReadable(name());
        }

        public String getCode()
        {
            return code;
        }
    }

    /**
     * Determines the country of origin for the returned search results.
     */
    @Input
    @Processing
    @Attribute
    @Label("Country")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.FILTERING)
    public Country country = Country.ALL;

    /**
     * Enumeration for languages supported by {@link EToolsDocumentSource}, see
     * {@link EToolsDocumentSource#language}.
     */
    public enum Language
    {
        ALL("all"), 
        ENGLISH("en"), 
        FRENCH("fr"), 
        GERMAN("de"), 
        ITALIAN("it"), 
        SPANISH("es");

        /**
         * Maps <b>some</b> of the values of this enum to {@link LanguageCode}s.
         */
        private final static Map<Language, LanguageCode> TO_LANGUAGE_CODE;
        static
        {
            final Map<Language, LanguageCode> map = Maps.newEnumMap(Language.class);
            map.put(ENGLISH, LanguageCode.ENGLISH);
            map.put(FRENCH, LanguageCode.FRENCH);
            map.put(GERMAN, LanguageCode.GERMAN);
            map.put(ITALIAN, LanguageCode.ITALIAN);
            map.put(SPANISH, LanguageCode.SPANISH);
            
            TO_LANGUAGE_CODE = Collections.unmodifiableMap(map);
        }
        
        private String code;

        private Language(String code)
        {
            this.code = code;
        }

        @Override
        public String toString()
        {
            return StringUtils.identifierToHumanReadable(name());
        }

        public String getCode()
        {
            return code;
        }
        
        /**
         * Returns a corresponding {@link LanguageCode} or <code>null</code> if no
         * {@link LanguageCode} corresponds to this {@link Language} constant.
         */
        public LanguageCode toLanguageCode()
        {
            return TO_LANGUAGE_CODE.get(this);
        }
    }

    /**
     * Determines the language of the returned search results.
     */
    @Input
    @Processing
    @Attribute
    @Label("Language")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.FILTERING)    
    public Language language = Language.ENGLISH;

    /**
     * Maximum time in milliseconds to wait for all data sources to return results.
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 0)
    @Label("Timeout")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)    
    public int timeout = 4000;

    /**
     * Determines which data sources to search.
     */
    @Input
    @Processing
    @Attribute
    @Label("Data sources")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)
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
            return StringUtils.identifierToHumanReadable(name());
        }

        public String getCode()
        {
            return code;
        }
    }

    /**
     * If enabled, excludes offensive content from the results.
     */
    @Input
    @Processing
    @Attribute
    @Label("Safe search")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.FILTERING)
    public boolean safeSearch = false;

    /**
     * eTools partner identifier. If you have commercial arrangements with eTools, specify
     * your partner id here.
     */
    @Input
    @Processing
    @Attribute
    @Internal
    @Label("Partner ID")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)
    public String partnerId = "Carrot2";

    /** Some constants for calculation of request parameters */
    private static final int MAX_DATA_SOURCE_RESULTS = 40;
    private static final int FASTEST_SOURCES_COUNT = 5;
    private static final int ALL_SOURCES_COUNT = 10;

    @Override
    protected IResource getXsltResource()
    {
        return new ClassResource(EToolsDocumentSource.class, "etools-to-c2.xsl");
    }

    @Override
    protected String buildServiceUrl()
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

    @Override
    protected void afterFetch(SearchEngineResponse response)
    {
        // Set document's language
        for (Document document : response.results)
        {
            document.setLanguage(language.toLanguageCode());
        }
    }
}
