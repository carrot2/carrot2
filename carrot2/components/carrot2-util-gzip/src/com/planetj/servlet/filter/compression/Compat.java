
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
