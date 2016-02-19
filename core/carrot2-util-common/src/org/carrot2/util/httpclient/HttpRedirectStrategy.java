
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

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * HTTP redirect strategy.
 */
public enum HttpRedirectStrategy
{
    NO_REDIRECTS, FOLLOW;

    public RedirectStrategy value()
    {
        switch (this)
        {
            case NO_REDIRECTS:
                return new RedirectStrategy()
                {
                    public HttpUriRequest getRedirect(HttpRequest req, HttpResponse resp,
                        HttpContext ctx) throws ProtocolException
                    {
                        throw new UnsupportedOperationException();
                    }

                    public boolean isRedirected(HttpRequest req, HttpResponse resp,
                        HttpContext ctx) throws ProtocolException
                    {
                        return false;
                    }
                };

            case FOLLOW:
                return new DefaultRedirectStrategy();
        }

        throw new RuntimeException();
    }
}
