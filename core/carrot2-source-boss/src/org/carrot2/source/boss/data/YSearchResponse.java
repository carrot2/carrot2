package org.carrot2.source.boss.data;

import org.carrot2.core.Document;
import org.carrot2.source.SearchEngineResponse;
import org.simpleframework.xml.*;

/**
 * Search response model for Yahoo Boss.
 */
@Root(name = "ysearchresponse", strict = false)
public final class YSearchResponse
{
    @Attribute(name = "responsecode", required = false)
    public Integer responseCode;

    @Element(name = "nextpage", required = false)
    public String nextPageURI;

    @Element(name = "resultset_web", required = false)
    public WebResultSet webResultSet;

    /**
     * Populate {@link SearchEngineResponse} depending on the type of the search result
     * returned.
     */
    public void populate(SearchEngineResponse response)
    {
        if (webResultSet != null)
        {
            response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                webResultSet.deephits);

            if (webResultSet.results != null)
            {
                for (WebResult result : webResultSet.results)
                {
                    final Document document = new Document(
                        result.title, result.summary, result.url);
    
                    document.addField(Document.CLICK_URL, result.clickURL);
                    document.addField(Document.SIZE, result.size);
    
                    response.results.add(document);
                }
            }
        }
    }
}
