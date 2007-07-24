
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.alexa;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;
import org.carrot2.core.test.LocalInputComponentTestBase;
import org.carrot2.core.test.Range;

/**
 * Test {@link AlexaInputComponent}.
 *
 * @author Dawid Weiss
 */
public final class AlexaInputComponentTest extends LocalInputComponentTestBase
{
    public AlexaInputComponentTest(String testName)
    {
        super(testName);
    }

    protected LocalComponentFactory getLocalInputFactory()
    {
        final String accessKey = System.getProperty("carrot2.alexa.access.key");
        final String secretKey = System.getProperty("carrot2.alexa.secret.key");

        if (accessKey != null && secretKey != null)
        {
            final LocalComponentFactory inputFactory = new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    final AlexaInputComponent aic = new AlexaInputComponent(accessKey, secretKey);
                    aic.setParallelMode(true);
                    return aic;
                }
            };
            return inputFactory;
        }
        else
        {
            throw new RuntimeException("Alexa keys (alexa.access.key, alexa.secret.key) not defined. Skipping test.");
        }
    }

    public void testNoHitsQuery() throws Exception
    {
        performQuery("asdhasd alksjdhar swioer doihdefoihewf", 50, 0);
    }

    public void testDataMiningQuery() throws Exception
    {
        performQuery("data mining", 80, new Range(80, 80));
    }

    public void testCatsAndDogsQuery() throws Exception
    {
        performQuery("cats and dogs", 200, new Range(200, 200));
    }

    /**
     * Checks if alexa keys (and thus the {@link #controller} are available.
     */
    private static boolean keysAvailable()
    {
        final String accessKey = System.getProperty("carrot2.alexa.access.key");
        final String secretKey = System.getProperty("carrot2.alexa.secret.key");

        return (accessKey != null && secretKey != null);
    }

    /**
     * 
     */
    public static Test suite()
    {
        if (isApiTestingEnabled() && keysAvailable())
        {
            return new TestSuite(AlexaInputComponentTest.class);
        }
        else
        {
            return new TestSuite();
        }
    }
}
