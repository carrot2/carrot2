package org.carrot2.dcs;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RequestCountLimitExceededMapper implements
    ExceptionMapper<RequestCountLimitExceeded>
{
    @Override
    public Response toResponse(RequestCountLimitExceeded e)
    {
        return Response.status(Status.FORBIDDEN).entity("").build();
    }
}