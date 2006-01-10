
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.tools;


import java.io.*;
import java.net.URL;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.Query;
import com.dawidweiss.carrot.util.net.http.*;


/**
 * This command-line tool makes it easier to debug input type components.
 */
public class QueryInputComponent
{
    /**
     * Sends a query to an input component.
     *
     * @param inputComponentServiceURL An URL to where the input component service point is.
     * @param queryString The query
     * @param resultsRequested Number of requested results. This should be reasonably low
     *
     * @return An InputStream to what the component returned.
     *
     * @throws IOException If an error occurrs.
     */
    public InputStream queryInputComponent(
        URL inputComponentServiceURL, String queryString, int resultsRequested
    )
        throws IOException
    {
        FormActionInfo actionInfo = new FormActionInfo(inputComponentServiceURL, "post");
        FormParameters queryArgs = new FormParameters();
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

        final StringWriter sw = new StringWriter();
        Query query = new Query(queryString, resultsRequested, true);
        query.marshal(sw);

        Parameter queryRequestXml = new Parameter(
                "carrot-request", sw.getBuffer().toString(), false
            );
        queryArgs.addParameter(queryRequestXml);

        InputStream data = submitter.submit(queryArgs, null, "UTF-8");

        if (data == null)
        {
            throw new IOException(
                "Server returned suspicious HTTP response code: ("
                + ((java.net.HttpURLConnection) submitter.getConnection()).getResponseCode() + ") "
                + ((java.net.HttpURLConnection) submitter.getConnection()).getResponseMessage()
            );
        }

        return data;
    }


    /**
     * Command-line entry point.
     */
    public static void main(String [] args)
    {
        URL url;
        int resultsNum;
        StringBuffer query;

        try
        {
            url = new URL(args[0]);
            resultsNum = Integer.parseInt(args[1]);

            query = new StringBuffer();

            for (int i = 2; i < args.length; i++)
            {
                query.append(args[i]);
            }

            if (query.length() == 0)
            {
                throw new RuntimeException("No query terms.");
            }
        }
        catch (Exception e)
        {
            System.err.println("An exception occurred: " + e.toString());
            System.err.println(
                "Usage: QueryInputComponent [serviceURL] [requested results number] [query terms]"
            );

            return;
        }

        QueryInputComponent me = new QueryInputComponent();

        try
        {
            InputStream result = me.queryInputComponent(url, query.toString(), resultsNum);

            byte [] output = com.dawidweiss.carrot.util.common.StreamUtils.readFullyAndCloseInput(result);
            System.out.write(output);
        }
        catch (IOException e)
        {
            System.err.println("An exception occurred when querying: " + e.toString());
        }
    }
}
