
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

package org.carrot2.log4j;

import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;

/**
 * Buffering appender used for assertions on log streams.
 */
public class BufferingAppender extends WriterAppender
{
    StringWriter writer = new StringWriter();

    private BufferingAppender(StringWriter w)
    {
        super(new SimpleLayout(), w);
        this.writer = w;

        super.setImmediateFlush(true);
    }

    public BufferingAppender()
    {
        this(new StringWriter());
    }

    /**
     * @param loggerName Attach to the logger with the given name. <code>null</code> indicates
     * root logger.
     */
    public static BufferingAppender attach(String loggerName)
    {
        BufferingAppender appender = new BufferingAppender();
        resolve(loggerName).addAppender(appender);
        return appender;
    }
    
    /**
     * Creates a new buffering appender and attaches it to the root logger.
     */
    public static BufferingAppender attachToRootLogger()
    {
        return attach(null);
    }
    
    /**
     * Resolve logger name.
     */
    private static Logger resolve(String loggerName)
    {
        if (loggerName == null || loggerName.length() == 0)
            return Logger.getRootLogger();

        return Logger.getLogger(loggerName);
    }

    /**
     * @param loggerName Detach from the logger with the given name. <code>null</code> indicates
     * root logger.
     */
    public static void detach(String loggerName, BufferingAppender appender)
    {
        resolve(loggerName).removeAppender(appender);
    }
    
    /**
     * Detaches the provided appender from the root logger.
     */
    public static void detachFromRootLogger(BufferingAppender appender)
    {
        detach(null, appender);
    }

    /**
     * Clear the log buffer.
     */
    public void clear()
    {
        this.writer.getBuffer().setLength(0);
    }

    public String getBuffer()
    {
        return this.writer.toString();
    }
}
