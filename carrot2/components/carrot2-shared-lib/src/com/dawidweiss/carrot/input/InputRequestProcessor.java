

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


package com.dawidweiss.carrot.input;


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;
import com.dawidweiss.carrot.util.AbstractRequestProcessor;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
            String carrotRequest = request.getParameter("carrot-request");

            Query query = Query.unmarshal(new StringReader(carrotRequest));

            Writer outputXml = new OutputStreamWriter(response.getOutputStream(), "UTF-8");

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
        catch (org.exolab.castor.xml.MarshalException e)
        {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Query could not be unmarshalled: " + e.toString()
            );
            org.apache.log4j.Logger.getLogger(this.getClass()).info(
                "Query could not be unmarshalled.", e
            );
        }
        catch (org.exolab.castor.xml.ValidationException e)
        {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST, "Query does not validate: " + e.toString()
            );
            org.apache.log4j.Logger.getLogger(this.getClass()).warn("Query does not validate.", e);
        }
    }


    // ------------------------------------------------------- protected section

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
    )
        throws Exception;
}
