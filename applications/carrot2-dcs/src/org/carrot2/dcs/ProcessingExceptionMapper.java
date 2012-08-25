package org.carrot2.dcs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.carrot2.core.ProcessingException;
import org.slf4j.LoggerFactory;

@Provider
public class ProcessingExceptionMapper implements ExceptionMapper<ProcessingException>
{
    @Override
    public Response toResponse(ProcessingException e)
    {
        LoggerFactory.getLogger("DcsApplication").error("Processing error", e);
        return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN)
            .entity("Processing error: " + e.getMessage() + ". See logs for details.")
            .build();
    }
}