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
import org.carrot2.language.LanguageComponents;

@FunctionalInterface
public interface ContextPreprocessor {
  PreprocessingContext preprocess(
      Stream<? extends Document> documents, String query, LanguageComponents langModel);
}
