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

import org.carrot2.core.ProcessingResult;
import org.carrot2.log4j.BufferingAppender;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.SystemPropertyStack;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.util.tests.CarrotTestCase;
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

/**
 * Test cases for the {@link DcsApp}.
 */
@ThreadLeakLingering(linger = 1000)
@ThreadLeakScope(Scope.SUITE)
public class DcsRestTest extends CarrotTestCase
{
    private static DcsApp dcs;
    private static Client client;
    private static WebResource dcsBase;

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
        appenderProperty = new SystemPropertyStack(DcsApplication.DISABLE_LOGFILE_APPENDER);
        appenderProperty.push("true");

        classpathLocatorProperty = new SystemPropertyStack(DcsApplication.ENABLE_CLASSPATH_LOCATOR);
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

    @BeforeClass
    public static void prepareClient()
    {
        client = Client.create();
        dcsBase = client.resource("http://localhost:" + dcs.port + "/");
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
    public void getXmlFromExternalSourceDefaultParameters() throws Exception
    {
        final ProcessingResult result = ProcessingResult.deserialize(dcsBase
            .path("cluster/xml").queryParam("query", "test").get(String.class));

        assertThat(result.getDocuments().size()).isGreaterThan(0);
        assertThat(result.getClusters().size()).isGreaterThan(1);
    }
}
