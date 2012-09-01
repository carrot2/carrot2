package org.carrot2.dcs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.util.ExceptionUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.sun.jersey.api.json.JSONWithPadding;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
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
    @Path("/xml")
    @Produces(MediaType.APPLICATION_XML + "; charset=UTF-8")
    public Response xmlGet(
        @QueryParam("dcs.source") String source,
        @QueryParam("dcs.algorithm") String algorithm,
        @QueryParam("query") String query,
        @QueryParam("results") Integer results,
        @QueryParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @QueryParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @QueryParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes,
        @Context UriInfo uriInfo) throws Exception
    {
        return application()
            .ok()
            .entity(
                new XmlStreamingOutput(processFromExternalSource(
                    source,
                    algorithm,
                    query,
                    results,
                    new MultivaluedMapCustomAttributesSupplier(uriInfo
                        .getQueryParameters())), outputDocuments, outputClusters,
                    outputAttributes)).type(MediaType.APPLICATION_XML).build();
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
        @FormParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @FormParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @FormParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes,
        MultivaluedMap<String, String> allParams) throws Exception
    {
        return application()
            .ok()
            .entity(
                new XmlStreamingOutput(processPost(source,
                    new StringProcessingResultSupplier(c2stream), algorithm, query,
                    results, new MultivaluedMapCustomAttributesSupplier(allParams)),
                    outputDocuments, outputClusters, outputAttributes))
            .type(MediaType.APPLICATION_XML).build();
    }

    @POST
    @Path("/xml")
    @Produces("application/xml; charset=UTF-8")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response xmlPostMultipart(
        @FormDataParam("dcs.source") String source,
        final @FormDataParam("dcs.c2stream") InputStream c2stream,
        @FormDataParam("dcs.algorithm") String algorithm,
        @FormDataParam("query") String query,
        @FormDataParam("results") Integer results,
        @FormDataParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @FormDataParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @FormDataParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes,
        FormDataMultiPart formData) throws Exception
    {
        return application()
            .ok()
            .entity(
                new XmlStreamingOutput(processPost(source,
                    new InputStreamProcessingResultSupplier(c2stream), algorithm, query,
                    results, new FormDataMultiPartCustomAttributesSupplier(formData)),
                    outputDocuments, outputClusters, outputAttributes))
            .type(MediaType.APPLICATION_XML).build();
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
        @QueryParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes,
        @QueryParam("dcs.json.callback") @DefaultValue("") String callback,
        @Context UriInfo uriInfo) throws IOException
    {
        return wrapWithCallback(
            application().ok(),
            callback,
            new JsonStreamingOutput(
                processFromExternalSource(
                    source,
                    algorithm,
                    query,
                    results,
                    new MultivaluedMapCustomAttributesSupplier(uriInfo
                        .getQueryParameters())), outputDocuments, outputClusters,
                outputAttributes)).build();
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
        @FormParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @FormParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @FormParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes,
        @FormParam("dcs.json.callback") @DefaultValue("") String callback,
        MultivaluedMap<String, String> allParams) throws Exception
    {
        return wrapWithCallback(
            application().ok(),
            callback,
            new JsonStreamingOutput(processFromExternalSource(source, algorithm, query,
                results, new MultivaluedMapCustomAttributesSupplier(allParams)),
                outputDocuments, outputClusters, outputAttributes)).build();
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
        @FormDataParam("dcs.output.documents") @DefaultValue("true") boolean outputDocuments,
        @FormDataParam("dcs.output.clusters") @DefaultValue("true") boolean outputClusters,
        @FormDataParam("dcs.output.attributes") @DefaultValue("true") boolean outputAttributes,
        @FormDataParam("dcs.json.callback") @DefaultValue("") String callback,
        FormDataMultiPart formData) throws Exception
    {
        return wrapWithCallback(
            application().ok(),
            callback,
            new JsonStreamingOutput(processPost(source,
                new InputStreamProcessingResultSupplier(c2stream), algorithm, query,
                results, new FormDataMultiPartCustomAttributesSupplier(formData)),
                outputDocuments, outputClusters, outputAttributes)).build();
    }

    /**
     * Processes clustering of documents from external sources.
     */
    private ProcessingResult processFromExternalSource(String source, String algorithm,
        String query, Integer results,
        Supplier<Map<String, Object>> customAttributesSupplier)
    {
        final Map<String, Object> attrs = customAttributesSupplier.get();

        // Override common attributes
        CommonAttributesDescriptor
            .attributeBuilder(attrs)
            .query(query)
            .results(
                Math.min(application().config.maxResultsFromExternalSource, Objects
                    .firstNonNull(results,
                        application().config.defaultResultsFromExternalSource)));

        return application().process(attrs, source, algorithm);
    }

    /**
     * Processes the POST clustering requests.
     */
    private ProcessingResult processPost(String source,
        Supplier<ProcessingResult> processingResultSupplier, String algorithm,
        String query, Integer results,
        Supplier<Map<String, Object>> customAttributesSupplier) throws Exception,
        UnsupportedEncodingException
    {
        if (!Strings.isNullOrEmpty(source))
        {
            return processFromExternalSource(source, algorithm, query, results,
                customAttributesSupplier);
        }
        else
        {
            final Map<String, Object> attrs = Maps.newHashMap();
            final ProcessingResult input;
            try
            {
                input = processingResultSupplier.get();
            }
            catch (InvalidInputException e)
            {
                throw e;
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

            // Override custom attributes, if provided
            attrs.putAll(customAttributesSupplier.get());

            // Override by parameter-provided query, if present
            if (!Strings.isNullOrEmpty(query))
            {
                CommonAttributesDescriptor.attributeBuilder(attrs).query(query);
            }

            return application().process(attrs, algorithm);
        }
    }

    static ResponseBuilder wrapWithCallback(ResponseBuilder builder, String callback,
        Object result)
    {
        if (Strings.isNullOrEmpty(callback))
        {
            return builder.entity(result).type(
                MediaType.APPLICATION_JSON + "; charset=UTF-8");
        }
        else
        {
            return builder.entity(new JSONWithPadding(result, callback)).type(
                "application/x-javascript; charset=UTF-8");
        }
    }

    private static final class FormDataMultiPartCustomAttributesSupplier implements
        Supplier<Map<String, Object>>
    {
        private final FormDataMultiPart formData;

        public FormDataMultiPartCustomAttributesSupplier(FormDataMultiPart formData)
        {
            this.formData = formData;
        }

        @Override
        public Map<String, Object> get()
        {
            final Map<String, Object> attrs = Maps.newHashMap();

            // Custom attributes
            for (Entry<String, List<FormDataBodyPart>> entry : formData.getFields()
                .entrySet())
            {
                if (!entry.getKey().equals("dcs.c2stream"))
                {
                    attrs.put(entry.getKey(), entry.getValue().get(0).getValue());
                }
            }

            return attrs;
        }
    }

    private static final class MultivaluedMapCustomAttributesSupplier implements
        Supplier<Map<String, Object>>
    {
        private final MultivaluedMap<String, String> map;

        public MultivaluedMapCustomAttributesSupplier(MultivaluedMap<String, String> map)
        {
            this.map = map;
        }

        @Override
        public Map<String, Object> get()
        {
            final Map<String, Object> attrs = Maps.newHashMap();

            // Custom attributes
            for (String key : map.keySet())
            {
                attrs.put(key, map.getFirst(key));
            }

            return attrs;
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
            if (c2stream == null)
            {
                throw new InvalidInputException(
                    "Non-empty dcs.source or dcs.c2stream is required");
            }

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
            if (Strings.isNullOrEmpty(c2stream))
            {
                throw new InvalidInputException(
                    "Non-empty dcs.source or dcs.c2stream is required");
            }

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