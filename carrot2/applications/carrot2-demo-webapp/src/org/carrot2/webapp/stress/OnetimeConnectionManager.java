
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
