
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test;

/**
 * Contains assumptions for tests whose results depend on the correct functioning of some
 * external APIs. Such tests may be ignored e.g. during post-commit builds and executed
 * only during daily builds.
 */
public final class ExternalApiTestAssumptions
{
    /**
     * Allows to skip running tests that can fail because of a failure of some external
     * system, e.g. search engine API.
     */
    public static boolean externalApiTestsEnabled()
    {
        return !Boolean.valueOf(
            System.getProperty("external.api.tests.disabled")).booleanValue();
    }
}
