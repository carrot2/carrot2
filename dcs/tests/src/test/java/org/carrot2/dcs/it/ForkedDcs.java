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
import com.carrotsearch.procfork.ForkedProcess;
import com.carrotsearch.procfork.ProcessBuilderLauncher;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.carrot2.HttpRequest;
import org.carrot2.dcs.DcsLauncher;
import org.carrot2.dcs.JettyContainer;

public class ForkedDcs implements DcsService {
  private final ForkedProcess process;
  private final String shutdownToken;
  private Integer port;
  private Path distributionDir;

  public ForkedDcs(Path distributionDir, String shutdownToken) throws IOException {
    this.shutdownToken = Objects.requireNonNull(shutdownToken);
    this.distributionDir = distributionDir.toAbsolutePath();

    // Try to launch the DCS from exactly the same JVM as we're currently running.
    // This avoids problems with PATH pointing to Java 1.8 on IntelliJ, for example.
    Path javaCmd = Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java");

    this.process =
        new ProcessBuilderLauncher()
            .cwd(distributionDir)
            .envvar("DCS_OPTS", "-Xmx256m")
            .envvar("JAVA_CMD", javaCmd.toAbsolutePath().toString())
            .executable(Paths.get(File.separatorChar == '\\' ? "dcs" : "./dcs.sh"))
            .args(DcsLauncher.OPT_SHUTDOWN_TOKEN, shutdownToken)
            .args(DcsLauncher.OPT_PORT, "0")
            .viaShellLauncher()
            .execute();

    // Wait for the process to become alive.
    Loggers.CONSOLE.info("Launching DCS at: {}", distributionDir);
    Path stdout = process.getProcessOutputFile();
    Instant deadline = Instant.now().plusSeconds(15);
    Pattern pattern =
        Pattern.compile(Pattern.quote(JettyContainer.SERVICE_STARTED_ON) + "(?<port>[0-9]+)");
    while (Instant.now().isBefore(deadline) && process.getProcess().isAlive()) {
      String log = new String(Files.readAllBytes(stdout), Charset.defaultCharset());
      Matcher matcher = pattern.matcher(log);
      if (matcher.find()) {
        port = Integer.parseInt(matcher.group("port"));
        break;
      }
    }

    if (port == null) {
      Loggers.CONSOLE.error(
          "Forked DCS emitted this log: {}",
          new String(Files.readAllBytes(stdout), Charset.defaultCharset()));

      process.close();
      throw new IOException("Could not start forked DCS (no port emitted within deadline).");
    }
  }

  @Override
  public URI getAddress() {
    return URI.create("http://localhost:" + port);
  }

  @Override
  public boolean isRunning() {
    return process.getProcess().isAlive();
  }

  @Override
  public void close() throws IOException {
    try {
      if (process.getProcess().isAlive()) {
        new Thread(
                () -> {
                  try {
                    HttpRequest.builder()
                        .queryParam("token", shutdownToken)
                        .sendPost(getAddress().resolve("/shutdown"));
                  } catch (Exception e) {
                    // We don't care about the result of /shutdown call since
                    // there's an internal race condition inside the shutdown handler's
                    // code that may cause the request to be dropped.
                  }
                })
            .start();
      }

      Instant deadline = Instant.now().plusSeconds(5);
      while (Instant.now().isBefore(deadline)) {
        if (!process.getProcess().isAlive()) {
          Loggers.CONSOLE.info("isAlive() indicates DCS is dead at: {}", distributionDir);
          try {
            process.waitFor();
          } catch (InterruptedException e) {
            // Fall through.
          }
          break;
        }

        try {
          Thread.sleep(250);
        } catch (InterruptedException e) {
          break;
        }
      }

      if (process.getProcess().isAlive()) {
        throw new IOException("Forked Jetty didn't shut down properly within the timeout.");
      }
    } finally {
      process.close();
    }
  }
}
