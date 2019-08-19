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

import com.carrotsearch.console.launcher.Loggers;
import com.carrotsearch.randomizedtesting.LifecycleScope;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.rules.TestRuleAdapter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.carrot2.RestoreFolderStateRule;

class DistMirrorRule extends TestRuleAdapter {
  private Path realDistDir;
  private Path mirrorPath;

  protected void before() throws Throwable {
    realDistDir =
        Paths.get(System.getProperty("dist.dir", "./build/distribution"))
            .normalize()
            .toAbsolutePath();

    if (!Files.isDirectory(realDistDir)) {
      throw new AssertionError("Distribution not assembled at path: " + realDistDir);
    }

    // Sometimes add awkward or problematic characters to distribution path.
    Assertions.assertThat(mirrorPath).isNull();
    mirrorPath =
        RandomizedTest.randomFrom(
                Arrays.<Function<Path, Path>>asList(
                    (path) -> path.resolve("New Folder (2)"), (path) -> path))
            .apply(RandomizedTest.newTempDir(LifecycleScope.SUITE).toAbsolutePath().normalize());

    Files.createDirectories(mirrorPath);
    Loggers.CONSOLE.info("Using distribution from: {} mirrored to: {}", realDistDir, mirrorPath);
  }

  @Override
  protected void afterAlways(List<Throwable> errors) throws Throwable {
    mirrorPath = null;
    super.afterAlways(errors);
  }

  public RestoreFolderStateRule createRestoreRule() {
    return new RestoreFolderStateRule(realDistDir, mirrorPath);
  }

  public Path mirrorPath() {
    return Objects.requireNonNull(mirrorPath);
  }
}
