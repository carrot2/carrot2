
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

package com.chilang.util;

import java.text.NumberFormat;

/**
 * Factor for different array formatter
 */
public class FormatterFactory {

    private FormatterFactory() {};


    public static  StringFormatter getConsoleFormatter() {
        return CONSOLE_FORMATTER;
    }

    public static StringFormatter getTEXFormatter() {
        return TEX_FORMATTER;
    }

    public static StringFormatter getCSVFormatter() {
        return CSV_FORMATTER;
    }

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    static {
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(false);
    }

    private static final StringFormatter objectFormatter = new StringFormatter() {
        public String toString(Object obj) {
            if (obj == null)
                return "null";
            if (obj instanceof Number)
                return numberFormat.format(obj);
            return obj.toString();
        }
    };

    private static StringFormatter CONSOLE_FORMATTER =
            new ArrayFormatter(new String[]{"", "\t", ""}, objectFormatter);
    private static final StringFormatter TEX_FORMATTER =
            new ArrayFormatter(new String[]{"", " & ", " \\\\"}, objectFormatter);
    private static final StringFormatter CSV_FORMATTER =
            new ArrayFormatter(new String[]{"", ",", ""}, objectFormatter);

    public static NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public static NumberFormat getNumberFormat(int minFractionDigits) {
        numberFormat.setMaximumFractionDigits(minFractionDigits);
        return numberFormat;
    }
}
