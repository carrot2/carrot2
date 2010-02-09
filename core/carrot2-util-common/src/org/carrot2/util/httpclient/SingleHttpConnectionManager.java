
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

package org.carrot2.util.httpclient;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.*;

/**
 * A simple connection manager serving a new connection every time it is asked to do so.
 */
public final class SingleHttpConnectionManager extends SimpleHttpConnectionManager
{
    /** */
    @Override
    public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout)
    {
        final HttpConnection conn = new HttpConnection(hostConfiguration);
        conn.setHttpConnectionManager(this);
        conn.getParams().setDefaults(this.getParams());
        conn.getParams().setSoTimeout((int) timeout);
        return conn;
    }

    /** */
    @Override
    public void releaseConnection(HttpConnection conn)
    {
        // copied from superclass because it wasn't made available to subclasses
        final InputStream lastResponse = conn.getLastResponseInputStream();
        if (lastResponse != null)
        {
            conn.setLastResponseInputStream(null);
            try
            {
                lastResponse.close();
            }
            catch (final IOException ioe)
            {
                // ignore.
            }
        }

        if (conn.isOpen())
        {
            conn.close();
        }
    }
}
