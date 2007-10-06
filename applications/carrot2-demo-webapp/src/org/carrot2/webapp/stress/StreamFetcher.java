package org.carrot2.webapp.stress;

import java.io.IOException;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.StringUtils;

public class StreamFetcher implements Runnable
{
    private static final int SOCKET_TIMEOUT = 100 * 1000;

    private static final Logger logger = Logger.getLogger(StreamFetcher.class);

    private final String uri;

    public StreamFetcher(String uri)
    {
        this.uri = uri;
    }

    public void run()
    {
        final HttpClientParams params = new HttpClientParams();
        params.setSoTimeout(SOCKET_TIMEOUT);

        final HttpConnectionManager manager = new OnetimeConnectionManager();
        final HttpClient httpClient = new HttpClient(params, manager);

        GetMethod get = null;
        try
        {
            get = new GetMethod(uri);
            final int status = httpClient.executeMethod(get);
            if (status != HttpStatus.SC_OK)
            {
                throw new IOException(HttpStatus.getStatusText(status));                
            }
            success(StreamUtils.readFully(get.getResponseBodyAsStream()));
        }
        catch (IOException e)
        {
            final String reason = "Request error: " + StringUtils.chainExceptionMessages(e);
            logger.debug(reason);
            failure(reason);
        }
        finally
        {
            if (get != null) get.releaseConnection();
        }
    }
    
    public void success(byte [] content)
    {
        // Do nothing.
    }
    
    public void failure(String reason)
    {
        // Do nothing.
    }
}
