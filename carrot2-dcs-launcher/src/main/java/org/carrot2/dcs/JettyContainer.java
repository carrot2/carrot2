package org.carrot2.dcs;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.BindException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.carrot2.dcs.Loggers.CONSOLE;

class JettyContainer {
  private final int port;
  private final Path webappContext;

  JettyContainer(int port, Path context) {
    this.port = port;
    this.webappContext = context;
  }

  void start() throws Exception {
    Server server = createServer();

    addContexts(server, webappContext);

    server.start();
    server.join();
  }

  private void addContexts(Server server, Path context) {
    if (!Files.isRegularFile(context.resolve("WEB-INF").resolve("web.xml"))) {
      throw new RuntimeException("Not a web application context folder: "
        + context.toAbsolutePath());
    }

    ArrayList<ContextHandler> contexts = new ArrayList<>();

    WebAppContext ctx = new WebAppContext("dcs", "/");
    ctx.setThrowUnavailableOnStartupException(true);
    ctx.setWar(context.normalize().toAbsolutePath().toString());
    contexts.add(ctx);

    server.setHandler(new ContextHandlerCollection(contexts.toArray(new ContextHandler[0])));
  }

  private LifeCycle.Listener createLifecycleLogger(Server server, ServerConnector connector) {
    return new AbstractLifeCycle.AbstractLifeCycleListener() {
      @Override
      public void lifeCycleStarted(LifeCycle event) {
        CONSOLE.info("Service started on port {}.", connector.getLocalPort());
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
      public void lifeCycleFailure(LifeCycle event, Throwable cause) {
        if (cause instanceof BindException) {
          CONSOLE.error("Network port binding error: " + cause.getMessage());
        } else {
          CONSOLE.error("Server failed to start.", cause);
        }

        try {
          server.stop();
        } catch (Exception e) {
          CONSOLE.error("Could not stop the server.", e);
        }
      }
    };
  }

  private Server createServer() {
    QueuedThreadPool threadPool = new QueuedThreadPool() {
      private AtomicInteger tid = new AtomicInteger();

      @Override
      protected Thread newThread(Runnable runnable) {
        return new Thread(() -> {
          Thread.currentThread().setName("T" + tid.incrementAndGet());
          runnable.run();
        });
      }
    };
    threadPool.setMaxThreads(50);

    Server server = new Server(threadPool);

    ServerConnector connector = new ServerConnector(server);
    connector.setPort(port);
    server.addConnector(connector);

    server.addLifeCycleListener(createLifecycleLogger(server, connector));

    return server;
  }
}
