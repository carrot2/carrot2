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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.carrot2.attrs.AttrString;

public class SharedInfrastructure {
  public static AttrString queryHintAttribute() {
    return AttrString.builder().label("Query hint").defaultValue(null);
  }

  private static class ClusterData<T> {
    final Cluster<T> cluster;
    final double score;
    final String label;
    final int recursiveDocumentCount;

    public ClusterData(Cluster<T> cluster, double score, int recursiveDocumentCount) {
      this.cluster = cluster;
      this.label = String.join(", ", cluster.getLabels());
      this.score = score;
      this.recursiveDocumentCount = recursiveDocumentCount;
    }
  }

  public static <T> List<Cluster<T>> reorderByWeightedScoreAndSize(
      List<Cluster<T>> clusters, double scoreWeight) {
    Comparator<ClusterData<T>> comparator =
        Comparator.<ClusterData<T>>comparingDouble(data -> data.score)
            .reversed()
            .thenComparing(Comparator.nullsFirst(Comparator.comparing(data -> data.label)));

    return clusters.stream()
        .map(
            cluster -> {
              int docCount = recursiveDocumentCount(cluster);
              double score =
                  Math.pow(docCount, 1d - scoreWeight) * Math.pow(cluster.getScore(), scoreWeight);
              return new ClusterData<T>(cluster, score, docCount);
            })
        .sorted(comparator)
        .map(data -> data.cluster)
        .collect(Collectors.toList());
  }

  public static <T extends Document> List<Cluster<T>> reorderByDescendingSizeAndLabel(
      ArrayList<Cluster<T>> clusters) {
    Comparator<ClusterData<T>> comparator =
        Comparator.<ClusterData<T>>comparingInt(data -> data.recursiveDocumentCount)
            .reversed()
            .thenComparing(Comparator.nullsFirst(Comparator.comparing(data -> data.label)));

    return clusters.stream()
        .map(
            cluster -> {
              int docCount = recursiveDocumentCount(cluster);
              return new ClusterData<T>(cluster, 0, docCount);
            })
        .sorted(comparator)
        .map(data -> data.cluster)
        .collect(Collectors.toList());
  }

  public static int recursiveDocumentCount(Cluster<?> cluster) {
    Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
    ArrayDeque<Cluster<?>> queue = new ArrayDeque<>();
    queue.add(cluster);

    while (!queue.isEmpty()) {
      Cluster<?> c = queue.removeLast();
      visited.addAll(c.getDocuments());
      queue.addAll(cluster.getClusters());
    }

    return visited.size();
  }
}
