package com.dawidweiss.carrot.tests.httpunit.regression;


import java.io.IOException;
import java.net.*;
import java.util.*;

import org.xml.sax.SAXException;

import com.dawidweiss.carrot.tests.httpunit.AbstractTestCase;
import com.meterware.httpunit.*;


/**
 * Tests whether Carrot2 controller has been successfully started. 
 */
public class RandomCachedQueryTest 
    extends AbstractTestCase
{
    private final static int REPEATS = 60*60;
    
    public RandomCachedQueryTest(String s)
        throws IOException, ClassNotFoundException
    {
        super(s);
    }
    
    public void testRandomAccessToLinks()
        throws MalformedURLException, IOException, SAXException
    {
        HttpUnitOptions.setScriptingEnabled(false);
        HttpUnitOptions.setDefaultCharacterSet("UTF-8");
        try
        {
            WebConversation wc = new WebConversation();
            wc.getResponse( getControllerURL().toExternalForm() );

            WebLink [] links = wc.getFrameContents("controller").getMatchingLinks(WebLink.MATCH_URL_STRING, "/demo.jsp");
            
            assertTrue( "One link to demo page from controller frame", links.length == 1);
            
            WebResponse demoPage = links[0].click();
            
            links = demoPage.getLinks();
            
            log.debug("Retrieved: " + links.length + " from demo page.");

            List all = new ArrayList(links.length);
            for (int i=0;i<links.length;i++)
            {
                if (links[i].getURLString().indexOf("query=") != -1)
                {
                    all.add(links[i]);
                }
            }

            Random rnd = new Random( System.currentTimeMillis() );
            int repeats = REPEATS;
            Map errors = new HashMap();
            while (repeats-- > 0)
            {
                WebLink o = (WebLink) all.get(rnd.nextInt(all.size()));
                log.debug("[" + repeats + "] Trying demo query " + o.getURLString());

                URL controller = super.getControllerURL();
                
                String decodedURL = controller.getProtocol() + "://" + controller.getHost() + ":" + controller.getPort() + 
                    o.getURLString();
                
                WebResponse response = wc.getResponse(decodedURL);
                    
                assertTrue( "Two frames in the response", response.getFrameNames().length == 2);
                response = wc.getFrameContents("output");
                assertEquals("Response code 200", 200, response.getResponseCode());
                if (response.getText().indexOf("*C2TESTSUNHANDLEDERRORPAGE*") >= 0)
                {
                    log.error("Unhandled error page returned: " + response.getText());
                    String key = response.getURL() + " :: unhandled errors";
                    if (!errors.containsKey(key))
                        errors.put(key, new int [] { 0 });
                    ((int []) errors.get(key))[0]++;
                    // fail("Unhandled error page returned: " + response.getURL());
                }
                if (response.getText().indexOf("*C2TESTSERRORPAGE*") >= 0)
                {
                    log.error("Component error page returned: " + response.getText());
                    String key = response.getURL() + " :: component error page errors";
                    if (!errors.containsKey(key))
                        errors.put(key, new int [] { 0 });
                    ((int []) errors.get(key))[0]++;
                    // fail("Component error page returned: " + response.getURL());
                }
            }
            if (errors.keySet().size() > 0)
            {
                for (Iterator i = errors.keySet().iterator();i.hasNext();)
                {
                    String key = (String) i.next();
                    log.error( key + " times: " + ((int[]) errors.get(key))[0]);
                }
                fail("Randomized demo query test failed.");
            }
        }
        finally
        {
            HttpUnitOptions.reset();
        }
    }

}


