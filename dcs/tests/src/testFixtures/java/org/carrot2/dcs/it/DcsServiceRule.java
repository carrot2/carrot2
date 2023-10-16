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
package org.carrot2.dcs.it;

import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.rules.TestRuleAdapter;
import java.util.List;
import java.util.Objects;

public class DcsServiceRule extends TestRuleAdapter {
  private final DistMirrorRule distMirrorRule;
  private DcsService dcsService;

  public DcsServiceRule(DistMirrorRule restoreRule) {
    this.distMirrorRule = restoreRule;
  }

  @Override
  protected void before() throws Throwable {
    DcsConfig config =
        new DcsConfig(distMirrorRule.mirrorPath(), AbstractDcsTest.DCS_SHUTDOWN_TOKEN)
            .withGzip(RandomizedTest.randomBoolean());
    dcsService = new ForkedDcs(config);
  }

  @Override
  protected void afterAlways(List<Throwable> errors) throws Throwable {
    if (dcsService != null) {
      dcsService.close();
      dcsService = null;
    }
  }

  public DcsService getDcsService() {
    return Objects.requireNonNull(dcsService);
  }
}
