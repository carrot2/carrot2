package org.carrot2.util;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
     * Close a {@link Connection}, ignoring the exception if any.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore.
            }
        }
    }

    /**
     * Close a {@link Statement}, ignoring the exception if any.
     */
    public static void close(Statement conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore.
            }
        }
    }
}
