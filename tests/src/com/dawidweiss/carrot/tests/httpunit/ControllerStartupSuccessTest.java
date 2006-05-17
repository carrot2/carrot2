

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


package com.dawidweiss.carrot.tests.httpunit;


import com.meterware.httpunit.*;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;


/**
 * Tests whether Carrot2 controller has been successfully started.
 */
public class ControllerStartupSuccessTest
    extends TestCaseBase
{
    private WebConversation wc;

    public ControllerStartupSuccessTest(String s)
        throws IOException, ClassNotFoundException
    {
        super(s);
    }
    
    public void setUp() {
        this.wc = new WebConversation();
        HttpUnitOptions.setExceptionsThrownOnScriptError(false);
    }

    public void testStartPageExists()
        throws MalformedURLException, IOException, SAXException
    {
        WebResponse topFrame = wc.getResponse(getControllerURL().toExternalForm());

        // get the controller frame.
        List frames = Arrays.asList(topFrame.getFrameNames());
        assertTrue(frames.contains("output"));
        assertTrue(frames.contains("controller"));
    }


    public void testControllerPagesOk()
        throws MalformedURLException, IOException, SAXException
    {
        wc.getResponse(super.getControllerURL().toExternalForm());

        // get the controller frame.
        WebResponse controllerFrame = wc.getFrameContents("controller");
        WebLink [] links = controllerFrame.getLinks();

        log.debug(links.length + " links found on controller page. Following...");

        for (int i = 0; i < links.length; i++)
        {
            wc.setAuthorization("dweiss", "chief");
            log.debug(links[i].getURLString());
            wc.getCurrentPage();
        }
    }


    public void testUnhandledErrorPageDetection()
        throws MalformedURLException, IOException, SAXException
    {
        // request unexisting process
        WebResponse response = wc.getResponse(
                super.getControllerURL().toExternalForm()
                + "/index.jsp?query=data%20mining&processingChain=carrot2.procss.lsi-alltheweb-en&resultsRequested=100"
            );
        assertTrue("Two frames in the response", response.getFrameNames().length == 2);
        response = wc.getFrameContents("output");
        assertEquals("Response code 200", 200, response.getResponseCode());

        if (response.getText().indexOf("*C2TESTSUNHANDLEDERRORPAGE*") == -1)
        {
            log.error("Unhandler error remained undetected: " + response.getText());
            fail("Unhandled error not detected.");
        }
    }


    public void testComponentErrorPageDetection()
        throws MalformedURLException, IOException, SAXException
    {
        // malformed request (verbatim output)
        WebResponse response = wc.getResponse(
                super.getControllerURL().toExternalForm()
                + "/newsearch.do?query=data+mining&processingChain=carrot2.process.html-display.scoresort.x-carrot-clustering-groups&resultsRequested=100"
            );
        assertTrue("Two frames in the response", response.getFrameNames().length == 2);
        response = wc.getFrameContents("output");
        assertEquals("Response code 200", 200, response.getResponseCode());

        if (response.getText().indexOf("*C2TESTSERRORPAGE*") == -1)
        {
            log.error("Component error remained undetected: " + response.getText());
            fail("Component error not detected.");
        }
    }

}
