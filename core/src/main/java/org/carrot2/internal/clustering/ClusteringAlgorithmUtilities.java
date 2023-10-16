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
package org.carrot2.internal.clustering;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.carrot2.attrs.AttrGroup;
import org.carrot2.attrs.AttrObject;
import org.carrot2.language.EphemeralDictionaries;

public class ClusteringAlgorithmUtilities {
  public static void registerDictionaries(
      AttrGroup attributes,
      Supplier<EphemeralDictionaries> getter,
      Consumer<EphemeralDictionaries> setter) {
    attributes.register(
        "dictionaries",
        AttrObject.builder(EphemeralDictionaries.class)
            .label("Per-request overrides of language components")
            .getset(getter, setter)
            .defaultValue(EphemeralDictionaries::new));
  }
}
