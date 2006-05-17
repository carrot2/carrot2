

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
 * An abstract test case with configuration 
 * and certain utility methods.
 */
public abstract class DemoLinkTestBase
    extends Assert implements Test
{
    protected final Logger log = Logger.getLogger(this.getClass());
    protected final String controllerURL;

    public DemoLinkTestBase()
        throws IOException, ClassNotFoundException
    {
        this.controllerURL = System.getProperty("test.url");
        if (this.controllerURL == null) {
            throw new RuntimeException("Define test.url property.");
        }
    }

    protected void setUp()
        throws Exception
    {
        log.debug("Setting cache-only mode to on...");

        String cacheoff = controllerURL + "/debug.jsp?usecacheonly=true&history=false";
        WebConversation wc = new WebConversation();
        HttpUnitOptions.setExceptionsThrownOnScriptError(false);
        wc.getResponse(cacheoff);
    }


    protected void tearDown()
        throws Exception
    {
        String cacheon = controllerURL + "/debug.jsp?usecacheonly=false&history=true";
        WebConversation wc = new WebConversation();
        HttpUnitOptions.setExceptionsThrownOnScriptError(false);
        wc.getResponse(cacheon);
    }

    public abstract void runBare() throws Throwable;

    public void run(TestResult testResult)
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
            testResult.addListener(tl);
            
            try {
                try {
					setUp();
				} catch (Throwable e) {
                    testResult.addError(this, e);
				}

                testResult.startTest(this);

                Protectable p = new Protectable() {
                    public void protect() throws Throwable {
                        runBare();
                    }
                };
                testResult.runProtected(this, p);

                testResult.endTest(this);

            } finally {
                try {
                	tearDown();
                } catch (Throwable e) {
                    testResult.addError(this, e);
                }
            }

        }
        finally
        {
            testResult.removeListener(tl);
        }
    }
    
	public int countTestCases() {
		return 1;
	}

    public String toString() {
        return "Demo link test: " + getName();
    }

    public abstract String getName();
}
