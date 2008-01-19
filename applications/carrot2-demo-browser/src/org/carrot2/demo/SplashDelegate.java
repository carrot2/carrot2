
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

package org.carrot2.demo;

import javax.swing.JWindow;

/**
 * Execution delegate for {@link DemoSplash}
 * 
 * @author Dawid Weiss
 */
public interface SplashDelegate
{
    /**
     * Run the delegate with <code>args</code>
     * and <code>splashWindow</code>.
     * 
     * @param args
     * @param splashWindow
     */
    void main(String [] args, JWindow splashWindow);
}
