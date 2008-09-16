package org.carrot2.source.google;

import org.carrot2.core.Document;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.xml.RemoteXmlSimpleSearchEngineBase;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.Resource;

/**
 * Fetches documents from an instance of Google Desktop search engine.
 */
@Bindable
public class GoogleDesktopDocumentSource extends RemoteXmlSimpleSearchEngineBase
{
    /**
     * Query URL. Installation-specific URL at which Google Desktop search service is
     * available. On Windows machines, the URL is available at the
     * <code>HKEY_CURRENT_USER\Software\Google\Google Desktop\API\search_url</code> system
     * registry key. Please consult Google Desktop API documents for further instructions
     * if needed.
     * 
     * @see http://code.google.com/apis/desktop/docs/queryapi.html#httpxml
     */
    @Input
    @Processing
    @Attribute
    public String queryUrl = getQueryUrlFromRegistry();

    /**
     * Keep query word highlighting. Google by default highlights query words in
     * snippets using the bold HTML tag. Set this attribute to <code>true</code> to keep
     * these highlights.
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

            response.results.add(new Document("Could not connect to Google Desktop",
                "Is Google Desktop installed on your machine?", ""));
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
    protected Resource getXsltResource()
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
        final String osName = System.getProperty("os.name");
        if (org.apache.commons.lang.StringUtils.isBlank(osName)
            || !osName.contains("Windows"))
        {
            return null;
        }

        final String command = "reg query \"HKCU\\Software\\Google\\Google Desktop\\API\" /v search_url";
        final String stringSymbol = "REG_SZ";

        try
        {
            final Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            final String result = new String(StreamUtils.readFullyAndClose(process
                .getInputStream()), "UTF-8");

            final int p = result.indexOf(stringSymbol);
            if (p == -1)
            {
                return null;
            }

            return result.substring(p + stringSymbol.length()).trim();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
