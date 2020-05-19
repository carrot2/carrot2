/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.servlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.clustering.Document;
import org.carrot2.language.LanguageComponents;

public class DummyAlgorithmProvider implements ClusteringAlgorithmProvider {
  static class DummyAlgorithm extends AttrComposite implements ClusteringAlgorithm {
    AttrInteger groupSize =
        attributes.register("groupSize", AttrInteger.builder().min(1).defaultValue(5));

    @Override
    public Set<Class<?>> requiredLanguageComponents() {
      return Collections.emptySet();
    }

    @Override
    public <T extends Document> List<Cluster<T>> cluster(
        Stream<? extends T> documents, LanguageComponents languageComponents) {
      List<? extends T> docs = documents.collect(Collectors.toList());

      List<Cluster<T>> clusters = new ArrayList<>();

      Supplier<Cluster<T>> newCluster =
          () -> {
            Cluster<T> c = new Cluster<>();
            c.addLabel("Group " + (clusters.size() + 1));
            return c;
          };

      Cluster<T> c = newCluster.get();
      for (T doc : docs) {
        c.addDocument(doc);
        if (c.getDocuments().size() >= groupSize.get()) {
          clusters.add(c);
          c = newCluster.get();
        }
      }
      if (c.getDocuments().size() > 0) {
        clusters.add(c);
      }

      return clusters;
    }
  }

  @Override
  public String name() {
    return "Dummy";
  }

  @Override
  public DummyAlgorithm get() {
    return new DummyAlgorithm();
  }
}
