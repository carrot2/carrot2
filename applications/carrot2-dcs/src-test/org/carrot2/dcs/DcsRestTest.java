/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.util.tests.UsesExternalServices;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * Test cases for the {@link DcsApp}.
 */
public class DcsRestTest extends DcsTestBase
{
    private static String RESOURCE_KACZYNSKI_UTF8 = "/xml/carrot2-kaczynski.utf8.xml";
    private static String RESOURCE_KACZYNSKI_UTF16 = "/xml/carrot2-kaczynski.utf16.xml";
    private static String RESOURCE_NO_DOCUMENTS_UTF8 = "/xml/carrot2-no-documents.utf8.xml";
    private static String RESOURCE_INVALID_SYNTAX_UTF8 = "/xml/carrot2-invalid-syntax.utf8.xml";

    @Test
    @UsesExternalServices
    public void getXmlFromExternalSourceDefaultParameters() throws Exception
    {
        assertXmlHasDocumentsAndClusters(requestExternalSource(Method.GET, xmlUrl,
            "dcs.source", null));
    }

    @Test
    @UsesExternalServices
    public void getJsonFromExternalSourceDefaultParameters() throws Exception
    {
        assertJsonHasDocumentsAndClusters(requestExternalSource(Method.GET, jsonUrl,
            "dcs.source", null));
    }

    @Test
    @UsesExternalServices
    public void postUrlencodedXmlFromExternalSource() throws Exception
    {
        assertXmlHasDocumentsAndClusters(requestExternalSource(Method.POST_URLENCODED,
            xmlUrl));
    }

    @Test
    @UsesExternalServices
    public void postUrlencodedJsonFromExternalSource() throws Exception
    {
        assertJsonHasDocumentsAndClusters(requestExternalSource(Method.POST_URLENCODED,
            jsonUrl));
    }

    @Test
    @UsesExternalServices
    public void postMultipartXmlFromExternalSource() throws Exception
    {
        assertXmlHasDocumentsAndClusters(requestExternalSource(Method.POST_MULTIPART,
            xmlUrl));
    }

    @Test
    @UsesExternalServices
    public void postMultipartJsonFromExternalSource() throws Exception
    {
        assertJsonHasDocumentsAndClusters(requestExternalSource(Method.POST_MULTIPART,
            jsonUrl));
    }

    @Test
    public void postUrlencodedXmlFromStream() throws Exception
    {
        final String result = xmlUrl.type(MediaType.APPLICATION_FORM_URLENCODED).post(
            String.class,
            resourceFormData(RESOURCE_KACZYNSKI_UTF8, Charsets.UTF_8.name()));
        assertXmlHasDocumentsAndClusters(result);
    }

    @Test
    public void postMultipartXmlFromStream() throws Exception
    {
        final String result = xmlUrl.type(MediaType.MULTIPART_FORM_DATA).post(
            String.class, resourceMultiPart(RESOURCE_KACZYNSKI_UTF8));
        assertXmlHasDocumentsAndClusters(result);
    }

    @Test
    public void postMultipartJsonFromStream() throws Exception
    {
        assertJsonHasDocumentsAndClusters(jsonUrl.type(MediaType.MULTIPART_FORM_DATA)
            .post(String.class, resourceMultiPart(RESOURCE_KACZYNSKI_UTF8)));
    }

    @Test
    public void postMultipartWithVariousEncodings() throws Exception
    {
        final ProcessingResult utf16Result = ProcessingResult.deserialize(xmlUrl.type(
            MediaType.MULTIPART_FORM_DATA).post(String.class,
            resourceMultiPart(RESOURCE_KACZYNSKI_UTF16)));
        final ProcessingResult utf8Result = ProcessingResult.deserialize(xmlUrl.type(
            MediaType.MULTIPART_FORM_DATA).post(String.class,
            resourceMultiPart(RESOURCE_KACZYNSKI_UTF8)));

        final List<Document> doc16 = utf16Result.getDocuments();
        final List<Document> doc8 = utf8Result.getDocuments();
        assertThat(doc16.size()).isEqualTo(doc8.size());
        for (int i = 0; i < Math.min(doc16.size(), doc8.size()); i++)
        {
            final Document d1 = doc16.get(i);
            final Document d2 = doc8.get(i);
            assertThat(d1.getTitle()).isEqualTo(d2.getTitle());
            assertThat(d1.getSummary()).isEqualTo(d2.getSummary());
        }
    }

