package org.carrot2.util.xsltfilter;


/**
 * A public class with several constants used in the XSLT filter.
 * 
 * @author Dawid Weiss
 */
public final class XSLTFilterConstants {
    /**
     * To disable XSLT filtering for a given request (useful to return an XML verbatim, for example),
     * set any object under this key in the request context, for example:
     * <pre>
     * request.setAttribute(XsltFilter.NO_XSLT_PROCESSING, Boolean.TRUE);
     * </pre>
     */
    public static final String NO_XSLT_PROCESSING = "xslt.filter:disable";
    
    /**
     * To pass parameters to a stylesheet, register a Map attribute
     * with the key equal to this constant in the request context.
     */
    public static final String XSLT_PARAMS_MAP = "xslt.filter:stylesheet-params";
    
    /**
     * Error token returned in error pages (for testing).
     */
    final static String ERROR_TOKEN = "xslt.filter:error";
}
