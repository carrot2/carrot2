package org.carrot2.dcs.http.xmlrpc;

import org.apache.log4j.Logger;

/**
 * A simple service for echoing strings (used for testing character encoding
 * with different APIs).
 * 
 * @author Dawid Weiss
 */
public final class StringEncodingHandler {
    private final static Logger logger = Logger.getLogger(StringEncodingHandler.class);

    /**
     * A test string in Unicode, containing Polish characters (with diacritics):
     * <pre>start:łóęąśżźćńŁÓĘŻĆŚ:end</pre>
     */
    private final static String testString = "start:\u0142\u00f3\u0119\u0105\u015b\u017c" 
        + "\u017a\u0107\u0144\u0141\u00d3\u0118\u017b\u0106\u015a:end";

    /**
     * Echoes the incoming string.
     */
    public String echo(String incoming) {
        logger.info("Incoming string: " + incoming);
        return incoming;
    }

    /**
     * Returns some UTF-8 encoded characters (polish diacritics).
     */
    public String echoUtfChars() {
        logger.info("Echoing string: " + testString);
        return testString;
    }

    /**
     * Returns true.
     */
    public boolean echoTrue() {
        logger.info("Echoing 'true'");
        return true;
    }
}