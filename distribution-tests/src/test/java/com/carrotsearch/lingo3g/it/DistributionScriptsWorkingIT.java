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

import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.procfork.Launcher;
import com.carrotsearch.procfork.ProcessBuilderLauncher;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DistributionScriptsWorkingIT extends DistributionTestBase {
  @Test
  public void runDcsCheck() throws Exception {
    Path dcsDir = getDistributionBasePath().resolve("dcs");
    Path dcs = resolveScript(dcsDir.resolve("dcs"));
    Launcher task =
        new ProcessBuilderLauncher()
            .executable(dcs)
            .viaShellLauncher()
            .cwd(dcsDir)
            .arg("--version");

    execute(
        task,
        ExitCodes.SUCCESS,
        (output) ->
            Assertions.assertThat(output.split("\n"))
                .allSatisfy(
                    line -> Assertions.assertThat(line).contains("Document Clustering Server")));
  }
}
