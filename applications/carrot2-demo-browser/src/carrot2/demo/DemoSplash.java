package carrot2.demo;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.*;

/**
 * Displays a splash image and load a different class.
 * 
 * @author Dawid Weiss
 */
public class DemoSplash {
    /**
     * Object used for synchronizations.
     */
    private final static Object monitor = new Object();
    private static boolean waitCancelled = false;

    public static void main(String [] args) {
        if (args.length < 3) {
            JOptionPane.showMessageDialog(null,
                    "Expect at least three arguments: iconResource minWaitSecs targetClass",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        JWindow splashWindow = null;
        try {
            final String iconResource = args[0];
            final int minWait;
            try {
                minWait = 1000 * Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Expected a number: " + args[1]);
            }
            final String targetClass = args[2];

            final long startTime = System.currentTimeMillis();
            splashWindow = displaySplash(iconResource);

            Class delegate = loadAttempt(
                    Thread.currentThread().getContextClassLoader(), targetClass);
            if (delegate == null) {
                delegate = loadAttempt(DemoSplash.class.getClassLoader(), targetClass);
            }
            if (delegate == null)
                throw new IllegalArgumentException("Target class not found: "
                        + targetClass);

            try {
                final Method mainMethod = delegate.getMethod("main", new Class [] {String[].class});
                final String [] targetArgs = new String[args.length - 3];
                System.arraycopy(args, 3, targetArgs, 0, targetArgs.length);
                new Thread() {
                    public void run() {
                        try {
                            mainMethod.invoke(null, new Object [] {targetArgs});
                        } catch (InvocationTargetException e) {
                            JOptionPane.showMessageDialog(null,
                                    "Target class exception: " + e.getCause(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        } catch (IllegalAccessException e) {
                            JOptionPane.showMessageDialog(null,
                                    "Illegal access when invoking the target class: " + e.getCause(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }.start();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Target class has no main method.");
            }

            final long remaining = startTime + minWait - System.currentTimeMillis();
            if (remaining > 0) {
                synchronized (monitor) {
                    if (waitCancelled == false) {
                        try {
                            monitor.wait(remaining);
                        } catch (InterruptedException e) { 
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null,
                    "Incorrect argument: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (splashWindow != null) {
                splashWindow.dispose();
            }
        }
    }

    /**
     * Attempt to load a class from a given class loader.
     */
    private static Class loadAttempt(ClassLoader contextClassLoader, String className) {
        try {
            return contextClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            // Ignore this exception.
            return null;
        }
    }

    /**
     * Display the splash on screen.
     */
    private static JWindow displaySplash(String splashResource) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(splashResource);
        if (resource == null) {
            // Try another class loader.
            resource = DemoSplash.class.getResource(splashResource);
        }
        
        if (resource == null) {
            throw new IllegalArgumentException("Splash resource not found: "
                    + splashResource);
        }

        final ImageIcon splashImage = new ImageIcon(resource);

        final Dimension position = Toolkit.getDefaultToolkit().getScreenSize();
        final JWindow splashWindow = new JWindow();
        final JLabel splashLabel = new JLabel(splashImage);
        splashWindow.getContentPane().add(splashLabel);
        splashWindow.pack();
        splashWindow.setLocation(
                (position.width - splashWindow.getWidth())/2,
                (position.height - splashWindow.getHeight())/2);
        splashWindow.setVisible(true);
        
        // Add click listeners.
        splashLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                synchronized (monitor) {
                    waitCancelled = true;
                    monitor.notifyAll();
                }
            }
        });
        
        try {
            // Use reflection to find setAlwaysOnTop method (we want to compile
            // on older JDKs)
            // splashWindow.setAlwaysOnTop(true);
            final Class clazz = java.awt.Window.class;
            final Method method = clazz.getMethod("setAlwaysOnTop", new Class [] {Boolean.TYPE});
            method.invoke(splashWindow, new Object [] {Boolean.TRUE});
        } catch (Throwable t) {
            // Ignore non-JDK1.5 method.
        }

        return splashWindow;
    }
}