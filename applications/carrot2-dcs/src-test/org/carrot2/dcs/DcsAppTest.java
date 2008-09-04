package org.carrot2.dcs;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import org.carrot2.core.ProcessingResult;
import org.carrot2.core.test.ExternalApiTestBase;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * Test cases for the {@link DcsApp}.
 */
@RunWith(AnnotationRunner.class)
public class DcsAppTest extends ExternalApiTestBase
{
    private static DcsApp dcs;

    @BeforeClass
    public static void startDcs() throws Exception
    {
        dcs = new DcsApp("dcs");
        dcs.port = 57913;
        dcs.start(System.getProperty("dcs.test.web.dir.prefix"));
    }

    @AfterClass
    public static void stopDcs() throws Exception
    {
        dcs.stop();
    }

    @Test
    public void testStartPage() throws Exception
    {
        final HtmlPage page = getStartPage();
        // Wait for AJAX calls to complete
        page.getEnclosingWindow().getThreadManager().joinAll(10000);
        assertThat(page.getTitleText()).isEqualTo("Document Clustering Server - Carrot2");
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testExternalSource() throws Exception
    {
        final HtmlPage startPage = getStartPage();
        
        // Wait for AJAX calls to complete
        startPage.getEnclosingWindow().getThreadManager().joinAll(10000);
        
        final HtmlForm form = startPage.getFormByName("dcs");
        form.getInputByName("query").setValueAttribute("test");
        final HtmlSelect source = form.getSelectByName("dcs.source");
        
        assertThat(source.getOptions().size()).isGreaterThan(0);
        source.setSelectedAttribute(source.getOptions().get(0), true);
        
        final XmlPage dcsResponse = (XmlPage) form.getButtonByName("submit").click();
        final String responseXml = dcsResponse.asXml();

        final ProcessingResult dcsResult = ProcessingResult.deserialize(new StringReader(
            responseXml));
        assertThat(dcsResult.getDocuments().size()).isGreaterThan(0);
        assertThat(dcsResult.getClusters().size()).isGreaterThan(0);
    }

    private HtmlPage getStartPage() throws IOException, MalformedURLException
    {
        final WebClient webClient = new WebClient();
        final HtmlPage startPage = (HtmlPage) webClient.getPage("http://localhost:"
            + dcs.port);
        return startPage;
    }
}
