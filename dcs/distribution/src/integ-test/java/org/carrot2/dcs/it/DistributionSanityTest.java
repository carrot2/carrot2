package org.carrot2.dcs.it;

import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DistributionSanityTest extends AbstractDistributionTest {
  @Test
  public void checkRequiredFiles() {
    for (String file : Arrays.asList("carrot2.LICENSE", "dcs.cmd", "dcs.sh"))
      Assertions.assertThat(getDistributionDir().resolve(file)).isRegularFile();
  }
}
