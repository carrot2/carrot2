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

import java.io.IOException;
import java.util.Objects;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.rules.RuleChain;

public abstract class AbstractDcsTest extends AbstractDistributionTest {
  protected static String DCS_SHUTDOWN_TOKEN = "_shutdown_";

  private static DcsService dcsService;

  @AfterClass
  public static void stopDcs() throws IOException {
    if (dcsService != null) {
      dcsService.close();
    }
    dcsService = null;
  }

  @BeforeClass
  public static void startDcs() throws IOException {
    createTempDistMirror.createRestoreRule().restore();
    dcsService = new ForkedDcs(createTempDistMirror.mirrorPath(), DCS_SHUTDOWN_TOKEN);
  }

  protected AbstractDcsTest() {
    // reset rule chain to exclude restoration o pristine state since we'll want the DCS to be
    // initialized once.
    super.testRuleChain = RuleChain.emptyRuleChain();
  }

  protected final DcsService dcs() throws IOException {
    return Objects.requireNonNull(dcsService);
  }
}
