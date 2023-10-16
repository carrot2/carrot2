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

import java.util.function.BiConsumer;

/**
 * A representation of a single document for clustering. The document must provide named fields to a
 * clustering algorithm. The fields are only required once, so any content may be cleared after the
 * visitor returns.
 */
public interface Document {
  /**
   * An implementation of this method must present <code>fieldConsumer</code> with each field's name
   * and its corresponding value. The same field can be presented more than once if it has multiple
   * values.
   */
  // fragment-start{visitor-method}
  void visitFields(BiConsumer<String, String> fieldConsumer);
  // fragment-end{visitor-method}
}
