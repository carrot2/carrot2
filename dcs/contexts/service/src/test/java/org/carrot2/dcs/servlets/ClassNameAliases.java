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
package org.carrot2.dcs.servlets;

import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.AliasMapperFactory;

public class ClassNameAliases implements AliasMapperFactory {
  @Override
  public AliasMapper mapper() {
    return new AliasMapper()
        .alias(
            "Dummy",
            DummyAlgorithmProvider.DummyAlgorithm.class,
            DummyAlgorithmProvider.DummyAlgorithm::new);
  }

  @Override
  public String name() {
    return "Tests";
  }
}
