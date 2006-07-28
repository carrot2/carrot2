
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

package org.carrot2.demo.swing;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;

import org.carrot2.core.clustering.RawDocument;

/**
 * An implementation of {@link org.carrot2.demo.swing.HtmlDisplay} using
 * pure Java.
 * 
 * @author Dawid Weiss
 */
final class HtmlDisplayWithSwing extends HtmlDisplay {
    private final JEditorPane documentsView;

    private final class InternalHyperlinkListener implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent event) {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    documentsView.setPage(event.getURL());
                } catch (IOException e) {
                    SwingUtils.showExceptionDialog(documentsView, "Could not open URL.", e);
                }
            }
        }
    }

    public HtmlDisplayWithSwing() {
        this.documentsView = new JEditorPane();
        documentsView.setContentType("text/html");
        documentsView.setEditable(false);
        documentsView.setBorder(BorderFactory.createEmptyBorder());
        documentsView.addHyperlinkListener(new InternalHyperlinkListener());

        final JScrollPane scrollerRight = new JScrollPane();
        scrollerRight.setBorder(BorderFactory.createEmptyBorder());
        scrollerRight.getViewport().add(documentsView);

        this.setLayout(new BorderLayout());
        this.add(scrollerRight, BorderLayout.CENTER);
    }

    public void setContent(String htmlContent) {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Invoke updateDocumentsView() from AWT thread only.");

        final Document doc = documentsView.getDocument();
        try {
            doc.remove(doc.getStartPosition().getOffset(), doc.getLength());

            EditorKit kit = documentsView.getEditorKit();
            kit.read(new StringReader(htmlContent), doc, 0);

            documentsView.setCaretPosition(0);
        } catch (BadLocationException e) {
            throw new RuntimeException("Unexpected exception: " + e);
        } catch (IOException e) {
            // not possible with a string reader.
        }
    }

    protected void appendHtmlHeader(StringBuffer buffer) {
        buffer.append("<html>");
        buffer.append("<meta><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></meta>\n");
        buffer.append("<body style=\"font-size: 11px; font-family: Arial, Helvetica, sans-serif;\">");
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

        buffer.append("<font color=\"green\">");
        buffer.append(doc.getUrl());
        buffer.append("</font><br><br>");
    }
}