
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.util;

public class MathUtils {
    private MathUtils(){}

    /**
     * Calculate logarithm of base for an argument
     * @param base logarithm base
     * @param arg argument
     */
    public static double log(double base, double arg) {
        return Math.log(arg) / Math.log(base);
    }
    
    public static double log10(double arg) {
        return Math.log(arg) / Math.log(10);
    }
}
