

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
import java.net.URL;
import java.util.Arrays;
import java.util.List;


/**
 * Tests whether Carrot2 controller has been successfully started.
 */
public class ControllerStartupSuccessTest
    extends AbstractTestCase
{
    public ControllerStartupSuccessTest(String s)
        throws IOException, ClassNotFoundException
    {
        super(s);
    }

    public void testStartPageExists()
        throws MalformedURLException, IOException, SAXException
    {
        WebConversation wc = new WebConversation();
        WebResponse topFrame = wc.getResponse(getControllerURL().toExternalForm());

        // get the controller frame.
        List frames = Arrays.asList(topFrame.getFrameNames());
        assertTrue(frames.contains("output"));
        assertTrue(frames.contains("controller"));
    }


    public void testControllerPagesOk()
        throws MalformedURLException, IOException, SAXException
    {
        WebConversation wc = new WebConversation();
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
        WebConversation wc = new WebConversation();

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
        WebConversation wc = new WebConversation();

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


    public void testAllDemoLinks()
        throws MalformedURLException, IOException, SAXException
    {
        HttpUnitOptions.setScriptingEnabled(false);
        HttpUnitOptions.setDefaultCharacterSet("UTF-8");

        try
        {
            WebConversation wc = new WebConversation();
            wc.getResponse(getControllerURL().toExternalForm());

            WebLink [] links = wc.getFrameContents("controller").getMatchingLinks(
                    WebLink.MATCH_URL_STRING, "/demo.jsp"
                );

            assertTrue("One link to demo page from controller frame", links.length == 1);

            WebResponse demoPage = links[0].click();

            links = demoPage.getMatchingLinks(WebLink.MATCH_URL_STRING, "query=");

            log.debug("Retrieved: " + links.length + " from demo page.");

            StringBuffer errors = new StringBuffer();

            for (int i = 0; i < links.length; i++)
            {
                if (links[i].getURLString().indexOf("query=") != -1)
                {
                    log.debug("Trying demo query " + links[i].getURLString());

                    // Avoid a bug in HTTPUnit which prevents links from being rendered properly.
                    // this MAY result in incorrect URLs!
                    URL controller = super.getControllerURL();

                    String decodedURL = controller.getProtocol() + "://" + controller.getHost()
                        + ":" + controller.getPort() + links[i].getURLString();

                    WebResponse response = wc.getResponse(decodedURL);

                    assertTrue("Two frames in the response", response.getFrameNames().length == 2);
                    response = wc.getFrameContents("output");
                    assertEquals("Response code 200", 200, response.getResponseCode());

                    if (response.getText().indexOf("*C2TESTSUNHANDLEDERRORPAGE*") >= 0)
                    {
                        log.error("Unhandled error page returned: " + response.getText());
                        errors.append("Unhandled error page for URL: " + decodedURL + "\n");
                    }

                    if (response.getText().indexOf("*C2TESTSERRORPAGE*") >= 0)
                    {
                        log.error("Component error page returned: " + response.getText());
                        errors.append(
                            "Component error page returned for URL: " + decodedURL + "\n"
                        );
                    }

                    if (response.getText().indexOf("*PAGE_RENDERED_CORRECTLY*") < 0)
                    {
                        // this is not the right input, mate...
                        log.error(
                            "Suspicious output (not ending with '*PAGE_RENDERED_CORRECTLY*') for URL: "
                            + decodedURL + "\n"
                        );
                        errors.append(
                            "Suspicious output (not ending with '*PAGE_RENDERED_CORRECTLY*') for URL: "
                            + decodedURL + "\n"
                        );
                    }
                }
            }

            if (errors.length() > 0)
            {
                fail(errors.toString());
            }
        }
        finally
        {
            HttpUnitOptions.reset();
        }
    }
}
