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
package org.carrot2.dcs.proxies;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyServlet extends HttpServlet {

  private static final String PARAM_PROXY_TARGET = "proxy-target";

  private Logger logger = LoggerFactory.getLogger(getClass());
  private URI target;

  private CloseableHttpClient httpClient;
  private PoolingHttpClientConnectionManager cm;

  private static Set<String> FILTERED_HEADERS =
      new HashSet<>(
          Arrays.asList(
              "proxy-connection",
              "connection",
              "keep-alive",
              "transfer-encoding",
              "te",
              "trailer",
              "proxy-authorization",
              "proxy-authenticate",
              "upgrade"));

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    String targetString = config.getInitParameter(PARAM_PROXY_TARGET);
    if (targetString == null) {
      throw new ServletException(
          String.format(
              Locale.ROOT,
              "Proxy servlet %s requires init-param '%s' (target URI).",
              config.getServletName(),
              PARAM_PROXY_TARGET));
    }

    try {
      target = new URI(targetString);
    } catch (URISyntaxException e) {
      throw new ServletException(
          String.format(
              Locale.ROOT,
              "Proxy servlet %s has unparseable target URI: %s",
              config.getServletName(),
              targetString),
          e);
    }

    // TODO: add rate-limiter here to comply with PubMed (for example)?
    // TODO: make the hard-coded values below configurable via init-params.
    cm = new PoolingHttpClientConnectionManager(3000, TimeUnit.SECONDS);
    cm.setMaxTotal(1);

    this.httpClient =
        HttpClientBuilder.create()
            .disableAutomaticRetries()
            .disableRedirectHandling()
            .disableAuthCaching()
            .disableCookieManagement()
            .disableDefaultUserAgent()
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setMaxRedirects(0)
                    .setConnectionRequestTimeout(2000)
                    .setConnectTimeout(2000)
                    .build())
            .setConnectionManager(cm)
            .build();
  }

  @Override
  public void destroy() {
    if (cm != null) {
      cm.close();
    }
    if (httpClient != null) {
      try {
        httpClient.close();
      } catch (IOException e) {
        logger.error("Could not close HTTP client.", e);
      }
    }
    closeHttpClientJar();

    super.destroy();
  }

  /**
   * A hack to close resource URL connections httpclient initiates.
   */
  private void closeHttpClientJar() {
    final URL url = PublicSuffixMatcherLoader.class.getResource("/mozilla/public-suffix-list.txt");
    try {
      URLConnection conn = url.openConnection();
      if (conn instanceof JarURLConnection) {
        JarURLConnection juc = (JarURLConnection) conn;
        juc.getJarFile().close();
      }
    } catch (IOException e) {
      // Ignore.
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String delegate = req.getPathInfo();
    if (delegate == null || !delegate.startsWith("/")) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid delegate path: {}", delegate);
      return;
    }

    // Minimal sanity check: strip any leading '/'.
    delegate = delegate.replaceFirst("/+", "");

    String queryString = req.getQueryString();
    if (queryString != null && !queryString.isEmpty()) {
      delegate = delegate + "?" + queryString;
    }

    URI finalTarget = target.resolve(delegate);
    String method = req.getMethod();

    HttpUriRequest request;
    switch (method) {
      case "GET":
        request = new HttpGet(finalTarget);
        break;
      case "OPTIONS":
        request = new HttpOptions(finalTarget);
        break;
      case "HEAD":
        request = new HttpHead(finalTarget);
        break;
      default:
        sendError(
            resp, HttpServletResponse.SC_BAD_REQUEST, "Request method not supported: {}", method);
        return;
    }

    // Copy headers request -> proxy request
    Enumeration<String> e = req.getHeaderNames();
    while (e.hasMoreElements()) {
      String name = e.nextElement().toLowerCase(Locale.ROOT);
      if (!FILTERED_HEADERS.contains(name)) {
        request.setHeader(name, req.getHeader(name));
      }

      if (name.startsWith("x-forwarded")) {
        sendError(
            resp,
            HttpServletResponse.SC_BAD_REQUEST,
            "Request is already forwarded and this is not supported.");
      }
    }

    // Add forwarding headers.
    resp.setHeader("Via", "1.1 (" + getServletName() + ")");
    resp.setHeader("X-Forwarded-For", req.getRemoteAddr());
    resp.setHeader("X-Forwarded-Proto", req.getScheme());
    resp.setHeader("X-Forwarded-Host", req.getServerName());
    resp.setHeader("X-Forwarded-Server", req.getLocalName());

    // TODO: Add timeout and emit gateway error?

    logger.debug("Proxy {}: {}", method, finalTarget);
    try (CloseableHttpResponse targetResponse = httpClient.execute(request)) {
      StatusLine statusLine = targetResponse.getStatusLine();
      resp.setStatus(statusLine.getStatusCode(), statusLine.getReasonPhrase());

      for (Header header : targetResponse.getAllHeaders()) {
        resp.setHeader(header.getName(), header.getValue());
      }

      try (ServletOutputStream outputStream = resp.getOutputStream()) {
        targetResponse.getEntity().writeTo(outputStream);
      }
    }
  }

  private void sendError(HttpServletResponse resp, int responseCode, String message, Object... args)
      throws IOException {
    if (resp.isCommitted()) {
      logger.trace("Response already committed, can't send proper reply code.");
    } else {
      resp.sendError(responseCode);
      resp.setContentType("text/plain");
      resp.setCharacterEncoding("UTF-8");
      message = String.format(Locale.ROOT, message, args);

      PrintWriter pw = resp.getWriter();
      pw.write(message);
      pw.flush();

      logger.trace("Sent error code {}: {}", responseCode, message);
    }
  }
}
