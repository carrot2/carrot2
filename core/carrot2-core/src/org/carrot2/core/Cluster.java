
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.carrot2.core.attribute.AttributeNames;

import com.google.common.base.Joiner;

/**
 * A cluster (group) of {@link Document}s. Each cluster has a human-readable label
 * consisting of one or more phrases, a list of documents it contains and a list of its
 * subclusters. Optionally, additional attributes can be associated with a cluster, e.g.
 * {@link #OTHER_TOPICS}.
 */
public final class Cluster extends AttributeSet
{
    /**
     * Indicates that the cluster is an <i>Other Topics</i> cluster. Such a pseudo-cluster
     * contains documents that didn't fit anywhere else at the given level of 
     * cluster hierarchy.
     * <p>
     * Type of this attribute is {@link Boolean}.
     * </p>
     */
    public static final String OTHER_TOPICS = "other-topics";

    /**
     * Default English label for the <i>Other Topics</i> cluster.
     */
    public static final String OTHER_TOPICS_LABEL = "Other Topics";

    /**
     * The score of this cluster indicating its "quality". 
     * The exact semantics of the score varies across algorithms and is not normalized.
     * 
     * <p>
     * Type of this attribute is {@link Double}.
     * </p>
     */
    public static final String SCORE = "score";

    /**
     * The identifier of this cluster.
     * 
     * <p>
     * Type of this attribute is {@link Integer}.
     * </p>
     */
    public static final String ID = "id";

    // NOCOMMIT: add docs
    public static final String PHRASES = "phrases";

    // NOCOMMIT: add docs
    public static final String DOCUMENTS = AttributeNames.DOCUMENTS;

    // NOCOMMIT: add docs
    public static final String CLUSTERS = AttributeNames.CLUSTERS;

    /**
     * Create a cluster with the given set of attributes. 
     */
    public Cluster(Map<String, Object> attributes)
    {
        super(attributes);

        assert checkListContainsOnly(attributes.get(DOCUMENTS), Document.class) &&
               checkListContainsOnly(attributes.get(CLUSTERS), Cluster.class) &&
               checkListContainsOnly(attributes.get(PHRASES), String.class);
    }

    /**
     * Internal identifier of this cluster within the {@link ProcessingResult}. This
     * identifier is assigned dynamically after clusters are passed to
     * {@link ProcessingResult}.
     * 
     * @see ProcessingResult
     */
    public Integer getId()
    {
        return getAttribute(ID, Integer.class);
    }

    /**
     * Returns this cluster's label. If there is more than one phrase describing this
     * cluster they will be separated by a comma followed by a space, e.g. "Phrase
     * one, Phrase two".
     * 
     * @return formatted label of this cluster
     */
    // NOCOMMIT: this should be moved to an external label formatter accepting a cluster.
    public String getLabel()
    {
        return Joiner.on(", ").join(getPhrases());
    }

    /**
     * Returns all phrases describing this cluster.
     */
    @SuppressWarnings("unchecked")
    public List<String> getPhrases()
    {
        return (List<String>) getAttribute(PHRASES);
    }

    /**
     * Returns all subclusters of this cluster or an empty list if none.
     */
    @SuppressWarnings("unchecked")
    public List<Cluster> getSubclusters()
    {
        List<Cluster> subclusters = (List<Cluster>) getAttribute(CLUSTERS);
        return subclusters != null ? subclusters : Collections.<Cluster> emptyList();
    }

    /**
     * Returns all documents contained in this cluster or an empty list if none.
     */
    @SuppressWarnings("unchecked")
    public List<Document> getDocuments()
    {
        List<Document> docs = (List<Document>) getAttribute(DOCUMENTS);
        return (docs != null ? docs : Collections.<Document> emptyList());
    }

    /**
     * Returns the score of this cluster (or <code>null</code> if not present).
     */
    public Double getScore()
    {
        return getAttribute(SCORE, Double.class);
    }
    
    /**
     * Returns all documents contained in this cluster and (recursively) all documents
     * from this cluster's subclusters. The returned list contains unique documents, i.e.
     * if a document is attached to multiple subclusters of this cluster, it
     * will appear only once on the list.
     */
    // NOCOMMIT: this somehow doesn't fit here.
    public List<Document> getAllDocuments()
    {
        return new ArrayList<>(collectAllDocuments(this, new LinkedHashSet<Document>()));
    }

    /**
     * Returns <code>true</code> if this cluster is the {@link #OTHER_TOPICS} cluster.
     */
    public boolean isOtherTopics()
    {
        final Boolean otherTopics = getAttribute(OTHER_TOPICS, Boolean.class);
        return otherTopics != null && otherTopics.booleanValue();
    }

    /**
     * A recursive routine for collecting unique documents from this cluster and subclusters.
     */
    private static Set<Document> collectAllDocuments(Cluster cluster, Set<Document> docs)
    {
        if (cluster == null)
        {
            return docs;
        }

        docs.addAll(cluster.getDocuments());

        final List<Cluster> subclusters = cluster.getSubclusters();
        for (final Cluster subcluster : subclusters)
        {
            collectAllDocuments(subcluster, docs);
        }

        return docs;
    }

    @Override
    public String toString()
    {
        return "[Cluster, label: " + getLabel() + ", docs: " + getAllDocuments().size() + ", subclusters: " + getSubclusters().size() + "]";
    }
}
