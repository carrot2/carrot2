package org.carrot2.dcs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/admin")
public class AdminResource
{
    @Context
    private Application application;

    private DcsApplication application()
    {
        return (DcsApplication) application;
    }

    @GET
    @Path("/status/json")
    public Response status() throws Exception
    {
        return application().ok().entity(application().controller.getStatistics())
            .type(MediaType.APPLICATION_JSON).build();
    }
}