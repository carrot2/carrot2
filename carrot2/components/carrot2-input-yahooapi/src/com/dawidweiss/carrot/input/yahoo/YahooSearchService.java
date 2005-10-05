package com.dawidweiss.carrot.input.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.dawidweiss.carrot.util.net.http.HTTPFormSubmitter;

/**
 * A Yahoo Search Service.
 * 
 * @author Dawid Weiss
 */
public class YahooSearchService {
    private YahooSearchServiceDescriptor descriptor;

    public YahooSearchService(YahooSearchServiceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Searches Yahoo and retrieves a maximum of <code>requestedResults</code>
     * snippets. May throw an exception if service is no longer available.
     * 
     * @throws IOException If an I/O exception occurred. 
     */
    public YahooSearchResult [] query(final String query, 
            final int requestedResults) throws IOException {

        final ArrayList result = new ArrayList(requestedResults); 
        final HTTPFormSubmitter submitter = descriptor.getHttpSubmitter();
        InputStream is = null; 

        int startFrom = 1;
        long resultsLeft = requestedResults;
        boolean firstPass = true;

        try {
            final YahooResponseHandler handler = new YahooResponseHandler(result);
            final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            final XMLReader reader = parser.getXMLReader();
            reader.setFeature("http://xml.org/sax/features/validation", false);
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setContentHandler(handler);

            while (true) {
                final long perQueryResults = Math.min(descriptor.getMaxResultsPerQuery(), resultsLeft);
                if (perQueryResults <= 0) {
                    break;
                }

                final HashMap mappedParameters = new HashMap();
                mappedParameters.put("query.string", query);
                mappedParameters.put("query.startFrom", Integer.toString(startFrom));
                mappedParameters.put("query.results", Long.toString(perQueryResults));
                is = submitter.submit(descriptor.getFormParameters(), mappedParameters, "UTF-8");
                reader.parse(new InputSource(is));
                is.close();
                is = null;

                if (handler.firstResultPosition != startFrom && handler.resultsReturned > 0) {
                    throw new IOException("Assertion failed: returned startFrom different.");
                }
                if (firstPass) {
                    // Correct the number of requested results to the maximum available.
                    resultsLeft = Math.min(requestedResults, handler.totalResults);
                    firstPass = false;
                } else {
                    if (handler.resultsReturned == 0) {
                        throw new IOException("Assertion failed: returned zero results?");
                    }
                }
                resultsLeft -= handler.resultsReturned;
            }
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Problems setting up XML parser: " + e.toString(), e);
        } catch (SAXException e) {
            throw new RuntimeException("Problems parsing Yahoo output: " + e.toString(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore exceptions on close.
                }
            }
        }

        return (YahooSearchResult []) result.toArray(new YahooSearchResult [result.size()]);
    }
}
