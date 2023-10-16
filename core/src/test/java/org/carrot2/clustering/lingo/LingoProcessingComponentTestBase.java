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
package org.carrot2.clustering.lingo;

import java.util.stream.Stream;
import org.carrot2.clustering.Document;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.ReducedTermDocumentMatrixBuilderTestBase;

/** Test cases for cluster merging in {@link ClusterBuilder}. */
public class LingoProcessingComponentTestBase extends ReducedTermDocumentMatrixBuilderTestBase {
  protected LingoProcessingContext lingoContext;
  protected int desiredClusterCountBase = 0;

  protected void buildLingoModel(Stream<? extends Document> documents) {
    buildReducedTermDocumentMatrix(documents);
    lingoContext = new LingoProcessingContext(reducedVsmContext);
  }

  @Override
  protected int getDimensions(PreprocessingContext ctx) {
    return LingoClusteringAlgorithm.computeClusterCount(desiredClusterCountBase, ctx.documentCount);
  }
}
