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
