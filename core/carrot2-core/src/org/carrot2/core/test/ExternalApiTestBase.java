
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test;

import org.apache.commons.lang.StringUtils;

/**
 * A base class for tests whose results depend on the correct functioning of some external
 * APIs. Such tests may be ignored e.g. during post-commit builds and executed only during
 * daily builds.
 */
public class ExternalApiTestBase
{
    /**
     * A query that in theory should not return any results.
     */
    protected static final String NO_RESULTS_QUERY = "duiogig oiudgisugvi\u0078 siug iugw iusviuwg";

    /**
     * Allows to skip running tests that can fail because of a failure of some external
     * system, e.g. search engine API.
     */
    public boolean externalApiTestsEnabled()
    {
        return !Boolean.valueOf(System.getProperty("external.api.tests.disabled"))
            .booleanValue();
    }

    /**
     * Allows to skip running tests when details of the Carrot2 search feed are not
     * provided.
     */
    public boolean carrot2XmlFeedTestsEnabled()
    {
        return externalApiTestsEnabled() && StringUtils.isNotBlank(getCarrot2XmlFeedUrlBase());
    }

    /**
     * Returns the Carrot2 XML feed URL base or <code>null</code> if not provided.
     */
    protected static String getCarrot2XmlFeedUrlBase()
    {
        return System.getProperty("carrot2.xml.feed.url.base");
    }
}
