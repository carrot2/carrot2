/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 * 
 * OpenSearchHttpClientFeedFetcher - A feedfetcher using the
 * Apache HttpComponents 4.x API and capable of basic authentication
 * Vittal Aithal - Cognidox Ltd <opensource@cognidox.com>
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.opensearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.AbstractFeedFetcher;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * An {@link FeedFetcher} implementation that supports basic authentication.
 * 
 * @author Vittal Aithal
 */
public class OpenSearchHttpClientFeedFetcher extends AbstractFeedFetcher
{
    private FeedFetcherCache feedInfoCache;
    private String userName;
    private String password;

    /**
     * Creates a new feed fetcher with a null feed fetcher cache.
     * 
     * @see #OpenSearchHttpClientFeedFetcher(FeedFetcherCache)
     */
    public OpenSearchHttpClientFeedFetcher()
    {
        this(null);
    }

    /**
     * Creates a new feed fetcher with the provided feed fetcher cache.
     */
    public OpenSearchHttpClientFeedFetcher(FeedFetcherCache cache)
    {
        setFeedInfoCache(cache);
    }

    public FeedFetcherCache getFeedInfoCache()
    {
        return feedInfoCache;
    }

    public void setFeedInfoCache(FeedFetcherCache feedInfoCache)
    {
        this.feedInfoCache = feedInfoCache;
    }

    /**
     * @see com.sun.syndication.fetcher.FeedFetcher#retrieveFeed(java.net.URL)
     */
    public SyndFeed retrieveFeed(URL feedUrl) throws IllegalArgumentException,
        IOException, FeedException, FetcherException
    {
        return retrieveFeed(feedUrl, 0);
    }

    /**
     * @see com.sun.syndication.fetcher.FeedFetcher#retrieveFeed(java.net.URL)
     */
    public SyndFeed retrieveFeed(URL feedUrl, int timeout)
        throws IllegalArgumentException, IOException, FeedException, FetcherException
    {
        if (feedUrl == null)
        {
            throw new IllegalArgumentException("null is not a valid URL");
        }

        // Create a client that can accept gzip encoded responses
        ContentEncodingHttpClient client = new ContentEncodingHttpClient();
        BasicHttpContext localcontext = null;
        if (timeout != 0)
        {
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
            HttpConnectionParams.setSoTimeout(client.getParams(), timeout);
        }

        // If the username and password have been set, set up the authentication
        // details for this client
        if (this.userName != null && this.password != null)
        {
            client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(this.userName, this.password));

            // Set up the pre-emptive authentication - without this, two requests
            // are made for the feed - one which will return a 401 and one which succeeds
            HttpHost targetHost = new HttpHost(feedUrl.getHost(), feedUrl.getPort(),
                feedUrl.getProtocol());

            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);

