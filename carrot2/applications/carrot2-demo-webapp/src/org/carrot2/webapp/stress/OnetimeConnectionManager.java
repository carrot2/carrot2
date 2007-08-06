package org.carrot2.webapp.stress;

import org.apache.commons.httpclient.*;

/**
 * Implements {@link HttpConnectionManager} but does not attempt
 * to reuse or pool connections.
 * 
 * @author Dawid Weiss
 */
public class OnetimeConnectionManager extends SimpleHttpConnectionManager
{
    public void releaseConnection(HttpConnection conn)
    {
        if (this.httpConnection != null)
        {
            super.releaseConnection(conn);
            this.httpConnection.close();
            this.httpConnection = null;
        }
    }
}
