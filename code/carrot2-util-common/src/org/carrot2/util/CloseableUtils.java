package org.carrot2.util;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Static methods for closing various stuff and ignoring
 * exceptions not much can be done about.
 */
public final class CloseableUtils {
    
    private CloseableUtils() {
        // no instances.
    }

    /*
     * 
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
    
    /*
     * 
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

    /*
     * 
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
