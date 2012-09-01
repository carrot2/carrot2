package org.carrot2.dcs;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithmDescriptor;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.text.clustering.MultilingualClusteringDescriptor;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Path("/format")
public class FormatsResource
{
    @Context
    private Application application;

    private DcsApplication application()
    {
        return (DcsApplication) application;
    }

    @GET
    @Path("/xml")
    @Produces(MediaType.APPLICATION_XML)
    public Response xml() throws Exception
    {
        final List<Document> documents = Lists.newArrayList();
        documents.add(new Document("Document title", "Document content.", null, null,
            "d1"));
        documents.add(new Document("Document title", "Document content",
            "http://url.com", LanguageCode.ENGLISH, "d2"));
        documents.add(new Document("Document with custom fields", null, null, null, "d3")
            .setField("price", "199.89").setField("items", "23"));

        final Map<String, Object> attrs = Maps.newHashMap();
        CommonAttributesDescriptor.attributeBuilder(attrs).documents(documents)
            .query("query");
        MultilingualClusteringDescriptor.attributeBuilder(attrs).defaultLanguage(
            LanguageCode.ENGLISH);
        LingoClusteringAlgorithmDescriptor.attributeBuilder(attrs)
            .desiredClusterCountBase(35);

        // Use a one-off simple controller so that we don't disturb the query count stats
        final ProcessingResult result = ControllerFactory.createSimple().process(attrs,
            new String [] {});
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        result.serialize(out);

        return application().ok().entity(out.toString(Charsets.UTF_8.name()))
            .type(MediaType.APPLICATION_XML).build();
    }
}