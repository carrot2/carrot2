

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


package com.dawidweiss.carrot.tools;


import org.put.util.net.http.FormActionInfo;
import org.put.util.net.http.FormParameters;
import org.put.util.net.http.HTTPFormSubmitter;
import org.put.util.net.http.Parameter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This command-line tool makes it easier to debug filter type components.
 */
public class QueryFilterComponent
{
    /**
     * Sends a query to an component.
     *
     * @param inputComponentServiceURL An URL to where the component service point is (must accept
     *        HTTP POST)
     * @param carrotRequestStream The data stream to be sent in a POST request as
     *        "carrot-xchange-data" parameter.
     *
     * @return An InputStream to what the component returned.
     *
     * @throws IOException If an error occurrs.
     */
    public InputStream queryInputComponent(URL componentServiceURL, Reader carrotRequest)
        throws IOException
    {
        FormActionInfo actionInfo = new FormActionInfo(componentServiceURL, "post");
        FormParameters queryArgs = new FormParameters();
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

        Parameter queryRequestXml = new Parameter("carrot-xchange-data", carrotRequest, false);

        queryArgs.addParameter(queryRequestXml);

        return submitter.submit(queryArgs, null, "UTF-8");
    }


    /**
     * Command-line entry point.
     */
    public static void main(String [] args)
    {
        InputStream requestXML = null;
        URL service;

        try
        {
            service = new URL(args[0]);

            if (args.length > 1)
            {
                try
                {
                    URL resourceURL = new URL(args[1]);
                    requestXML = resourceURL.openStream();
                }
                catch (MalformedURLException e)
                {
                    // attempt to open a file
                    requestXML = new FileInputStream(args[1]);
                }
            }
            else
            {
                requestXML = System.in;
            }
        }
        catch (Exception e)
        {
            System.err.println("An exception occurred: " + e.toString());
            System.err.println(
                "Usage: QueryFilterComponent [serviceURL] {request.xml (URL/ or a file)}"
            );
            System.err.println("[] - required, {} - optional.");

            return;
        }

        QueryFilterComponent me = new QueryFilterComponent();

        try
        {
            InputStream result = me.queryInputComponent(
                    service, new InputStreamReader(requestXML, "UTF-8")
                );

            if (result == null)
            {
                System.err.println("Error in response (null returned from the submitter)");
            }

            byte [] output = org.put.util.io.FileHelper.readFullyAndCloseInput(result);
            System.out.write(output);
            System.err.println("Bytes of response: " + output.length);
        }
        catch (IOException e)
        {
            System.err.println("An exception occurred when querying: " + e.toString());
        }
    }
}
