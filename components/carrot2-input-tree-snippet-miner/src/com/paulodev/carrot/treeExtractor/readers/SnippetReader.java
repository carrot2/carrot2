package com.paulodev.carrot.treeExtractor.readers;

/**
 * <p>Description: Snippet reader based on tree analysis</p>
 * <p>Copyright: Copyright (c) 2002 Dawid Weiss, Institute of Computing Science, Poznan University of Technology</p>
 * <p>Company: Institute of Computing Science, Poznan University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.dawidweiss.carrot.util.jdom.JDOMHelper;
import com.dawidweiss.carrot.util.net.http.*;
import com.paulodev.carrot.util.html.parser.*;
import org.jdom.*;
import com.paulodev.carrot.httpmultipage.HttpMultiPageReader;

import com.paulodev.carrot.treeExtractor.extractors.*;

public class SnippetReader {
  protected final Logger log = Logger.getLogger(getClass());

  Element config;
  HttpMultiPageReader reader;
  TreeExtractor extractor;

  public SnippetReader(Element config) throws java.net.MalformedURLException {
    this.config = config;

    FormActionInfo actionInfo = new FormActionInfo(JDOMHelper.getElement(
        "/service/request", config));
    FormParameters queryParameters = new FormParameters(JDOMHelper.getElement(
        "/service/request/parameters", config));
    HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

    reader = new HttpMultiPageReader(submitter, queryParameters);

    Vector tokens = new Vector();
    tokens.add("url");
    tokens.add("description");
    tokens.add("title");
    extractor = new TreeExtractor(JDOMHelper.getElement(
        "/service/response/extractor", config), tokens.elements());
  }

  /**
   * Writes a Carrot2 XML stream to the given Writer.
   */
  public void getSnippetsAsCarrot2XML(final Writer outputStream, String query,
                                      final int snippetsNeeded)
                                      throws Exception
  {
      outputStream.write("<searchresult>\n");

      // output the optional query tag at the beginning of the document.
      outputStream.write("<query requested-results=\"" + snippetsNeeded +
                         "\"><![CDATA["
                         + query + "]]></query>\n");
      int rec = 0;
      String encoding;
      if ( (encoding = JDOMHelper.getStringFromJDOM(
          "/service/request#encoding",
          config, false)) == null)
      {
          encoding = "iso8859-1";

      }
      String resEncoding;
      if ( (resEncoding = JDOMHelper.getStringFromJDOM(
          "/service/response#encoding",
          config, false)) == null)
      {
          resEncoding = "iso8859-1";

      }
      Enumeration e =
          reader.getQueryResultsPages(query, (int)Math.round(snippetsNeeded * 1.3), encoding,
                                      JDOMHelper.getElement(
          "/service/response/pageinfo", config));

      int hasSnippets = 0;
      while (e.hasMoreElements() && hasSnippets < snippetsNeeded)
      {
          InputStream i = (InputStream)e.nextElement();
          HTMLTokenizer tok = new HTMLTokenizer(i, resEncoding);
          HTMLTree t = new HTMLTree(tok);
          Enumeration snipps = extractor.parseTree(t, hasSnippets);
          while (snipps.hasMoreElements() && hasSnippets < snippetsNeeded)
          {
              hasSnippets++;
              outputStream.write(snipps.nextElement().toString());
          }
      }
      outputStream.write("</searchresult>\n");
      log.debug("Tree extractor finished ok");
  }

}