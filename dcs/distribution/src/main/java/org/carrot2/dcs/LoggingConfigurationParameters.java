package org.carrot2.dcs;

import com.beust.jcommander.Parameter;
import java.nio.file.Path;

public final class LoggingConfigurationParameters {
  public static final String OPT_QUIET = "--quiet";
  public static final String OPT_VERBOSE = "--verbose";
  public static final String OPT_TRACE = "--trace";

  @Parameter(
      names = {"-q", OPT_QUIET},
      description = "Use quiet logging.")
  public boolean quiet;

  @Parameter(
      names = {"-v", OPT_VERBOSE},
      description = "Use verbose logging.")
  public boolean verbose;

  @Parameter(
      hidden = true,
      names = {OPT_TRACE},
      description = "Use trace-level (development) logging.")
  public boolean trace;

  @Parameter(
      required = false,
      names = {"--log4j2"},
      description = "Explicit log4j2 XML configuration location.")
  public Path configuration;
}
