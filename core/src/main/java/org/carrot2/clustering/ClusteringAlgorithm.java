package org.carrot2.clustering;

import java.util.List;
import java.util.stream.Stream;
import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.language.LanguageComponents;

public interface ClusteringAlgorithm extends AcceptingVisitor {
  boolean supports(LanguageComponents languageComponents);

  <T extends Document> List<Cluster<T>> cluster(
      Stream<? extends T> documents, LanguageComponents languageComponents);
}
