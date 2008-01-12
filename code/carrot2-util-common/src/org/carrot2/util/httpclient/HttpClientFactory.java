package org.carrot2.util.httpclient;

import org.apache.commons.httpclient.HttpClient;

/**
 * Prepare instances of {@link HttpClient} with desired socket configuration settings.
 * <p>
 * TODO: By keeping specific code (such as http client utils) in the carrot2-util-comon
 * project, we can easily create a messy "dependency sink subproject". Ideally, if needed
 * by more than one project, I'd put the http-related stuff in a separate subproject like
 * carrot2-util-http. This way, we can avoid a situation that projects pull useless (for
 * them) dependencies from the carrot2-util-common. One kind of dependency we could keep
 * in carrot2-util-common is commons-lang, which has some useful methods for manipulating
 * Objects, Strings etc.
 */
public final class HttpClientFactory
{
    private HttpClientFactory()
    {
        // no instances
    }

    /**
     * @return Returns a client with sockets configured to timeout after some sensible
     *         time.
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
