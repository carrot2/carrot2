
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.xsltfilter;

/**
 * A public class with several constants used in the XSLT filter.
 */
public final class XSLTFilterConstants
{
    /**
     * To disable XSLT filtering for a given request (useful to return an XML verbatim,
     * for example), set any object under this key in the request context, for example:
     * 
     * <pre>
     * request.setAttribute(XsltFilter.NO_XSLT_PROCESSING, Boolean.TRUE);
     * </pre>
     * 
     * or pass a request parameter with this value.
     */
    public static final String NO_XSLT_PROCESSING = "xslt.filter:disable";

    /**
     * To pass parameters to a stylesheet, register a Map<String,Object> attribute with
     * the key equal to this constant in the request context.
     */
    public static final String XSLT_PARAMS_MAP = "xslt.filter:stylesheet-params";

    /**
     * Error token returned in error pages (for testing).
     */
    final static String ERROR_TOKEN = "xslt.filter:error";
}
