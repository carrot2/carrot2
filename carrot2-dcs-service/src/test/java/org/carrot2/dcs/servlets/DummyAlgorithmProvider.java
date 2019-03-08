package org.carrot2.dcs.servlets;

import org.carrot2.attrs.AttrVisitor;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.clustering.Document;
import org.carrot2.language.LanguageComponents;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DummyAlgorithmProvider implements ClusteringAlgorithmProvider<DummyAlgorithmProvider.DummyAlgorithm> {
  static class DummyAlgorithm implements ClusteringAlgorithm {
    @Override
    public <T extends Document> List<Cluster<T>> cluster(Stream<? extends T> documents, LanguageComponents languageComponents) {
      List<? extends T> docs = documents.collect(Collectors.toList());

      if (docs.isEmpty()) {
        return Collections.emptyList();
      } else {
        Cluster<T> c = new Cluster<T>();
        c.addLabel("Leading docs");
        docs.stream().limit(5).forEachOrdered(c::addDocument);
        return Collections.singletonList(c);
      }
    }

    @Override
    public void accept(AttrVisitor visitor) {
      // No custom attributes.
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
