package org.carrot2.examples;

import java.util.List;
import org.carrot2.clustering.Cluster;

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
