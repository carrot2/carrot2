

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


package com.dawidweiss.carrot.tests.httpunit.regression;


import com.dawidweiss.carrot.tests.httpunit.TestCaseBase;
import java.io.IOException;


/**
 * Tests STC component (because there were shamelessly large number of bugs around it). This test
 * is normally not active.
 */
public class STCComponentTest
    extends TestCaseBase
{
    private int RETRIES = 20;

    public STCComponentTest(String s)
        throws IOException, ClassNotFoundException
    {
        super(s);
    }

    public void testDummySoThatJUnitDoesNotComplain()
    {
    }

/*
   public void testDirectSTC()
       throws Exception
   {
       Socket s = new Socket(getControllerURL().getHost(), getControllerURL().getPort());
       InputStream is = s.getInputStream();
       OutputStream os = s.getOutputStream();

       byte [] correct = org.put.util.io.FileHelper.readFully(
       this.getClass().getResourceAsStream("stcgood.in"));
       byte [] in = org.put.util.io.FileHelper.readFully(
           this.getClass().getResourceAsStream("stcfeed.in"));
       os.write(in);

       // get the response
       byte [] bytes = org.put.util.io.FileHelper.readFully( is );

           log.debug(new String(bytes));

           for (int i=0;i<bytes.length;i++)
           {
               if (bytes[i] != correct[i])
                   fail("Files differ: " + bytes.length);
           }
       }
 */
/*
   public void testDirectSTC()
       throws Exception
   {
       int iter = 0;
       while (true)
       {
           HTTPClient.HTTPConnection conn = new HTTPClient.HTTPConnection("localhost", 8080);

           byte [] in = org.put.util.io.FileHelper.readFully(
               this.getClass().getResourceAsStream("stcfeed.in"));
           byte [] correct = org.put.util.io.FileHelper.readFully(
               this.getClass().getResourceAsStream("stcgood.in"));

           String s = "carrot-xchange-data=" +
               new String(org.put.util.net.URLEncoding.encode(in));
           HTTPClient.HTTPResponse resp = conn.Post("/stc/service/fullstc", s.getBytes());

               byte [] data = resp.getData();

               File f = new File("F:\\fufu");
               OutputStream os = new FileOutputStream( f );
               os.write(data);
               os.close();

               assertEquals( correct.length, data.length );
               log.debug("iter=" + iter);
               iter++;
           }
       }
 */

    /**
     * Test STC component (because there were so many problems with it).
     */

/*
   public void testManyQueriesToSTCComponent()
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

               links = demoPage.getMatchingLinks(WebLink.MATCH_URL_STRING, "carrot2.process.stc-full");

               log.debug("Retrieved: " + links.length + " from demo page.");


               for (int times = RETRIES ; times > 0 ; times--)
               {
                   for (int i=0;i<links.length;i++)
                   {
                       log.debug("[" + times + "] STC query " + links[i].getURLString());

                       // Avoid a bug in HTTPUnit which prevents links from being rendered properly.
                       // this MAY result in incorrect URLs!
                       URL controller = super.getControllerURL();

                       String decodedURL = controller.getProtocol() + "://" + controller.getHost() + ":" + controller.getPort() +
                           links[i].getURLString();

                       WebResponse response = wc.getResponse(decodedURL);

                       assertTrue( "Two frames in the response", response.getFrameNames().length == 2);
                       response = wc.getFrameContents("output");
                       assertEquals("Response code 200", 200, response.getResponseCode());
                       if (response.getText().indexOf("*C2TESTSUNHANDLEDERRORPAGE*") >= 0)
                       {
                           log.error("Unhandled error page returned: " + response.getText());
                           fail("Unhandled error page returned: " + response.getURL());
                       }
                       if (response.getText().indexOf("*C2TESTSERRORPAGE*") >= 0)
                       {
                           log.error("Component error page returned: " + response.getText());
                           fail("Component error page returned: " + response.getURL());
                       }
                   }
               }
           }
           finally
           {
               HttpUnitOptions.reset();
           }
       }
 */
}