    @Test
    public void queryOverriding() throws Exception
    {
        // Check query from the XML
        FormDataMultiPart multiPart = resourceMultiPart(RESOURCE_KACZYNSKI_UTF8);
        assertXmlHasQuery(
            xmlUrl.type(MediaType.MULTIPART_FORM_DATA).post(String.class, multiPart),
            "kaczyński");

        // Add query override
        final String query = "overridden";
        multiPart = resourceMultiPart(RESOURCE_KACZYNSKI_UTF8);
        multiPart.field("query", query);
        assertXmlHasQuery(
            xmlUrl.type(MediaType.MULTIPART_FORM_DATA).post(String.class, multiPart),
            query);
    }

    @Test
    public void attributeOverriding() throws Exception
    {
        // Check attribute from the XML
        FormDataMultiPart multiPart = resourceMultiPart(RESOURCE_KACZYNSKI_UTF8);
        assertXmlHasAttribute(
            xmlUrl.type(MediaType.MULTIPART_FORM_DATA).post(String.class, multiPart),
            "DocumentAssigner.exactPhraseAssignment", true);

        // Add attribute override
        multiPart = resourceMultiPart(RESOURCE_KACZYNSKI_UTF8);
        multiPart.field("DocumentAssigner.exactPhraseAssignment", "false");
        assertXmlHasAttribute(
            xmlUrl.type(MediaType.MULTIPART_FORM_DATA).post(String.class, multiPart),
            "DocumentAssigner.exactPhraseAssignment", "false");
    }

    @Test
    @UsesExternalServices
    public void getCustomAttributes() throws Exception
    {
        assertXmlHasAttribute(
            requestExternalSource(Method.GET, xmlUrl,
                "DocumentAssigner.exactPhraseAssignment", "true"),
            "DocumentAssigner.exactPhraseAssignment", "true");
    }

    @Test
    @UsesExternalServices
    public void postUrlencodedCustomAttributes() throws Exception
    {
        assertXmlHasAttribute(
            requestExternalSource(Method.POST_URLENCODED, xmlUrl,
                "DocumentAssigner.exactPhraseAssignment", "true"),
            "DocumentAssigner.exactPhraseAssignment", "true");
    }

    @Test
    @UsesExternalServices
    public void postMultipartCustomAttributes() throws Exception
    {
        assertXmlHasAttribute(
            requestExternalSource(Method.POST_MULTIPART, xmlUrl,
                "DocumentAssigner.exactPhraseAssignment", "true"),
            "DocumentAssigner.exactPhraseAssignment", "true");
    }

    @Test
    @UsesExternalServices
    public void getJsonCallback() throws Exception
    {
        assertJsonHasCallback(
            requestExternalSource(Method.GET, jsonUrl, "dcs.json.callback", "cb"), "cb");
    }

    @Test
    @UsesExternalServices
    public void postUrlencodedJsonCallback() throws Exception
    {
        assertJsonHasCallback(
            requestExternalSource(Method.POST_URLENCODED, jsonUrl, "dcs.json.callback",
                "cb"), "cb");
    }

    @Test
    @UsesExternalServices
    public void postMultipartJsonCallback() throws Exception
    {
        assertJsonHasCallback(
            requestExternalSource(Method.POST_MULTIPART, jsonUrl, "dcs.json.callback",
                "cb"), "cb");
    }

