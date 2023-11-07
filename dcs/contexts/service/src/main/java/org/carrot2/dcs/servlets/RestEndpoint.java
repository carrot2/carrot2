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
package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.carrot2.dcs.model.ErrorResponse;
import org.carrot2.dcs.model.ErrorResponseHandler;
import org.carrot2.dcs.model.ErrorResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
class RestEndpoint extends HttpServlet {
  public static final String PARAM_INDENT = "indent";

  private static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=UTF-8";
  private static final Set<String> BOOLEAN_YES_VALUES =
      new HashSet<>(Arrays.asList("yes", "true", "on", "", "enabled"));

  private static Logger CONSOLE = LoggerFactory.getLogger("console");
  private ObjectMapper om;

  private ArrayList<ErrorResponseHandler> errorResponseHandlers;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    this.errorResponseHandlers =
        StreamSupport.stream(ServiceLoader.load(ErrorResponseHandler.class).spliterator(), false)
            .collect(Collectors.toCollection(ArrayList::new));

    // Always add fallback handler at the end.
    this.errorResponseHandlers.add(
        exception -> {
          if (exception instanceof TerminateRequestException) {
            return new ErrorResponse(
                ((TerminateRequestException) exception).type,
                exception.getMessage(),
                exception.getCause());
          } else {
            return new ErrorResponse(
                ErrorResponseType.UNHANDLED_ERROR, "Unhandled internal exception.", exception);
          }
        });

    this.om = new ObjectMapper();
  }

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Permit cross-site requests by default.
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader(
        "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST");
    super.service(request, response);
  }

  protected void handleException(
      HttpServletRequest request, HttpServletResponse response, Throwable exception)
      throws IOException {
    if (response.isCommitted()) {
      CONSOLE.debug("Response already committed. Ignoring: {}", exception);
    } else {
      ErrorResponse errorResponse = null;
      for (ErrorResponseHandler handler : errorResponseHandlers) {
        errorResponse = handler.handle(exception);
        if (errorResponse != null) {
          break;
        }
      }

      ErrorResponseType type = errorResponse.type;

      if (type == ErrorResponseType.LICENSING) {
        CONSOLE.warn(errorResponse.message);
      }

      if (CONSOLE.isDebugEnabled()) {
        CONSOLE.debug(
            "Request resulted in an error {}: {}", type, request.getRequestURI(), exception);
      }

      response.setStatus(type.httpStatusCode);
      writeJsonResponse(response, true, errorResponse);
    }
  }

  protected void writeJsonResponse(
      HttpServletResponse response, boolean indent, Object jsonResponse) throws IOException {
    response.setContentType(CONTENT_TYPE_JSON_UTF8);

    ObjectWriter writer = om.writer();
    if (indent) {
      DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
      pp.indentArraysWith(new DefaultIndenter("  ", DefaultIndenter.SYS_LF));
      writer = writer.with(pp);
    }

    writer.writeValue(response.getWriter(), jsonResponse);
  }

  protected boolean shouldIndent(HttpServletRequest request) {
    return isEnabled(request, PARAM_INDENT);
  }

  protected boolean isEnabled(HttpServletRequest request, String paramName) {
    String parameter = Objects.requireNonNullElse(request.getParameter(paramName), "false");
    return BOOLEAN_YES_VALUES.contains(parameter.toLowerCase(Locale.ROOT));
  }
}
