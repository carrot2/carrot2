package com.paulodev.carrot.treeSnippetMiner;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import java.io.*;
import java.util.*;

import org.jdom.*;
import com.paulodev.carrot.httpmultipage.*;


import com.dawidweiss.carrot.util.jdom.JDOMHelper;
import com.dawidweiss.carrot.util.net.http.*;
import com.paulodev.carrot.util.html.parser.*;

public class pageGrabber
{
    private int snippetsOnPage;
    private Element config;
    private HttpMultiPageReader reader;
    private String resPath;

    public pageGrabber(Element config, String resPath)
        throws java.net.MalformedURLException
    {
        this.config = config;
        this.resPath = resPath;

        FormActionInfo actionInfo = new FormActionInfo(JDOMHelper.getElement(
            "/service/request", config));
        FormParameters queryParameters = new FormParameters(JDOMHelper.
            getElement(
            "/service/request/parameters", config));
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);
        reader = new HttpMultiPageReader(submitter, queryParameters);
    }

    public HTMLTree getPage(String query)
        throws gnu.regexp.REException, java.io.IOException, java.lang.Exception
    {
        String encoding, resEncoding;
        if ( (encoding = JDOMHelper.getStringFromJDOM(
            "/service/request#encoding",
            config, false)) == null)
        {
            encoding = "iso8859-1";
        }
        if ( (resEncoding = JDOMHelper.getStringFromJDOM(
            "/service/response#encoding",
            config, false)) == null)
        {
            resEncoding = "iso8859-1";
        }
        String tmp = JDOMHelper.getStringFromJDOM(
            "/service/response/pageinfo/expected-results-per-page", config, false);
        snippetsOnPage = Integer.parseInt(tmp);
        Enumeration e = reader.getQueryResultsPages(query, snippetsOnPage * 2,
            encoding,
            JDOMHelper.getElement(
            "/service/response/pageinfo", config));
        boolean firstSkip = true;
        if (e != null)
        {
            while (e.hasMoreElements())
            {
                InputStream i = (InputStream)e.nextElement();
                if (firstSkip)
                {
                    firstSkip = false;
                    continue;
                }
                FileOutputStream ou = new FileOutputStream(resPath + "source.html");
                ByteArrayOutputStream tmpStream = new ByteArrayOutputStream(i.available());
                byte[] buf = new byte[10];
                int read = 0;
                while ((read = i.read(buf, 0, buf.length)) != -1)
                    tmpStream.write(buf, 0, read);
                ou.write(tmpStream.toByteArray(), 0, tmpStream.size());
                ou.close();
                HTMLTokenizer tok = new HTMLTokenizer(new ByteArrayInputStream(tmpStream.toByteArray(), 0, tmpStream.size()),
                    resEncoding);
                HTMLTree tr = new HTMLTree(tok);
                try {
                  OutputStream st = new java.io.FileOutputStream(resPath + "parsed.html");
                  st.write(tr.getRootNode().toString().getBytes());
                  st.close();
                }
                catch (java.io.IOException e1) {
                }
                return tr;
            }
        }
        return null;
    }

    public int getSnippetsOnPage()
    {
        return snippetsOnPage;
    }
}