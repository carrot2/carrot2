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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.carrot2.RestoreFolderStateRule;
import org.carrot2.TestBase;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;

public abstract class AbstractDistributionTest extends TestBase {
  protected static String DCS_SHUTDOWN_TOKEN = "_shutdown_";

  private static Path mirrorPath;
  private static RestoreFolderStateRule restoreDistRule;

  @ClassRule
  public static RuleChain classChain =
      RuleChain.outerRule(
          new TestRuleAdapter() {

            protected void before() throws Throwable {
              Path distDir =
                  Paths.get(System.getProperty("dist.dir", "./build/distribution"))
                      .normalize()
                      .toAbsolutePath();

              if (!Files.isDirectory(distDir)) {
                throw new AssertionError("Distribution not assembled at path: " + distDir);
              }

              // Sometimes add awkward or problematic characters to distribution path.
              Assertions.assertThat(mirrorPath).isNull();
              mirrorPath =
                  RandomizedTest.randomFrom(
                          Arrays.<Function<Path, Path>>asList(
                              (path) -> path.resolve("New Folder (2)"), (path) -> path))
                      .apply(newTempDir(LifecycleScope.SUITE).toAbsolutePath().normalize());

              Files.createDirectories(mirrorPath);
              restoreDistRule = new RestoreFolderStateRule(distDir, mirrorPath);

              Loggers.CONSOLE.info(
                  "Using distribution from: {} mirrored to: {}", distDir, mirrorPath);
            }

            @Override
            protected void afterAlways(List<Throwable> errors) throws Throwable {
              mirrorPath = null;
              restoreDistRule = null;
              super.afterAlways(errors);
            }
          });

  @Rule public final RuleChain testChain = RuleChain.outerRule(restoreDistRule);

  protected final DcsService startDcs() {
    try {
      return new ForkedDcs(getDistributionDir(), DCS_SHUTDOWN_TOKEN);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected final Path getDistributionDir() {
    return mirrorPath;
  }
}
