

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


package com.dawidweiss.carrot.filter.textonly;


import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


/**
 * This is an example of a filter component for parsing snippets and removing all weird characters.
 * Sentence-borders (.!?) are left intact.
 */
public class TextOnlyFilter
    extends com.dawidweiss.carrot.filter.FilterRequestProcessor
{
    /**
     * Filters Carrot2 XML data as specified in class description.
     *
     * @param carrotData A valid InputStream to search results data as specified in the Manual.
     * @param request Http request which caused this processing (not used in this filter)
     * @param response Http response for this request
     * @param params A map of parameters sent before data stream (unused in this filter)
     */
    public void processFilterRequest(
        InputStream carrotData, HttpServletRequest request, HttpServletResponse response, Map params
    )
        throws Exception
    {
        SAXHandler p = new SAXHandler();
        SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();
        XMLReader reader = parser.getXMLReader();

        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setErrorHandler(p);
        reader.setContentHandler(p);

        OutputStreamWriter ow = null;

        try
        {
            //
            // The NeverBlockingOutputStream is needed to avoid a HTTP deadlock problem
            // (HTTP is specification-wise a request-response protocol, while we're trying
            // to stream the response while the input is still read.
            //
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
        finally
        {
            if (ow != null)
            {
                ow.close();
            }
        }
    }
}
