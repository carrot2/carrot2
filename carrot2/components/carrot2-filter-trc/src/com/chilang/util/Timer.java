
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.chilang.util;

import java.text.NumberFormat;

public class Timer {

    private static final NumberFormat format = NumberFormat.getNumberInstance();
    private long timerStart;

    public static final int SECS = 1;
    public static final int MILIS = 2;

    private int prec;
    public Timer() {
        prec = MILIS;
    }
    public Timer(int precision) {
        prec = precision;
    }
    public void start() {
        timerStart = System.currentTimeMillis();
//        format.setMinimumFractionDigits(3);
    }

    public long elapsedAndStart() {
        long elapsed = System.currentTimeMillis() - timerStart;
        start();
        return elapsed;
    }

    public long elapsed() {
        return System.currentTimeMillis() - timerStart;
    }

    public String elapsedAsString() {
        return asString(elapsed());
    }
    
    public String asString(long elapsed) {
        switch(prec) {
            case SECS : return format.format((double)elapsed / 1000.0) + " sec";
            case MILIS : return elapsed + " msec";
            default :
                return elapsed + " msec";
        }
    }

    public String elapsedAsStringAndStart() {
        return asString(elapsedAndStart());
    }
}
