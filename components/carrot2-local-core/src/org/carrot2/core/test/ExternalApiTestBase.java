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

package org.carrot2.core.test;

import junit.framework.TestCase;

/**
 * A base class for tests whose success or failure depends on external resources, e.g.
 * Yahoo or MSN APIs.
 *
 * @author Stanislaw Osinski
 */
public class ExternalApiTestBase extends TestCase
{
    public ExternalApiTestBase()
    {
        super();
    }

    public ExternalApiTestBase(String name)
    {
        super(name);
    }

    /**
     * Tests whether the tests have been launched in a mode allowing for external API
     * testing (tests requiring e.g. a connection to Yahoo or MSN API). If this methods
     * returns <code>true</code>, external API tests should proceed. Otherwise, no
     * tests relying on external resources should be performed.
     *
     * @return
     */
    public static boolean isApiTestingEnabled()
    {
        String disabled = System.getProperty("carrot2.api.testing.disabled");
        return !Boolean.valueOf(disabled).booleanValue();
    }
}
