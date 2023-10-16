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
package org.carrot2.util;

import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

/** Test cases for {@link MutableCharArrayUtils}. */
public class MutableCharArrayUtilsTest extends TestBase {
  @Test
  public void toLowerCaseNoReallocation() {
    final MutableCharArray source = new MutableCharArray("ŁÓdŹ");
    final MutableCharArray result = new MutableCharArray("    z");

    Assertions.assertThat(MutableCharArrayUtils.toLowerCase(source, result)).isTrue();
    Assertions.assertThat(result.getBuffer()).isEqualTo("łódźz".toCharArray());
  }

  @Test
  public void toLowerCaseNoWithReallocation() {
    final MutableCharArray source = new MutableCharArray("ŁÓdŹ");
    final MutableCharArray result = new MutableCharArray("abc");

    Assertions.assertThat(MutableCharArrayUtils.toLowerCase(source, result)).isTrue();
    Assertions.assertThat(result.getBuffer()).isEqualTo("łódź".toCharArray());
  }
}
