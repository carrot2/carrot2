
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

package org.carrot2.launcher;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.util.*;

/**
 * A launcher is a command-line utility which can invoke another Java class with a public, static
 * <code>main(String [] args}</code> method, providing classpath entries contained in a set of JAR files and/or
 * directories.
 * 
 * @author Dawid Weiss
 */
public class Launcher
{
    /**
     * Parse command line arguments and invoke another class.
     */
    public static void main(String [] args) throws Exception
    {
        final LaunchOptions options = processOptions(args);
        try {
            if (options != null) {
                new Launcher().launch(options);
            }
        } catch (LaunchException e) {
            System.err.println("Launch problem occurred (-help for help): " + e.getMessage());
        }
    }

    /**
     * Parses command-line arguments and launches the designated class.
     */
    private void launch(final LaunchOptions options)
    {
        final ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
        final URLClassLoader childLoader = new URLClassLoader(options.getClasspathURLs(), currentContextClassLoader);

        try
        {
            // Load the launched class using the new class loader.
            Thread.currentThread().setContextClassLoader(childLoader);

            final Class launchedClass = childLoader.loadClass(options.getClassName());
            final Method main = launchedClass.getMethod("main", new Class []
            {
                String [].class
            });

            // Sanity check.
            final int modifiers = main.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers))
            {
                main.invoke(launchedClass, new Object []
                {
                    options.getClassArgs()
                });
            }
            else
            {
                throw new NoSuchMethodException();
            }
        }
        catch (InvocationTargetException e)
        {
            final Throwable targetException = e.getTargetException();
            System.out.println("Class " + options.getClassName() 
                + " threw an exception: " + targetException.toString());
            targetException.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            System.err.println("Class " + options.getClassName() + " has no command line entry point method.");
        }
        catch (IllegalAccessException e)
        {
            System.err.println("Class " + options.getClassName() + " is not accessible: " + e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("Class " + options.getClassName() + " not found.");
        }
    }

    /**
     * Processes a list of options and extract {@link LaunchOptions}.
     */
    private static LaunchOptions processOptions(String [] args)
    {
        final LaunchOptions options = new LaunchOptions();

        int i = 0;
        try
        {
            while (i < args.length)
            {
                if (args[i].equals("-help")) {
                    help();
                    return null;
                }
                else if (args[i].equals("-cpdir"))
                {
                    final File dir = new File(args[++i]);
                    if (dir.isDirectory())
                    {
                        final File [] jarFiles = dir.listFiles(new FilenameFilter()
                        {
                            public boolean accept(File dir, String name)
                            {
                                return name.toLowerCase().endsWith(".jar");
                            }
                        });

                        Arrays.sort(jarFiles, new Comparator()
                        {
                            public int compare(Object o1, Object o2)
                            {
                                final File f1 = (File) o1;
                                final File f2 = (File) o2;
                                return f1.getName().compareTo(f2.getName());
                            }
                        });

                        for (int j = 0; j < jarFiles.length; j++) {
                            options.addJarLocation(jarFiles[j]);
                        }
                    } else {
                        throw new LaunchException("Not a directory: " + dir.getAbsolutePath());
                    }
                    i++;
                }
                else if (args[i].equals("-cpjar"))
                {
                    final File jarFile = new File(args[++i]);
                    if (jarFile.isFile() && jarFile.canRead())
                    {
                        options.addJarLocation(jarFile);
                    }
                    i++;
                }
                else if (args[i].equals("-cp"))
                {
                    final File dirFile = new File(args[++i]);
                    if (dirFile.isDirectory())
                    {
                        options.addDirLocation(dirFile);
                    }
                    i++;
                }
                else
                {
                    options.setClassName(args[i++]);

                    final String [] classArgs = new String [args.length - i];
                    System.arraycopy(args, i, classArgs, 0, classArgs.length);
                    options.setClassArgs(classArgs);
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new LaunchException("Required argument missing.");
        }

        return options;
    }

    /**
     * Display help.
     */
    private static void help()
    {
        System.out.println("Usage: java {java options} -jar Launcher.jar [OPTION] CLASS arg1 arg2 ...");
        System.out.println("[OPTION] list");
        System.out.println("    -cpdir dir   Adds all JARs in a given folder to classpath.");
        System.out.println("    -cpjar dir   Adds a single JAR to a given classpath.");
        System.out.println("    -cp dir      Adds a folder to classpath.");
    }
}