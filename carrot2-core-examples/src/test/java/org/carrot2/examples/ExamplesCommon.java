package org.carrot2.examples;

import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.Document;

import java.util.List;

class ExamplesCommon {
  static void printClusters(List<Cluster<Document>> clusters, String indent) {
    for (Cluster<Document> c : clusters) {
      System.out.println(indent + c);
      printClusters(c.getSubclusters(), indent + "  ");
    }
  }
}
