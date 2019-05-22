/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
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
