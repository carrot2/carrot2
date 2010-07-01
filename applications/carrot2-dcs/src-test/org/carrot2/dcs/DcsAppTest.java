
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
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

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.resource.*;
import org.junit.*;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.google.common.collect.Maps;

/**
 * Test cases for the {@link DcsApp}.
 */
public class DcsAppTest
{
    private static DcsApp dcs;

    private static String appenderProp;
    private static String KEY_KACZYNSKI = "/xml/carrot2-kaczynski.utf8.xml";
    private static HashMap<String, File> testFiles = Maps.newHashMap();

    @BeforeClass
    public static void startDcs() throws Exception
    {
        appenderProp = System.getProperty(RestProcessorServlet.ENABLE_CUSTOM_APPENDER);
        System.setProperty(RestProcessorServlet.ENABLE_CUSTOM_APPENDER, "false");

        dcs = new DcsApp("dcs");
        dcs.port = 57913;
        dcs.start(System.getProperty("dcs.test.web.dir.prefix"));
    }

    @BeforeClass
    public static void prepareStaticFiles() throws Exception
    {
        String [] resources =
        {
            "/xml/carrot2-kaczynski.utf8.xml",
            "/xml/carrot2-kaczynski.utf16.xml"
        };

        final ResourceUtils resUtils = ResourceUtilsFactory.getDefaultResourceUtils();
        for (String resource : resources)
        {
            final IResource res = resUtils.getFirst(resource, DcsAppTest.class);
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
        
        if (appenderProp != null)
            System.setProperty(RestProcessorServlet.ENABLE_CUSTOM_APPENDER, appenderProp);
        else
            System.clearProperty(RestProcessorServlet.ENABLE_CUSTOM_APPENDER);
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
            if (sourceID.equals(option.getAttributeValue("value")))
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
            ((HtmlRadioButtonInput) form.getHtmlElementById("source-from-file")).click();
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
        ((HtmlRadioButtonInput) form.getHtmlElementById("output-format-json")).click();

        final Page dcsResponse = form.getButtonByName("submit").click();
        final String jsonResponse = new String(dcsResponse.getWebResponse()
            .getResponseBody(), "UTF-8");

        // Just simple assertions, more JSON tests are in ProcessingResultTest
        assertThat(jsonResponse).startsWith("{").endsWith("}").contains("kaczyński");
    }

    @Test
    public void testJsonCallback() throws Exception
    {
        final String callback = "callback";
        final HtmlForm form = getSourceFromStringForm();

        // Click on the appropriate radio option to get JSON output
        ((HtmlRadioButtonInput) form.getHtmlElementById("output-format-json")).click();

        form.getInputByName("dcs.json.callback").setValueAttribute(callback);
        final Page dcsResponse = form.getButtonByName("submit").click();
        final String jsonResponse = new String(dcsResponse.getWebResponse()
            .getResponseBody(), "UTF-8");

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
        ((HtmlRadioButtonInput) form.getHtmlElementById("source-from-string")).click();
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
        startPage.getEnclosingWindow().getThreadManager().joinAll(10000);

        return startPage;
    }

    private HtmlForm getSearchForm() throws IOException, MalformedURLException
    {
        final HtmlPage startPage = getStartPage();
        final HtmlForm form = startPage.getFormByName("dcs");
        return form;
    }
}
