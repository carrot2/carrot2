package org.carrot2.dcs;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Launcher {
  private static final String LAUNCHER_SCRIPT_NAME = "dcs";

  @Parameter(names = {"-p", "--port"}, description = "Port number to bind to.")
  public int port = 8080;

  @Parameter(names = {"--home"}, description = "DCS's home folder (for resource and application lookup).")
  public Path home;

  @SuppressWarnings("unused")
  @Parameter(names = "--help", help = true)
  private boolean help;

  public void start() throws Exception {
    autodetectHome();

    JettyContainer c = new JettyContainer(port, home.resolve("web"));
    c.start();
  }

  private Path autodetectHome() {
    if (home == null) {
      home = Paths.get(".").normalize().toAbsolutePath();
    }

    if (Files.exists(home.resolve("dcs.cmd")) ||
        Files.exists(home.resolve("dcs.sh")) ||
        Files.exists(home.resolve("web"))) {
      return home;
    } else {
      throw new ParameterException("Application's home folder not valid: "
         + home.toAbsolutePath());
    }
  }

  private static void configureLog4jInitial() {
    try {
      Configurator.initialize("log4j2-default.xml",
          Launcher.class.getClassLoader(),
          Launcher.class.getResource("log4j2-default.xml").toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
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

