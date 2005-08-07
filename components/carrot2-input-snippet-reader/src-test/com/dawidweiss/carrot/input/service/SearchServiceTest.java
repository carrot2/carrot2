package com.dawidweiss.carrot.input.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.dawidweiss.carrot.input.snippetreader.service.SearchService;
import com.dawidweiss.carrot.util.common.StreamUtils;

import junit.framework.TestCase;

/**
 * A test case for search service descriptor class.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class SearchServiceTest extends TestCase {

    public SearchServiceTest(String s) {
        super(s);
    }

    /**
     * Tests only parsing of descriptor files.
     * @throws IOException
     */
    public void testParsingDescriptorFiles() throws IOException {
        File servicesDir = new File("web" + File.separator + "services");
        if (!servicesDir.isDirectory()) {
            throw new RuntimeException("Services folder not found: "
                    + servicesDir.getAbsolutePath());
        }
        
        File services [] = servicesDir.listFiles(new FilenameFilter() {
			public boolean accept(File parent, String file) {
                return file.endsWith(".xml");
			}
        });
            
        for (int i=0 ; i<services.length ; i++) {
            new SearchService(services[i]);
        }
    }

    /**
     * Test generic page retrieval method.
     */
    public void testRetrievalOfAWebPage() throws Exception {
        File serviceFile = new File("web" + File.separator + "services"
                + File.separator + "google.xml");
        if (!serviceFile.isFile()) {
            throw new RuntimeException("Service file not found: "
                    + serviceFile.getAbsolutePath());
        }
        
        SearchService service = new SearchService(serviceFile);
        HashMap params = new HashMap();
        params.put("query.string", "apache ant");
        params.put("query.startFrom", "0");
        InputStream is = service.getRawPage(params);
        assertNotNull(is);
        byte [] page = StreamUtils.readFully(is);
        String pageContent = new String(page, "UTF-8");
        assertTrue(pageContent.indexOf("ant.apache.org") > 0);
    }
}
