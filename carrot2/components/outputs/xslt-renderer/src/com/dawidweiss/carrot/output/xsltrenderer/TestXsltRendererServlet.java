

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


package com.dawidweiss.carrot.output.xsltrenderer;


import com.dawidweiss.carrot.tools.QueryFilterComponent;
import java.io.FileInputStream;
import java.net.URL;


/**
 * Tests the XSLT renderer class by rendering a valid Carrot2 input against a chosen stylesheet.
 */
public class TestXsltRendererServlet
{
    public static void main(String [] args)
        throws Exception
    {
        QueryFilterComponent queryTool = new QueryFilterComponent();
        URL service = new URL("http://localhost:8080/xslt-renderer/service?stylesheet=dtree");

        String requestFile = "F:\\Repositories\\ophelia\\carrot2\\test\\sample results\\logika.xml";

        int loops = 0;

        org.put.util.time.ElapsedTimeTimer timer = new org.put.util.time.ElapsedTimeTimer();
        org.put.util.time.ElapsedTimeTimer avgt = new org.put.util.time.ElapsedTimeTimer();

        long tot = 0;

        while (loops++ < 100)
        {
            org.put.util.time.ElapsedTimeTimer timeri = new org.put.util.time.ElapsedTimeTimer();
            avgt.restart();
            org.put.util.io.FileHelper.readFully(
                queryTool.queryInputComponent(service, new java.util.HashMap(),
                    new FileInputStream(requestFile))
            );

            tot += avgt.elapsed();
            System.out.println("Request processing took: " + timeri.elapsedString() + " secs.");
        }

        System.out.println("Total: " + timer.elapsedString() + " sec.");
        System.out.println("Avg: " + ((double) tot / 100));
    }
}
