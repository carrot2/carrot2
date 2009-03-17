package org.carrot2.dcs;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.*;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * An example of calling Document Clustering Server service in Java using JAX-RS (JSR-311)
 * implementation (Jersey). Please see http://localhost:8080 for a list of all available
 * DCS parameters.
 */
public class JaxRsDcsExample extends AbstractDcsExample
{
    public static void main(String [] args) throws Exception
    {
        // See the runExamples() in the parent class for the example invocations
        new JaxRsDcsExample().runExamples();
    }

    @Override
    protected InputStream sendDcsPostRequest(Map<String, String> attributes)
        throws Exception
    {
        final Client client = Client.create();
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

        return response.getEntityInputStream();
    }
}
