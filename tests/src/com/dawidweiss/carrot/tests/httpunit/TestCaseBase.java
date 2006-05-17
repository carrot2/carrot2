

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


import java.io.IOException;

import junit.framework.*;

import org.apache.log4j.Logger;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;


/**
 * An abstract test case with configuration and certain utility methods.
 */
public abstract class TestCaseBase
    extends TestCase
{
    protected final Logger log = Logger.getLogger(this.getClass());

    protected final String controllerURL;

    public TestCaseBase(String s)
        throws IOException, ClassNotFoundException
    {
        super(s);

        this.controllerURL = System.getProperty("test.url");
        if (this.controllerURL == null) {
            throw new RuntimeException("Define test.url property.");
        }
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();

        log.debug("Setting cache-only mode to on...");

        String cacheoff = controllerURL + "/debug.jsp?usecacheonly=true&history=false";
        WebConversation wc = new WebConversation();
        HttpUnitOptions.setExceptionsThrownOnScriptError(false);
        wc.getResponse(cacheoff);
    }


    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        String cacheon = controllerURL + "/debug.jsp?usecacheonly=false&history=true";
        WebConversation wc = new WebConversation();
        wc.getResponse(cacheon);
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
