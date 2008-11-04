package org.carrot2.util.httpclient;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.carrot2.util.StreamUtils;

/**
 * Various utilities for working with HTTP data streams.
 */
public class HttpUtils
{
    /**
     * The type of compression. A {@link String} indicating the type of compression used
     * when calling {@link #openGzipHttpStream(String, Map)}. Possible values:
     * <code>uncompressed</code>, <code>gzip</code>.
     */
    public static final String STATUS_COMPRESSION_USED = "compression-used";

    /**
     * Status code. An {@link Integer} indicating response status code when calling
     * {@link #openGzipHttpStream(String, Map)}.
     */
    public static final String STATUS_CODE = "code";

    private HttpUtils()
    {
        // No instances.
    }
    
    /**
     * Opens a stream for the provided URL using the GET method, unwraps a gzip compressed
     * stream if supported by the other end of the connection.
     * 
     * @param url the URL to open. The URL must be properly escaped, this method will
     *            <b>not</b> perform any escaping.
     * @param status if not <code>null</code>, additional response status will be stored
     *            in the provided map. See {@link #STATUS_CODE} and
     *            {@link #STATUS_COMPRESSION_USED}.
     * @param headers extra headers to add to the request
     * @return the http stream, unwrapped if necessary. The stream needs to be closed by
     *         the caller.
     */
    public static InputStream openGzipHttpStream(String url, Map<String, Object> status,
        Header... headers) throws HttpException, IOException
    {
        final HttpClient client = HttpClientFactory.getTimeoutingClient();
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        final GetMethod request = new GetMethod();
        InputStream stream;
        try
        {
            request.setURI(new URI(url, true));
            request.setRequestHeader(HttpHeaders.URL_ENCODED);
            request.setRequestHeader(HttpHeaders.GZIP_ENCODING);
            for (Header header : headers)
            {
                request.setRequestHeader(header);
            }
    
            final int statusCode = client.executeMethod(request);
            if (status != null)
            {
                status.put(STATUS_CODE, statusCode);
            }
    
            stream = request.getResponseBodyAsStream();
            final Header encoded = request.getResponseHeader("Content-Encoding");
            if (encoded != null && "gzip".equalsIgnoreCase(encoded.getValue()))
            {
                stream = new GZIPInputStream(stream);
                if (status != null)
                {
                    status.put(STATUS_COMPRESSION_USED, "gzip");
                }
            }
            else
            {
                if (status != null)
                {
                    status.put(STATUS_COMPRESSION_USED, "uncompressed");
                }
            }

            return new ByteArrayInputStream(StreamUtils.readFully(stream));
        }
        finally
        {
            request.releaseConnection();
        }
    }
}
