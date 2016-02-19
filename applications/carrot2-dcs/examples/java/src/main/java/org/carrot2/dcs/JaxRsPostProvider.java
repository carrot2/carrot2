
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.client.*;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * An example of sending multipart HTTP POST request using JAX-RS (JSR-311) implementation
 * (Jersey).
 */
public class JaxRsPostProvider implements IHttpMultipartPostProvider
{
    public InputStream post(URI dcsURI, Map<String, String> attributes)
        throws IOException
    {
        final Client client = Client.create();
        try
        {
            final WebResource webResource = client.resource("http://localhost:8080/dcs/rest");
            final FormDataMultiPart form = new FormDataMultiPart();
            for (Map.Entry<String, String> entry : attributes.entrySet())
            {
                form.field(entry.getKey(), entry.getValue());
            }
    
            // We need to explicitly set form multipart boundary to some unique value
            final String boundary = "---------------------------"
                + System.currentTimeMillis();
            final ClientResponse response = webResource.type(
                MediaType.MULTIPART_FORM_DATA + "; boundary=" + boundary).post(
                ClientResponse.class, form);
    
            if (response.getResponseStatus() != Status.OK)
            {
                throw new IOException("Unexpected status: "
                    + response.getResponseStatus());
            }
    
            return response.getEntityInputStream();
        }
        finally
        {
            client.destroy();
        }
    }
    
    public static void main(String [] args) throws IOException
    {
        new Examples(new JaxRsPostProvider()).runAllExamples();
    }
}
