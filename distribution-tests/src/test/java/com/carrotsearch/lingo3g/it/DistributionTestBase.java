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

import static org.assertj.core.api.Assertions.assertThat;

import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.console.launcher.Loggers;
import com.carrotsearch.procfork.ForkedProcess;
import com.carrotsearch.procfork.Launcher;
import com.carrotsearch.procfork.ProcessBuilderLauncher;
import com.carrotsearch.progresso.util.OsDetection;
import com.carrotsearch.randomizedtesting.LifecycleScope;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.rules.TestRuleAdapter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.AssumptionViolatedException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.slf4j.LoggerFactory;

/** */
public abstract class DistributionTestBase extends TestBase {
  private static RestoreDistributionStateRule restoreDistributionRule;

  protected static Supplier<Charset> forkedProcessCharset =
      new Supplier<>() {
        private boolean detectionFailure;
        private Charset charset;

        @Override
        public synchronized Charset get() {
          if (charset == null) {
            try {
              charset = computeCharset();
              LoggerFactory.getLogger(this.getClass())
                  .info(
                      "Detected forked process charset: {}, current JVMs: {}",
                      charset.name(),
                      Charset.defaultCharset().name());
            } catch (Exception e) {
              detectionFailure = true;
              throw new RuntimeException("Failed to detect forked process charset.", e);
            }
          }

          // Only fail on the first attempt, ignore subsequent tests.
          if (detectionFailure) {
            throw new AssumptionViolatedException("Failed to detect forked process charset.");
          }

          return charset;
        }

        private Charset computeCharset() throws Exception {
          CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
          Path checkClassSource = Paths.get(codeSource.getLocation().toURI());

          try (ForkedProcess fp =
              new ProcessBuilderLauncher()
                  .executable(Paths.get(System.getProperty("java.home"), "bin", "java"))
                  .args(
                      "-cp",
                      checkClassSource.toAbsolutePath().normalize().toString(),
                      CharsetName.class.getName())
                  .execute()) {
            fp.waitFor();
            String charsetName =
                Files.readString(fp.getProcessOutputFile(), StandardCharsets.UTF_8);
            return Charset.forName(charsetName.trim());
          }
        }
      };

  public static class CharsetName {
    public static void main(String[] args) throws IOException {
      String name = Charset.defaultCharset().name();
      var bytes = name.getBytes(StandardCharsets.UTF_8);
      System.out.write(bytes, 0, bytes.length);
      System.out.flush();
    }
  }

  @ClassRule
  public static RuleChain classChain =
      RuleChain.outerRule(
          new TestRuleAdapter() {
            protected void before() throws Throwable {
              Path distributionZip = null;

              String location = System.getProperty("distribution.zip");
              if (location != null) {
                distributionZip = Paths.get(location);
                assertThat(Files.isRegularFile(distributionZip))
                    .as(
                        "distribution.zip path does not exist: "
                            + distributionZip.toAbsolutePath().normalize())
                    .isTrue();
              } else {
                // Try to auto-locate the public distribution.
                Path distributionProject =
                    Paths.get(".")
                        .resolve("../distribution/build/distZip")
                        .toAbsolutePath()
                        .normalize();

                if (Files.isDirectory(distributionProject)) {
                  try (Stream<Path> files =
                      Files.list(distributionProject)
                          .filter(p -> p.getFileName().toString().endsWith(".zip"))
                          .filter(p -> !p.getFileName().toString().contains("-dev"))
                          .sorted(
                              Comparator.comparing(DistributionTestBase::getFileTime).reversed())) {

                    Optional<Path> first = files.findFirst();
                    if (first.isPresent()) {
                      distributionZip = first.get();

                      LoggerFactory.getLogger(DistributionTestBase.class)
                          .info(
                              "Distribution ZIP detected automatically and pointing to: {}",
                              distributionZip);
                    }
                  }
                }
              }

              if (distributionZip == null) {
                throw new AssumptionViolatedException(
                    "No distribution zip assembled. Automatic detection failed.");
              }

              // Sometimes add awkward or problematic characters to distribution path.
              Path distributionUnpackPath =
                  RandomizedTest.randomFrom(
                          Arrays.<Function<Path, Path>>asList(
                              (path) -> path.resolve("New Folder (2)"), (path) -> path))
                      .apply(newTempDir(LifecycleScope.SUITE).toAbsolutePath().normalize());

              restoreDistributionRule =
                  new RestoreDistributionStateRule(distributionZip, distributionUnpackPath);
            }
          });

  private static FileTime getFileTime(Path a) {
    try {
      return Files.getLastModifiedTime(a);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Rule public final RuleChain testChain = RuleChain.outerRule(restoreDistributionRule);

  protected final Path getDistributionBasePath() {
    return restoreDistributionRule.getDistributionBasePath();
  }

  protected final Path getDistributionZip() {
    return restoreDistributionRule.getDistributionZip();
  }

  protected Path resolveScript(Path scriptPath) {
    List<Path> candidates = new ArrayList<>();
    candidates.add(scriptPath);

    String fileName = scriptPath.getFileName().toString();
    if (OsDetection.IS_OS_WINDOWS) {
      candidates.add(scriptPath.resolveSibling(fileName + ".cmd"));
      candidates.add(scriptPath.resolveSibling(fileName + ".bat"));
    } else if (OsDetection.IS_OS_UNIXISH) {
      candidates.add(scriptPath.resolveSibling(fileName + ".sh"));
    }

    Optional<Path> script =
        candidates.stream().sequential().filter(p -> Files.exists(p)).findFirst();

    if (script.isEmpty()) {
      throw new AssertionError("No script found for base script path: " + scriptPath);
    }

    return script.get();
  }

  protected String execute(
      Launcher task, ExitCode expectedExitCode, ThrowingConsumer<String> consumer)
      throws Exception {

    try (ForkedProcess forkedProcess = task.execute()) {
      String command = forkedProcess.getProcess().info().command().orElse("(unset command name)");

      Charset charset = forkedProcessCharset.get();
      try {
        int exitStatus = forkedProcess.waitFor();

        Assertions.assertThat(exitStatus)
            .as("forked process exit status")
            .isEqualTo(expectedExitCode.processReturnValue());

        String output = Files.readString(forkedProcess.getProcessOutputFile(), charset);
        consumer.accept(output);
        return output;
      } catch (Throwable t) {
        logSubprocessOutput(
            command, Files.readString(forkedProcess.getProcessOutputFile(), charset));
        throw t;
      }
    }
  }

  protected void logSubprocessOutput(String command, String output) {
    var logger = Loggers.CONSOLE;
    logger.warn("--- [forked subprocess output: {}] ---", command);
    logger.warn(output);
    logger.warn("--- [end of subprocess output: {}] ---", command);
  }

  protected String execute(Launcher task, ThrowingConsumer<String> consumer) throws Exception {
    return execute(task, ExitCodes.SUCCESS, consumer);
  }
}
