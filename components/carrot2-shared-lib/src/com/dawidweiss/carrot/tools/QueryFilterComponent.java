

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

import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.Iterator;
import java.util.Map;


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
     * @param params Configuration params for the filter component.
     *
     * @return An InputStream to what the component returned.
     *
     * @throws IOException If an error occurrs.
     */
    public InputStream queryInputComponent(URL componentServiceURL, Map params, InputStream carrotRequest)
        throws IOException
    {
        FormActionInfo actionInfo = new FormActionInfo(componentServiceURL, "post");
        FormParameters queryArgs = new FormParameters();
        
        for (Iterator i = params.keySet().iterator();i.hasNext();)
        {
            String key = (String) i.next();
            queryArgs.addParameter(new Parameter(key, params.get(key), false));
        }
        
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
        URL service = null;
        Map params = new HashMap();

        if (args.length == 0) {
            help();
            return;
        }
        
        int i = 0;
        try
        {
            while (i<args.length) {
                if ("-service".equalsIgnoreCase(args[i])) {
                    if (service != null) {
                        System.err.println("Service URL can be defined only once.");
                        return;
                    }
                    service = new URL(args[i+1]);
                    i += 2;
                }
                else
                if ("-param".equalsIgnoreCase(args[i])) {
                    if (params.containsKey(args[i+1])) {
                        System.err.println("Param value already defined for key: " + args[i+1]);
                        return;
                    }
                    params.put(args[i+1], args[i+2]);
                    i+=3;
                }
                else
                if ("-help".equalsIgnoreCase(args[i])) {
                    help();
                    return;
                }
                else {
                    if (requestXML != null) {
                        System.err.println("Input stream already defined, cannot accept option: "
                            + args[i]);
                        return;
                    }
                    File inputStream = new File(args[i]);
                    if (!inputStream.exists() || !inputStream.canRead() || !inputStream.isFile()) {
                        System.err.println("Something wrong with the input stream: "
                            + inputStream.getAbsolutePath());
                        return;
                    }
                    System.err.println("Reading input from: "
                    	+ args[i]);
                    requestXML = new BufferedInputStream( new FileInputStream( inputStream));
                    i++;
                }
            }
            
            if (service == null) {
                System.err.println("No service URL.");
                return;
            }
            if (requestXML == null)
                requestXML = System.in;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Missing argument for option: "
                + args[i]);
            return;
        }
        catch (Exception e)
        {
            System.err.println("An exception occurred (use '-help' for help): " + e.toString());
            return;
        }

        QueryFilterComponent me = new QueryFilterComponent();

        try
        {
            InputStream result = me.queryInputComponent( service, params, requestXML );

            if (result == null)
            {
                System.err.println("Error in response (null returned from the HTTP POST submitter)");
            }

            byte [] output = org.put.util.io.FileHelper.readFullyAndCloseInput(result);
            System.out.write(output);
            System.out.flush();
            System.err.println("Bytes of response: " + output.length);
        }
        catch (IOException e)
        {
            System.err.println("An exception occurred when querying: " + e.toString());
        }
        finally {
            if (requestXML != System.in) {
                try
                {
                    requestXML.close();
                }
                catch (IOException e1)
                {
                }
            }
        }
    }
    
    
    public static void help() {
        System.err.println(
            "Usage: QueryFilterComponent [-service url] [-param name value] {request file XML}"
        );
        System.err.println("[] - required, {} - optional (if not present, stdio is read)");
    }
}
