
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

package org.carrot2.ant.tasks;

import java.text.MessageFormat;

/**
 * A simple real-time timer. 
 */
public final class Timer {
    private MessageFormat format = new MessageFormat("{0,number,#.##} sec.");

    private long start;
    
    public Timer() {
        start();
    }

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public String elapsed() {
        final long elapsedMs = System.currentTimeMillis() - start;
        return format.format(new Object [] {
                new Double(elapsedMs / 1000.0d)
        });
    }
}
