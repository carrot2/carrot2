
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

package org.carrot2.util.httpclient;

import java.io.*;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.carrot2.util.StreamUtils;

/**
 * Various utilities for working with HTTP data streams.
 */
public class HttpUtils
{
    /**
     * A static holder storing HTTP response fields.
     */
    public final static class Response
    {
        public byte [] payload;
        public int status;
        public String compression;
        public String [][] headers;

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
     * @return The {@link HttpUtils.Response} object. Note that entire payload is read and
     *         buffered so that the HTTP connection can be closed when leaving this
     *         method.
     */
    public static Response doGET(String url, Collection<NameValuePair> params,
        Collection<Header> headers) throws HttpException, IOException
    {
        return doGET(url, params, headers, null, null);
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
    public static Response doGET(String url, Collection<NameValuePair> params,
        Collection<Header> headers, String user, String password) throws HttpException,
        IOException
    {
        return doGET(url, params, headers, user, password, HttpClientFactory.DEFAULT_TIMEOUT);
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
    public static Response doGET(String url, Collection<NameValuePair> params,
        Collection<Header> headers, String user, String password, int timeout) throws HttpException,
        IOException
        {
        final HttpClient client = HttpClientFactory.getTimeoutingClient(timeout);
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        final GetMethod request = new GetMethod();

        if (user != null && password != null)
        {
            client.getState().setCredentials(
                new AuthScope(null, 80, null),
                new UsernamePasswordCredentials(user, password));
            request.setDoAuthentication(true);
        }

        final Response response = new Response();
        try
        {
            request.setURI(new URI(url, true));

            if (params != null)
            {
                request.setQueryString(params.toArray(new NameValuePair [params.size()]));
            }

            request.setRequestHeader(HttpHeaders.URL_ENCODED);
            request.setRequestHeader(HttpHeaders.GZIP_ENCODING);
            if (headers != null)
            {
                for (Header header : headers)
                    request.setRequestHeader(header);
            }

            org.slf4j.LoggerFactory.getLogger(HttpUtils.class).debug(
                "GET: " + request.getURI());

            final int statusCode = client.executeMethod(request);
            response.status = statusCode;

            InputStream stream = request.getResponseBodyAsStream();
            final Header encoded = request.getResponseHeader("Content-Encoding");
            if (encoded != null && "gzip".equalsIgnoreCase(encoded.getValue()))
            {
                stream = new GZIPInputStream(stream);
                response.compression = COMPRESSION_GZIP;
            }
            else
            {
                response.compression = COMPRESSION_NONE;
            }

            final Header [] respHeaders = request.getResponseHeaders();
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
        finally
        {
            request.releaseConnection();
        }
    }
}
