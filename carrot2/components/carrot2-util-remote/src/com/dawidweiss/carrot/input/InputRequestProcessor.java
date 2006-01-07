
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
package com.dawidweiss.carrot.input;


import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.Query;
import com.dawidweiss.carrot.util.AbstractRequestProcessor;


/**
 * An input request processor class. This interface can be used in conjunction with template
 * servlet/ command line request
 */
public abstract class InputRequestProcessor
    extends AbstractRequestProcessor
{
    /**
     * In this method, the carrot standard request stream must be parsed, and some XML result
     * should be returned to the output.
     */
    public void processInputRequest(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        try
        {
            final String carrotRequest = request.getParameter("carrot-request");
            final SAXReader reader = new SAXReader();
            final Element root = reader.read(new StringReader(carrotRequest)).getRootElement();

            final Query query = Query.unmarshal(root);
            final Writer outputXml = new OutputStreamWriter(response.getOutputStream(), "UTF-8");

            try
            {
                processQuery(
                    query.getContent(),
                    query.hasRequestedResults() ? query.getRequestedResults()
                                                : 100, outputXml, request
                );
            }
            finally
            {
                try
                {
                    outputXml.close();
                }
                catch (java.io.IOException e)
                {
                }
            }
        }
        catch (Exception e)
        {
            if (e instanceof DocumentException
                    || e instanceof IllegalArgumentException) {
                org.apache.log4j.Logger.getLogger(this.getClass()).info(
                        "Query could not be unmarshalled.", e
                    );
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Query could not be unmarshalled: " + e.toString()
                );
            } else {
                org.apache.log4j.Logger.getLogger(this.getClass()).info(
                        "Internal error occurred: ", e
                );
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Internal error occurred: " + e.toString()
                    );
            }
        }
    }

    /**
     * Converts query request parameters from a map of string arrays
     * to normal strings (if the array is of size 1).
     */
    protected Map extractParameters( HttpServletRequest request ) {
        HashMap dupl = new HashMap();
        Map params = request.getParameterMap();
        for (Iterator i = params.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();
            Object value = params.get(name);
            if (value instanceof String[]) {
                String [] v = (String[]) value;
                if (v.length==1) {
                    dupl.put(name, v[0]);
                } else {
                    dupl.put(name, v);
                }
            } else {
                dupl.put(name,value);
            }
        }
        return dupl;
    }
    
    /**
     * Process the query. Override this method and return a result for the query and requested
     * number of documents.
     *
     * @param query The query
     * @param requestedNumberOfResults Number of requested results or 0 if it has not been
     *        specified
     * @param outputXml Output stream to which the result should be written.
     * @param request HTTP request, which caused this processing
     */
    protected abstract void processQuery(
        String query, int requestedNumberOfResults, Writer outputXml, HttpServletRequest request
    ) throws Exception;
}
