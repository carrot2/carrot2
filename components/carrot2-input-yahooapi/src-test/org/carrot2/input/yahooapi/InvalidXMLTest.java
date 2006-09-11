
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.yahooapi;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

/**
 * Tests of Yahoo API and results well-formedness.
 * 
 * @author Dawid Weiss
 */
public class InvalidXMLTest extends junit.framework.TestCase {
    private final static Logger logger = Logger.getLogger(InvalidXMLTest.class);

    private final String query;
    private final int results; 

    public InvalidXMLTest(String query, int results) {
        this.query = query;
        this.results = results;

        setName(query);
    }

    public static Test suite() {
        final String [][] queries = new String [][] {
                {"ArcIMS", "200"},
                {"talabis", "100"},
                {"Kosher or israel or torah", "100"},
                {"ole nilsson", "100"},
                {"chalva", "100"},
                {"Apple Computer", "100"},
                {"Public Transit or mass transit", "100"},
                {"west bank", "100"},
        };

        final TestSuite suite = new TestSuite();
        for (int i = 0; i < queries.length; i++) {
            suite.addTest(
                    new InvalidXMLTest(queries[i][0], Integer.parseInt(queries[i][1])));
        }

        return suite;
    }

    /**
     * 
     */
    public void testQuery() throws Exception {
        final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(
                this.getClass().getClassLoader().getResourceAsStream("resource/yahoo.xml"));
        final YahooSearchService service = new YahooSearchService(descriptor);

        service.setUseSaxParser(true); // force valid XML
        service.query(query, results);
    }

    protected void runTest() throws Throwable {
        this.testQuery();
    }
}
