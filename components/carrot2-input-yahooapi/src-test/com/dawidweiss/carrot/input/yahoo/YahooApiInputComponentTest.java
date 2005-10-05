package com.dawidweiss.carrot.input.yahoo;

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
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalProcessBase;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.impl.DocumentsConsumerOutputComponent;
import com.dawidweiss.carrot.input.yahoo.YahooApiInputComponent;
import com.dawidweiss.carrot.input.yahoo.YahooSearchService;
import com.dawidweiss.carrot.input.yahoo.YahooSearchServiceDescriptor;

public class YahooApiInputComponentTest extends junit.framework.TestCase {
    private final static Logger log = Logger.getLogger(YahooApiInputComponentTest.class);

    public YahooApiInputComponentTest(String s) {
        super(s);
    }

    public void testSiteQuery() throws Exception {
        final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo-site-cs.xml"));
        final YahooSearchService service = new YahooSearchService(descriptor);
        final LocalComponentFactory inputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent(service);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "weiss";
        final long start = System.currentTimeMillis();
        List results = (List) controller.query("testprocess", query, new HashMap()).getQueryResult();
        final long end = System.currentTimeMillis();
        log.info("YahooAPI query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Yahoo" + ":" + results.size(), results.size() > 0);
    }

	protected LocalControllerBase setUpController(LocalComponentFactory inputFactory) throws Exception {
		LocalControllerBase controller;
		
        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
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

	public void testMediumQuery() throws Exception {
        final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo.xml"));
        final YahooSearchService service = new YahooSearchService(descriptor);
        final LocalComponentFactory inputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent(service);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "dawid weiss ant styler docbook poznan";
        final long start = System.currentTimeMillis();
        List results = (List) controller.query("testprocess", query, new HashMap()).getQueryResult();
        final long end = System.currentTimeMillis();
        log.info("YahooAPI query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Yahoo"
                + ":" + results.size(), results.size() > 0 && results.size() < 100);
	}
	
	public void testEmptyQuery() throws Exception {
        final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo.xml"));
        final YahooSearchService service = new YahooSearchService(descriptor);
        final LocalComponentFactory inputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent(service);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "duiogig oiudgisugviw siug iugw iusviuwg";
        final long start = System.currentTimeMillis();
        List results = (List) controller.query("testprocess", query, new HashMap()).getQueryResult();
        final long end = System.currentTimeMillis();
        log.info("Yahoo query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Yahoo"
                + ":" + results.size(), results.size() == 0);
	}

    public void testResultsRequested() throws Exception {
        final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo.xml"));
        final YahooSearchService service = new YahooSearchService(descriptor);
        final LocalComponentFactory inputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent(service);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "apache";
        final long start = System.currentTimeMillis();
        HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(50));
        List results = (List) controller.query("testprocess", query, reqContext).getQueryResult();
        final long end = System.currentTimeMillis();
        log.info("Yahoo query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        log.debug("Results acquired from Yahoo: " + results.size());
        assertEquals("Results acquired from Yahoo is 50?"
                + ":" + results.size(), 50, results.size());
    }    
    
	public void testApacheAntQuery() throws Exception {
        final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo.xml"));
        final YahooSearchService service = new YahooSearchService(descriptor);
        final LocalComponentFactory inputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent(service);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "apache ant";
        final long start = System.currentTimeMillis();
        List results = (List) controller.query("testprocess", query, new HashMap()).getQueryResult();
        final long end = System.currentTimeMillis();
        log.info("Yahoo query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Yahoo"
                + ":" + results.size(), results.size() > 0);
        log.debug("Results acquired from Yahoo: " + results.size());

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
        }

        assertTrue(urls.contains("http://ant.apache.org/"));

        assertEquals(100, results.size());
	}
}
