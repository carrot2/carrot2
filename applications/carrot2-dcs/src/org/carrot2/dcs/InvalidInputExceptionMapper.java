package org.carrot2.dcs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.LoggerFactory;

@Provider
public class InvalidInputExceptionMapper implements
    ExceptionMapper<InvalidInputException>
{
    @Override
    public Response toResponse(InvalidInputException e)
    {
        final boolean hasCause = e.getCause() != null;
        if (hasCause)
        {
            LoggerFactory.getLogger("DcsApplication").error(
                "Invalid input: " + e.getMessage(), e.getCause());
        }
        return Response
            .status(Status.BAD_REQUEST)
            .type(MediaType.TEXT_PLAIN)
            .entity(
                "Invalid input: " + e.getMessage()
                    + (hasCause ? ". See logs for more details." : "")).build();
    }
}