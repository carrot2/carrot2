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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

/**
 * Test cases for the {@link DcsApp}.
 */
@ThreadLeakLingering(linger = 1000)
@ThreadLeakScope(Scope.SUITE)
public class DcsTestBase extends CarrotTestCase
{
    static DcsApp dcs;
    static Client client;
    static WebResource baseUrl;
    static WebResource xmlUrl;
    static WebResource jsonUrl;

    private static SystemPropertyStack appenderProperty;
    private static SystemPropertyStack classpathLocatorProperty;

    /**
     * Buffered log stream.
     */
    private static BufferingAppender logStream;

    /**
     * Path to the config file to use. Default implementation uses a no-limits
     * configuration.
     */
    String dcsConfigPath()
    {
        return "src-test/xml/dcs.nolimits.xml";
    }

    @BeforeClass
    public static void preserveSystemProperties()
    {
        appenderProperty = new SystemPropertyStack(
            DcsApplication.DISABLE_LOGFILE_APPENDER);
        appenderProperty.push("true");

        classpathLocatorProperty = new SystemPropertyStack(
            DcsApplication.ENABLE_CLASSPATH_LOCATOR);
        classpathLocatorProperty.push("true");

        // Tests run with slf4j-log4j, so attach to the logger directly.
        logStream = BufferingAppender.attachToRootLogger();
    }

    @Before
    public void startDcs() throws Throwable
    {
        if (dcs == null)
        {
            SystemPropertyStack dcsConfigFileProperty = new SystemPropertyStack(
                DcsApplication.DCS_CONFIG_PATH);
            dcsConfigFileProperty.push(dcsConfigPath());

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
            finally
            {
                dcsConfigFileProperty.pop();
            }

            // Prepare client
            client = Client.create();
            baseUrl = client.resource("http://localhost:" + dcs.port + "/");
            xmlUrl = baseUrl.path("cluster/xml");
            jsonUrl = baseUrl.path("cluster/json");
        }
    }

    @AfterClass
    public static void stopDcs() throws Exception
    {
        if (dcs != null)
        {
            dcs.stop();
        }

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

    enum Method
    {
        GET, POST_URLENCODED, POST_MULTIPART;
    }

    String requestExternalSource(Method method, WebResource resource)
    {
        return requestExternalSource(method, resource, new String [0]);
    }

    String requestExternalSource(Method method, WebResource resource, String... attrs)
    {
        assertTrue("Attribute key/values must be paired", attrs.length % 2 == 0);

        final Map<String, String> extraAttrs = Maps.newHashMap();
        for (int i = 0; i < attrs.length / 2; i++)
        {
            extraAttrs.put(attrs[i * 2], attrs[i * 2 + 1]);
        }

        if (!extraAttrs.containsKey("query"))
        {
            extraAttrs.put("query", "test");
        }
        if (!extraAttrs.containsKey("dcs.source"))
        {
            extraAttrs.put("dcs.source", "etools");
        }

        switch (method)
        {
            case GET:
                return resource.queryParams(toMultivalueMap(extraAttrs))
                    .get(String.class);

            case POST_URLENCODED:
                return resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(
                    String.class, toMultivalueMap(extraAttrs));

            case POST_MULTIPART:
                final FormDataMultiPart formData = new FormDataMultiPart();
                for (Entry<String, String> entry : extraAttrs.entrySet())
                {
                    formData.field(entry.getKey(), entry.getValue());
                }
                return resource.type(MediaType.MULTIPART_FORM_DATA).post(String.class,
                    formData);
        }

        throw new IllegalArgumentException("Method must not be null");
    }

    MultivaluedMap<String, String> toMultivalueMap(Map<String, String> map)
    {
        final MultivaluedMap<String, String> result = new MultivaluedMapImpl();
        for (Entry<String, String> entry : map.entrySet())
        {
            result.add(entry.getKey(), entry.getValue());
        }
        return result;
    }

    MultivaluedMap<String, String> resourceFormData(final String res, String encoding)
        throws IOException
    {
        final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("dcs.c2stream",
            new String(StreamUtils.readFully(prefetchResource(res)), encoding));
        return formData;
    }

    FormDataMultiPart resourceMultiPart(final String res) throws IOException
    {
        return (FormDataMultiPart) new FormDataMultiPart()
            .bodyPart(new StreamDataBodyPart("dcs.c2stream", prefetchResource(res)));
    }

    InputStream prefetchResource(String resource) throws IOException
    {
        final IResource res = new ResourceLookup(Location.CONTEXT_CLASS_LOADER)
            .getFirst(resource);
        assertThat(res).isNotNull();
        return StreamUtils.prefetch(res.open());
    }

    void assertXmlHasDocumentsAndClusters(final String xml) throws Exception
    {
        final ProcessingResult result = ProcessingResult.deserialize(xml);
        assertThat(result.getDocuments().size()).isGreaterThan(0);
        assertThat(result.getClusters().size()).isGreaterThan(1);
    }

    void assertXmlHasQuery(final String xml, String expectedQuery) throws Exception
    {
        assertXmlHasAttribute(xml, "query", expectedQuery);
    }

    void assertXmlHasAttribute(final String xml, String key, Object expectedValue)
        throws Exception
    {
        final ProcessingResult result = ProcessingResult.deserialize(xml);
        assertThat(result.getAttribute(key)).isEqualTo(expectedValue);
    }

    void assertJsonHasDocumentsAndClusters(final String json)
    {
        assertThat(json).contains("\"documents\":").contains("\"clusters\":");
    }

    void assertJsonHasCallback(final String json, String callback)
    {
        assertThat(json).startsWith(callback + "(").endsWith(")");
    }
}
