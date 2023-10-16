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
package org.carrot2.dcs.examples;

import com.carrotsearch.console.jcommander.Parameters;
import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.console.launcher.Launcher;
import com.carrotsearch.console.launcher.Loggers;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.carrot2.dcs.model.ListResponse;

@Parameters(commandNames = "configuration")
public class E01_DcsConfiguration extends CommandScaffold {
  @Override
  ExitCode run(CloseableHttpClient httpClient, ObjectMapper om) throws IOException {
    HttpUriRequest request = RequestBuilder.get(dcsService.resolve("list")).build();
    try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
      return ifValid(
          om,
          httpResponse,
          content -> {
            ListResponse response = om.readValue(content, ListResponse.class);

            Loggers.CONSOLE.info("Available algorithms: {}", response.algorithms.keySet());
            Loggers.CONSOLE.info("Available templates: {}", response.templates);
            return ExitCodes.SUCCESS;
          });
    }
  }

  public static void main(String[] args) {
    ExitCode exitCode = new Launcher().runCommand(new E01_DcsConfiguration(), args);
    System.exit(exitCode.processReturnValue());
  }
}
