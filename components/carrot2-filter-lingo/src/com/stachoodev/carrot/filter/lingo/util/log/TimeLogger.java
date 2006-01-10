
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

package com.stachoodev.carrot.filter.lingo.util.log;

import org.apache.log4j.Logger;

import java.text.NumberFormat;


/**
 * @author stachoo
 */
public class TimeLogger {
    /** */

    /** DOCUMENT ME! */
    public static final int UNIT_SECONDS = 1;

    /** DOCUMENT ME! */
    public static final int UNIT_MILISECONDS = 2;

    /** */

    /** DOCUMENT ME! */
    private long currentTimeMillis;

    /** */

    /** DOCUMENT ME! */
    private NumberFormat numberFormat;

    /**
     *
     */
    public TimeLogger() {
        super();

        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
    }

    /**
     *
     */
    public void start() {
        currentTimeMillis = System.currentTimeMillis();
    }

    /**
     * @return long
     */
    public long getElapsedAndStart() {
        long elapsed = System.currentTimeMillis() - currentTimeMillis;
        start();

        return elapsed;
    }

    /**
     * @return long
     */
    public long getElapsed() {
        return System.currentTimeMillis() - currentTimeMillis;
    }

    /**
     * @param elapsed
     *
     * @return String
     */
    public String elapsedToString(long elapsed) {
        return elapsedToString(elapsed, UNIT_SECONDS);
    }

    /**
     * @param elapsed
     * @param unit
     *
     * @return String
     */
    public String elapsedToString(long elapsed, int unit) {
        switch (unit) {
        case UNIT_SECONDS:
            return numberFormat.format(elapsed / 1000.0f) + " sec.";

        case UNIT_MILISECONDS:default:
            return Long.toString(elapsed) + " msec.";
        }
    }

    /**
     * @param logger
     */
    public void logElapsedAndStart(Logger logger, String operation) {
        logger.info(operation + " took: " +
            elapsedToString(getElapsedAndStart()));
    }

    /**
     * @param logger
     */
    public void logElapsed(Logger logger, String operation) {
        logger.info(operation + " took: " + elapsedToString(getElapsed()));
    }
}
