package org.carrot2.clustering;

import org.carrot2.language.LanguageComponents;

import java.util.List;

public interface ClusteringAlgorithm {
  List<Cluster> cluster(List<Document> documents, LanguageComponents languageModel);
}
