

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


import com.dawidweiss.carrot.tests.config.TestsConfiguration;
import com.meterware.httpunit.WebConversation;
import junit.framework.*;
import junit.framework.TestCase;
import junit.framework.TestListener;
import org.apache.log4j.Logger;
import org.put.util.component.*;
import org.put.util.config.ConfigUtils;
import org.put.util.resource.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * An abstract test case with configuration and certain utility methods.
 */
public abstract class TestCaseBase
    extends TestCase
{
    protected final Logger log = Logger.getLogger(this.getClass());
    private final TestsConfiguration configuration;

    public TestCaseBase(String s)
        throws IOException, ClassNotFoundException
    {
        super(s);

        ResourceLocator locator = new ResourceLocator();
        locator.addLocation(ConfigUtils.getCurrentDirectoryLocator());
        locator.addLocation(new ClassResourceLocator(TestsConfiguration.class, "", false));

        String CONF_FILE_NAME = "tests-conf.xml";

        if (locator.countMatchingResources(CONF_FILE_NAME) == 0)
        {
            throw new RuntimeException("No configuration file found.");
        }

        if (locator.countMatchingResources(CONF_FILE_NAME) > 1)
        {
            log.warn(
                "Overriding configuration file's location to: "
                + locator.getLookupLocations(CONF_FILE_NAME)[0]
            );
        }

        InputStream [] locations = locator.resolveResource(CONF_FILE_NAME);

        for (int i = 1; i < locations.length; i++)
        {
            locations[i].close();
        }

        try
        {
            Loader loader = Loader.loadLoader(
                    TestsConfiguration.class.getResourceAsStream("loader-conf.xml")
                );
            configuration = (TestsConfiguration) loader.load(locations[0]);

            log.debug(
                "Configuration loaded. Controller at: "
                + configuration.getControllerURL().toExternalForm()
            );
        }
        finally
        {
            locations[0].close();
        }
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();

        log.debug("Setting cache-only mode to on...");

        String cacheoff = getControllerURL() + "/debug.jsp?usecacheonly=true&history=false";
        WebConversation wc = new WebConversation();
        wc.getResponse(cacheoff);
    }


    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        String cacheon = getControllerURL() + "/debug.jsp?usecacheonly=false&history=true";
        WebConversation wc = new WebConversation();
        wc.getResponse(cacheon);
    }


    public URL getControllerURL()
    {
        return configuration.getControllerURL();
    }


    public void run(TestResult arg0)
    {
        // add custom listener here
        TestListener tl = new TestListener()
            {
                public void addError(Test arg0, Throwable arg1)
                {
                    log.error("JUnit error: " + arg0.toString(), arg1);
                }


                public void addFailure(Test arg0, AssertionFailedError arg1)
                {
                    log.error("JUnit failure: " + arg0.toString(), arg1);
                }


                public void endTest(Test arg0)
                {
                    log.info("JUnit end test: " + arg0.toString());
                }


                public void startTest(Test arg0)
                {
                    log.info("JUnit start test: " + arg0.toString());
                }
            };

        try
        {
            arg0.addListener(tl);
            super.run(arg0);
        }
        finally
        {
            arg0.removeListener(tl);
        }
    }
}
