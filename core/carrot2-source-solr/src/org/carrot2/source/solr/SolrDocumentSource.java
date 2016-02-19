
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

package org.carrot2.source.solr;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.xml.RemoteXmlSimpleSearchEngineBase;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.resource.ClassLoaderResource;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.FileResource;
import org.carrot2.util.resource.IResource;

import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.base.Strings;
import org.carrot2.shaded.guava.common.collect.Iterables;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.shaded.guava.common.collect.Sets;

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
     * Filter query appended to {@link #serviceUrlBase}.
     */
    @Input
    @Init
    @Processing
    @Attribute
    @Label("Filter query")
    @Level(AttributeLevel.MEDIUM)
    @Group(SERVICE)
    public String solrFilterQuery = "";

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
     * Document identifier field name (specified in Solr schema). This field is necessary
     * to connect Solr-side clusters or highlighter output to documents. 
     */
    @Input
    @Processing
    @Attribute
    @Label("ID field name")
    @Level(AttributeLevel.MEDIUM)
    @Group(FIELD_MAPPING)
    public String solrIdFieldName;

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
    @ImplementingClasses(classes =
    {
        ClassLoaderResource.class,
        FileResource.class
    }, strict = false)
    public IResource solrXsltAdapter;

    /**
     * If clusters are present in the Solr output they will be read and exposed to components
     * further down the processing chain. Note that {@link #solrIdFieldName} is required to match
     * document references.
     */
    @Input
    @Init 
    @Processing
    @Attribute
    @Label("Read Solr clusters if present")
    @Level(AttributeLevel.BASIC)
    @Group(FIELD_MAPPING)
    public boolean readClusters = false;

    /**
     * If highlighter fragments are present in the Solr output they will be used (and preferred) over full
     * field content. This may be used to decrease the memory required for clustering. In general if highlighter
     * is used the contents of full fields won't be emitted from Solr though (because it makes little sense).
     * 
     * <p>Setting this option to <code>false</code> will disable using the highlighter output
     * entirely.</p>
     */
    @Input
    @Init 
    @Processing
    @Attribute
    @Label("Use highlighter output if present")
    @Level(AttributeLevel.BASIC)
    @Group(FIELD_MAPPING)
    public boolean useHighlighterOutput = true;

    /**
     * Copy Solr fields from the search result to Carrot2 {@link org.carrot2.core.Document} instances (as fields).
     */
    @Input
    @Init 
    @Processing
    @Attribute
    @Label("Copy Solr document fields")
    @Level(AttributeLevel.ADVANCED)
    @Group(FIELD_MAPPING)
    public boolean copyFields = false;

    /**
     * If {@link #readClusters} is <code>true</code> and clusters are present in the input
     * XML, they will be deserialized and exposed to components further down the processing
     * chain.
     */
    @Processing
    @Input @Output
    @Internal
    @Attribute(key = AttributeNames.CLUSTERS)
    @Label("Clusters")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.RESULT_INFO)
    public List<Cluster> clusters;

    @Override
    protected void afterFetch(SearchEngineResponse response,
        ProcessingResult processingResult)
    {
        if (readClusters) {
            final Set<String> ids = Sets.newHashSet();
            List<Document> documents = processingResult.getDocuments();
            if (documents == null) documents = Collections.emptyList();
            List<Cluster> clusters = processingResult.getClusters();
            if (documents != null && clusters != null) {
                for (Document doc : documents) {
                    ids.add(doc.getStringId());
                }
    
                Predicate<Document> docFilter = new Predicate<Document>()
                {
                    @Override
                    public boolean apply(Document input)
                    {
                        return input != null && ids.contains(input.getStringId());
                    }
                };
                this.clusters = sanityCheck(clusters, docFilter);
            }
        }
    }

    @Override
    protected String buildServiceUrl()
    {
        return serviceUrlBase 
            + (serviceUrlBase.contains("?") ? "&" : "?")
            + "q=" + urlEncode(query)
            + (Strings.isNullOrEmpty(solrFilterQuery) ? "" : "&fq=" + urlEncode(solrFilterQuery))
            + "&start=" + start
            + "&rows=" + results 
            + "&indent=off";
    }

    @Override
    protected IResource getXsltResource()
    {
        if (solrXsltAdapter == null) {
            return new ClassResource(SolrDocumentSource.class, "solr-to-c2.xsl");
        } else {
            return solrXsltAdapter; 
        }
    }

    @Override
    protected Map<String, String> getXsltParameters()
    {
        final Map<String, String> parameters = Maps.newHashMap();

        parameters.put("solr.title-field", solrTitleFieldName);
        parameters.put("solr.summary-field", solrSummaryFieldName);
        parameters.put("solr.url-field", solrUrlFieldName);
        parameters.put("solr.id-field", Strings.nullToEmpty(solrIdFieldName));
        parameters.put("solr.use-highlighter-output", useHighlighterOutput ? "true" : "false");
        parameters.put("solr.copy-fields", copyFields ? "true" : "false");

        return parameters;
    }
    
    private static List<Cluster> sanityCheck(List<Cluster> in, Predicate<Document> docFilter)
    {
        List<Cluster> cloned = Lists.newArrayListWithCapacity(in.size());
        for (Cluster c : in) {
            Cluster c2 = new Cluster();
            c2.addPhrases(c.getPhrases());
            c2.addDocuments(
                Iterables.filter(c.getDocuments(), docFilter));
            c2.addSubclusters(sanityCheck(c.getSubclusters(), docFilter));
            cloned.add(c2);
        }
        return cloned;
    }    
}
