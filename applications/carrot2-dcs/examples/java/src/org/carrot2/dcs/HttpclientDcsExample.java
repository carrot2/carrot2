package org.carrot2.dcs;

import java.io.InputStream;
import java.util.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;

/**
 * An example of calling Document Clustering Server service in Java using Apache
 * Httpclient. Please see http://localhost:8080 for a list of all available DCS
 * parameters.
 */
public class HttpclientDcsExample extends AbstractDcsExample
{
    public static void main(String [] args) throws Exception
    {
        // See the runExamples() in the super class for the example invocations
        new HttpclientDcsExample().runExamples();
    }

    @Override
    protected InputStream sendDcsPostRequest(Map<String, String> attributes)
        throws Exception
    {
        final HttpClient client = new HttpClient();
        final PostMethod post = new PostMethod(DCS_URL);
        final List<Part> parts = new ArrayList<Part>();
        for (Map.Entry<String, String> entry : attributes.entrySet())
        {
            parts.add(new StringPart(entry.getKey(), entry.getValue()));
        }
        post.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part [parts
            .size()]), post.getParams()));
        client.executeMethod(post);
        return post.getResponseBodyAsStream();
    }
}
