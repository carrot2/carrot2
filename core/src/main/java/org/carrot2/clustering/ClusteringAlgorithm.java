package org.carrot2.clustering;

import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.language.LanguageComponents;

import java.util.List;
import java.util.stream.Stream;

public interface ClusteringAlgorithm extends AcceptingVisitor {
  <T extends Document> List<Cluster<T>> cluster(Stream<? extends T> documents, LanguageComponents languageComponents);
}
