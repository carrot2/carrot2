package org.carrot2.dcs.servlets;

import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.AliasMappingFactory;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;

public class ClassNameAliases implements AliasMappingFactory {
  @Override
  public AliasMapper mapper() {
    return new AliasMapper().alias("Dummy",
        DummyAlgorithmProvider.DummyAlgorithm.class,
        DummyAlgorithmProvider.DummyAlgorithm::new);
  }

  @Override
  public String name() {
    return "Tests";
  }
}
