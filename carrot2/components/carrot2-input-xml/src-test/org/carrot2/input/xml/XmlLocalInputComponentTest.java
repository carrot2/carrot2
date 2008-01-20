
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.xml;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.carrot2.core.*;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.core.linguistic.Language;
import org.carrot2.util.tokenizer.languages.english.English;
import org.carrot2.filter.lingo.local.LingoLocalFilterComponent;
import org.carrot2.input.xml.XmlLocalInputComponent;

/**
 * Tests the component. 
 * @author Dawid Weiss
 */
public class XmlLocalInputComponentTest extends TestCase {

    public XmlLocalInputComponentTest(String s) {
        super(s);
    }
    
	protected LocalControllerBase setUpController() throws Exception {
		LocalControllerBase controller;
		
        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new ArrayOutputComponent();
            }
        };
        
        LocalComponentFactory inputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new XmlLocalInputComponent();
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
	
	public void testNoParamsInContext() throws Exception {
        LocalControllerBase controller = setUpController();

        String query = "";
        HashMap params = new HashMap();
        try {
            controller.query("testprocess", query, params).getQueryResult();
	        fail();
        } catch (ProcessingException e) {
            // ok, this is expected.
        }
	}
	
	public void testSimpleTransformation() throws Exception {
        LocalControllerBase controller = setUpController();

        String query = "";
        HashMap params = new HashMap();
        params.put("source", this.getClass().getResourceAsStream("test1.xml"));
        params.put("xslt", this.getClass().getResourceAsStream("test1.xsl"));
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult()).documents;
        
        // there should be 4 documents in the result.
        assertEquals(4, results.size());
	}

	public void testSimpleTransformationWithURLs() throws Exception {
        LocalControllerBase controller = setUpController();

        String query = "";
        HashMap params = new HashMap();
        params.put("source", this.getClass().getResource("test1.xml"));
        params.put("xslt", this.getClass().getResource("test1.xsl"));
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult()).documents;
        
        // there should be 4 documents in the result.
        assertEquals(4, results.size());
	}

    public void testIdentityXslt() throws Exception {
        LocalControllerBase controller = setUpController();

        String query = "";
        HashMap params = new HashMap();
        params.put("source", this.getClass().getResource("test1.xml"));
        params.put("xslt", "identity");
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult()).documents;
        
        // there should be 4 documents in the result.
        assertEquals(4, results.size());
    }

	public void testTransformationWithReplaceableArgumentsInContext() throws Exception {
        LocalControllerBase controller = setUpController();

        String query = "";
        HashMap params = new HashMap();
        
        String url = this.getClass().getResource("test1.xml").toExternalForm();
        // replace part of the path (a package name) with an attribute
        if (url.indexOf("carrot2") >= 0) {
            url = url.substring(0, url.indexOf("carrot2"))
            	+ "${param}"
            	+ url.substring(url.indexOf("carrot2") + "carrot2".length());
        } else {
            throw new RuntimeException("Wrong test setup? (url: "
                    + url + ")");
        }
        
        params.put("source", url);
        params.put("xslt", this.getClass().getResource("test1.xsl"));
        params.put("param", "carrot2");
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult()).documents;

        // there should be 4 documents in the result.
        assertEquals(4, results.size());
	}
	
	public void testTransformationWithReplaceableQuery() throws Exception {
        LocalControllerBase controller = setUpController();

        String query = "carrot2";
        HashMap params = new HashMap();
        
        String url = this.getClass().getResource("test1.xml").toExternalForm();
        // replace part of the path (a package name) with an attribute
        if (url.indexOf("carrot2") >= 0) {
            url = url.substring(0, url.indexOf("carrot2"))
            	+ "${query}"
            	+ url.substring(url.indexOf("carrot2") + "carrot2".length());
        } else {
            throw new RuntimeException("Wrong test setup!");
        }
        
        params.put("source", url);
        params.put("xslt", this.getClass().getResource("test1.xsl"));
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult()).documents;

        // there should be 4 documents in the result.
        assertEquals(4, results.size());
	}
	
	public void testRssTransformationWithLingo() throws Exception {
		LocalControllerBase controller;
		
        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new ArrayOutputComponent();
            }
        };
        
		// Clustering component here.
		LocalComponentFactory lingoFactory = new LocalComponentFactory() {
			public LocalComponent getInstance()
			{
				HashMap defaults = new HashMap();
				
				// These are adjustments settings for the clustering algorithm...
				// You can play with them, but the values below are our 'best guess'
				// settings that we acquired experimentally.
				defaults.put("lsi.threshold.clusterAssignment", "0.150");
				defaults.put("lsi.threshold.candidateCluster",  "0.775");

				// Lingo uses stemmers and stop words from the languages below.
				return new LingoLocalFilterComponent(
					new Language[]
					{ 
						new English() 
					}, defaults);
			}
		};
        
        LocalComponentFactory inputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new XmlLocalInputComponent();
            }
        };        

        // Register with the controller
        controller = new LocalControllerBase();
        controller.addLocalComponentFactory("output", outputFactory);
        controller.addLocalComponentFactory("input", inputFactory);
		controller.addLocalComponentFactory("lingo", lingoFactory);        

        // Create and register the process
        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.addFilter("lingo");
        process.setOutput("output");
        controller.addProcess("testprocess", process);
        
        // first check the precached result:
        String query = "";
        HashMap params = new HashMap();
        params.put("source", this.getClass().getResource("rss-bbc.xml"));
        params.put("xslt", this.getClass().getResource("rss.xsl"));
        ArrayOutputComponent.Result output =
			(ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult();

        RawDocument rd = (RawDocument) ((RawCluster) output.clusters.get(0)).getDocuments().get(0);
        assertNotNull(rd.getUrl());
        assertNotNull(rd.getId());
        assertNotNull(rd.getSnippet());
        // there should be a 100 results:
        assertTrue(output.clusters.size() > 0);
	}	

	public void testRssTransformation() throws Exception {
        LocalControllerBase controller = setUpController();

        // first check the precached result:
        String query = "";
        HashMap params = new HashMap();
        params.put("source", this.getClass().getResource("rss-bbc.xml"));
        params.put("xslt", this.getClass().getResource("rss.xsl"));
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult()).documents;

        // there should be a 100 results:
        assertTrue(results.size() > 0);
	}

	public void testIndeedTransformation() throws Exception {
        LocalControllerBase controller = setUpController();

        // first check the precached result:
        String query = "";
        HashMap params = new HashMap();

        params.put("source", this.getClass().getResource("test2.xml"));
        params.put("xslt", this.getClass().getResource("test2.xsl"));
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult()).documents;

        // there should be some results
        assertTrue(results.size() > 0);
	}

    public void testDefaultConstructors() throws Exception {
        LocalControllerBase controller;
        
        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new ArrayOutputComponent();
            }
        };
        
        LocalComponentFactory inputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new XmlLocalInputComponent(
                        this.getClass().getResource("test2.xml"),
                        this.getClass().getResource("test2.xsl"));
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
        
        // first check the precached result:
        String query = "";
        HashMap params = new HashMap();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult()).documents;

        // there should be some results
        assertTrue(results.size() > 0);
    }

	public void testParameterPassingToStylesheet() throws Exception {
        LocalControllerBase controller = setUpController();

        String query = "query";
        HashMap params = new HashMap();
        
        params.put(LocalInputComponent.PARAM_QUERY, "value!");
        params.put("custom.param", "value!");

        params.put("source", this.getClass().getResourceAsStream("testParam.xml"));
        params.put("xslt", this.getClass().getResourceAsStream("testParam.xsl"));
        controller.query("testprocess", query, params).getQueryResult();
        // there should be no error in the transformation.
	}	
	
    public void testSubstitute() {
        // no replacements.
        HashMap params = new HashMap();
        String url = "http://www.google.com/bubu?haha=abc";
        String result = XmlLocalInputComponent.substituteParams(url, null, params, 0,
            XmlLocalInputComponent.DEFAULT_QUERY_ENCODING);
        assertEquals(url, result);

        // simple replacements
        params = new HashMap();
        params.put("param1", "value");
        params.put("param2", "value2");
        url = "http://www.google.com/q=${param1}&${param2}=abc";
        String expected = "http://www.google.com/q=value&value2=abc";
        result = XmlLocalInputComponent.substituteParams(url, null, params, 0,
            XmlLocalInputComponent.DEFAULT_QUERY_ENCODING);
        assertEquals(expected, result);
        
        // no value matching replacement.
        params = new HashMap();
        url = "http://www.google.com/q=${param1}&${param2}=abc";
        expected = url;
        result = XmlLocalInputComponent.substituteParams(url, null, params, 0,
            XmlLocalInputComponent.DEFAULT_QUERY_ENCODING);
        assertEquals(expected, result);

        // empty brackets (no param name)
        params = new HashMap();
        url = "http://www.google.com/q=${}";
        expected = url;
        result = XmlLocalInputComponent.substituteParams(url, null, params, 0,
            XmlLocalInputComponent.DEFAULT_QUERY_ENCODING);
        assertEquals(expected, result);

        // no closing bracket
        params = new HashMap();
        url = "http://www.google.com/q=${param1&${param2}=abc";
        expected = url;
        result = XmlLocalInputComponent.substituteParams(url, null, params, 0,
            XmlLocalInputComponent.DEFAULT_QUERY_ENCODING);
        assertEquals(expected, result);        

        // mixed boundary conditions
        params = new HashMap();
        params.put("param1", "");
        params.put("param2", "xx");
        url = "${param1}http://www.google.com/q=${paramx&${param2}${";
        expected = "http://www.google.com/q=${paramx&xx${";
        result = XmlLocalInputComponent.substituteParams(url, null, params, 0,
            XmlLocalInputComponent.DEFAULT_QUERY_ENCODING);
        assertEquals(expected, result);        
        
        // encoding of values
        params = new HashMap();
        params.put("param1", "x +x");
        url = "http://www.google.com/q=${param1}";
        expected = "http://www.google.com/q=x+%2Bx";
        result = XmlLocalInputComponent.substituteParams(url, null, params, 0,
            XmlLocalInputComponent.DEFAULT_QUERY_ENCODING);
        assertEquals(expected, result);
        
        // encoding of values
        params = new HashMap();
        url = "http://www.google.com/q=${query}&num=${results}";
        expected = "http://www.google.com/q=x+%2Bx&num=113";
        result = XmlLocalInputComponent.substituteParams(url, "x +x", params, 113,
            XmlLocalInputComponent.DEFAULT_QUERY_ENCODING);
        assertEquals(expected, result);        
    }
}
