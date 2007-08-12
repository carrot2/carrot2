/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.swing;

import java.io.File;
import java.util.Locale;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.util.ArrayUtils;

/**
 * All the functionality we need for the demo browser is in this interface. We may then use either Swing's
 * {@link javax.swing.JEditorPane} or JDIC's {@link org.jdesktop.jdic.browser.WebBrowser} class to provide the
 * implementation.
 * 
 * @author Dawid Weiss
 */
public abstract class HtmlDisplay extends JPanel
{
    private final static Logger logger = Logger.getLogger(HtmlDisplay.class);
   
    /**
     * If set, the native browser has been checked.
     */
    private static boolean nativeChecked;

    /**
     * If set, the native browser is available and can be used.
     */
    private static boolean useNativeBrowser;

    /**
     * Sets the HTML content of this HTML display.
     * 
     * @param htmlContent
     */
    public abstract void setContent(String htmlContent);

    /**
     * 
     */
    protected void appendHtmlHeader(StringBuffer buffer)
    {
        buffer.append("<html>");
        buffer.append("<meta><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></meta>\n");
        buffer.append("<body style=\"font-size: 10pt; font-family: Arial, Helvetica, sans-serif;\">");
    }

    /**
     * 
     */
    protected void appendHtmlTrailer(StringBuffer buffer)
    {
        buffer.append("</body></html>");
    }

    /**
     * 
     */
    protected void appendHtmlFor(StringBuffer buffer, Object seqNum, RawDocument doc)
    {
        buffer.append("<a href=\"" + doc.getUrl() + "\"><b>");

        if ((doc.getTitle() == null) || (doc.getTitle().length() == 0))
        {
            buffer.append("(no title)");
        }
        else
        {
            buffer.append(doc.getTitle());
        }
        buffer.append("</b></a>");

        if (seqNum != null)
        {
            buffer.append("<span style=\"color: gray;\"> [");
            buffer.append(seqNum);
            buffer.append("]</span>");
        }

        if (doc.getProperty(RawDocument.PROPERTY_LANGUAGE) != null)
        {
            buffer.append(" [" + doc.getProperty(RawDocument.PROPERTY_LANGUAGE) + "]");
        }

        buffer.append("<br>");

        String r = (String) doc.getProperty(RawDocument.PROPERTY_SNIPPET);

        if (r != null)
        {
            buffer.append(r);
            buffer.append("<br>");
        }

        buffer.append("<font color=green>");
        buffer.append(doc.getUrl());
        String [] sources = (String []) doc.getProperty(RawDocument.PROPERTY_SOURCES);
        if (sources != null)
        {
            buffer.append("</font> <font color=gray>");
            buffer.append("[");
            buffer.append(ArrayUtils.toString(sources));
            buffer.append("]");
        }
        buffer.append("</font><br><br>");
    }

    /**
     * @return Creates an instance and returns a {@link HtmlDisplay} relevant for the current
     * platform (native or Swing).
     */
    public static synchronized HtmlDisplay newHtmlDisplay()
    {
        if (!nativeChecked)
        {
            final boolean useJavaBrowser = "true".equals(System.getProperty("use.java.browser"));

            if (!useJavaBrowser && isWindows()) {
                useNativeBrowser = true;
                try {
                    new HtmlDisplayWithJDIC();
                } catch (Throwable t) {
                    Logger.getLogger(HtmlDisplay.class).warn("Could not instantiate native browser component.", t);
                    useNativeBrowser = false;
                }
            }

            nativeChecked = true;
        }

        if (useNativeBrowser)
        {
            return new HtmlDisplayWithJDIC();
        }
        else
        {
            return new HtmlDisplayWithSwing();
        }
    }

    /**
     * @return Returns <code>true</code> if the current platform is Windows.
     */
    final static boolean isWindows() {
        return getOsName().indexOf("windows") >= 0;
    }

    /**
     *
     */
    private static String getOsName()
    {
        String osname = System.getProperty("os.name");
        if (osname != null) {
            osname = osname.toLowerCase(Locale.US);
        } else {
            // Ooops, no such property? Try to determine the OS from file separator.
            if (File.separatorChar == '\\') {
                osname = "windows?";
            } else {
                osname = "unix?";
            }
        }
        return osname;
    }
}
