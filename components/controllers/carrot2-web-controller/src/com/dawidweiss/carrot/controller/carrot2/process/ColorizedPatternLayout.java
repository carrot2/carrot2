package com.dawidweiss.carrot.controller.carrot2.process;

import org.apache.log4j.*;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * Adds span HTML tags to the pattern layout, depending on
 * the level of the debug message.
 * 
 * added span sections have css class associated with them, which
 * is identical to level name.
 */
public class ColorizedPatternLayout
    extends PatternLayout
{
    /**
     * 
     */
    public ColorizedPatternLayout()
    {
        super();
    }

    /**
     * @param arg0
     */
    public ColorizedPatternLayout(String v)
    {
        super(v);
    }

    /**
     * @see org.apache.log4j.Layout#format(org.apache.log4j.spi.LoggingEvent)
     */
    public String format(LoggingEvent event)
    {
        String message = super.format(event);
        
        String wrapClass = null;
        switch (event.getLevel().toInt())
        {
            case Level.DEBUG_INT:
                wrapClass = "debug"; break;
            case Level.ERROR_INT:
            wrapClass = "error"; break;
            case Level.FATAL_INT:
            wrapClass = "fatal"; break;
            case Level.INFO_INT:
            wrapClass = "info"; break;
            case Level.WARN_INT:
            wrapClass = "warn"; break;
        }
        if (wrapClass != null)
            message = "<div class=\"" + wrapClass + "\">" 
                + org.put.util.string.StringUtils.wrap( 
                    org.put.util.text.HtmlHelper.escapeHtmlTags( message ),
                    110, "  | ", " -./", 10) 
                + "</div>";

        ThrowableInformation throwable = event.getThrowableInformation();
        if (throwable != null) {
            message = message + "\n<div class=\"throwable\">";
            String [] strrep = throwable.getThrowableStrRep();
            for (int i=0;i<strrep.length;i++)
            {
                if (i==0) {
                    message = message + "\n<span class=\"throwablehead\">" + org.put.util.string.StringUtils.wrap( 
                        org.put.util.text.HtmlHelper.escapeHtmlTags( strrep[i] ),
                        110, "  | ", " -./()", 20) + "</span>";
                }
                else
                {
                    message = message + "\n" + org.put.util.string.StringUtils.wrap( 
                        org.put.util.text.HtmlHelper.escapeHtmlTags( strrep[i] ),
                        110, "  | ", " -./()", 20) ;
                }
            }
            message = message + "</div>\n";
        }
        return message;
    }

    /**
     * @see org.apache.log4j.Layout#ignoresThrowable()
     */
    public boolean ignoresThrowable()
    {
        return false;
    }

}
