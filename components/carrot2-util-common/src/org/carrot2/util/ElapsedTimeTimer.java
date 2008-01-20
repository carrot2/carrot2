
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;


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
