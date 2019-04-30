
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
import java.util.Locale;
import java.util.Objects;

/**
 * A cluster is a named group of somehow related entities.
 */
public class Cluster<T> {
  /**
   * Labels describing this cluster's documents.
   */
  private List<String> labels = new ArrayList<>();

  /**
   * Documents contained in this cluster.
   */
  private List<T> documents = new ArrayList<>();

  /**
   * Subclusters of this cluster, if any.
   */
  private List<Cluster<T>> subclusters = new ArrayList<>();

  /**
   * This cluster's "score", interpretation left to algorithms.
   */
  private Double score;

  /**
   * @return Returns a list of labels describing this cluster.
   */
  public List<String> getLabels() {
    return labels;
  }

  /**
   * Add a single label to this cluster.
   */
  public Cluster addLabel(String label) {
    labels.add(label);
    return this;
  }

  /**
   * Returns all documents that belong directly to this cluster.
   */
  public List<T> getDocuments() {
    return documents;
  }

  /**
   * Add a single document to this cluster.
   */
  public Cluster<T> addDocument(T document) {
    this.documents.add(document);
    return this;
  }

  /**
   * Returns all subclusters of this cluster.
   */
  public List<Cluster<T>> getSubclusters() {
    return subclusters;
  }

  /**
   * Adds subclusters to this cluster.
   */
  public Cluster<T> addSubcluster(Cluster<T> cluster) {
    this.subclusters.add(cluster);
    return this;
  }

  /**
   * Returns this cluster's score or null, if not available.
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

  @Override
  public String toString() {
    return String.format(Locale.ROOT,
                         "[\"%s\", docs: %,d%s]",
                         String.join(", ", getLabels()),
                         getDocuments().size(),
                         getScore() == null ? "" : String.format(Locale.ROOT, ", score: %.2f", getScore().doubleValue()));
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null &&
        getClass().equals(obj.getClass()) &&
        equals((Cluster<?>) obj);
  }

  private boolean equals(Cluster<?> that) {
    return Objects.equals(this.labels, that.labels) &&
        Objects.equals(this.documents, that.documents) &&
        Objects.equals(this.subclusters, that.subclusters) &&
        Objects.equals(this.score, that.score);
  }

  @Override
  public int hashCode() {
    return Objects.hash(labels, documents, subclusters, score);
  }
}
