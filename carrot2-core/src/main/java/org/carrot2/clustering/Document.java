
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering;

import java.util.function.BiConsumer;

/**
 * A representation of a single document for clustering. The document
 * must provide named fields to a clustering algorithm. The fields are
 * only required once, so any content may be cleared after the visitor
 * returns.
 */
public interface Document {
  void visitFields(BiConsumer<String, String> fieldConsumer);
}
