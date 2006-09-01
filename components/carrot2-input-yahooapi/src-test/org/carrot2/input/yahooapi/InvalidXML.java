package org.carrot2.input.yahooapi;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Tests of Yahoo API and results well-formedness. 
 * 
 * @author Dawid Weiss
 */
public class InvalidXML extends junit.framework.TestCase {
    private final static Logger logger = Logger.getLogger(InvalidXML.class); 

    public InvalidXML(String s) {
        super(s);
    }
    
    public void testBrokenXMLResponses() throws Exception {
        // This test shows the problems present in Yahoo API (non well-formed XML)
        // returned from the engine.
        final String [][] queries = new String [][] {
                {"ArcIMS", "200"}
        };

        final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(
                this.getClass().getClassLoader().getResourceAsStream("resource/yahoo.xml"));
        final YahooSearchService service = new YahooSearchService(descriptor);

        for (int i = 0; i < queries.length; i++) {
            final String query = queries[i][0];
            try {
                service.query(queries[i][0], Integer.parseInt(queries[i][1]));
                logger.info("This query now passes OK: " + query);
            } catch (IOException e) {
                logger.warn("This query does not pass XML validation: " + query, e);
            }
        }
    }
}
