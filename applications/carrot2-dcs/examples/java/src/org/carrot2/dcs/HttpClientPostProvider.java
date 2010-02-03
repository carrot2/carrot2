
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

package org.carrot2.dcs;

import java.io.*;
import java.net.URI;
import java.util.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;

/**
 * HTTP POST provider using Apache HTTP client.
 */
public class HttpClientPostProvider implements IHttpMultipartPostProvider
{
    public InputStream post(URI dcsURI, Map<String, String> attributes)
        throws IOException
    {
        final HttpClient client = new HttpClient();
        final PostMethod post = new PostMethod(dcsURI.toString());
        final List<Part> parts = new ArrayList<Part>();
        for (Map.Entry<String, String> entry : attributes.entrySet())
        {
            parts.add(new StringPart(entry.getKey(), entry.getValue()));
        }
        post.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part [parts
            .size()]), post.getParams()));

        try
        {
            if (client.executeMethod(post) != HttpStatus.SC_OK)
            {
                throw new IOException("Unexpected DCS response: " + post.getStatusCode()
                    + ": " + post.getStatusText());
            }
            
            final byte [] body = StreamUtils.readFullyAndClose(
                post.getResponseBodyAsStream());
            return new ByteArrayInputStream(body);
        }
        finally
        {
            post.releaseConnection();
        }
    }

    public static void main(String [] args) throws IOException
    {
        new Examples(new HttpClientPostProvider()).runAllExamples();
    }
}
