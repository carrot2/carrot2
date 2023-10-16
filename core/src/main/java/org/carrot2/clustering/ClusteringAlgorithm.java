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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.language.LanguageComponents;

public interface ClusteringAlgorithm extends AcceptingVisitor {
  /**
   * @return A set of classes required to be present in the {@link LanguageComponents} instance
   *     provided for clustering.
   */
  Set<Class<?>> requiredLanguageComponents();

  /**
   * @return A set of classes used by the algorithm, if present, but optional in {@link
   *     LanguageComponents} instance provided for clustering.
   */
  default Set<Class<?>> optionalLanguageComponents() {
    return Collections.emptySet();
  }

  /**
   * Verify whether a given {@link LanguageComponents} instance contains all the required components
   * for the algorithm to run.
   *
   * @param languageComponents {@link LanguageComponents} to check against.
   * @return {@code true} if the provided {@link LanguageComponents} instance is sufficient for
   *     clustering.
   */
  default boolean supports(LanguageComponents languageComponents) {
    return languageComponents.components().containsAll(requiredLanguageComponents());
  }

  /**
   * Cluster a set of documents.
   *
   * @param documents A stream of {@link Document documents} for clustering.
   * @param languageComponents {@link LanguageComponents} with a set of suppliers for the required
   *     language-specific components.
   * @param <T> Any subclass of {@link Document}. Clusters of objects of the same type are returned.
   * @return A list of top-level clusters (clusters can form a hierarchy via {@link
   *     Cluster#getClusters()}.
   */
  <T extends Document> List<Cluster<T>> cluster(
      Stream<? extends T> documents, LanguageComponents languageComponents);
}
