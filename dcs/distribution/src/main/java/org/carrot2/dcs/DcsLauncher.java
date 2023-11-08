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
package org.carrot2.dcs;

import com.carrotsearch.console.jcommander.Parameter;
import com.carrotsearch.console.jcommander.Parameters;
import com.carrotsearch.console.launcher.Command;
import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.console.launcher.Launcher;
import com.carrotsearch.console.launcher.Loggers;
import com.carrotsearch.console.launcher.ReportCommandException;
import java.io.IOException;
import java.net.BindException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;
import java.util.stream.Stream;

@Parameters(
    commandNames = "launch-dcs",
    commandDescription = "Launch the Document Clustering Server.")
public class DcsLauncher extends Command<ExitCode> {
  public static final String DCS_HOME_SYSPROP = "dcs.home";
  public static final String ENV_SCRIPT_HOME = "DCS_HOME";

  public static final String OPT_SHUTDOWN_TOKEN = "--shutdown-token";
  public static final String OPT_PORT = "--port";
  public static final String OPT_HOST = "--host";
  public static final String OPT_HOME = "--home";
  public static final String OPT_MAX_THREADS = "--threads";
  public static final String OPT_USE_GZIP = "--gzip";
  public static final String OPT_PID_FILE = "--pid-file";
  public static final String OPT_IDLE_TIME = "--idle-time";
  public static final String OPT_VERSION = "--version";

  @Parameter(
      names = {"-p", OPT_PORT},
      description = "Port number to bind to.")
  public int port = 8080;

  @Parameter(
      names = {OPT_HOST},
      description =
          "The network interface this connector binds to as an IP address or a hostname (by default binds on all interfaces).")
  public String host;

  @Parameter(
      names = {OPT_HOME},
      description = "DCS's home folder (conf/ and web/ subfolder lookup).")
  public Path home;

  @Parameter(
      names = {OPT_MAX_THREADS},
      description = "Maximum number of processing threads for Jetty.",
      hidden = true)
  public Integer maxThreads;

  @Parameter(
      names = {OPT_SHUTDOWN_TOKEN},
      description = "The shutdown secret token. If empty, shutdown endpoint will be disabled.")
  public String shutdownToken;

  @Parameter(
      names = {OPT_USE_GZIP},
      description = "Use GZIP compression for responses.",
      arity = 1)
  public boolean useGzip = true;

  @Parameter(
      names = {OPT_PID_FILE},
      description = "Write process PID to a given file.",
      required = false)
  public Path pidFile;

  @Parameter(
      names = {OPT_IDLE_TIME},
      description = "Idle time before the connection is terminated, in milliseconds.",
      required = false)
  public Integer idleTime = Math.toIntExact(TimeUnit.SECONDS.toMillis(60));

  @Parameter(
      names = {OPT_VERSION},
      hidden = true,
      description = "Prints the software version and exits.")
  public boolean version;

  private final String tstamp =
      new DateTimeFormatterBuilder()
          .appendPattern("yyyy_MM_dd-HH_mm_ss-SSS")
          .toFormatter(Locale.ROOT)
          .format(LocalDateTime.now(ZoneOffset.systemDefault()));

  @Override
  public ExitCode run() {
    try {
      if (pidFile != null) {
        Files.writeString(
            pidFile, Long.toString(ProcessHandle.current().pid()), StandardCharsets.UTF_8);
      }

      if (version) {
        try (var is = getClass().getResourceAsStream("/META-INF/MANIFEST.MF")) {
          if (is == null) {
            Loggers.CONSOLE.info("Version unknown (manifest can't be read).");
          } else {
            Manifest mf = new Manifest(is);
            var mainAttrs = mf.getMainAttributes();
            Loggers.CONSOLE.info(
                "Document Clustering Server, {}",
                Objects.requireNonNullElse(
                    mainAttrs.getValue("Implementation-Version"), "(unknown)"));
          }
        }

        return ExitCodes.SUCCESS;
      }

      JettyContainer c =
          new JettyContainer(
              port, host, home.resolve("web"), shutdownToken, maxThreads, useGzip, idleTime);
      try {
        c.start();
      } catch (IOException e) {
        if (e.getCause() instanceof BindException) {
          Loggers.CONSOLE.error(
              "The built-in HTTP server could not start on port {}: {}", port, e.getMessage());
          return ExitCodes.ERROR_INTERNAL;
        }
      }
      c.join();
      return ExitCodes.SUCCESS;
    } catch (Exception e) {
      Loggers.CONSOLE.error("The built-in HTTP server stopped unexpectedly.", e);
      return ExitCodes.ERROR_INTERNAL;
    }
  }

  private void autodetectHome() {
    if (home == null) {
      home =
          Stream.of(System.getProperty(DCS_HOME_SYSPROP), System.getenv(ENV_SCRIPT_HOME), ".")
              .filter(Objects::nonNull)
              .map(s -> Paths.get(s))
              .findFirst()
              .get();
    }

    home = home.toAbsolutePath();

    if (Files.exists(home.resolve("dcs.cmd"))
        || Files.exists(home.resolve("dcs"))
        || Files.exists(home.resolve("web"))) {
      System.setProperty(DCS_HOME_SYSPROP, home.toString());
    } else {
      throw new ReportCommandException(
          "Application's home folder not valid: " + home, ExitCodes.ERROR_INVALID_ARGUMENTS);
    }
  }

  @Override
  protected List<URI> configureLogging(List<URI> defaults) {
    autodetectHome();

    Map<String, String> envVars = new HashMap<>();
    envVars.put("home", home.toAbsolutePath().toString());
    envVars.put("tstamp", tstamp);
    DcsLookup.setup(envVars);

    Path conf = home.resolve("conf").resolve("logging");
    Path configuration;

    if (super.logging.configuration != null) {
      // An explicit configuration is used. Leave as-is.
      return defaults;
    } else if (super.logging.trace) {
      configuration = conf.resolve("log4j2-trace.xml");
    } else if (super.logging.verbose) {
      configuration = conf.resolve("log4j2-verbose.xml");
    } else if (super.logging.quiet) {
      configuration = conf.resolve("log4j2-quiet.xml");
    } else {
      configuration = conf.resolve("log4j2-default.xml");
    }

    return Collections.singletonList(configuration.toUri());
  }

  public static void main(String[] args) {
    int retCode = new Launcher().runCommand(new DcsLauncher(), args).processReturnValue();
    System.exit(retCode);
  }
}
