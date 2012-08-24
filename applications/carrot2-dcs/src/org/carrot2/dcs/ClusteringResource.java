package org.carrot2.dcs;

import java.io.ByteArrayOutputStream;
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

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

@Path("/cluster")
public class ClusteringResource
{
    @Context
    private Application application;

    private DcsApplication application()
    {
        return (DcsApplication) application;
    }

    @GET
    @Path("/json")
    @Produces(
    {
        "application/x-javascript; charset=UTF-8", "application/json; charset=UTF-8"
    })
    public Response jsonGet(@QueryParam("dcs.source") String source,
        @QueryParam("dcs.algorithm") String algorithm, @QueryParam("query") String query,
        @QueryParam("results") int results,
        @QueryParam("dcs.clusters.only") boolean clustersOnly) throws IOException
    {
        final Map<String, Object> attrs = Maps.newHashMap();
        CommonAttributesDescriptor.attributeBuilder(attrs).query(query).results(50);
        final ProcessingResult result = application().controller.process(attrs, source,
            "lingo");

        final StringWriter writer = new StringWriter();
        result.serializeJson(writer);

        return application().ok().entity(writer.toString())
            .type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/xml")
    @Produces("application/xml; charset=UTF-8")
    public Response xmlGet(@QueryParam("dcs.source") String source,
        @QueryParam("dcs.algorithm") String algorithm, @QueryParam("query") String query,
        @QueryParam("results") int results,
        @QueryParam("dcs.clusters.only") boolean clustersOnly) throws Exception
    {
        final Map<String, Object> attrs = Maps.newHashMap();
        CommonAttributesDescriptor.attributeBuilder(attrs).query(query).results(50);
        final ProcessingResult result = application().controller.process(attrs, source,
            "lingo");

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        result.serialize(out, clustersOnly, true, true);

        return application().ok().entity(out.toString(Charsets.UTF_8.name()))
            .type(MediaType.APPLICATION_XML).build();
    }
}