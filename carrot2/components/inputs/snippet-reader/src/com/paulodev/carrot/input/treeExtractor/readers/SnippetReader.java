

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.paulodev.carrot.input.treeExtractor.readers;


import com.paulodev.carrot.input.treeExtractor.extractors.TreeExtractor;
import com.paulodev.carrot.input.treeExtractor.extractors.htmlParser.HTMLTokenizer;
import com.paulodev.carrot.input.treeExtractor.extractors.htmlParser.HTMLTree;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.put.snippetreader.readers.HtmlMultipage.HttpMultiPageReader;
import org.put.util.net.http.*;
import org.put.util.xml.JDOMHelper;
import java.io.InputStream;
import java.io.Writer;
import java.util.Enumeration;


public class SnippetReader
{
    protected final Logger log = Logger.getLogger(getClass());
    Element config;
    HttpMultiPageReader reader;
    TreeExtractor extractor;

    public SnippetReader(Element config)
        throws java.net.MalformedURLException
    {
        this.config = config;

        FormActionInfo actionInfo = new FormActionInfo(
                JDOMHelper.getElement("/service/request", config)
            );
        FormParameters queryParameters = new FormParameters(
                JDOMHelper.getElement("/service/request/parameters", config)
            );
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

        reader = new HttpMultiPageReader(submitter, queryParameters);

        extractor = new TreeExtractor(JDOMHelper.getElement("/service/response/extractor", config));
    }

    /**
     * Writes a Carrot2 XML stream to the given Writer.
     */
    public void getSnippetsAsCarrot2XML(
        final Writer outputStream, String query, final int snippetsNeeded
    )
        throws Exception
    {
        outputStream.write("<searchresult>\n");

        // output the optional query tag at the beginning of the document.
        outputStream.write(
            "<query requested-results=\"" + snippetsNeeded + "\"><![CDATA[" + query
            + "]]></query>\n"
        );

        try
        {
            String encoding;

            if (
                (encoding = JDOMHelper.getStringFromJDOM(
                            "/service/request#encoding", config, false
                        )) == null
            )
            {
                encoding = "iso8859-1";
            }

            String resEncoding;

            if (
                (resEncoding = JDOMHelper.getStringFromJDOM(
                            "/service/response#encoding", config, false
                        )) == null
            )
            {
                resEncoding = "iso8859-1";
            }

            Enumeration e = reader.getQueryResultsPages(
                    query, snippetsNeeded, encoding,
                    JDOMHelper.getElement("/service/response/pageinfo", config)
                );

            if (e != null)
            {
                final float warnLevel = Float.parseFloat(
                        JDOMHelper.getStringFromJDOM(
                            "/service/response/pageinfo/warn-when-below", config, true
                        )
                    );
                int hasSnippets = 0;

                while (e.hasMoreElements())
                {
                    InputStream i = (InputStream) e.nextElement();
                    HTMLTokenizer tok = new HTMLTokenizer(i, resEncoding);
                    HTMLTree t = new HTMLTree(tok);
                    Enumeration snipps = extractor.parseTree(t, hasSnippets);

                    while (snipps.hasMoreElements())
                    {
                        hasSnippets++;
                        outputStream.write(snipps.nextElement().toString());
                    }
                }
            }

            outputStream.write("</searchresult>\n");
            log.debug("Tree extractor finished ok");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
