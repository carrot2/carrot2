package org.carrot2.core.test;

/**
 * A base class for tests whose results depend on the correct functioning of some external
 * APIs. Such tests may be ignored e.g. during post-commit builds and executed only during
 * daily builds.
 */
public class ExternalApiTestBase
{
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
        return externalApiTestsEnabled() && getCarrot2XmlFeedUrlBase() != null;
    }

    /**
     * Returns the Carrot2 XML feed URL base or <code>null</code> if not provided.
     */
    protected static String getCarrot2XmlFeedUrlBase()
    {
        return System.getProperty("carrot2.xml.feed.url.base");
    }
}
