
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import static org.carrot2.core.test.ExternalApiTestAssumptions.externalApiTestsEnabled;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.log4j.BufferingAppender;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.SystemPropertyStack;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.google.common.collect.Maps;

import static org.carrot2.dcs.RestProcessorServlet.*;

/**
 * Test cases for the {@link DcsApp}.
 */
public class DcsAppTest
{
    private static DcsApp dcs;

    private static SystemPropertyStack appenderProperty;
    private static SystemPropertyStack classpathLocatorProperty;

    private static String KEY_KACZYNSKI = "/xml/carrot2-kaczynski.utf8.xml";
    private static HashMap<String, File> testFiles = Maps.newHashMap();

    /**
     * Buffered log stream.
     */
    private static BufferingAppender logStream;

    /**
     * DCS startup log.
     */
    private static String startupLog;

    @BeforeClass
    public static void startDcs() throws Throwable
    {
        appenderProperty = new SystemPropertyStack(DISABLE_LOGFILE_APPENDER);
        appenderProperty.push("true");

        classpathLocatorProperty = new SystemPropertyStack(ENABLE_CLASSPATH_LOCATOR);
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
        
        startupLog = logStream.getBuffer();
    }

    @BeforeClass
    public static void prepareStaticFiles() throws Exception
    {
        String [] resources =
        {
            "/xml/carrot2-kaczynski.utf8.xml",
            "/xml/carrot2-kaczynski.utf16.xml"
        };

        final ResourceLookup resourceLookup = 
            new ResourceLookup(Location.CONTEXT_CLASS_LOADER);

        for (String resource : resources)
        {
            final IResource res = resourceLookup.getFirst(resource);
            assertThat(res).isNotNull();

            final File tmp = File.createTempFile("dcs-xml-data", ".xml");
            StreamUtils.copyAndClose(
                res.open(), new FileOutputStream(tmp), 8192);
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
            Assert.fail("DCS not started.");
    }
    
    @Test
    public void testDcsConfigLocation()
    {
        assertThat(startupLog).as("Startup log").contains("[webapp: /WEB-INF/dcs-config.xml]");
    }

    @Test
    public void testStartPage() throws Exception
    {
        final HtmlPage page = getStartPage();
        assertThat(page.getTitleText()).isEqualTo(
            "Quick start - Document Clustering Server");
    }

    @Test
    public void testExternalSource() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());

        final String query = "kaczyński";
        final HtmlForm form = getSearchForm();
        form.getInputByName("query").setValueAttribute(query);
        final HtmlSelect source = form.getSelectByName("dcs.source");

