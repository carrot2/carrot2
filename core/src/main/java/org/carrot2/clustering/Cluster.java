/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** A cluster is a named group of somehow related entities. */
public class Cluster<T> {
  /** Labels describing this cluster's documents. */
  private List<String> labels = new ArrayList<>();

  /** Documents contained in this cluster. */
  private List<T> documents = new ArrayList<>();

  /** Sub-clusters of this cluster, if any. */
  private List<Cluster<T>> clusters = new ArrayList<>();

  /** This cluster's "score", interpretation left to algorithms. */
  private Double score;

  /**
   * @return Returns a list of labels describing this cluster.
   */
  public List<String> getLabels() {
    return labels;
  }

  /** Add a single label to this cluster. */
  public Cluster<T> addLabel(String label) {
    labels.add(label);
    return this;
  }

  // fragment-start{get-documents}
  /** Returns all documents that belong directly to this cluster. */
  public List<T> getDocuments() {
    return documents;
  }

  // fragment-end{get-documents}

  /** Add a single document to this cluster. */
  public Cluster<T> addDocument(T document) {
    this.documents.add(document);
    return this;
  }

  // fragment-start{get-clusters}
  /** Returns all child clusters belonging to this cluster. */
  public List<Cluster<T>> getClusters() {
    return clusters;
  }

  // fragment-end{get-clusters}

  /** Adds a child cluster to this cluster. */
  public Cluster<T> addCluster(Cluster<T> cluster) {
    this.clusters.add(cluster);
    return this;
  }

  /** Returns this cluster's score or null, if not available. */
  public Double getScore() {
    return score;
  }

  /** Sets this cluster's score. */
  public Cluster<T> setScore(Double score) {
    this.score = score;
    return this;
  }

  @Override
  public String toString() {
    return String.format(
        Locale.ROOT,
        "[\"%s\", docs: %,d%s]",
        String.join(", ", getLabels()),
        getDocuments().size(),
        getScore() == null ? "" : String.format(Locale.ROOT, ", score: %.2f", getScore()));
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && getClass().equals(obj.getClass()) && equals((Cluster<?>) obj);
  }

  private boolean equals(Cluster<?> that) {
    return Objects.equals(this.getLabels(), that.getLabels())
        && Objects.equals(this.getDocuments(), that.getDocuments())
        && Objects.equals(this.getClusters(), that.getClusters())
        && Objects.equals(this.getScore(), that.getScore());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getLabels(), getDocuments(), getClusters(), getScore());
  }
}
