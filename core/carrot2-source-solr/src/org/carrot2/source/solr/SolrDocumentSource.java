package org.carrot2.source.solr;

import java.util.Map;

import org.carrot2.core.attribute.Processing;
import org.carrot2.source.xml.RemoteXmlSimpleSearchEngineBase;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.Resource;

import com.google.common.collect.Maps;

/**
 * Fetches documents from an instance of Solr.
 * 
 * @see http://lucene.apache.org/solr/
 */
@Bindable(prefix = "SolrDocumentSource")
public class SolrDocumentSource extends RemoteXmlSimpleSearchEngineBase
{
    /**
     * Solr service URL base.
     * 
     * @label Service URL
     * @level Advanced
     */
    @Input
    @Processing
    @Attribute
    public String serviceUrlBase = "http://localhost:8983/solr/select";

    /**
     * Title field name. Name of the Solr field that will provide document titles.
     * 
     * @level Medium
     */
    @Input
    @Processing
    @Attribute
    public String solrTitleFieldName = "title";

    /**
     * Summary field name. Name of the Solr field that will provide document summary.
     * 
     * @level Medium
     */
    @Input
    @Processing
    @Attribute
    public String solrSummaryFieldName = "description";

    /**
     * Title field name. Name of the Solr field that will provide document URLs.
     * 
     * @level Medium
     */
    @Input
    @Processing
    @Attribute
    public String solrUrlFieldName = "url";

    @Override
    protected String buildServiceUrl()
    {
        return serviceUrlBase + "?q=" + urlEncode(query) + "&start=" + start + "&rows="
            + results + "&indent=off";
    }

    @Override
    protected Resource getXsltResource()
    {
        return new ClassResource(SolrDocumentSource.class, "solr-to-c2.xsl");
    }

    @Override
    protected Map<String, String> getXsltParameters()
    {
        final Map<String, String> parameters = Maps.newHashMap();

        parameters.put("solr.title-field", solrTitleFieldName);
        parameters.put("solr.summary-field", solrSummaryFieldName);
        parameters.put("solr.url-field", solrUrlFieldName);
        
        return parameters;
    }
}
