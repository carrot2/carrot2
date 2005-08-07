package com.dawidweiss.carrot.input.snippetreader.local;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.core.local.LocalComponentFactoryBase;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.core.local.LocalProcessBase;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.impl.DocumentsConsumerOutputComponent;

/**
 * 
 * @author Dawid Weiss
 */
public class TestSnippetReaderLocalInputComponent extends junit.framework.TestCase {
    private final static Logger log = Logger.getLogger(TestSnippetReaderLocalInputComponent.class);

    public TestSnippetReaderLocalInputComponent(String s) {
        super(s);
    }
    
	protected LocalControllerBase setUpController(LocalComponentFactory inputFactory) throws Exception {
		LocalControllerBase controller;
		
        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new DocumentsConsumerOutputComponent();
            }
        };

        // Register with the controller
        controller = new LocalControllerBase();
        controller.addLocalComponentFactory("output", outputFactory);

        controller.addLocalComponentFactory("input", inputFactory);

        // Create and register the process
        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        controller.addProcess("testprocess", process);
        
        return controller;
	}
	
	public void testApacheAntQuery() throws Exception {
        // input component factory
        LocalComponentFactory inputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                SnippetReaderLocalInputComponent sr = new SnippetReaderLocalInputComponent();
                File serviceFile = new File("web" + File.separator + "services"
                        + File.separator + "google.xml");
        	    try {
                    sr.setConfigurationXml(
                            new FileInputStream(serviceFile));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return sr;
            }
        };

        LocalControllerBase controller = setUpController( inputFactory );
        String query = "apache ant";
        List results = (List) controller.query("testprocess", query, new HashMap()).getQueryResult();

        // the results should contain some documents.
        assertTrue("Results acquired from Google"
                + ":" + results.size(), results.size() > 0);
        log.debug("Results acquired from Google: "
                + results.size());

        HashSet urls = new HashSet();
        for (Iterator i = results.iterator(); i.hasNext(); ) {
            RawDocument rd = (RawDocument) i.next();
            // Check the URL.
            try {
                new URL(rd.getUrl());
            } catch (MalformedURLException e) {
                fail("Snippet reader failure (malformed URL): "
                        + rd.toString());
            }
            urls.add(rd.getUrl());
            log.debug(rd.getUrl());
        }
        assertTrue(urls.contains("http://ant.apache.org/"));
        assertTrue(urls.contains("http://ant.apache.org/manual/"));
        assertTrue(urls.contains("http://ant-contrib.sourceforge.net/"));
        assertTrue(urls.contains("http://www.freshports.org/devel/apache-ant/"));
	}
}
