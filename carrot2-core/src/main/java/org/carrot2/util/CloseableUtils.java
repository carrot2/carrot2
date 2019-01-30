
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

/**
 * Static methods for closing various objects (including implementations of {@link Closeable}).
 */
public final class CloseableUtils
{
    /*
     * No instances.
     */
    private CloseableUtils()
    {
        // no instances.
    }

    /**
     * Close a {@link Closeable}, ignoring the exception if any.
     */
    public static void close(Closeable conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                // Ignore.
            }
        }
    }

    /**
     * Close all {@link Closeable}, ignoring exceptions.
     */
    public static void close(Closeable... closeables) {
        for (Closeable c : closeables) close(c);
    }

    /**
     * Close all {@link Socket}s, ignoring exceptions.
     */
    public static void close(Socket... sockets)
    {
        for (Socket s : sockets)
        {
            try
            {
                if (s != null && !s.isClosed()) s.close();
            }
            catch (Exception e)
            {
                // ignore
            }
        }
    }
}
