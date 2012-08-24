package org.carrot2.dcs;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;

import com.google.common.collect.Maps;

@Path("/dcs")
public class ClusteringResource
{
    @Context
    private Application application;

    private DcsApplication application()
    {
        return (DcsApplication) application;
    }

    @GET
    @Path("rest")
    @Produces(
    {
        "application/x-javascript; charset=UTF-8", "application/json; charset=UTF-8"
    })
    public Response cluster(@QueryParam("dcs.source") String source,
        @QueryParam("query") String query) throws IOException
    {
        final Map<String, Object> attrs = Maps.newHashMap();
        CommonAttributesDescriptor.attributeBuilder(attrs).query(query).results(50);
        final ProcessingResult result = application().controller.process(attrs, source,
            "lingo");

        final StringWriter writer = new StringWriter();
        result.serializeJson(writer);

        return Response.ok().entity(writer.toString()).type(MediaType.APPLICATION_JSON)
            .build();
    }
}