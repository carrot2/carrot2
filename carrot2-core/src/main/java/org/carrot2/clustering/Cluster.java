
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering;

import java.util.ArrayList;
import java.util.List;

/**
 * A cluster is a named group of related {@link Document}s.
 */
public final class Cluster {
    /** Labels describing this cluster's documents. */
    private List<String> labels = new ArrayList<>();

    /** Subclusters of this cluster, if any. */
    private List<Cluster> subclusters = new ArrayList<>();

    /** Documents contained in this cluster. */
    private List<Document> documents = new ArrayList<>();

    private Double score;

    /**
     * Creates a {@link Cluster} with an empty label, no documents and no subclusters.
     */
    public Cluster() {
    }

    public List<String> getLabels() {
        return labels;
    }

    /**
     * Returns all subclusters of this cluster.
     */
    public List<Cluster> getSubclusters() {
        return subclusters;
    }

    /**
     * Returns all documents that belong directly to this cluster.
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Adds phrases to the description of this cluster.
     */
    public Cluster addLabels(Iterable<String> labels)
    {
        labels.forEach(label -> this.labels.add(label));
        return this;
    }

    /**
     * Adds document to this cluster.
     */
    public Cluster addDocuments(Iterable<Document> documents) {
        documents.forEach(doc -> this.documents.add(doc));
        return this;
    }

    public Cluster addDocument(Document document) {
        this.documents.add(document);
        return this;
    }

    /**
     * Adds children clusters to this cluster.
     */
    public Cluster addClusters(Iterable<Cluster> clusters) {
        clusters.forEach(cluster -> this.subclusters.add(cluster));
        return this;
    }

    /**
     * Returns this cluster's score, if available.
     */
    public Double getScore() {
        return score;
    }

    /**
     * Sets this cluster's score.
     */
    public Cluster setScore(Double score) {
        this.score = score;
        return this;
    }
}
