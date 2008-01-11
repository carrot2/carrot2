package org.carrot2.util.httpclient;

import org.apache.commons.httpclient.HttpClient;

/**
 * Prepare instances of {@link HttpClient} with desired socket configuration settings.
 */
public final class HttpClientFactory
{
    private HttpClientFactory()
    {
        // no instances
    }

    /**
     * @return Returns a client with sockets configured
     * to timeout after some sensible time.
     */
    public static HttpClient getTimeoutingClient()
    {
        final int timeout = 5 * 1000;
        final HttpClient httpClient = new HttpClient(new SingleHttpConnectionManager());

        // Setup default timeouts.
        httpClient.getParams().setSoTimeout(timeout);
        httpClient.getParams().setIntParameter("http.connection.timeout", timeout);

        // Not important (single http connection manager), but anyway.
        httpClient.getParams().setConnectionManagerTimeout(timeout);

        return httpClient;
    }
}
