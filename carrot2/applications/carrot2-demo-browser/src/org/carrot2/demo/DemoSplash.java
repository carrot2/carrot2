
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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.*;

/**
 * Displays a splash image and loads a different class.
 * 
 * @author Dawid Weiss
 */
public final class DemoSplash
{
    /** Splash image (resource name). */
    private static String splashImageResource;

    /** Minimum splash display time. */
    private static int minDisplayTimeMillis;

    /** Target class to invoke. */
    private static String targetClass;

    /**
     * Command line entry point.
     */
    public static void main(String [] args)
    {
        JWindow splashWindow = null;
        try
        {
            // Parse command line arguments.
            args = parseParameters(args);

            // Display splash.
            splashWindow = displaySplash(splashImageResource);

            // Close splash after some time, if not closed before the deadline.
            startTimeoutDispose(splashWindow);

            // Attempt to load target class.
            Class delegate = loadAttempt(Thread.currentThread().getContextClassLoader(), targetClass);
            if (delegate == null)
            {
                delegate = loadAttempt(DemoSplash.class.getClassLoader(), targetClass);
            }
            if (delegate == null)
            {
                throw new IllegalArgumentException("Target class not found: " + targetClass);
            }

            final SplashDelegate instance = (SplashDelegate) delegate.newInstance();
            instance.main(args, splashWindow);
        }
        catch (IllegalArgumentException e)
        {
            splashClose(splashWindow);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Illegal arguments", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e)
        {
            splashClose(splashWindow);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Program error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void splashClose(final JWindow splashWindow)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (splashWindow != null && splashWindow.isVisible()) splashWindow.dispose();
            }
        });
    }

    /**
     * Starts a background thread for disposing the thread after some time. 
     */
    private static void startTimeoutDispose(JWindow splashWindow)
    {
        final JWindow splashWnd = splashWindow;
        final Thread t = new Thread()
        {
            public void run()
            {
                try
                {
                    if (SwingUtilities.isEventDispatchThread()) {
                        // We are the AWT thread, check splash.
                        if (splashWnd.isVisible()) {
                            splashWnd.dispose();
                        }
                    } else {
                        // Wait some time, invoke from AWT thread.
                        Thread.sleep(minDisplayTimeMillis);
                        SwingUtilities.invokeLater(this);
                    }
                }
                catch (InterruptedException e)
                {
                    // ignore.
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    /**
     * Parses arguments and sets fields of this class to proper values.
     * 
     * @param args
     * @return Returns original array of parameters with splash-specific
     * parameters removed.
     */
    private static String [] parseParameters(String [] args)
    {
        if (args.length < 3) {
            throw new IllegalArgumentException("Expected three arguments: iconResource minWaitSecs targetClass");
        }

        DemoSplash.splashImageResource = args[0];
        DemoSplash.targetClass = args[2];

        try
        {
            DemoSplash.minDisplayTimeMillis = 1000 * Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Expected a number: " + args[1]);
        }

        final String [] targetArgs = new String [args.length - 3];
        System.arraycopy(args, 3, targetArgs, 0, targetArgs.length);

        return targetArgs;
    }

    /**
     * Attempt to load a class from a given class loader.
     */
    private static Class loadAttempt(ClassLoader contextClassLoader, String className)
    {
        try
        {
            return contextClassLoader.loadClass(className);
        }
        catch (ClassNotFoundException e)
        {
            // Ignore this exception.
            return null;
        }
    }

    /**
     * Display the splash on screen.
     */
    private static JWindow displaySplash(String splashResource)
    {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(splashResource);
        if (resource == null)
        {
            // Try another class loader.
            resource = DemoSplash.class.getResource(splashResource);
        }

        if (resource == null)
        {
            throw new IllegalArgumentException("Splash resource not found: " + splashResource);
        }

        final ImageIcon splashImage = new ImageIcon(resource);
        final Dimension position = Toolkit.getDefaultToolkit().getScreenSize();
        
        final Rectangle splashRect = new Rectangle(splashImage.getIconWidth(), splashImage.getIconHeight());
        splashRect.x = (position.width - splashRect.width) / 2;
        splashRect.y = (position.height - splashRect.height) / 2;

        final BufferedImage background = captureDesktop(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(), 
            splashRect);
        final Graphics2D g2d = (Graphics2D) background.getGraphics();
        g2d.drawImage(splashImage.getImage(), 0, 0, null);
        g2d.dispose();

        final JWindow splashWindow = new JWindow();
        final JLabel splashLabel = new JLabel(new ImageIcon(background));
        splashWindow.getContentPane().add(splashLabel);
        splashWindow.pack();
        splashWindow.setLocation(splashRect.getLocation());
        splashWindow.setVisible(true);

        // Add click listener closing the splash.
        splashLabel.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                splashWindow.dispose();
            }
        });

        try
        {
            // Use reflection to find setAlwaysOnTop method (we want to compile on older JDKs)
            // splashWindow.setAlwaysOnTop(true);
            final Class clazz = java.awt.Window.class;
            final Method method = clazz.getMethod("setAlwaysOnTop", new Class []
            {
                Boolean.TYPE
            });
            method.invoke(splashWindow, new Object []
            {
                Boolean.TRUE
            });
        }
        catch (Throwable t)
        {
            // Ignore non-JDK1.5 method.
        }

        return splashWindow;
    }

    /**
     * Captures a screenshot of the indicated {@link GraphicsDevice}.
     * 
     * @param device Device to capture.
     * @return Returns a {@link BufferedImage}.
     */
    private static BufferedImage captureDesktop(GraphicsDevice device, Rectangle clientRect)
    {
        try
        {
            final Robot robot = new Robot(device);
            return robot.createScreenCapture(clientRect);
        }
        catch (AWTException e)
        {
            System.out.println("Could not capture desktop image: " + e.toString());
            return device.getDefaultConfiguration().createCompatibleImage(clientRect.width, clientRect.height);
        }
    }
}