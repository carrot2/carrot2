
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

package org.carrot2.source.pubmed;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.*;

import org.apache.commons.httpclient.HttpStatus;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.SimpleSearchEngine;
import org.carrot2.util.*;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.httpclient.HttpUtils;
import org.xml.sax.*;

import com.google.common.collect.Maps;

/**
 * Performs searches on the PubMed database using its on-line e-utilities:
 * http://eutils.ncbi.nlm.nih.gov/entrez/query/static/eutils_help.html
 */
@Bindable(prefix = "PubMedDocumentSource")
public class PubMedDocumentSource extends SimpleSearchEngine
{
    /** PubMed search service URL */
    public static final String E_SEARCH_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi";

    /** PubMed fetch service URL */
    public static final String E_FETCH_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi";

    @Override
    protected SearchEngineResponse fetchSearchResponse() throws Exception
    {
        return getPubMedAbstracts(getPubMedIds(query, results));
    }

    /**
     * Gets PubMed entry ids matching the query.
     */
    private List<String> getPubMedIds(final String query, final int requestedResults)
        throws Exception
    {
        final XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
            .getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);

        PubMedSearchHandler searchHandler = new PubMedSearchHandler();
        reader.setContentHandler(searchHandler);

        InputStream stream = null;
        try
        {
            final String url = E_SEARCH_URL + "?db=pubmed&usehistory=n&term="
                + StringUtils.urlEncodeWrapException(query, "UTF-8") + "&retmax="
                + Integer.toString(requestedResults);
            final Map<String, Object> status = Maps.newHashMap();

            stream = HttpUtils.openGzipHttpStream(url, status);

            // Get document IDs
            int statusCode = (Integer) status.get(HttpUtils.STATUS_CODE);
            if (statusCode == HttpStatus.SC_OK)
            {
                reader.parse(new InputSource(stream));
            }
            else
            {
                final byte [] message = StreamUtils.readFully(stream);
                throw new IOException("PubMed returned HTTP Error: " + statusCode
                    + ", HTTP payload: " + new String(message, "iso8859-1"));
            }
        }
        finally
        {
            CloseableUtils.close(stream);
        }

        return searchHandler.getPubMedPrimaryIds();
    }

    /**
     * Gets PubMed abstracts corresponding to the provided ids.
     */
    private SearchEngineResponse getPubMedAbstracts(List<String> ids) throws Exception
    {
        final XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
            .getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);

        final PubMedFetchHandler fetchHandler = new PubMedFetchHandler();
        reader.setContentHandler(fetchHandler);

        InputStream stream = null;
        try
        {
            final String url = E_FETCH_URL
                + "?db=pubmed&retmode=xml&rettype=abstract&id=" + getIdsString(ids);
            final Map<String, Object> status = Maps.newHashMap();

            stream = HttpUtils.openGzipHttpStream(url, status);

            // Get document contents
            // No URL logging here, as the url can get really long
            int statusCode = (Integer) status.get(HttpUtils.STATUS_CODE);
            if (statusCode == HttpStatus.SC_OK)
            {
                reader.parse(new InputSource(stream));
            }
            else
            {
                final byte [] message = StreamUtils.readFully(stream);
                throw new IOException("PubMed returned HTTP Error: " + statusCode
                    + ", HTTP payload: " + new String(message, "iso8859-1"));
            }
        }
        finally
        {
            CloseableUtils.close(stream);
        }

        return fetchHandler.getResponse();
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
