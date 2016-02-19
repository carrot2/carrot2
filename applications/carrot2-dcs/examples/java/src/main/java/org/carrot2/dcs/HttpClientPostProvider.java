
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

package org.carrot2.dcs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * HTTP POST provider using Apache HTTP client.
 */
public class HttpClientPostProvider implements IHttpMultipartPostProvider
{
    private final static Charset UTF8 = Charset.forName("UTF-8");

    public InputStream post(URI dcsURI, Map<String, String> attributes)
        throws IOException
    {
        final HttpClient client = new DefaultHttpClient();
        final HttpPost post = new HttpPost(dcsURI);

        final MultipartEntity body = new MultipartEntity(
            HttpMultipartMode.STRICT, null, UTF8);

        for (Map.Entry<String, String> entry : attributes.entrySet())
        {
            body.addPart(entry.getKey(), new StringBody(entry.getValue(), UTF8));
        }
        post.setEntity(body);

        try
        {
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            {
                throw new IOException("Unexpected DCS response: " 
                    + response.getStatusLine());
            }

            final byte [] responseBody = StreamUtils.readFullyAndClose(
                response.getEntity().getContent());
            return new ByteArrayInputStream(responseBody);
        }
        finally
        {
            client.getConnectionManager().shutdown();
        }
    }

    public static void main(String [] args) throws IOException
    {
        new Examples(new HttpClientPostProvider()).runAllExamples();
    }
}