    @Test(expected = UniformInterfaceException.class)
    @UsesExternalServices
    public void getNoQuerySpecified() throws Exception
    {
        try
        {
            requestExternalSource(Method.GET, xmlUrl, "query", null);
        }
        catch (UniformInterfaceException e)
        {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity(String.class)).contains(
                "Non-empty query is required");
            throw e;
        }
    }

    @Test(expected = UniformInterfaceException.class)
    public void postUrlencodedNoSourceNoStream() throws Exception
    {
        try
        {
            requestExternalSource(Method.POST_URLENCODED, xmlUrl, "dcs.source", null);
        }
        catch (UniformInterfaceException e)
        {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity(String.class)).contains(
                "Non-empty dcs.source or dcs.c2stream is required");
            throw e;
        }
    }

    @Test(expected = UniformInterfaceException.class)
    public void postMultipartNoSourceNoStream() throws Exception
    {
        try
        {
            requestExternalSource(Method.POST_MULTIPART, xmlUrl, "dcs.source", "");
        }
        catch (UniformInterfaceException e)
        {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity(String.class)).contains(
                "Non-empty dcs.source or dcs.c2stream is required");
            throw e;
        }
    }

    @Test(expected = UniformInterfaceException.class)
    public void postFromStreamNoDocuments() throws Exception
    {
        try
        {
            xmlUrl.type(MediaType.MULTIPART_FORM_DATA).post(String.class,
                resourceMultiPart(RESOURCE_NO_DOCUMENTS_UTF8));
        }
        catch (UniformInterfaceException e)
        {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity(String.class)).contains(
                "The dcs.c2stream must contain at least one document");
            throw e;
        }
    }

    @Test(expected = UniformInterfaceException.class)
    public void postMultipartFromStreamInvalidSyntax() throws Exception
    {
        try
        {
            xmlUrl.type(MediaType.MULTIPART_FORM_DATA).post(String.class,
                resourceMultiPart(RESOURCE_INVALID_SYNTAX_UTF8));
        }
        catch (UniformInterfaceException e)
        {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity(String.class)).contains(
                "Could not parse Carrot2 XML stream");
            throw e;
        }
    }

    @Test(expected = UniformInterfaceException.class)
    public void postUrlencodedFromStreamInvalidSyntax() throws Exception
    {
        try
        {
            xmlUrl.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class,
                resourceFormData(RESOURCE_INVALID_SYNTAX_UTF8, Charsets.UTF_8.name()));
        }
        catch (UniformInterfaceException e)
        {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getResponse().getEntity(String.class)).contains(
                "Could not parse Carrot2 XML stream");
            throw e;
        }
    }

    @Test(expected = UniformInterfaceException.class)
    public void processingError() throws Exception
    {
        try
        {
            final MultivaluedMap<String, String> form = resourceFormData(
                RESOURCE_KACZYNSKI_UTF8, Charsets.UTF_8.name());

            // Invalid attribute value to force a processing error
            form.add("LingoClusteringAlgorithm.desiredClusterCountBase", "0");

            xmlUrl.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, form);
        }
        catch (UniformInterfaceException e)
        {
            assertThat(e.getResponse().getStatus()).isEqualTo(500);
            assertThat(e.getResponse().getEntity(String.class)).contains(
                "Processing error");
            throw e;
        }
    }

    @Test
    public void metadataEndpoint()
    {
        final String json = baseUrl.path("metadata/json").get(String.class);
        assertThat(json).startsWith("{").endsWith("}");
    }
    
    @Test
    public void statsEndpoint()
    {
        final String json = baseUrl.path("admin/status/json").get(String.class);
        assertThat(json).startsWith("{").endsWith("}");
    }
    
    @Test
    public void violationsEndpoint()
    {
        final String json = baseUrl.path("admin/violations/json").get(String.class);
        assertThat(json).startsWith("{").endsWith("}");
    }

    @Test
    public void formatEndpoint() throws Exception
    {
        final ProcessingResult result = ProcessingResult.deserialize(baseUrl.path(
            "format/xml").get(String.class));
        assertThat(result.getDocuments()).isNotEmpty();
    }
}
