package com.paulodev.carrot.treeExtractor;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.jdom.*;
import org.jdom.input.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;
import com.paulodev.carrot.treeExtractor.readers.SnippetReader;

/**
 * @author Paweł Kowalik
 * @author Dawid Weiss
 */

public class SnippetReaderServlet
    extends HttpServlet {

  protected final Logger log = Logger.getLogger(getClass());

  private HashMap registredEngines = new HashMap();
  private HashMap engineFiles = new HashMap();

  /**
   * Initialization of service provider.
   */
  public void init(ServletConfig servletConfig) throws ServletException {
    String errorMessage;

    try {
      String engines = servletConfig.getInitParameter("searchEngineDescriptors");
      if (engines == null) {
        errorMessage =
            "Required parameter 'searchEngineDescriptors' not found.";
        log.error(errorMessage);
        return;
      }

      File servicesDir = new File(servletConfig.getServletContext().getRealPath(
          engines));
      if (servicesDir.isDirectory() == false) {
        errorMessage =
            "'searchEngineDescriptors' parameter does not point to a directory.";
        log.error(errorMessage);
        return;
      }

      File[] engineList = servicesDir.listFiles(
          new java.io.FilenameFilter() {
        public boolean accept(File dir, String name) {
          if (name.endsWith(".xml")) {
            return true;
          }
          else {
            return false;
          }
        }
      }
      );

      log.info("Tree snippet readers servlet: engine count: " +
               engineList.length);
      for (int i = 0; i < engineList.length; i++) {
        try {
          // add an instance of this class as default handler
          SAXBuilder builder = new SAXBuilder();
          Document config;
          builder.setValidation(false);
          config = builder.build(engineList[i]);
          SnippetReader snippetReaderService = new SnippetReader(config.
              getRootElement());

          String engineName = engineList[i].getName();
          engineName = engineName.substring(0, engineName.lastIndexOf(".xml"));

          registredEngines.put(engineName, snippetReaderService);
          engineFiles.put(engineName, engineList[i]);

          log.info("Added service " + engineName);
        }
        catch (Exception e) {
          log.error("Exception when adding service: " + engineList[i].getName(),
                    e);
        }
      }
    }
    catch (Exception x) {
      log.fatal("Exception initializing SnippetReader servlet.", x);
    }
  }

  /**
   * Map the POST request to a Carrot2 call
   */
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws
      ServletException, IOException {
    String method = req.getPathInfo();

    req.setCharacterEncoding("UTF-8");

    String engineName = method.substring(1);
    log.fatal("Processing request method: " + method + ", engine name: " +
              engineName);

    // process Carrot2 request.
    Object engine = registredEngines.get(engineName);
    if (engine == null || ! (engine instanceof SnippetReader)) {
      // this will throw a parser exception on client side, but
      // at least indicate the cause of the error.
      res.getOutputStream().write( ("Engine " + engine +
          " not available or does not support Carrot2.").getBytes());
    }
    else {
      try {
        // process request. Let container parse POST parameters (they're not streams anyway).
        String reqxml = req.getParameter("carrot-request");
        Query query = Query.unmarshal(new StringReader(reqxml));

        OutputStream output = res.getOutputStream();
        Writer w = new OutputStreamWriter(output, "UTF-8");
        ( (SnippetReader) engine).getSnippetsAsCarrot2XML(w,
            query.getContent(),
            query.hasRequestedResults() ? query.getRequestedResults() : 100);

        w.flush();
        output.close();
      }
      catch (Exception e) {
        log.error("Exception when processing request.", e);
        if (res.isCommitted()==false) {
            // send error code
            res.resetBuffer();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Could not process request: " + e.toString());
        }
        return; 
      }
    }
  }

  /**
   * GET works as POST - for debugging purposes only
   */
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws
      ServletException, IOException {
    doPost(req, res);
  }
}