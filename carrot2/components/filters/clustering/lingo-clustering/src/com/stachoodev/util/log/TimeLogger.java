

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.util.log;


import java.text.NumberFormat;

import org.apache.log4j.Logger;


/**
 * @author stachoo
 */
public class TimeLogger
{
    /** */
    public static final int UNIT_SECONDS = 1;
    public static final int UNIT_MILISECONDS = 2;

    /** */
    private long currentTimeMillis;

    /** */
    private NumberFormat numberFormat;

    /**
     *
     */
    public TimeLogger()
    {
        super();

        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
    }

    /**
     *
     */
    public void start()
    {
        currentTimeMillis = System.currentTimeMillis();
    }


    /**
     * @return long
     */
    public long getElapsedAndStart()
    {
        long elapsed = System.currentTimeMillis() - currentTimeMillis;
        start();

        return elapsed;
    }


    /**
     * @return long
     */
    public long getElapsed()
    {
        return System.currentTimeMillis() - currentTimeMillis;
    }


    /**
     * @param elapsed
     *
     * @return String
     */
    public String elapsedToString(long elapsed)
    {
        return elapsedToString(elapsed, UNIT_SECONDS);
    }


    /**
     * @param elapsed
     * @param unit
     *
     * @return String
     */
    public String elapsedToString(long elapsed, int unit)
    {
        switch (unit)
        {
            case UNIT_SECONDS:
                return numberFormat.format(elapsed / 1000.0f) + " sec.";

            case UNIT_MILISECONDS:default:
                return Long.toString(elapsed) + " msec.";
        }
    }


    /**
     * @param logger
     */
    public void logElapsedAndStart(Logger logger, String operation)
    {
        logger.info(operation + " took: " + elapsedToString(getElapsedAndStart()));
    }


    /**
     * @param logger
     */
    public void logElapsed(Logger logger, String operation)
    {
        logger.info(operation + " took: " + elapsedToString(getElapsed()));
    }
}
