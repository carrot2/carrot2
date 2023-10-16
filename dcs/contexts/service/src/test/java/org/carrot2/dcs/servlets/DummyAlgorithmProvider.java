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
package org.carrot2.dcs.servlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.clustering.Document;
import org.carrot2.internal.clustering.ClusteringAlgorithmUtilities;
import org.carrot2.language.EphemeralDictionaries;
import org.carrot2.language.LabelFilter;
import org.carrot2.language.LanguageComponents;

public class DummyAlgorithmProvider implements ClusteringAlgorithmProvider {
  static class DummyAlgorithm extends AttrComposite implements ClusteringAlgorithm {
    AttrInteger groupSize =
        attributes.register("groupSize", AttrInteger.builder().min(1).defaultValue(5));

    EphemeralDictionaries dictionaries;

    {
      ClusteringAlgorithmUtilities.registerDictionaries(
          attributes, () -> dictionaries, (v) -> dictionaries = v);
    }

    @Override
    public Set<Class<?>> requiredLanguageComponents() {
      return Collections.emptySet();
    }

    @Override
    public <T extends Document> List<Cluster<T>> cluster(
        Stream<? extends T> documents, LanguageComponents languageComponents) {

      if (dictionaries != null) {
        languageComponents = dictionaries.override(languageComponents);
      }

      LabelFilter labelFilter = languageComponents.get(LabelFilter.class);

      List<Cluster<T>> clusters = new ArrayList<>();
      Consumer<Cluster<T>> addToClusters =
          (c) -> {
            if (c.getDocuments().size() > 0 && c.getLabels().stream().allMatch(labelFilter)) {
              clusters.add(c);
            }
          };

      AtomicInteger groupCount = new AtomicInteger();
      Supplier<Cluster<T>> newCluster =
          () -> {
            Cluster<T> c = new Cluster<>();
            c.addLabel("Group " + (groupCount.incrementAndGet()));
            return c;
          };

      List<? extends T> docs = documents.collect(Collectors.toList());
      Cluster<T> c = newCluster.get();
      for (T doc : docs) {
        c.addDocument(doc);
        if (c.getDocuments().size() >= groupSize.get()) {
          addToClusters.accept(c);
          c = newCluster.get();
        }
      }
      addToClusters.accept(c);

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
