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
import org.carrot2.TestBase;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant2;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TfTermWeighting;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import org.junit.Before;

/** A base class for tests requiring that the main term-document document matrix is built. */
public class TermDocumentMatrixBuilderTestBase extends TestBase {
  /** Matrix builder */
  protected TermDocumentMatrixBuilder matrixBuilder;

  /** VSM processing context with all the data */
  protected VectorSpaceModelContext vsmContext;

  protected CompletePreprocessingPipeline preprocessingPipeline;

  protected String queryHint;

  @Before
  public void setUpMatrixBuilder() {
    preprocessingPipeline = new CompletePreprocessingPipeline();
    preprocessingPipeline.labelFilters.minLengthLabelFilter = null;

    matrixBuilder = new TermDocumentMatrixBuilder();
    matrixBuilder.termWeighting = new TfTermWeighting();
    matrixBuilder.maxWordDf.set(1.0);
    matrixBuilder.boostFields.set("title");
  }

  protected PreprocessingContext buildTermDocumentMatrix(Stream<? extends Document> documents) {
    LanguageComponents languageComponents =
        CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant2.NAME);

    PreprocessingContext context =
        preprocessingPipeline.preprocess(documents, queryHint, languageComponents);

    vsmContext = new VectorSpaceModelContext(context);
    matrixBuilder.buildTermDocumentMatrix(vsmContext);
    matrixBuilder.buildTermPhraseMatrix(vsmContext);

    return context;
  }

  protected static Stream<TestDocument> createDocumentsWithTitles(String... content) {
    return Stream.of(content).map(TestDocument::new);
  }
}
