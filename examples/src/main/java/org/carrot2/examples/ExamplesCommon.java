package org.carrot2.examples;

import org.carrot2.clustering.Cluster;

import java.util.List;

public class ExamplesCommon {
  public static <T> void printClusters(List<Cluster<T>> clusters) {
    printClusters(clusters, "");
  }

  private static <T> void printClusters(List<Cluster<T>> clusters, String indent) {
    for (Cluster<T> c : clusters) {
      System.out.println(indent + c);
      printClusters(c.getClusters(), indent + "  ");
    }
  }
}
