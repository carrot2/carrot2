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
