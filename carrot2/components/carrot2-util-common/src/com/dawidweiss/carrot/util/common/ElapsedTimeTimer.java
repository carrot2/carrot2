

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util.common;


/**
 * A simple class used mostly for timing applications' execution time
 * (not a real CPU time though).
 *
 * <p>Has some time-formatting methods for convenience.</p>
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class ElapsedTimeTimer
{
    private long start;

    /** Creates a timer object and starts counting. */
    public ElapsedTimeTimer()
    {
        restart();
    }

    /** Starts counting from now. */
    public void restart()
    {
        start = System.currentTimeMillis();
    }

    /** Returns the number of milliseconds since the timer was started. */
    public long elapsed()
    {
        return System.currentTimeMillis() - start;
    }

    /** Returns the number of milliseconds since the timer was started as a preformatted
     *  string: <code>#.####</code> format is used.
     */
    public String elapsedString()
    {
        return java.text.MessageFormat.format("{0,number,#.####}", new Object [] { new Float( elapsed()/1000.0 ) } );
    }
}
