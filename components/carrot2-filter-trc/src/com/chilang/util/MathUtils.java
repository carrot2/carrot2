package com.chilang.util;

public class MathUtils {
    private MathUtils(){}

    /**
     * Calculate logarithm of base for an argument
     * @param base logarithm base
     * @param arg argument
     * @return
     */
    public static double log(double base, double arg) {
        return Math.log(arg) / Math.log(base);
    }
    
    public static double log10(double arg) {
        return Math.log(arg) / Math.log(10);
    }
}
