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

import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.console.launcher.Launcher;
import com.carrotsearch.randomizedtesting.LifecycleScope;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.carrot2.dcs.examples.E01_DcsConfiguration;
import org.carrot2.dcs.examples.E02_DcsCluster;
import org.carrot2.dcs.examples.E03_DcsClusterWithParams;
import org.carrot2.dcs.examples.E04_DcsDataModels;
import org.junit.Test;

public class DcsExamplesTest extends AbstractDcsTest {
  @Test
  public void runE01() throws IOException {
    URI dcsService = dcs().getAddress().resolve("/service/");

    ExitCode exitCode =
        new Launcher()
            .runCommand(
                new E01_DcsConfiguration(),
                E01_DcsConfiguration.ARG_DCS_URI,
                dcsService.toString());
    Assertions.assertThat(exitCode).isEqualTo(ExitCodes.SUCCESS);
  }

  @Test
  public void runE02() throws IOException {
    URI dcsService = dcs().getAddress().resolve("/service/");

    Path input = RandomizedTest.newTempFile(LifecycleScope.TEST);
    Files.write(input, resourceBytes("exampleData.json"));

    ExitCode exitCode =
        new Launcher()
            .runCommand(
                new E02_DcsCluster(),
                E02_DcsCluster.ARG_DCS_URI,
                dcsService.toString(),
                input.toAbsolutePath().toString());
    Assertions.assertThat(exitCode).isEqualTo(ExitCodes.SUCCESS);
  }

  @Test
  public void runE03() throws IOException {
    URI dcsService = dcs().getAddress().resolve("/service/");

    Path input = RandomizedTest.newTempFile(LifecycleScope.TEST);
    Files.write(input, resourceBytes("exampleData.json"));

    ExitCode exitCode =
        new Launcher()
            .runCommand(
                new E03_DcsClusterWithParams(),
                E03_DcsClusterWithParams.ARG_DCS_URI,
                dcsService.toString(),
                input.toAbsolutePath().toString());
    Assertions.assertThat(exitCode).isEqualTo(ExitCodes.SUCCESS);
  }

  @Test
  public void runE04() throws IOException {
    URI dcsService = dcs().getAddress().resolve("/service/");

    ExitCode exitCode =
        new Launcher()
            .runCommand(
                new E04_DcsDataModels(),
                E03_DcsClusterWithParams.ARG_DCS_URI,
                dcsService.toString());
    Assertions.assertThat(exitCode).isEqualTo(ExitCodes.SUCCESS);
  }
}
