
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.filter;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.dawidweiss.carrot.util.AbstractRequestProcessor;


/**
 * A filter request processor class. This is an abstract scheleton class which should be overriden
 * to include some processing. <code>processFilterRequest</code> gets called in response to user
 * query, override it as needed. Convenience methods for serializing input data to XML object
 * structure also exist. Please note that for optimum performance, you should override
 * <code>processFilterRequest</code> and start processing as soon as possible, not waiting for the
 * full XML to be available.
 */
public abstract class FilterRequestProcessor
    extends AbstractRequestProcessor
{
    /**
     * In this method, the carrot standard request stream must be parsed, and some XML result
     * should be returned to the output.
     *
     * @param carrotData An Carrot<sup>2</sup> data stream as passed in the POST request.
     * @param request Http request object.
     * @param response Http response object.
     * @param paramsBeforeData A Map object containing parameter names and their values (always
     *        embedded in Lists), sent in the POST stream before Carrot data. This may be used to
     *        customize processing, however is not a standard extension.
     */
    public abstract void processFilterRequest(
        InputStream carrotData, HttpServletRequest request, HttpServletResponse response,
        Map paramsBeforeData
    )
        throws Exception;

    /**
     * Convenience method for parsing XML stream.
     */
    protected Element parseXmlStream(InputStream stream, String encoding)
        throws IOException, UnsupportedEncodingException
    {
        final SAXReader reader = new SAXReader();
        reader.setValidation(false);

        try {
            return reader.read(
                    new InputStreamReader(stream, encoding)).getRootElement();
        } catch (DocumentException e) {
            throw new IOException("Error parsing XML: " + e.toString());
        }
    }


    /**
     * Convenience method for serializing XML stream.
     */
    protected void serializeXmlStream(Element root, OutputStream stream, String encoding)
        throws IOException
    {
        final OutputFormat format = new OutputFormat();
        format.setEncoding(encoding);
        format.setIndent(true);
        format.setIndentSize(2);
        final XMLWriter writer = new XMLWriter(stream, format);

        writer.write(root);
    }
    
    public static void removeChildren(Element element, String childrenNames) {
        List list = element.elements(childrenNames);
        for (Iterator i = list.iterator(); i.hasNext();) {
            element.remove(((Element) i.next())); 
        }
    }
}
