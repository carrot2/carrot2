package org.carrot2.dcs;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
  private static final String LAUNCHER_SCRIPT_NAME = "dcs";

  public static final String OPT_SHUTDOWN_TOKEN = "--shutdown-token";
  public static final String OPT_PORT = "--port";
  public static final String OPT_HOME = "--home";

  @Parameter(
      names = {"-p", OPT_PORT},
      description = "Port number to bind to.")
  public int port = 8080;

  @Parameter(
      names = {OPT_HOME},
      description = "DCS's home folder (for resource and application lookup).")
  public Path home;

  @Parameter(
      names = {OPT_SHUTDOWN_TOKEN},
      description = "Shutdown service's validation token.")
  public String shutdownToken;

  @ParametersDelegate
  public LoggingConfigurationParameters loggingParameters = new LoggingConfigurationParameters();

  @SuppressWarnings("unused")
  @Parameter(names = "--help", help = true, hidden = true)
  private boolean help;

  private final String tstamp;

  public Launcher() {
    tstamp =
        new DateTimeFormatterBuilder()
            .appendPattern("yyyy_MM_dd-HH_mm_ss-SSS")
            .toFormatter(Locale.ROOT)
            .format(LocalDateTime.now(ZoneOffset.systemDefault()));
  }

  public void start() throws Exception {
    autodetectHome();
    reconfigureLogging(home, loggingParameters);

    JettyContainer c = new JettyContainer(port, home.resolve("web"), shutdownToken);
    c.start();
    c.join();
  }

  private Path autodetectHome() {
    if (home == null) {
      home = Paths.get(".").normalize().toAbsolutePath();
    }

    if (Files.exists(home.resolve("dcs.cmd"))
        || Files.exists(home.resolve("dcs.sh"))
        || Files.exists(home.resolve("web"))) {
      return home;
    } else {
      throw new ParameterException("Application's home folder not valid: " + home.toAbsolutePath());
    }
  }

  private static void configureLog4jInitial() {
    try {
      Configurator.initialize(
          "log4j2-default.xml",
          Launcher.class.getClassLoader(),
          Launcher.class.getResource("log4j2-default.xml").toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  /** Configure logging. */
  private void reconfigureLogging(Path home, LoggingConfigurationParameters loggingParameters) {
    List<Path> configurations = new ArrayList<>();
    if (loggingParameters.configuration != null) {
      configurations.add(loggingParameters.configuration);
    }

    if (loggingParameters.trace) {
      configurations.add(Paths.get("log4j2-trace.xml"));
    }

    if (loggingParameters.verbose) {
      configurations.add(Paths.get("log4j2-verbose.xml"));
    }

    if (loggingParameters.quiet) {
      configurations.add(Paths.get("log4j2-quiet.xml"));
    }

    if (configurations.size() > 1) {
      throw new ParameterException(
          "Conflicting logging configuration options:\n\t- "
              + configurations.stream()
                  .map(p -> p.toString())
                  .collect(Collectors.joining("\n\t- ")));
    }

    if (configurations.size() == 0) {
      configurations.add(Paths.get("log4j2-default.xml"));
    }

    Path configuration = configurations.iterator().next();

    // Try to be smart and resolve it if not absolute.
    if (!configuration.isAbsolute()) {
      configuration = resolveRelativeConfiguration(home, configuration);
      configuration = configuration.toAbsolutePath().normalize();
    }

    if (!Files.isRegularFile(configuration)) {
      throw new ParameterException("Configuration file does not exist: " + configuration);
    }

    try {
      HashMap<String, String> properties = new HashMap<>();
      properties.put("home", home.toString());
      properties.put("tstamp", tstamp);
      DcsLookup.setup(properties);

      List<URI> multiConfigs = new ArrayList<>();
      multiConfigs.add(configuration.toUri());

      ClassLoader cl = getClass().getClassLoader();
      LoggerContext ctx = (LoggerContext) LogManager.getContext(cl, false);
      reloadLoggingConfiguration(ctx, multiConfigs.toArray(new URI[multiConfigs.size()]));
    } catch (Exception e) {
      throw new RuntimeException("Configuration could not be parsed: " + configuration, e);
    }
  }

  public static void reloadLoggingConfiguration(LoggerContext ctx, URI... configurations) {
    ConfigurationFactory configFactory = ConfigurationFactory.getInstance();

    List<AbstractConfiguration> configs = new ArrayList<AbstractConfiguration>();
    for (URI configUri : configurations) {
      Configuration configuration =
          configFactory.getConfiguration(ctx, configUri.toString(), configUri);
      if (configuration == null || !(configuration instanceof AbstractConfiguration)) {
        throw new RuntimeException("Oddball config problem: " + configUri);
      }
      configs.add((AbstractConfiguration) configuration);
    }

    ctx.start(new CompositeConfiguration(configs));
  }

  private static Path resolveRelativeConfiguration(Path home, Path configuration) {
    Path candidate;

    // cwd
    String userDir = System.getProperty("user.dir");
    if (userDir != null) {
      candidate = Paths.get(userDir).resolve(configuration);
      if (Files.isRegularFile(candidate)) {
        return candidate;
      }
    }

    // {home}/conf/logging
    candidate = home.resolve("conf").resolve("logging").resolve(configuration);
    if (Files.isRegularFile(candidate)) {
      return candidate;
    }

    // {home}.home/
    candidate = home.resolve(configuration);
    if (Files.isRegularFile(candidate)) {
      return candidate;
    }

    return configuration;
  }

  public static void main(String[] args) {
    configureLog4jInitial();

    Logger logger = LoggerFactory.getLogger(Launcher.class);
    Launcher launcher = new Launcher();
    try {
      JCommander jcommander = JCommander.newBuilder().addObject(launcher).build();
      jcommander.setProgramName(LAUNCHER_SCRIPT_NAME);
      jcommander.parse(args);

      if (launcher.help) {
        StringBuilder v = new StringBuilder();
        jcommander.usage(v);
        logger.info(v.toString());
      } else {
        launcher.start();
      }
    } catch (ParameterException e) {
      logger.error(e.getMessage());
      logger.error("(pass --help to display all options).");
    } catch (Exception e) {
      logger.error("Unhandled program error: " + e.getMessage(), e);
    }
  }
}
