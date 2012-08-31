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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.carrot2.core.ProcessingResult;
import org.carrot2.log4j.BufferingAppender;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.SystemPropertyStack;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.util.tests.CarrotTestCase;
import org.carrot2.util.tests.UsesExternalServices;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * Test cases for the {@link DcsApp}.
 */
@ThreadLeakLingering(linger = 1000)
@ThreadLeakScope(Scope.SUITE)
public class DcsRestTest extends CarrotTestCase
{
    private static DcsApp dcs;
    private static Client client;
    private static WebResource baseUrl;
    private static WebResource xmlUrl;
    private static WebResource jsonUrl;

    private static SystemPropertyStack appenderProperty;
    private static SystemPropertyStack classpathLocatorProperty;

    private static String KEY_KACZYNSKI = "/xml/carrot2-kaczynski.utf8.xml";
    private static String KEY_KACZYNSKI_UTF16 = "/xml/carrot2-kaczynski.utf16.xml";
    private static HashMap<String, File> testFiles = Maps.newHashMap();

    /**
     * Buffered log stream.
     */
    private static BufferingAppender logStream;

    @BeforeClass
    public static void startDcs() throws Throwable
    {
        appenderProperty = new SystemPropertyStack(
            DcsApplication.DISABLE_LOGFILE_APPENDER);
        appenderProperty.push("true");

        classpathLocatorProperty = new SystemPropertyStack(
            DcsApplication.ENABLE_CLASSPATH_LOCATOR);
        classpathLocatorProperty.push("true");

        // Tests run with slf4j-log4j, so attach to the logger directly.
        logStream = BufferingAppender.attachToRootLogger();

        dcs = new DcsApp("dcs");
        dcs.port = 57913;
        try
        {
            dcs.start(System.getProperty("dcs.test.web.dir.prefix"));
        }
        catch (Throwable e)
        {
            dcs = null;
            throw e;
        }

        // Prepare client
        client = Client.create();
        baseUrl = client.resource("http://localhost:" + dcs.port + "/");
        xmlUrl = baseUrl.path("cluster/xml");
        jsonUrl = baseUrl.path("cluster/json");
    }

    @BeforeClass
    public static void prepareStaticFiles() throws Exception
    {
        String [] resources =
        {
            "/xml/carrot2-kaczynski.utf8.xml", "/xml/carrot2-kaczynski.utf16.xml"
        };

        final ResourceLookup resourceLookup = new ResourceLookup(
            Location.CONTEXT_CLASS_LOADER);

        for (String resource : resources)
        {
            final IResource res = resourceLookup.getFirst(resource);
            assertThat(res).isNotNull();

            final File tmp = File.createTempFile("dcs-xml-data", ".xml");
            StreamUtils.copyAndClose(res.open(), new FileOutputStream(tmp), 8192);
            tmp.deleteOnExit();

            testFiles.put(resource, tmp);
        }
    }

    @AfterClass
    public static void stopDcs() throws Exception
    {
        dcs.stop();

        BufferingAppender.detachFromRootLogger(logStream);
        logStream = null;

        appenderProperty.pop();
        classpathLocatorProperty.pop();
    }

    @Before
    public void clearLogStream()
    {
        logStream.clear();
    }

    @Before
    public void checkDcsStarted()
    {
        if (dcs == null)
        {
            Assert.fail("DCS not started.");
        }
    }

    @Test
    @UsesExternalServices
    public void getXmlFromExternalSourceDefaultParameters() throws Exception
    {
        assertXmlHasDocumentsAndClusters(xmlUrl.queryParam("query", "test").get(
            String.class));
    }

    @Test
    @UsesExternalServices
    public void getJsonFromExternalSourceDefaultParameters() throws Exception
    {
        assertJsonHasDocumentsAndClusters(jsonUrl.queryParam("query", "test").get(
            String.class));
    }

    @Test
    @UsesExternalServices
    public void postUrlencodedXmlFromExternalSource() throws Exception
    {
        assertXmlHasDocumentsAndClusters(xmlUrl.type(
            MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(String.class,
            sourceQueryFormData("etools", "test")));
    }

    @Test
    @UsesExternalServices
    public void postUrlencodedJsonFromExternalSource() throws Exception
    {
        assertJsonHasDocumentsAndClusters(jsonUrl.type(
            MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(String.class,
            sourceQueryFormData("etools", "test")));
    }

    @Test
    @UsesExternalServices
    public void postMultipartXmlFromExternalSource() throws Exception
    {
        assertXmlHasDocumentsAndClusters(xmlUrl.type(MediaType.MULTIPART_FORM_DATA)
            .post(String.class, sourceQueryFormDataMultipart("etools", "test")));
    }
    
    @Test
    @UsesExternalServices
    public void postMultipartJsonFromExternalSource() throws Exception
    {
        assertJsonHasDocumentsAndClusters(jsonUrl.type(MediaType.MULTIPART_FORM_DATA)
            .post(String.class, sourceQueryFormDataMultipart("etools", "test")));
    }

    private MultivaluedMap<String, String> sourceQueryFormData(final String source,
        final String query)
    {
        final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("dcs.source", source);
        formData.add("query", query);
        return formData;
    }

    private FormDataMultiPart sourceQueryFormDataMultipart(final String source,
        final String query)
    {
        final FormDataMultiPart formData = new FormDataMultiPart();
        formData.field("dcs.source", source);
        formData.field("query", query);
        return formData;
    }

    private void assertXmlHasDocumentsAndClusters(final String xml) throws Exception
    {
        final ProcessingResult result = ProcessingResult.deserialize(xml);
        assertThat(result.getDocuments().size()).isGreaterThan(0);
        assertThat(result.getClusters().size()).isGreaterThan(1);
    }

    private void assertJsonHasDocumentsAndClusters(final String json)
    {
        assertThat(json).contains("\"documents\":").contains("\"clusters\":");
    }
}
