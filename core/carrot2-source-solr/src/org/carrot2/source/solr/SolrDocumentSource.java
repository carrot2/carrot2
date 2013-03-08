
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

package org.carrot2.source.solr;

import java.util.Map;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.xml.RemoteXmlSimpleSearchEngineBase;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.IResource;

import com.google.common.collect.Maps;

/**
 * Fetches documents from an instance of Solr.
 * 
 * @see <a href="http://lucene.apache.org/solr/">Apache SOLR</a>
 */
@Bindable(prefix = "SolrDocumentSource")
public class SolrDocumentSource extends RemoteXmlSimpleSearchEngineBase
{
    protected static final String FIELD_MAPPING = "Index field mapping";
    
    /**
     * Solr service URL base. The URL base can contain additional Solr parameters, 
     * for example: <tt>http://localhost:8983/solr/select?fq=timestemp:[NOW-24HOUR TO NOW]</tt>
     */
    @Input
    @Processing
    @Attribute
    @Label("Service URL")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)
    public String serviceUrlBase = "http://localhost:8983/solr/select";

    /**
     * Title field name. Name of the Solr field that will provide document titles.
     */
    @Input
    @Processing
    @Attribute
    @Label("Title field name")
    @Level(AttributeLevel.MEDIUM)
    @Group(FIELD_MAPPING)
    public String solrTitleFieldName = "title";

    /**
     * Summary field name. Name of the Solr field that will provide document summary.
     */
    @Input
    @Processing
    @Attribute
    @Label("Summary field name")
    @Level(AttributeLevel.MEDIUM)
    @Group(FIELD_MAPPING)
    public String solrSummaryFieldName = "description";

    /**
     * URL field name. Name of the Solr field that will provide document URLs.
     */
    @Input
    @Processing
    @Attribute
    @Label("URL field name")
    @Level(AttributeLevel.MEDIUM)
    @Group(FIELD_MAPPING)
    public String solrUrlFieldName = "url";

    /**
     * Provides a custom XSLT stylesheet for converting from Solr's output to
     * an XML format <a href="http://download.carrot2.org/head/manual/index.html#section.architecture.xml-formats">
     * parsed by Carrot2</a>. For performance reasons this attribute
     * can be provided at initialization time only (no processing-time overrides).  
     */
    @Input
    @Init
    @Attribute
    @Label("Custom XSLT adapter from Solr to Carrot2 format")
    @Level(AttributeLevel.ADVANCED)
    @Group(FIELD_MAPPING)
    public IResource solrXsltAdapter = new ClassResource(SolrDocumentSource.class, "solr-to-c2.xsl");

    @Override
    protected String buildServiceUrl()
    {
        return serviceUrlBase + (serviceUrlBase.contains("?") ? "&" : "?") 
            + "q=" + urlEncode(query) + "&start=" + start + "&rows="
            + results + "&indent=off";
    }

    @Override
    protected IResource getXsltResource()
    {
        return solrXsltAdapter;
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
