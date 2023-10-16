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

import java.io.IOException;
import org.junit.ClassRule;
import org.junit.rules.RuleChain;

public abstract class AbstractDcsTest extends AbstractDistributionTest {
  public static String DCS_SHUTDOWN_TOKEN = "_shutdown_";

  protected static DcsServiceRule dcsServiceRule;

  @ClassRule
  public static RuleChain subclassRuleChain =
      RuleChain.outerRule(dcsServiceRule = new DcsServiceRule(getDistMirrorRule()));

  protected AbstractDcsTest() {
    // Reset the parent per-test rule chain.
    super.testRuleChain = RuleChain.emptyRuleChain();
  }

  protected final DcsService dcs() throws IOException {
    return dcsServiceRule.getDcsService();
  }
}
