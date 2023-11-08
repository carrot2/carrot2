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

import static com.carrotsearch.console.launcher.Loggers.CONSOLE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyContainer {
  public static final String SERVICE_STARTED_ON = "Service started on port ";

  private final int port;
  private final String host;
  private final Path webappContexts;
  private final String shutdownToken;
  private final boolean useGzip;

  private Server server;
  private ServerConnector connector;
  private Integer maxThreads;
  private Integer idleTime;

  public JettyContainer(
      int port,
      String host,
      Path contexts,
      String shutdownToken,
      Integer maxThreads,
      boolean useGzip,
      Integer idleTime) {
    this.host = host;
    this.port = port;
    this.webappContexts = contexts;
    this.shutdownToken = shutdownToken;
    this.maxThreads = maxThreads;
    this.useGzip = useGzip;
    this.idleTime = idleTime;
  }

  public void start() throws Exception {
    server = createServer();
    addContexts(server, connector, webappContexts);
    server.start();
  }

  public void join() throws InterruptedException {
    server.join();
  }

  public boolean isRunning() {
    return server.isRunning();
  }

  public void stop() throws Exception {
    server.stop();
  }

  public int getPort() {
    return connector.getLocalPort();
  }

  private void addContexts(Server server, ServerConnector connector, Path contexts)
      throws IOException {
    List<Path> webapps;
    try (Stream<Path> list = Files.list(contexts)) {
      webapps =
          list.filter(dir -> !Files.isDirectory(dir.resolve("WEB-INF").resolve("web.xml")))
              .collect(Collectors.toList());
    }

    ArrayList<WebAppContext> ctxHandlers = new ArrayList<>();
    for (Path context : webapps) {
      if (!Files.isRegularFile(context.resolve("WEB-INF").resolve("web.xml"))) {
        throw new RuntimeException(
            "Not a web application context folder?: " + context.toAbsolutePath());
      }

      String ctxName = context.getFileName().toString();
      String ctxPath = "root".equalsIgnoreCase(ctxName) ? "/" : "/" + ctxName;

      WebAppContext ctx = new WebAppContext();
      ctx.setContextPath(ctxPath);
      ctx.setThrowUnavailableOnStartupException(true);
      ctx.setWar(context.normalize().toAbsolutePath().toString());
      ctx.setParentLoaderPriority(true);

      // Don't allow directory listings and don't use mmap buffers for serving static content.
      ctx.setInitParameter(DefaultServlet.CONTEXT_INIT + "dirAllowed", "false");
      ctx.setInitParameter(DefaultServlet.CONTEXT_INIT + "useFileMappedBuffer", "false");

      CONSOLE.debug("Deploying context '{}' at: {}.", ctxName, ctxPath);
      ctxHandlers.add(ctx);
    }

    server.addEventListener(
        new LifeCycle.Listener() {
          @Override
          public void lifeCycleStarted(LifeCycle event) {
            CONSOLE.info(
                "The following contexts are available:\n"
                    + ctxHandlers.stream()
                        .filter(AbstractLifeCycle::isStarted)
                        .sorted(Comparator.comparing(ContextHandler::getContextPath))
                        .map(ctx -> formatContext(ctx, connector))
                        .collect(Collectors.joining("\n")));
          }

          private String formatContext(WebAppContext ctx, ServerConnector connector) {
            return String.format(
                Locale.ROOT,
                "  http://localhost:%s%-10s %s",
                connector.getLocalPort(),
                ctx.getContextPath(),
                ctx.getDisplayName());
          }
        });

    HandlerList handlers = new HandlerList();
    if (shutdownToken != null && !shutdownToken.trim().isEmpty()) {
      handlers.addHandler(new ShutdownHandler(shutdownToken, false, false));
    }

    Handler contentHandler =
        new ContextHandlerCollection(ctxHandlers.toArray(new ContextHandler[0]));
    if (useGzip) {
      GzipHandler gzipHandler = new GzipHandler();
      gzipHandler.setHandler(contentHandler);
      gzipHandler.setMinGzipSize(1024);
      gzipHandler.setIncludedMethods("GET", "POST");
      gzipHandler.addIncludedMimeTypes(
          "application/json",
          "application/javascript",
          "application/x-javascript",
          "application/xml",
          "font/woff2",
          "text/css",
          "text/jsx",
          "text/html",
          "image/svg+xml");
      handlers.addHandler(gzipHandler);
    } else {
      handlers.addHandler(contentHandler);
    }

    server.setHandler(handlers);
  }

  private LifeCycle.Listener createLifecycleLogger(ServerConnector connector) {
    return new LifeCycle.Listener() {
      @Override
      public void lifeCycleStarted(LifeCycle event) {
        CONSOLE.info(
            "{}{}{}.",
            SERVICE_STARTED_ON,
            connector.getLocalPort(),
            connector.getHost() == null ? "" : " of interface " + connector.getHost());
      }

      @Override
      public void lifeCycleStopping(LifeCycle event) {
        CONSOLE.info("Service stopping...");
      }

      @Override
      public void lifeCycleStopped(LifeCycle event) {
        CONSOLE.info("Service stopped.");
      }

      @Override
      public void lifeCycleFailure(LifeCycle event, Throwable ex) {
        CONSOLE.trace("Server failed to start.", ex);

        try {
          JettyContainer.this.stop();
        } catch (Exception e) {
          CONSOLE.error("Could not stop the server.", e);
        }
      }
    };
  }

  private Server createServer() {
    QueuedThreadPool threadPool =
        new QueuedThreadPool() {
          private AtomicInteger tid = new AtomicInteger();

          @Override
          public Thread newThread(Runnable runnable) {
            return new Thread(
                () -> {
                  Thread.currentThread().setName("T" + tid.incrementAndGet());
                  runnable.run();
                });
          }
        };

    if (maxThreads != null) {
      threadPool.setMaxThreads(maxThreads);
    }

    Server server = new Server(threadPool);
    connector = new ServerConnector(server);
    connector.setPort(port);
    connector.setHost(host);
    if (idleTime != null) {
      connector.setIdleTimeout(idleTime);
    }
    server.addConnector(connector);
    server.addEventListener(createLifecycleLogger(connector));
    return server;
  }
}
