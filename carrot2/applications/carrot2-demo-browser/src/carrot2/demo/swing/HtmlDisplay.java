
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

package carrot2.demo.swing;

import javax.swing.JPanel;

import com.dawidweiss.carrot.core.local.clustering.RawDocument;

/**
 * All the functionality we need for the demo
 * browser is in this interface. We may then
 * use either Swing's {@link javax.swing.JEditorPane}
 * or JDIC's {@link org.jdesktop.jdic.browser.WebBrowser}
 * class to provide the implementation.
 * 
 * @author Dawid Weiss
 */
public abstract class HtmlDisplay extends JPanel {
    public abstract void setContent(String htmlContent);

    protected void appendHtmlHeader(StringBuffer buffer) {
        buffer.append("<html>");
        buffer.append("<meta><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></meta>\n");
        buffer.append("<body style=\"font-size: 10pt; font-family: Arial, Helvetica, sans-serif;\">");
    }

    protected void appendHtmlTrailer(StringBuffer buffer) {
        buffer.append("</body></html>");
    }

    protected void appendHtmlFor(StringBuffer buffer, Object seqNum, RawDocument doc) {
        buffer.append("<a href=\"" + doc.getUrl() + "\"><b>");

        if ((doc.getTitle() == null) || (doc.getTitle().length() == 0)) {
            buffer.append("(no title)");
        } else {
            buffer.append(doc.getTitle());
        }
        buffer.append("</b></a>");

        if (seqNum != null) {
            buffer.append("<span style=\"color: gray;\"> [");
            buffer.append(seqNum);
            buffer.append("]</span>");
        }

        if (doc.getProperty(RawDocument.PROPERTY_LANGUAGE) != null) {
            buffer.append(" [" +
                doc.getProperty(RawDocument.PROPERTY_LANGUAGE) + "]");
        }

        buffer.append("<br>");

        String r = (String) doc.getProperty(RawDocument.PROPERTY_SNIPPET);

        if (r != null) {
            buffer.append(r);
            buffer.append("<br>");
        }

        buffer.append("<font color=green>");
        buffer.append(doc.getUrl());
        buffer.append("</font><br><br>");
    }
}
