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

import static com.google.common.base.Charsets.UTF_8;
import static org.carrot2.core.test.ExternalApiTestAssumptions.externalApiTestsEnabled;
import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatClusters;
import static org.carrot2.dcs.RestProcessorServlet.DISABLE_LOGFILE_APPENDER;
import static org.carrot2.dcs.RestProcessorServlet.ENABLE_CLASSPATH_LOCATOR;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.log4j.BufferingAppender;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.SystemPropertyStack;
import org.carrot2.util.resource.*;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.junit.*;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.google.common.collect.*;
import com.google.common.io.Files;

/**
 * Test cases for the {@link DcsApp}.
 */
public class DcsAppTest
{
    private static DcsApp dcs;

    private static SystemPropertyStack appenderProperty;
    private static SystemPropertyStack classpathLocatorProperty;

    private static String KEY_KACZYNSKI = "/xml/carrot2-kaczynski.utf8.xml";
    private static String KEY_KACZYNSKI_UTF16 = "/xml/carrot2-kaczynski.utf16.xml";
    private static HashMap<String, File> testFiles = Maps.newHashMap();

    /**
     * Buffered log stream.
     */
    private static BufferingAppender logStream;

    /**
     * DCS startup log.
     */
    private static String startupLog;

    private static enum RequestType {
        GET,
        POST_WWW_URL_ENCODING,
        POST_MULTIPART
    }

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
        if (dcs == null) Assert.fail("DCS not started.");
    }

    @Test
    public void testDcsConfigLocation()
    {
        assertThat(startupLog).as("Startup log").contains(
            "[webapp: /WEB-INF/dcs-config.xml]");
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
        final String sourceID = "bing-web";
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
            form.getInputByName("dcs.c2stream").setValueAttribute(
                dataFile.getAbsolutePath());

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
        final String jsonResponse = dcsResponse.getWebResponse().getContentAsString(
            "UTF-8");

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
        final String jsonResponse = dcsResponse.getWebResponse().getContentAsString(
            "UTF-8");

        // Just simple assertions, more JSON tests are in ProcessingResultTest
        assertThat(jsonResponse).startsWith(callback + "(").endsWith(");")
            .contains("kaczyński");
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

    @Test
    public void testGetWithExternalSource() throws Exception
    {
        final ProcessingResult result = getOrPost(RequestType.GET, ImmutableMap.<String, Object> of(
            "query", "kaczyński",
            "dcs.source", "bing-web",
            "results", "50",
            "dcs.algorithm", "url"
        ));
        assertThatClusters(result.getClusters()).isNotEmpty();
        assertThat(result.getAttribute(AttributeNames.QUERY)).isEqualTo("kaczyński");
    }

    @Test
    public void testPostUrlEncodedWithExternalSource() throws Exception
    {
        final ProcessingResult result = getOrPost(RequestType.POST_WWW_URL_ENCODING, ImmutableMap.<String, Object> of(
            "query", "kaczyński",
            "dcs.source", "bing-web",
            "results", "50",
            "dcs.algorithm", "url"
        ));
        assertThatClusters(result.getClusters()).isNotEmpty();
        assertThat(result.getAttribute(AttributeNames.QUERY)).isEqualTo("kaczyński");
    }

    @Test
    public void testPostWithVariousC2StreamXmlEncoding() throws Exception
    {
        final ProcessingResult result16 = post(KEY_KACZYNSKI_UTF16,
            ImmutableMap.<String, Object> of());
        final ProcessingResult result8 = post(KEY_KACZYNSKI,
            ImmutableMap.<String, Object> of());
        
        List<Document> doc16 = result16.getDocuments();
        List<Document> doc8 = result8.getDocuments();
        assertThat(doc16.size()).isEqualTo(doc8.size());
        for (int i = 0; i < Math.min(doc16.size(), doc8.size()); i++)
        {
            Document d1 = doc16.get(i);
            Document d2 = doc8.get(i);
            assertThat(d1.getTitle()).isEqualTo(d2.getTitle());
            assertThat(d1.getSummary()).isEqualTo(d2.getSummary());
        }
    }

    @Test
    public void directFeedAttributeOverriding() throws Exception
    {
        // Check the original query and attribute values contained in the XML
        final ProcessingResult result = post(KEY_KACZYNSKI,
            ImmutableMap.<String, Object> of());
        assertThatClusters(result.getClusters()).isNotEmpty();
        assertThat(result.getAttribute(AttributeNames.QUERY)).isEqualTo("kaczyński");
        assertThat(result.getAttribute("DocumentAssigner.exactPhraseAssignment"))
            .isEqualTo(true);
        final int initialClusterCount = result.getClusters().size();

        // Override query
        final String otherQuery = "other query";
        final ProcessingResult overriddenQueryResult = post(KEY_KACZYNSKI,
            ImmutableMap.<String, Object> of(AttributeNames.QUERY, otherQuery));
        assertThat(overriddenQueryResult.getAttribute(AttributeNames.QUERY)).isEqualTo(
            otherQuery);

        // Override some attributes
        final ProcessingResult overriddenAttributesResult = post(KEY_KACZYNSKI,
            ImmutableMap.<String, Object> of("LingoClusteringAlgorithm.desiredClusterCountBase", "5"));
        assertThat(overriddenAttributesResult.getClusters().size()).isNotEqualTo(
            initialClusterCount);

        // Note the string instead of an integer here. The reason for this is that the
        // attributes get passed as a string POST parameters and the controller echoes
        // input attributes to output exactly in the form they were provided, from string
        // type conversion is performed only for the purposes of binding to the
        // component's fields.
        assertThat(
            overriddenAttributesResult
                .getAttribute("LingoClusteringAlgorithm.desiredClusterCountBase")).isEqualTo("5");

    }

    private HtmlForm getSourceFromStringForm() throws IOException, MalformedURLException
    {
        final HtmlForm form = getSearchForm();

        // Click on the appropriate radio option to enable fields
        ((HtmlRadioButtonInput) form.getElementById("source-from-string")).click();
        form.getTextAreaByName("dcs.c2stream").setText(
            Files.toString(testFiles.get(KEY_KACZYNSKI), UTF_8));
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

        final ProcessingResult dcsResult = ProcessingResult
            .deserialize(new ByteArrayInputStream(responseXml.getBytes(UTF_8)));
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
        final HtmlPage startPage = (HtmlPage) webClient.getPage(getDcsUrl(url));

        // Wait for AJAX calls to complete
        startPage.getEnclosingWindow().getJobManager().waitForJobs(10000);
        return startPage;
    }

    private String getDcsUrl(final String url)
    {
        return "http://localhost:" + dcs.port + "/" + url;
    }

    private HtmlForm getSearchForm() throws IOException, MalformedURLException
    {
        final HtmlPage startPage = getStartPage();
        final HtmlForm form = startPage.getFormByName("dcs");
        return form;
    }

    /**
     * Makes a direct document feed POST request.
     */
    private ProcessingResult post(String inputDataKey, Map<String, Object> otherAttributes)
        throws IllegalStateException, Exception
    {
        final Map<String, Object> attributes = Maps.newHashMap(otherAttributes);
        attributes.put("dcs.c2stream",
            new ByteArrayBody(Files.toByteArray(testFiles.get(inputDataKey)), "testfile.xml"));

        return getOrPost(RequestType.POST_MULTIPART, attributes);
    }

    /**
     * Makes a GET request.
     */
    private ProcessingResult getOrPost(RequestType requestType, Map<String, Object> otherAttributes)
        throws IllegalStateException, Exception
    {
        final HttpClient client = new DefaultHttpClient();
        final HttpRequestBase request;
        switch (requestType)
        {
            case POST_MULTIPART:
                HttpPost post = new HttpPost(getDcsUrl("dcs/rest"));
                post.setEntity(multipartParams(otherAttributes));
                request = post;
                break;

            case POST_WWW_URL_ENCODING:
                post = new HttpPost(getDcsUrl("dcs/rest"));
                post.setEntity(new UrlEncodedFormEntity(formParams(otherAttributes), "UTF-8"));
                request = post;
                break;

            case GET:
                request = new HttpGet(
                    getDcsUrl("dcs/rest") + "?" 
                        + URLEncodedUtils.format(formParams(otherAttributes), "UTF-8"));
                break;

            default:
                throw new RuntimeException();
        }

        try
        {
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            {
                throw new IOException("Unexpected DCS response: "
                    + response.getStatusLine());
            }

            return ProcessingResult.deserialize(response.getEntity().getContent());
        }
        finally
        {
            client.getConnectionManager().shutdown();
        }
    }

    private HttpEntity multipartParams(Map<String, Object> attributes) throws UnsupportedEncodingException
    {
        final MultipartEntity body = new MultipartEntity(HttpMultipartMode.STRICT, null, UTF_8);
        for (Map.Entry<String, Object> entry : attributes.entrySet())
        {
            if (entry.getValue() instanceof ContentBody) {
                body.addPart(entry.getKey(), (ContentBody) entry.getValue());
            } else {
                body.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), UTF_8));
            }
        }
        return body;
    }

    private List<? extends NameValuePair> formParams(Map<String, Object> otherAttributes)
    {
        final Map<String, Object> attributes = Maps.newHashMap(otherAttributes);
        Assert.assertFalse(attributes.containsKey("dcs.c2stream"));

        final List<NameValuePair> params = Lists.newArrayList();
        for (Map.Entry<String, Object> entry : attributes.entrySet())
        {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        
        return params;
    }
}
