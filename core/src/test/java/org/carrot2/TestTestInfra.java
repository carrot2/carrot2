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
package org.carrot2;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TestTestInfra extends TestBase {
  @Test
  public void testA() {
    System.out.println("sout testA");
    System.err.println("serr testA");
    Assertions.assertThat(System.getProperty("failOn", "")).doesNotContain("testA");
  }

  @Test
  public void testB() {
    System.out.println("sout testB");
    System.err.println("serr testB");
    Assertions.assertThat(System.getProperty("failOn", "")).doesNotContain("testB");
  }
}
