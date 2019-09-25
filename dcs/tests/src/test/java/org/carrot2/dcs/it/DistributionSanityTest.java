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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DistributionSanityTest extends AbstractDistributionTest {
  @Test
  public void checkRequiredFiles() {
    for (String file : Arrays.asList("carrot2.LICENSE", "dcs.cmd", "dcs.sh"))
      Assertions.assertThat(getDistributionDir().resolve(file)).isRegularFile();
  }

  @Test
  public void checkShEols() throws IOException {
    String content =
        new String(
            Files.readAllBytes(getDistributionDir().resolve("dcs.sh")), StandardCharsets.UTF_8);
    if (content.indexOf('\r') >= 0) {
      throw new AssertionError("dcs.sh contains carriage returns.");
    }
  }
}
