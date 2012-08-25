package org.carrot2.dcs;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/metadata/json")
public class MetadataResource
{
    @Context
    private Application application;

    private DcsApplication application()
    {
        return (DcsApplication) application;
    }

    @GET
    @Produces("application/json; charset=UTF-8")
    public Response metadata() throws Exception
    {
        final StringWriter writer = new StringWriter();
        application().componentSuite.serializeJson(writer);
        return application().ok().entity(writer.toString())
            .type(MediaType.APPLICATION_JSON).build();
    }
}