

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
import java.io.IOException;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Returns a test suite with all test cases covering
 * all available demo links.
 * 
 * @author Dawid Weiss
 */
public class DemoLinksTest extends TestCaseBase {

    public static class SingleDemoLink extends DemoLinkTestBase {
        private String url;

        public SingleDemoLink(String url) throws Exception {
            this.url = url;
        }
        
        public void runBare() throws Exception {
            try {        
                HttpUnitOptions.setScriptingEnabled(false);
                HttpUnitOptions.setDefaultCharacterSet("UTF-8");
                
                WebConversation wc = new WebConversation();
                WebResponse response = wc.getResponse(url);

                assertTrue("Two frames in the response", response.getFrameNames().length == 2);
                response = wc.getFrameContents("output");
                assertEquals("Response code 200", 200, response.getResponseCode());

                if (response.getText().indexOf("*C2TESTSUNHANDLEDERRORPAGE*") >= 0)
                {
                    log.error("Unhandled error page returned: " + response.getText());
                    fail("Unhandled error page for URL: " + url);
                }

                if (response.getText().indexOf("*C2TESTSERRORPAGE*") >= 0)
                {
                    log.error("Component error page returned: " + response.getText());
                    fail("Component error page returned for URL: " + url);
                }

                if (response.getText().indexOf("*PAGE_RENDERED_CORRECTLY*") < 0)
                {
                    // this is not the right input, mate...
                    log.error(
                        "Suspicious output (not ending with '*PAGE_RENDERED_CORRECTLY*') for URL: " + url + "\n"
                    );
                    fail("Suspicious output (not ending with '*PAGE_RENDERED_CORRECTLY*') for URL: " + url );
                }
            } finally {
                HttpUnitOptions.reset();
            }
        }
        
        public String getName() {
            return url;
        }
    }

    public DemoLinksTest(String s) throws IOException, ClassNotFoundException {
        super(s);
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();

        DemoLinksTest myself = new DemoLinksTest("");
        
        try {        
            HttpUnitOptions.setScriptingEnabled(false);
            HttpUnitOptions.setDefaultCharacterSet("UTF-8");
            
            WebConversation wc = new WebConversation();
            wc.getResponse(myself.getControllerURL().toExternalForm());
    
            WebLink [] links = wc.getFrameContents("controller").getMatchingLinks(
                    WebLink.MATCH_URL_STRING, "/demo.jsp"
                );
    
            assertTrue("A link to demo page from controller frame should exist.", links.length == 1);

            WebResponse demoPage = links[0].click();

            links = demoPage.getMatchingLinks(WebLink.MATCH_URL_STRING, "query=");

            myself.log.debug("Retrieved: " + links.length + " from demo page.");

            for (int i = 0; i < links.length; i++)
            {
                if (links[i].getURLString().indexOf("query=") != -1)
                {
                    myself.log.debug("Adding test link " + links[i].getURLString());
    
                    // Avoid a bug in HTTPUnit which prevents links from being rendered properly.
                    // this MAY result in incorrect URLs!
                    URL controller = myself.getControllerURL();
    
                    String decodedURL = controller.getProtocol() + "://" + controller.getHost()
                        + ":" + controller.getPort() + links[i].getURLString();

                    SingleDemoLink dlink = new SingleDemoLink( decodedURL );
                    suite.addTest(dlink);
                }
            }


        } finally {
            HttpUnitOptions.reset();
        }
        
        return suite;
    }
    
}
