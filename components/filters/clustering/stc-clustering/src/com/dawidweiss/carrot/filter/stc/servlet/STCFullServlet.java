

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


package com.dawidweiss.carrot.filter.stc.servlet;


import com.dawidweiss.carrot.filter.stc.Processor;
import org.apache.log4j.Logger;
import org.xml.sax.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;


/**
 * Performs full Suffix Tree Clustering algorithm.
 */
public class STCFullServlet
    extends com.dawidweiss.carrot.filter.FilterRequestProcessor
{
    private final Logger log = Logger.getLogger(this.getClass());

    /**
     * Sets the servlet configuration. This method is invoked by template class instantiating the
     * request processor.
     */
    public void setServletConfig(ServletConfig servletConfig)
    {
        super.setServletConfig(servletConfig);
    }


    /**
     * Processes a Carrot2 request.
     */
    public void processFilterRequest(
        InputStream carrotData, HttpServletRequest request, HttpServletResponse response,
        Map paramsBeforeData
    )
        throws Exception
    {
        Processor p = new Processor();
        SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();
        XMLReader reader = parser.getXMLReader();

        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setErrorHandler(p);
        reader.setContentHandler(p);

        OutputStreamWriter ow = null;

        try
        {
            ow = new OutputStreamWriter(
                    new com.dawidweiss.util.io.NeverBlockingOutputStream(
                        response.getOutputStream()
                    ), "UTF-8"
                );

            ow.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            p.setOutput(ow);

            InputSource source = new InputSource(carrotData);
            source.setEncoding("UTF-8");
            reader.parse(source);
        }
        catch (Exception e)
        {
            log.error("Internal server error.", e);
            throw e;
        }
        finally
        {
            log.debug("closing.");

            if (ow != null)
            {
                ow.close();
            }

            log.debug("finished.");
        }
    }
}
