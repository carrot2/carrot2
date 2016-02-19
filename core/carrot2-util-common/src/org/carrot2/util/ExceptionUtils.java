
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;

/**
 * A number of utility classes for working with {@link Throwable}s.
 */
public final class ExceptionUtils
{
    private ExceptionUtils()
    {
        // No instantiation
    }

    /**
     * If <code>t</code> if an instance of <code>clazz</code>, then <code>t</code> is
     * returned. Otherwise an instance of <code>clazz</code> is created using a
     * single-parameter constructor accepting <code>t</code> and the wrapper exception
     * instance is returned. If no matching constructor can be found, a
     * {@link RuntimeException} is returned.
     * 
     * @param clazz The exception class to return (or wrap) <code>t</code>.
     * @param t Throwable instance to wrap.
     */
    public static <T extends Throwable> T wrapAs(Class<T> clazz, Throwable t)
    {
        if (t == null)
        {
            return null;
        }

        if (clazz.isAssignableFrom(t.getClass()))
        {
            return clazz.cast(t);
        }

        try
        {
            @SuppressWarnings("unchecked")
            final Constructor<T> [] constructors = (Constructor<T> []) clazz
                .getConstructors();

            // Try single-argument (T extends Throwable) constructor.
            for (Constructor<T> constructor : constructors)
            {
                Class<?> [] params = constructor.getParameterTypes();
                if (params.length == 1 && params[0].isAssignableFrom(t.getClass()))
                {
                    return constructor.newInstance(new Object []
                    {
                        t
                    });
                }
            }

            // Try single-argument (T extends String) constructor.
            for (Constructor<T> constructor : constructors)
            {
                Class<?> [] params = constructor.getParameterTypes();
                if (params.length == 1 && params[0].isAssignableFrom(String.class))
                {
                    T instance = constructor.newInstance(new Object []
                    {
                        t.toString()
                    });
                    instance.initCause(t);
                    return instance;
                }
            }

            // Try parameterless constructor, if it exists.
            for (Constructor<T> constructor : constructors)
            {
                Class<?> [] params = constructor.getParameterTypes();
                if (params.length == 0)
                {
                    T instance = constructor.newInstance();
                    instance.initCause(t);
                    return instance;
                }
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            // fall through, no matching constructor found.
        }

        throw new RuntimeException("(No constructor found in wrapper class "
            + clazz.getName() + "): ", t);
    }

    /**
     * Calls {@link #wrapAs(Class, Throwable)} with the first parameter set to
     * {@link RuntimeException}.
     */
    public static RuntimeException wrapAsRuntimeException(Throwable t)
    {
        return wrapAs(RuntimeException.class, t);
    }

    /**
     * Returns the current stack trace of the calling thread.
     */
    public static String currentStackTrace()
    {
        final Exception e = new Exception();
        e.fillInStackTrace();
        
        final StringWriter w = new StringWriter();
        final PrintWriter pw = new PrintWriter(w);
        e.printStackTrace(pw);
        pw.flush();

        return w.toString();
    }
}
