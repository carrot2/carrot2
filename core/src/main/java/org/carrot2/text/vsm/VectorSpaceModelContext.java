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
package org.carrot2.text.vsm;

import com.carrotsearch.hppc.IntIntHashMap;
import org.carrot2.math.mahout.matrix.DoubleMatrix2D;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.PreprocessingContext.AllLabels;
import org.carrot2.text.preprocessing.PreprocessingContext.AllStems;

/** Stores data related to the Vector Space Model of the processed documents. */
public class VectorSpaceModelContext {
  /** Preprocessing context for the underlying documents. */
  public final PreprocessingContext preprocessingContext;

  /**
   * Term-document matrix. Rows of the matrix correspond to word stems, columns correspond to the
   * processed documents. For mapping between rows of this matrix and {@link AllStems}, see {@link
   * #stemToRowIndex}.
   *
   * <p>This matrix is produced by {@link
   * TermDocumentMatrixBuilder#buildTermDocumentMatrix(VectorSpaceModelContext)}.
   */
  public DoubleMatrix2D termDocumentMatrix;

  /**
   * Term-document-like matrix for phrases from {@link AllLabels}. If there are no phrases in {@link
   * AllLabels}, phrase matrix is <code>null</code>. For mapping between rows of this matrix and
   * {@link AllStems}, see {@link #stemToRowIndex}.
   *
   * <p>This matrix is produced by {@link
   * TermDocumentMatrixBuilder#buildTermPhraseMatrix(VectorSpaceModelContext)}.
   */
  public DoubleMatrix2D termPhraseMatrix;

  /**
   * Stem index to row index mapping for the <code>tdMatrix</code>. Keys in this map are indices of
   * entries in {@link AllStems} arrays, values are the indices of <code>tdMatrix</code> rows
   * corresponding to the stems. Please note that depending on the limit on the size of the matrix,
   * some stems may not have their corresponding matrix rows.
   *
   * <p>This object is produced by {@link
   * TermDocumentMatrixBuilder#buildTermDocumentMatrix(VectorSpaceModelContext)}.
   */
  public IntIntHashMap stemToRowIndex;

  /** Creates a vector space model context with the provided preprocessing context. */
  public VectorSpaceModelContext(PreprocessingContext preprocessingContext) {
    this.preprocessingContext = preprocessingContext;
  }
}
