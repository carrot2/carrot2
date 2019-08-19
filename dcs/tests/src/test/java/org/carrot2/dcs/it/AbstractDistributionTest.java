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
package org.carrot2.dcs.it;

import java.nio.file.Path;
import org.carrot2.RestoreFolderStateRule;
import org.carrot2.TestBase;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;

public abstract class AbstractDistributionTest extends TestBase {
  @ClassRule public static DistMirrorRule createTempDistMirror = new DistMirrorRule();

  protected final RestoreFolderStateRule restorePristineState;

  @Rule public RuleChain testRuleChain;

  protected AbstractDistributionTest() {
    restorePristineState = createTempDistMirror.createRestoreRule();
    testRuleChain = RuleChain.outerRule(restorePristineState);
  }

  protected final Path getDistributionDir() {
    return restorePristineState.getMirror();
  }
}
