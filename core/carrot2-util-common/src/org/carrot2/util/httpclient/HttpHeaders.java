
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.httpclient;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * Some commonly used HTTP headers.
 */
public class HttpHeaders
{
    /** HTTP header for url-encoded content type. */
    public static final Header URL_ENCODED = new BasicHeader("Content-type",
        "application/x-www-form-urlencoded; charset=UTF-8");

    /** HTTP header for declaring allowed GZIP encoding. */
    public static final Header GZIP_ENCODING = new BasicHeader("Accept-Encoding", "gzip");

    /**
     * HTTP header faking Mozilla as the user agent.
     */
    public static final Header USER_AGENT_HEADER_MOZILLA = new BasicHeader("User-Agent",
        "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:0.9.7) Gecko/20011221");

    private HttpHeaders()
    {
    }
}
