
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp;

/**
 * Constants used in this package.
 * 
 * @author Dawid Weiss
 */
public final class Constants {
    
    // MIME types
    
    public final static String MIME_XML = "text/xml";
    public final static String MIME_HTML = "text/html";

    // encodings
    
    public final static String ENCODING_UTF = "utf-8";

    // full HTTP response types
    
    public final static String MIME_XML_CHARSET_UTF
        = MIME_XML + "; charset=" + ENCODING_UTF;

    public final static String MIME_HTML_CHARSET_UTF
        = MIME_HTML + "; charset=" + ENCODING_UTF;

    public final static String MIME_JSON
        = "application/json";

    // Resource bundle handling
    public final static String DEFAULT_LOCALE = "en";
    public static final String RESOURCE_BUNDLE_KEY = "resournce-bundle";
    
    // Resource bundle keys
    public static final String RB_SHOW_IN_CLUSTTERS = "show-in-clusters";
    public static final String RB_OPEN_IN_NEW_WINDOW = "open-in-new-window";
    public static final String RB_QUERY_RETURNED_NO_DOCUMENTS = "query-returned-no-documents";
    public static final String RB_TRY_MORE_GENERAL_QUERY = "try-more-general-query";
    public static final String RB_NO_CLUSTERS_CREATED = "no-clusters-created";
    public static final String RB_OTHER_TOPICS = "other-topics";
    public static final String RB_MORE_CLUSTERS = "more-clusters";
    public static final String RB_SHOW_ALL_CLUSTERS = "show-all-clusters";
    public static final String RB_ALWAYS_SHOW_ALL_CLUSTERS = "always-show-all-clusters";
    public static final String RB_SAVED = "saved";
    public static final String RB_ALL_RESULTS = "all-results";

    // Cookies
    public static final String COOKIE_DD_TIP = "dd-tip";
    public static final String COOKIE_TAB_ORDER = "tab-order";
    public static final String COOKIE_ACTIVE_TAB = "active-tab";
    public static final String COOKIE_ACTIVE_FACET = "active-facet";
    public static final String COOKIE_SHOW_ALL_CLUSTERS = "always-all-clusters";

    
    /**
     * No instances.
     */
    private Constants() {
        // no instances.
    }
}
