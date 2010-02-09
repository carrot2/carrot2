
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.google;

import java.io.*;

import org.carrot2.core.Document;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.xml.RemoteXmlSimpleSearchEngineBase;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.IResource;

/**
 * Fetches documents from an instance of Google Desktop search engine.
 */
@Bindable(prefix = "GoogleDesktopDocumentSource")
public class GoogleDesktopDocumentSource extends RemoteXmlSimpleSearchEngineBase
{
    /**
     * Query URL. Installation-specific URL at which Google Desktop search service is
     * available. On Windows machines, the URL is available at the
     * <code>HKEY_CURRENT_USER\Software\Google\Google Desktop\API\search_url</code> system
     * registry key and Carrot2 will attempt to automatically read the value from the
     * registry when run with Administrator provileges. Please consult <a
     * href="http://code.google.com/apis/desktop/docs/queryapi.html#httpxml">Google
     * Desktop API documentation</a> for further instructions on how to determine the
     * query URL on other systems.
     * 
     * @see <a
     *      href="http://code.google.com/apis/desktop/docs/queryapi.html#httpxml">Google
     *      API</a>
     * @label Query URL
     * @level Advanced
     * @group Service
     */
    @Input
    @Processing
    @Attribute
    public String queryUrl = getQueryUrlFromRegistry();

    /**
     * Keep query word highlighting. Google by default highlights query words in snippets
     * using the bold HTML tag. Set this attribute to <code>true</code> to keep these
     * highlights.
     * 
     * @group Postprocessing
     * @level Advanced
     * @label Keep highlights
     */
    @Input
    @Processing
    @Attribute
    public boolean keepHighlights = false;

    @Override
    protected SearchEngineResponse fetchSearchResponse() throws Exception
    {
        if (org.apache.commons.lang.StringUtils.isBlank(queryUrl))
        {
            // Return the error in a more gentle way
            final SearchEngineResponse response = new SearchEngineResponse();

            final String windowsHint = "2) Try running the application as an Administrator, "
                + "3) If you can't run the application as an Administrator, "
                + "set the Query URL attribute manually.";
            final String otherHint = "2) In the Search view, set the Query URL (optional) attribute "
                + "to point to the Query URL of your Google Desktop installation. See the attribute's "
                + "inline help for more information.";
            response.results.add(new Document("Could not connect to Google Desktop",
                "To fix the problem: 1) Make sure Google Desktop is installed on your machine, "
                    + (org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS ? windowsHint
                        : otherHint), ""));
            return response;
        }
        else
        {
            return super.fetchSearchResponse();
        }
    }

    @Override
    protected String buildServiceUrl()
    {
        return queryUrl + StringUtils.urlEncodeWrapException(query, "UTF-8")
            + "&format=xml&num=" + results;
    }

    @Override
    protected IResource getXsltResource()
    {
        return new ClassResource(GoogleDesktopDocumentSource.class,
            "google-desktop-to-c2.xsl");
    }

    @Override
    protected void afterFetch(SearchEngineResponse response)
    {
        clean(response, keepHighlights, Document.TITLE, Document.SUMMARY);
    }

    /**
     * Tries to retrieve the query url from Windows registry. Returns <code>null</code> in
     * case of failure or non-Windows environment.
     */
    static String getQueryUrlFromRegistry()
    {
        /*
         * Should we exclude Vista here (because it will tend to show this blocking
         * hack-prevention screen (User Account Control; UAC).
         */
        if (!org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS)
        {
            return null;
        }

        final String command = "reg query \"HKCU\\Software\\Google\\Google Desktop\\API\" /v search_url";
        final String stringSymbol = "REG_SZ";

        try
        {
            final Process process = Runtime.getRuntime().exec(command);

            final InputStream is = process.getInputStream();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            new Thread()
            {
                public void run()
                {
                    try
                    {
                        StreamUtils.copyAndClose(is, baos, 1024 * 4);
                    }
                    catch (IOException e)
                    {
                        // Ignore.
                    }
                }
            }.start();

            process.waitFor();

            final String result = new String(baos.toByteArray(), "UTF-8");

            final int p = result.indexOf(stringSymbol);
            if (p == -1)
            {
                return null;
            }

            return result.substring(p + stringSymbol.length()).trim();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