        assertThat(source.getOptions().size()).isGreaterThan(0);
        final String sourceID = "boss-web";
        for (HtmlOption option : source.getOptions())
        {
            if (sourceID.equals(option.getAttribute("value")))
            {
                source.setSelectedAttribute(option, true);
                checkXmlOutput(query, form);
                return;
            }
        }
        Assert.fail("No required external source: " + sourceID);
    }

    @Test
    public void testTextarea() throws Exception
    {
        final HtmlForm form = getSourceFromStringForm();

        checkXmlOutput("kaczyński", form);
    }

    @Test
    public void testFileUpload() throws Exception
    {
        for (String resource : testFiles.keySet())
        {
            final HtmlForm form = getSearchForm();

            // Click on the appropriate radio option to enable fields
            ((HtmlRadioButtonInput) form.getElementById("source-from-file")).click();
            final File dataFile = testFiles.get(resource);
            form.getInputByName("dcs.c2stream").setValueAttribute(dataFile.getAbsolutePath());

            checkXmlOutput("kaczyński", form);
        }
    }

    @Test
    public void testOnlyClusters() throws Exception
    {
        final HtmlForm form = getSourceFromStringForm();
        form.getInputByName("dcs.clusters.only").setChecked(true);

        checkXmlOutput("kaczyński", form, true);
    }

    @Test
    public void testJsonOutput() throws Exception
    {
        final HtmlForm form = getSourceFromStringForm();

        // Click on the appropriate radio option to get JSON output
        ((HtmlRadioButtonInput) form.getElementById("output-format-json")).click();

        final Page dcsResponse = form.getButtonByName("submit").click();
        final String jsonResponse = dcsResponse.getWebResponse().getContentAsString("UTF-8");

        // Just simple assertions, more JSON tests are in ProcessingResultTest
        assertThat(jsonResponse).startsWith("{").endsWith("}").contains("kaczyński");
    }

    @Test
    public void testJsonCallback() throws Exception
    {
        final String callback = "callback";
        final HtmlForm form = getSourceFromStringForm();

        // Click on the appropriate radio option to get JSON output
        ((HtmlRadioButtonInput) form.getElementById("output-format-json")).click();

        form.getInputByName("dcs.json.callback").setValueAttribute(callback);
        final Page dcsResponse = form.getButtonByName("submit").click();
        final String jsonResponse = dcsResponse.getWebResponse().getContentAsString("UTF-8");

        // Just simple assertions, more JSON tests are in ProcessingResultTest
        assertThat(jsonResponse).startsWith(callback + "(").endsWith(");").contains(
            "kaczyński");
    }

    @Test
    public void testParametersPage() throws Exception
    {
        final HtmlPage page = getPage("parameters.html");
        assertThat(page.getTitleText()).isEqualTo(
            "Request parameters - Document Clustering Server");
        assertThat(page.getBody().getTextContent()).contains("dcs.source").doesNotMatch(
            "Loading\\.\\.\\.");
    }

    @Test
    public void testInputPage() throws Exception
    {
        final HtmlPage page = getPage("input.html");
        assertThat(page.getTitleText()).isEqualTo(
            "Input format - Document Clustering Server");
        assertThat(page.getBody().getTextContent()).contains("(optional)").doesNotMatch(
            "Loading\\.\\.\\.");
    }

    @Test
    public void testOutputPage() throws Exception
    {
        final HtmlPage page = getPage("output.html");
        assertThat(page.getTitleText()).isEqualTo(
            "Output format - Document Clustering Server");
        assertThat(page.getBody().getTextContent()).contains("(optional)").doesNotMatch(
            "Loading\\.\\.\\.");
    }

    private HtmlForm getSourceFromStringForm() throws IOException, MalformedURLException
    {
        final HtmlForm form = getSearchForm();

        // Click on the appropriate radio option to enable fields
        ((HtmlRadioButtonInput) form.getElementById("source-from-string")).click();
        form.getTextAreaByName("dcs.c2stream").setText(
            FileUtils.readFileToString(testFiles.get(KEY_KACZYNSKI), "UTF-8"));
        return form;
    }

    private void checkXmlOutput(final String query, final HtmlForm form)
        throws IOException, Exception
    {
        checkXmlOutput(query, form, false);
    }

    private void checkXmlOutput(final String query, final HtmlForm form,
        boolean onlyClusters) throws IOException, Exception
    {
        final XmlPage dcsResponse = (XmlPage) form.getButtonByName("submit").click();
        final String responseXml = dcsResponse.asXml();

        final ProcessingResult dcsResult = ProcessingResult.deserialize(
            new ByteArrayInputStream(responseXml.getBytes("UTF-8")));
        assertThat(dcsResult.getAttributes().get(AttributeNames.QUERY)).isEqualTo(query);
        if (onlyClusters)
        {
            assertThat(dcsResult.getDocuments()).isNull();
        }
        else
        {
            assertThat(dcsResult.getDocuments().size()).isGreaterThan(0);
        }
        assertThat(dcsResult.getClusters().size()).isGreaterThan(0);
    }

    private HtmlPage getStartPage() throws IOException, MalformedURLException
    {
        return getPage("");
    }

    private HtmlPage getPage(final String url) throws IOException, MalformedURLException
    {
        final WebClient webClient = new WebClient();
        final HtmlPage startPage = (HtmlPage) webClient.getPage("http://localhost:"
            + dcs.port + "/" + url);

        // Wait for AJAX calls to complete
        startPage.getEnclosingWindow().getJobManager().waitForJobs(10000);
        return startPage;
    }

    private HtmlForm getSearchForm() throws IOException, MalformedURLException
    {
        final HtmlPage startPage = getStartPage();
        final HtmlForm form = startPage.getFormByName("dcs");
        return form;
    }
}
