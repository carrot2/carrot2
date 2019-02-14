package org.carrot2.clustering.kmeans;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.text.linguistic.LanguageModel;

import java.util.List;

public interface ClusteringAlgorithm {
  List<Cluster> cluster(List<Document> documents, LanguageModel languageModel);
}
