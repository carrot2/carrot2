package com.planetj.servlet.filter.compression;

/**
 * A few methods removed or reimplemented to be JDK14-compatible
 * 
 * @author Dawid Weiss
 */
public class Compat {
    public static void assertion(boolean state) {
        if (state == false) {
            throw new RuntimeException("Assertion failed.");
        }
    }
}
