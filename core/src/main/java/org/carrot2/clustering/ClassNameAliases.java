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
package org.carrot2.clustering;

import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.AliasMapperFactory;
import org.carrot2.clustering.kmeans.BisectingKMeansClusteringAlgorithm;
import org.carrot2.clustering.lingo.ClusterBuilder;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.lingo.SimpleLabelAssigner;
import org.carrot2.clustering.lingo.UniqueLabelAssigner;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.language.DefaultDictionaryImpl;
import org.carrot2.language.EphemeralDictionaries;
import org.carrot2.math.matrix.KMeansMatrixFactorizationFactory;
import org.carrot2.math.matrix.LocalNonnegativeMatrixFactorizationFactory;
import org.carrot2.math.matrix.NonnegativeMatrixFactorizationEDFactory;
import org.carrot2.math.matrix.NonnegativeMatrixFactorizationKLFactory;
import org.carrot2.math.matrix.PartialSingularValueDecompositionFactory;
import org.carrot2.text.preprocessing.BasicPreprocessingPipeline;
import org.carrot2.text.preprocessing.CompletePreprocessingPipeline;
import org.carrot2.text.preprocessing.DocumentAssigner;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.filter.CompleteLabelFilter;
import org.carrot2.text.preprocessing.filter.GenitiveLabelFilter;
import org.carrot2.text.preprocessing.filter.MinLengthLabelFilter;
import org.carrot2.text.preprocessing.filter.NumericLabelFilter;
import org.carrot2.text.preprocessing.filter.QueryLabelFilter;
import org.carrot2.text.preprocessing.filter.StopLabelFilter;
import org.carrot2.text.preprocessing.filter.StopWordLabelFilter;
import org.carrot2.text.vsm.LinearTfIdfTermWeighting;
import org.carrot2.text.vsm.LogTfIdfTermWeighting;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.TfTermWeighting;

public class ClassNameAliases implements AliasMapperFactory {
  @Override
  public AliasMapper mapper() {
    return new AliasMapper()
        .alias(
            LingoClusteringAlgorithm.NAME,
            LingoClusteringAlgorithm.class,
            LingoClusteringAlgorithm::new)
        .alias(
            BisectingKMeansClusteringAlgorithm.NAME,
            BisectingKMeansClusteringAlgorithm.class,
            BisectingKMeansClusteringAlgorithm::new)
        .alias(
            STCClusteringAlgorithm.NAME, STCClusteringAlgorithm.class, STCClusteringAlgorithm::new)
        .alias("SimpleLabelAssigner", SimpleLabelAssigner.class, SimpleLabelAssigner::new)
        .alias("UniqueLabelAssigner", UniqueLabelAssigner.class, UniqueLabelAssigner::new)
        .alias(
            "BasicPreprocessingPipeline",
            BasicPreprocessingPipeline.class,
            BasicPreprocessingPipeline::new)
        .alias(
            "CompletePreprocessingPipeline",
            CompletePreprocessingPipeline.class,
            CompletePreprocessingPipeline::new)
        .alias("ClusterBuilder", ClusterBuilder.class, ClusterBuilder::new)
        .alias("DocumentAssigner", DocumentAssigner.class, DocumentAssigner::new)
        .alias("CompleteLabelFilter", CompleteLabelFilter.class, CompleteLabelFilter::new)
        .alias("LabelFilterProcessor", LabelFilterProcessor.class, LabelFilterProcessor::new)
        .alias("GenitiveLabelFilter", GenitiveLabelFilter.class, GenitiveLabelFilter::new)
        .alias("MinLengthLabelFilter", MinLengthLabelFilter.class, MinLengthLabelFilter::new)
        .alias("NumericLabelFilter", NumericLabelFilter.class, NumericLabelFilter::new)
        .alias("QueryLabelFilter", QueryLabelFilter.class, QueryLabelFilter::new)
        .alias("StopLabelFilter", StopLabelFilter.class, StopLabelFilter::new)
        .alias("StopWordLabelFilter", StopWordLabelFilter.class, StopWordLabelFilter::new)
        .alias(
            "TermDocumentMatrixBuilder",
            TermDocumentMatrixBuilder.class,
            TermDocumentMatrixBuilder::new)
        .alias(
            "TermDocumentMatrixReducer",
            TermDocumentMatrixReducer.class,
            TermDocumentMatrixReducer::new)
        .alias("TfTermWeighting", TfTermWeighting.class, TfTermWeighting::new)
        .alias(
            "LinearTfIdfTermWeighting",
            LinearTfIdfTermWeighting.class,
            LinearTfIdfTermWeighting::new)
        .alias("LogTfIdfTermWeighting", LogTfIdfTermWeighting.class, LogTfIdfTermWeighting::new)
        .alias(
            "KMeansMatrixFactorizationFactory",
            KMeansMatrixFactorizationFactory.class,
            KMeansMatrixFactorizationFactory::new)
        .alias(
            "LocalNonnegativeMatrixFactorizationFactory",
            LocalNonnegativeMatrixFactorizationFactory.class,
            LocalNonnegativeMatrixFactorizationFactory::new)
        .alias(
            "NonnegativeMatrixFactorizationEDFactory",
            NonnegativeMatrixFactorizationEDFactory.class,
            NonnegativeMatrixFactorizationEDFactory::new)
        .alias(
            "NonnegativeMatrixFactorizationKLFactory",
            NonnegativeMatrixFactorizationKLFactory.class,
            NonnegativeMatrixFactorizationKLFactory::new)
        .alias(
            "PartialSingularValueDecompositionFactory",
            PartialSingularValueDecompositionFactory.class,
            PartialSingularValueDecompositionFactory::new)
        .alias("EphemeralDictionaries", EphemeralDictionaries.class, EphemeralDictionaries::new)
        .alias("DefaultDictionaryImpl", DefaultDictionaryImpl.class, DefaultDictionaryImpl::new);
  }

  @Override
  public String name() {
    return "Core classes";
  }
}
