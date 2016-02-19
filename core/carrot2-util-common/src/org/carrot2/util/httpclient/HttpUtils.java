
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.carrot2.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Various utilities for working with HTTP data streams.
 */
@SuppressWarnings("deprecation")
public class HttpUtils
{
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * A static holder storing HTTP response fields.
     */
    public final static class Response
    {
        public byte [] payload;
        public int status;
        public String compression;
        public String [][] headers;
        public String statusMessage;

        public InputStream getPayloadAsStream()
        {
            return new ByteArrayInputStream(payload);
        }
    }

    /**
     * GZIP compression was used.
     * 
     * @see HttpUtils.Response#compression
     */
    private static final String COMPRESSION_GZIP = "gzip";

    /**
     * No compression was used.
     * 
     * @see HttpUtils.Response#compression
     */
    private static final String COMPRESSION_NONE = "uncompressed";

    /*
     * 
     */
    private HttpUtils()
    {
        // No instances.
    }

    /**
     * Opens a HTTP/1.1 connection to the given URL using the GET method, decompresses
     * compressed response streams, if supported by the server.
     * 
     * @param url The URL to open. The URL must be properly escaped, this method will
     *            <b>not</b> perform any escaping.
     * @param params Query string parameters to be attached to the url.
     * @param headers Any extra HTTP headers to add to the request.
     * @param user if not <code>null</code>, the user name to send during Basic
     *            Authentication
     * @param password if not <code>null</code>, the password name to send during Basic
     *            Authentication
     * @return The {@link HttpUtils.Response} object. Note that entire payload is read and
     *         buffered so that the HTTP connection can be closed when leaving this
     *         method.
     */
    public static Response doGET(
        String url, 
        Collection<NameValuePair> params,
        Collection<Header> headers, 
        String user, String password, 
        int timeoutMillis,
        RedirectStrategy redirectStrategy)
        throws IOException
    {
        final DefaultHttpClient client = HttpClientFactory.getTimeoutingClient(timeoutMillis);
        client.setRedirectStrategy(redirectStrategy);

        client.getParams().setParameter(
            CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        final HttpGet request = new HttpGet();
        final BasicHttpContext context = new BasicHttpContext();

        final Response response = new Response();
        try
        {
            if (params == null) 
                params = Lists.newArrayList();
            else
                params = Lists.newArrayList(params);

            URI uri = new URI(url);
            params.addAll(URLEncodedUtils.parse(uri, "UTF-8"));

            uri = URIUtils.createURI(uri.getScheme(), uri.getHost(), uri.getPort(),
                uri.getPath(),
                URLEncodedUtils.format(Lists.newArrayList(params), "UTF-8"), null);
            request.setURI(uri);

            request.setHeader(HttpHeaders.URL_ENCODED);
            request.setHeader(HttpHeaders.GZIP_ENCODING);
            if (headers != null)
            {
                for (Header header : headers)
                    request.setHeader(header);
            }

            if (user != null && password != null)
            {
                client.getCredentialsProvider().setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(user, password));
                
                // Use preemptive authentication (send credentials immediately to the target
                // URI, without waiting for 401).
                AuthCache authCache = new BasicAuthCache();
                authCache.put(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()), new BasicScheme());
                context.setAttribute(ClientContext.AUTH_CACHE, authCache);    
            }

            logger.debug("GET: " + request.getURI());

            final HttpResponse httpResponse = client.execute(request, context);
            response.status = httpResponse.getStatusLine().getStatusCode();
            response.statusMessage = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();
            InputStream stream = entity.getContent();
            final Header encoded = entity.getContentEncoding();
            if (encoded != null && "gzip".equalsIgnoreCase(encoded.getValue()))
            {
                stream = new GZIPInputStream(stream);
                response.compression = COMPRESSION_GZIP;
            }
            else
            {
                response.compression = COMPRESSION_NONE;
            }

            final Header [] respHeaders = httpResponse.getAllHeaders();
            response.headers = new String [respHeaders.length] [];
            for (int i = 0; i < respHeaders.length; i++)
            {
                response.headers[i] = new String []
                {
                    respHeaders[i].getName(), respHeaders[i].getValue()
                };
            }

            response.payload = StreamUtils.readFullyAndClose(stream);
            return response;
        }
        catch (URISyntaxException e)
        {
            throw new IOException(e);
        }
        finally
        {
            client.getConnectionManager().shutdown();
        }
    }
}
