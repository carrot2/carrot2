
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

package org.carrot2.source.pubmed;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpStatus;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.SimpleSearchEngine;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.httpclient.HttpClientFactory;
import org.carrot2.util.httpclient.HttpRedirectStrategy;
import org.carrot2.util.httpclient.HttpUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Performs searches on the PubMed database using its on-line e-utilities:
 * http://eutils.ncbi.nlm.nih.gov/entrez/query/static/eutils_help.html
 */
@Bindable(prefix = "PubMedDocumentSource")
public class PubMedDocumentSource extends SimpleSearchEngine
{
    /** PubMed search service URL */
    public static final String E_SEARCH_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi";

    /** PubMed fetch service URL */
    public static final String E_FETCH_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi";

    /** HTTP timeout for pubmed services.*/
    public static final int PUBMED_TIMEOUT = HttpClientFactory.DEFAULT_TIMEOUT * 3;

    /**
     * Tool name, if registered.
     * @see "http://www.ncbi.nlm.nih.gov"
     */
    @Init
    @Input
    @Attribute
    @Label("EUtils Registered Tool Name")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.QUERY)
    public String toolName = "Carrot Search";

    /**
     * Maximum results to fetch. No more than the specified number of results
     * will be fetched from PubMed, regardless of the requested number of results. 
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1)
    @Internal(configuration = true)
    @Label("Maximum results")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.QUERY)
    public int maxResults = 150;
    
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

    @Override
    protected SearchEngineResponse fetchSearchResponse() throws Exception
    {
        PubMedIdSearchHandler idResponse = getPubMedIds(query, results);
        SearchEngineResponse response = getPubMedAbstracts(idResponse.getPubMedPrimaryIds());
        response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, idResponse.getMatchCount());
        return response;
    }

    @Override
    protected void afterFetch(SearchEngineResponse response)
    {
        for (Document document : response.results)
        {
            document.setLanguage(LanguageCode.ENGLISH);
        }
    }

    /**
     * Gets PubMed entry ids matching the query.
     */
    private PubMedIdSearchHandler getPubMedIds(final String query, final int requestedResults)
        throws Exception
    {
        final XMLReader reader = newXmlReader();
        PubMedIdSearchHandler searchHandler = new PubMedIdSearchHandler();
        reader.setContentHandler(searchHandler);

        final String url = E_SEARCH_URL
            + "?db=pubmed"
            + "&usehistory=n&" 
            + "&term=" + StringUtils.urlEncodeWrapException(query, "UTF-8") 
            + "&retmax=" + Integer.toString(Math.min(requestedResults, maxResults))
            + "&tool=" + StringUtils.urlEncodeWrapException(toolName, "UTF-8");

        final HttpUtils.Response response = HttpUtils.doGET(
            url, 
            null, 
            null,
            null, null, 
            PUBMED_TIMEOUT,
            redirectStrategy.value());

        // Get document IDs
        if (response.status == HttpStatus.SC_OK)
        {
            reader.parse(new InputSource(response.getPayloadAsStream()));
        }
        else
        {
            throw new IOException("PubMed returned HTTP Error: " + response.status
                + ", HTTP payload: " + new String(response.payload, "iso8859-1"));
        }

        return searchHandler;
    }

    /**
     * Gets PubMed abstracts corresponding to the provided ids.
     */
    private SearchEngineResponse getPubMedAbstracts(List<String> ids) throws Exception
    {
        if (ids.isEmpty()) 
        {
            return new SearchEngineResponse();
        }
        
        final XMLReader reader = newXmlReader();
        final PubMedContentHandler fetchHandler = new PubMedContentHandler();
        reader.setContentHandler(fetchHandler);

        final String url = E_FETCH_URL 
            + "?db=pubmed"
            + "&retmode=xml" 
            + "&rettype=abstract" 
            + "&id=" + getIdsString(ids)
            + "&tool=" + StringUtils.urlEncodeWrapException(toolName, "UTF-8");

        final HttpUtils.Response response = HttpUtils.doGET(
            url, 
            null, null,
            null, null, 
            PUBMED_TIMEOUT,
            redirectStrategy.value());

        // Get document contents
        // No URL logging here, as the url can get really long
        if (response.status == HttpStatus.SC_OK)
        {
            reader.parse(new InputSource(response.getPayloadAsStream()));
        }
        else
        {
            throw new IOException("PubMed returned HTTP Error: " + response.status
                + ", HTTP payload: " + new String(response.payload, "iso8859-1"));
        }

        return fetchHandler.getResponse();
    }

    static XMLReader newXmlReader()
        throws SAXException, ParserConfigurationException
    {
        XMLReader reader = SAXParserFactory.newInstance()
            .newSAXParser()
            .getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setEntityResolver(new EmptyEntityResolver());
        return reader;
    }

    private String getIdsString(List<String> ids)
    {
        final StringBuilder buf = new StringBuilder();
        for (String id : ids)
        {
            buf.append(id);
            buf.append(",");
        }

        if (buf.length() > 0)
        {
            return buf.substring(0, buf.length() - 1);
        }
        else
        {
            return "";
        }
    }
}
