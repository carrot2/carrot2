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
package org.carrot2.text.preprocessing;

import java.util.stream.Stream;
import org.carrot2.clustering.Document;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.junit.Before;

/**
 * A base class for tests requiring that a dimensionality-reduced term-document document matrix is
 * built.
 */
public abstract class ReducedTermDocumentMatrixBuilderTestBase
    extends TermDocumentMatrixBuilderTestBase {
  protected ReducedVectorSpaceModelContext reducedVsmContext;
  protected TermDocumentMatrixReducer reducer;

  @Before
  public void setUpMatrixReducer() {
    reducer = new TermDocumentMatrixReducer();
  }

  protected void buildReducedTermDocumentMatrix(Stream<? extends Document> documents) {
    PreprocessingContext ctx = buildTermDocumentMatrix(documents);

    reducedVsmContext = new ReducedVectorSpaceModelContext(vsmContext);
    reducer.reduce(reducedVsmContext, getDimensions(ctx));
  }

  protected abstract int getDimensions(PreprocessingContext ctx);
}
