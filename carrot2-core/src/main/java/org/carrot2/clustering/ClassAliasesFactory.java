package org.carrot2.clustering;

import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.AliasMapperFactory;
import org.carrot2.clustering.kmeans.BisectingKMeansClusteringAlgorithm;
import org.carrot2.clustering.lingo.ClusterBuilder;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.lingo.UniqueLabelAssigner;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.math.matrix.*;
import org.carrot2.text.vsm.LinearTfIdfTermWeighting;
import org.carrot2.text.vsm.LogTfIdfTermWeighting;
import org.carrot2.text.vsm.TfTermWeighting;

public class ClassAliasesFactory implements AliasMapperFactory {
  @Override
  public AliasMapper mapper() {
    return new AliasMapper()
        .alias("Lingo", LingoClusteringAlgorithm.class, LingoClusteringAlgorithm::new)
        .alias("Bisecting K-Means", BisectingKMeansClusteringAlgorithm.class, BisectingKMeansClusteringAlgorithm::new)
        .alias("STC", STCClusteringAlgorithm.class, STCClusteringAlgorithm::new)

        .alias("UniqueLabelAssigner", UniqueLabelAssigner.class, UniqueLabelAssigner::new)
        .alias("ClusterBuilder", ClusterBuilder.class, ClusterBuilder::new)

        .alias("TfTermWeighting", TfTermWeighting.class, TfTermWeighting::new)
        .alias("LinearTfIdfTermWeighting", LinearTfIdfTermWeighting.class, LinearTfIdfTermWeighting::new)
        .alias("LogTfIdfTermWeighting", LogTfIdfTermWeighting.class, LogTfIdfTermWeighting::new)

        .alias("KMeansMatrixFactorizationFactory", KMeansMatrixFactorizationFactory.class, KMeansMatrixFactorizationFactory::new)
        .alias("LocalNonnegativeMatrixFactorizationFactory", LocalNonnegativeMatrixFactorizationFactory.class, LocalNonnegativeMatrixFactorizationFactory::new)
        .alias("NonnegativeMatrixFactorizationEDFactory", NonnegativeMatrixFactorizationEDFactory.class, NonnegativeMatrixFactorizationEDFactory::new)
        .alias("NonnegativeMatrixFactorizationKLFactory", NonnegativeMatrixFactorizationKLFactory.class, NonnegativeMatrixFactorizationKLFactory::new)
        .alias("PartialSingularValueDecompositionFactory", PartialSingularValueDecompositionFactory.class, PartialSingularValueDecompositionFactory::new);
  }

  @Override
  public String name() {
    return "lingo";
  }
}
