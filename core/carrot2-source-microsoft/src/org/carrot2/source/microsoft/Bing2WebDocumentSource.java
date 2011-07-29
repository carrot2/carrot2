package org.carrot2.source.microsoft;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.attribute.*;

import com.google.common.base.Strings;

/**
 * Web search specific document source fetching from Bing using Bing2 API. 
 */
@Bindable(prefix = "Bing2WebDocumentSource")
public class Bing2WebDocumentSource extends Bing2DocumentSource
{
    /** Web search specific metadata. */
    final static MultipageSearchEngineMetadata metadata = 
        new MultipageSearchEngineMetadata(50, 950);

    /**
     * Miscellaneous Web-request specific options. Bing provides the following options:
     * <ul>
     * <li>DisableHostCollapsing</li>
     * <li>DisableQueryAlterations</li>
     * </ul>
     * 
     * <p>Options should be space-separated.</p>
     * 
     * @label Web Request Options
     * @group Miscellaneous
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @Internal
    public String webOptions;

    /**
     * Specify the allowed file types. Space-separated list of file extensions (upper-case). 
     * See <a href="http://msdn.microsoft.com/en-us/library/dd250876%28v=MSDN.10%29.aspx">Bing documentation</a>.
     * 
     * @label File Types
     * @group Results filtering
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    @Internal
    public String fileTypes;

    /**
     * Site restriction to return results under a given URL. 
     * Example: <tt>http://www.wikipedia.com</tt> or simply <tt>wikipedia.com</tt>.
     * 
     * @label Site restriction
     * @group Results filtering
     * @level Advanced
     */
    @Processing
    @Input
    @Attribute
    public String site;
    
    /**
     * Initialize source type properly.
     */
    public Bing2WebDocumentSource()
    {
        super.sourceType = SourceType.WEB;
    }
    
    /**
     * Process the query.
     */
    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata, getSharedExecutor(MAX_CONCURRENT_THREADS, getClass()));
    }
    
    @Override
    protected void appendSourceParams(ArrayList<NameValuePair> params)
    {
        super.appendSourceParams(params);
        addIfNotEmpty(params, "Web.Options", webOptions);
        addIfNotEmpty(params, "Web.FileType", fileTypes);

        if (!Strings.isNullOrEmpty(site))
        {
            addToQuery(params, " site:" + site);
        }
    }

    private void addToQuery(ArrayList<NameValuePair> params, String value)
    {
        String query = null;
        for (NameValuePair nv : params)
        {
            if (nv.getName().equals("Query"))
            {
                params.remove(nv);
                query = nv.getValue();
                break;
            }
        }

        addIfNotEmpty(params, "Query", Strings.nullToEmpty(query) + value);        
    }
}
