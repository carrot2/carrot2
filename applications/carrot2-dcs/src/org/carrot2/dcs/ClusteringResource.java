package org.carrot2.dcs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.util.ExceptionUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.sun.jersey.multipart.FormDataParam;

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
        "application/x-javascript; charset=UTF-8",
        MediaType.APPLICATION_JSON + "; charset=UTF-8"
    })
    public Response jsonGet(
        @QueryParam("dcs.source") String source,
        @QueryParam("dcs.algorithm") String algorithm,
        @QueryParam("query") String query,
        @QueryParam("results") Integer results,
        @QueryParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @QueryParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @QueryParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes)
        throws IOException
    {
        return application()
            .ok()
            .entity(
                new JsonStreamingOutput(processFromExternalSource(source, algorithm,
                    query, results), outputDocuments, outputClusters, outputAttributes))
            .type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/xml")
    @Produces(MediaType.APPLICATION_XML + "; charset=UTF-8")
    public Response xmlGet(
        @QueryParam("dcs.source") String source,
        @QueryParam("dcs.algorithm") String algorithm,
        @QueryParam("query") String query,
        @QueryParam("results") Integer results,
        @QueryParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @QueryParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @QueryParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes)
        throws Exception
    {
        return application()
            .ok()
            .entity(
                new XmlStreamingOutput(processFromExternalSource(source, algorithm,
                    query, results), outputDocuments, outputClusters, outputAttributes))
            .type(MediaType.APPLICATION_XML).build();
    }

    /**
     * Processes clustering of documents from external sources.
     */
    private ProcessingResult processFromExternalSource(String source, String algorithm,
        String query, Integer results)
    {
        final Map<String, Object> attrs = Maps.newHashMap();
        CommonAttributesDescriptor
            .attributeBuilder(attrs)
            .query(query)
            .results(
                Math.min(application().config.maxResultsFromExternalSource, Objects
                    .firstNonNull(results,
                        application().config.defaultResultsFromExternalSource)));
        return application().process(attrs, source, algorithm);
    }

    @POST
    @Path("/xml")
    @Produces("application/xml; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response xmlPostForm(
        @FormParam("dcs.source") String source,
        final @FormParam("dcs.c2stream") String c2stream,
        @FormParam("dcs.algorithm") String algorithm,
        @FormParam("query") String query,
        @FormParam("results") Integer results,
        @QueryParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @QueryParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @QueryParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes)
        throws Exception
    {
        return application()
            .ok()
            .entity(
                new XmlStreamingOutput(processPost(source,
                    new StringProcessingResultSupplier(c2stream), algorithm, query,
                    results), outputDocuments, outputClusters, outputAttributes))
            .type(MediaType.APPLICATION_XML).build();
    }

    @POST
    @Path("/xml")
    @Produces("application/xml; charset=UTF-8")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response xmlPostMultipart(
        @FormDataParam("dcs.source") String source,
        final @FormDataParam("dcs.c2stream") String c2stream,
        @FormDataParam("dcs.algorithm") String algorithm,
        @FormDataParam("query") String query,
        @FormDataParam("results") Integer results,
        @QueryParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @QueryParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @QueryParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes)
        throws Exception
    {
        return application()
            .ok()
            .entity(
                new XmlStreamingOutput(processPost(source,
                    new StringProcessingResultSupplier(c2stream), algorithm, query,
                    results), outputDocuments, outputClusters, outputAttributes))
            .type(MediaType.APPLICATION_XML).build();
    }

    @POST
    @Path("/json")
    @Produces(
    {
        "application/x-javascript; charset=UTF-8",
        MediaType.APPLICATION_JSON + "; charset=UTF-8"
    })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response jsonPostForm(
        @FormParam("dcs.source") String source,
        final @FormParam("dcs.c2stream") String c2stream,
        @FormParam("dcs.algorithm") String algorithm,
        @FormParam("query") String query,
        @FormParam("results") Integer results,
        @QueryParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @QueryParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @QueryParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes)
        throws Exception
    {
        return application()
            .ok()
            .entity(
                new JsonStreamingOutput(processPost(source,
                    new StringProcessingResultSupplier(c2stream), algorithm, query,
                    results), outputDocuments, outputClusters, outputAttributes))
            .type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/json")
    @Produces(
    {
        "application/x-javascript; charset=UTF-8",
        MediaType.APPLICATION_JSON + "; charset=UTF-8"
    })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response jsonPostMultipart(
        @FormDataParam("dcs.source") String source,
        final @FormDataParam("dcs.c2stream") InputStream c2stream,
        @FormDataParam("dcs.algorithm") String algorithm,
        @FormDataParam("query") String query,
        @FormDataParam("results") Integer results,
        @QueryParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @QueryParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @QueryParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes)
        throws Exception
    {
        return application()
            .ok()
            .entity(
                new JsonStreamingOutput(processPost(source,
                    new InputStreamProcessingResultSupplier(c2stream), algorithm, query,
                    results), outputDocuments, outputClusters, outputAttributes))
            .type(MediaType.APPLICATION_JSON).build();
    }

    /**
     * Processes the POST clustering requests.
     */
    private ProcessingResult processPost(String source,
        Supplier<ProcessingResult> processingResultSupplier, String algorithm,
        String query, Integer results) throws Exception, UnsupportedEncodingException
    {
        if (!Strings.isNullOrEmpty(source))
        {
            return processFromExternalSource(source, algorithm, query, results);
        }
        else
        {
            final Map<String, Object> attrs = Maps.newHashMap();
            final ProcessingResult input;
            try
            {
                input = processingResultSupplier.get();
            }
            catch (Exception e)
            {
                // This exception will get converted to a bad request response
                throw new InvalidInputException("Could not parse Carrot2 XML stream", e);
            }

            if (input == null)
            {
                throw new InvalidInputException(
                    "Non-empty dcs.source or dcs.c2stream is required");
            }

            attrs.putAll(input.getAttributes());
            if (attrs.get(CommonAttributesDescriptor.Keys.DOCUMENTS) == null)
            {
                throw new InvalidInputException(
                    "The dcs.c2stream must contain at least one document");
            }
            CommonAttributesDescriptor.attributeBuilder(attrs).query(query);

            return application().process(attrs, algorithm);
        }
    }

    private static final class InputStreamProcessingResultSupplier implements
        Supplier<ProcessingResult>
    {
        private final InputStream c2stream;

        private InputStreamProcessingResultSupplier(InputStream c2stream)
        {
            this.c2stream = c2stream;
        }

        @Override
        public ProcessingResult get()
        {
            try
            {
                return ProcessingResult.deserialize(c2stream);
            }
            catch (Exception e)
            {
                throw ExceptionUtils.wrapAsRuntimeException(e);
            }
            finally
            {
                Closeables.closeQuietly(c2stream);
            }
        }
    }

    private static final class StringProcessingResultSupplier implements
        Supplier<ProcessingResult>
    {
        private final String c2stream;

        private StringProcessingResultSupplier(String c2stream)
        {
            this.c2stream = c2stream;
        }

        @Override
        public ProcessingResult get()
        {
            try
            {
                return ProcessingResult.deserialize(c2stream);
            }
            catch (Exception e)
            {
                throw ExceptionUtils.wrapAsRuntimeException(e);
            }
        }
    }

    private static final class JsonStreamingOutput implements StreamingOutput
    {
        private final ProcessingResult result;
        private final boolean outputDocuments;
        private final boolean outputClusters;
        private final boolean outputAttributes;

        public JsonStreamingOutput(ProcessingResult result, boolean outputDocuments,
            boolean outputClusters, boolean outputAttributes)
        {
            this.result = result;
            this.outputDocuments = outputDocuments;
            this.outputClusters = outputClusters;
            this.outputAttributes = outputAttributes;
        }

        @Override
        public void write(OutputStream out) throws IOException, WebApplicationException
        {
            result.serializeJson(new OutputStreamWriter(out, Charsets.UTF_8), null,
                false, outputDocuments, outputClusters, outputAttributes);
        }
    }

    private static final class XmlStreamingOutput implements StreamingOutput
    {
        private final ProcessingResult result;
        private final boolean outputDocuments;
        private final boolean outputClusters;
        private final boolean outputAttributes;

        public XmlStreamingOutput(ProcessingResult result, boolean outputDocuments,
            boolean outputClusters, boolean outputAttributes)
        {
            super();
            this.result = result;
            this.outputDocuments = outputDocuments;
            this.outputClusters = outputClusters;
            this.outputAttributes = outputAttributes;
        }

        @Override
        public void write(OutputStream out) throws IOException, WebApplicationException
        {
            try
            {
                result.serialize(out, outputDocuments, outputClusters, outputAttributes);
            }
            catch (Exception e)
            {
                throw new IOException("Could not serialize result", e);
            }
        }
    }
}