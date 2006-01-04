package com.dawidweiss.carrot.input.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.impl.DocumentsConsumerOutputComponent;
import com.dawidweiss.carrot.input.snippetreader.local.SnippetReaderLocalInputComponent;

public class ServiceDescriptorsTest extends TestCase {
    private final static Logger log = Logger.getLogger(ServiceDescriptorsTest.class);

    public ServiceDescriptorsTest(String s) {
        super(s);
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

    /**
     * Test number of results.
     */
    public void testAllTheWeb() throws Exception {
        final File serviceFile = new File("web" + File.separator + "services"
                + File.separator + "alltheweb.xml");

        assertTrue("Service file does not exist (probably run in a wrong folder):"
                + serviceFile.getAbsolutePath(), serviceFile.isFile());

        final LocalComponentFactory factory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                try {
                    final SnippetReaderLocalInputComponent input =
                        new SnippetReaderLocalInputComponent();
                    final FileInputStream is = new FileInputStream(serviceFile);
                    try {
                        input.setConfigurationXml(is);
                    } finally {
                        is.close();
                    }
                    return input;
                } catch (Exception e) {
                    throw new RuntimeException("Could not configure input.", e);
                }
            }
        };

        final LocalControllerBase controller = setUpController(factory);
        final String query = "data mining";
        final long start = System.currentTimeMillis();
        List results = (List) controller.query("testprocess", query, new HashMap()).getQueryResult();
        final long end = System.currentTimeMillis();
        log.info("SnippetReader (alltheweb) query time: " + (end - start) + " ms., results: "
                + results.size());

        // Ensure we got some results.
        assertTrue("Expected more then 50 results, was: " + results.size(),
                results.size() > 50);
    }
}
