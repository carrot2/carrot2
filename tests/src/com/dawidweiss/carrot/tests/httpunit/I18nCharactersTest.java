

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


/**
 *
 */
public class I18nCharactersTest
    extends AbstractTestCase
{
    public I18nCharactersTest(String s)
        throws IOException, ClassNotFoundException
    {
        super(s);
    }

    /**
     * Note: The httpunit is broken in parsing international characters in query strings!
     */
    public void testI18nCharactersInQuery()
        throws MalformedURLException, IOException, SAXException
    {
        HttpUnitOptions.setScriptingEnabled(false);
        HttpUnitOptions.setDefaultCharacterSet("UTF-8");

        try
        {
            WebConversation wc = new WebConversation();

            // @TODO avoid httpunit bug by decoding query arguments
            // BEFORE submitting them. This forms invalid URLs, but
            // it at least allows testing...
            String url = getControllerURL().toExternalForm() + "/index.jsp?query="
                + org.put.util.net.URLEncoding.encode("s\u0142owi\u0144ski", "UTF-8")
                + "&processingChain=carrot2.process.lingo-google-cs-only&resultsRequested=100";

            log.debug("Query: " + url);

            WebResponse response = wc.getResponse(url);

            log.debug("charset: " + response.getCharacterSet());
            log.debug("Response URL: " + response.getURL().toExternalForm());
            assertTrue("Two frames in the response", response.getFrameNames().length == 2);
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
        finally
        {
            HttpUnitOptions.reset();
        }
    }
}
