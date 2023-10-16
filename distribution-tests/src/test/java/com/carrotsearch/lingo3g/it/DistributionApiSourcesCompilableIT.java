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
package com.carrotsearch.lingo3g.it;

import com.carrotsearch.procfork.Launcher;
import com.carrotsearch.procfork.ProcessBuilderLauncher;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DistributionApiSourcesCompilableIT extends DistributionTestBase {
  @Test
  public void runCheck() throws Exception {
    runGradleTask("check");
  }

  private void runGradleTask(String... args) throws Exception {
    Path src = getDistributionBasePath().resolve("examples");

    Path gradlew = resolveScript(src.resolve("gradlew"));
    Launcher task =
        new ProcessBuilderLauncher()
            .executable(gradlew)
            .viaShellLauncher()
            .cwd(src)
            .arg("--no-daemon")
            .args(args);

    execute(
        task,
        (output) -> {
          Assertions.assertThat(output).contains("BUILD SUCCESSFUL in");
        });
  }
}
