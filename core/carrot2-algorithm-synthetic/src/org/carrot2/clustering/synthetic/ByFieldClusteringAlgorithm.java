
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

package org.carrot2.clustering.synthetic;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.NotBlank;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Clusters documents into a flat structure based on the values of some field of the
 * documents. By default the {@link Document#SOURCES} field is used.
 */
@Bindable(prefix = "ByAttributeClusteringAlgorithm", inherit = CommonAttributes.class)
@Label("By Attribute Clustering")
public class ByFieldClusteringAlgorithm extends ProcessingComponentBase implements
    IClusteringAlgorithm
{
    /**
     * Documents to cluster.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS, inherit = true)
    public List<Document> documents;

    /**
     * Clusters created by the algorithm.
     */
    @Processing
    @Output
    @Internal
    @Attribute(key = AttributeNames.CLUSTERS, inherit = true)
    public List<Cluster> clusters = null;

    /**
     * Name of the field to cluster by. Each non-null scalar field value with distinct
     * hash code will give rise to a single cluster, named using the
     * value returned by {@link #buildClusterLabel(Object)}. If the field value is a collection,
     * the document will be assigned to all clusters corresponding to the values in the
     * collection. Note that arrays will not be 'unfolded' in this way.
     */
    @Processing
    @Input
    @Attribute
    @Required
    @NotBlank
    @Level(AttributeLevel.BASIC)
    @Group("Fields")
    @Label("Field name")
    public String fieldName = Document.SOURCES;

    /**
     * Performs by URL clustering.
     */
    @Override
    public void process() throws ProcessingException
    {
        final Map<Object, Cluster> clusterMap = Maps.newHashMap();
        for (Document document : documents)
        {
            final Object field = document.getField(fieldName);
            if (field instanceof Collection<?>)
            {
                for (Object value : (Collection<?>) field)
                {
                    addToCluster(clusterMap, value, document);
                }
            }
            else
            {
                addToCluster(clusterMap, field, document);
            }
        }
        
        clusters = Lists.newArrayList(clusterMap.values());
        Collections.sort(clusters, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR);
        Cluster.appendOtherTopics(documents, clusters);
    }

    private void addToCluster(Map<Object, Cluster> clusters, Object fieldValue,
        Document document)
    {
        if (fieldValue == null)
        {
            return;
        }

        Cluster cluster = clusters.get(fieldValue);
        if (cluster == null)
        {
            cluster = new Cluster();
            cluster.addPhrases(buildClusterLabel(fieldValue));
            clusters.put(fieldValue, cluster);
        }

        cluster.addDocuments(document);
    }

    /**
     * Builds cluster label based on the field value. This implementation returns
     * <code>fieldValue.toString()</code>.
     */
    protected String buildClusterLabel(Object fieldValue)
    {
        return fieldValue.toString();
    }
}