            // Add AuthCache to the execution context
            localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        }

        String urlStr = feedUrl.toString();
        FeedFetcherCache cache = getFeedInfoCache();
        if (cache != null)
        {
            // retrieve feed
            HttpGet method = new HttpGet(urlStr);
            HttpProtocolParams.setUserAgent(client.getParams(), getUserAgent());
            try
            {
                if (isUsingDeltaEncoding())
                {
                    method.setHeader("A-IM", "feed");
                }

                // Get the feed info from the cache
                // Note that syndFeedInfo will be null if it is not in the cache
                SyndFeedInfo syndFeedInfo = cache.getFeedInfo(feedUrl);
                if (syndFeedInfo != null)
                {
                    method.setHeader("If-None-Match", syndFeedInfo.getETag());

                    if (syndFeedInfo.getLastModified() instanceof String)
                    {
                        method.setHeader("If-Modified-Since",
                            (String) syndFeedInfo.getLastModified());
                    }
                }

                HttpResponse response = client.execute(method, localcontext);
                int statusCode = response.getStatusLine().getStatusCode();
                fireEvent(FetcherEvent.EVENT_TYPE_FEED_POLLED, urlStr);
                handleErrorCodes(statusCode);

                SyndFeed feed = getFeed(syndFeedInfo, urlStr, response, statusCode);

                syndFeedInfo = buildSyndFeedInfo(feedUrl, urlStr, response, feed,
                    statusCode);

                cache.setFeedInfo(new URL(urlStr), syndFeedInfo);

                // the feed may have been modified to pick up cached values
                // (eg - for delta encoding)
                feed = syndFeedInfo.getSyndFeed();

                return feed;
            }
            finally
            {
                client.getConnectionManager().shutdown();
            }

        }
        else
        {
            // Cache is not in use
            HttpGet method = new HttpGet(urlStr);
            HttpProtocolParams.setUserAgent(client.getParams(), getUserAgent());
            try
            {
                HttpResponse response = client.execute(method, localcontext);
                int statusCode = response.getStatusLine().getStatusCode();
                fireEvent(FetcherEvent.EVENT_TYPE_FEED_POLLED, urlStr);
                handleErrorCodes(statusCode);

                return getFeed(null, urlStr, response, statusCode);
            }
            finally
            {
                client.getConnectionManager().shutdown();
            }
        }
    }

    private SyndFeedInfo buildSyndFeedInfo(URL feedUrl, String urlStr,
        HttpResponse response, SyndFeed feed, int statusCode)
        throws MalformedURLException
    {
        SyndFeedInfo syndFeedInfo;
        syndFeedInfo = new SyndFeedInfo();

        // this may be different to feedURL because of 3XX redirects
        syndFeedInfo.setUrl(new URL(urlStr));
        syndFeedInfo.setId(feedUrl.toString());

        Header imHeader = response.getFirstHeader("IM");
        if (imHeader != null && imHeader.getValue().indexOf("feed") >= 0
            && isUsingDeltaEncoding())
        {
            FeedFetcherCache cache = getFeedInfoCache();
            if (cache != null && statusCode == 226)
            {
                // client is setup to use http delta encoding and the server supports it
                // and has returned a delta encoded response
                // This response only includes new items
                SyndFeedInfo cachedInfo = cache.getFeedInfo(feedUrl);
                if (cachedInfo != null)
                {
                    SyndFeed cachedFeed = cachedInfo.getSyndFeed();

                    // set the new feed to be the orginal feed plus the new items
                    feed = combineFeeds(cachedFeed, feed);
                }
            }
        }

        Header lastModifiedHeader = response.getFirstHeader("Last-Modified");
        if (lastModifiedHeader != null)
        {
            syndFeedInfo.setLastModified(lastModifiedHeader.getValue());
        }

        Header eTagHeader = response.getFirstHeader("ETag");
        if (eTagHeader != null)
        {
            syndFeedInfo.setETag(eTagHeader.getValue());
        }

        syndFeedInfo.setSyndFeed(feed);

        return syndFeedInfo;
    }

    private static SyndFeed retrieveFeed(String urlStr, HttpResponse response)
        throws IOException, IllegalStateException, FetcherException, FeedException
    {

        InputStream stream = response.getEntity().getContent();
        try
        {
            XmlReader reader = null;
            if (response.getFirstHeader("Content-Type") != null)
            {
                reader = new XmlReader(stream, response.getFirstHeader("Content-Type")
                    .getValue(), true);
            }
            else
            {
                reader = new XmlReader(stream, true);
            }
            return new SyndFeedInput().build(reader);
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }

    private SyndFeed getFeed(SyndFeedInfo syndFeedInfo, String urlStr,
        HttpResponse response, int statusCode) throws IOException, FetcherException,
        FeedException
    {

        if (statusCode == HttpURLConnection.HTTP_NOT_MODIFIED && syndFeedInfo != null)
        {
            fireEvent(FetcherEvent.EVENT_TYPE_FEED_UNCHANGED, urlStr);
            return syndFeedInfo.getSyndFeed();
        }

        SyndFeed feed = retrieveFeed(urlStr, response);
        fireEvent(FetcherEvent.EVENT_TYPE_FEED_RETRIEVED, urlStr, feed);
        return feed;
    }

    /**
     * Set the username and password to be used for HTTP basic authentication
     * 
     * @param userName
     * @param password
     */
    public void setBasicCredentials(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

}
