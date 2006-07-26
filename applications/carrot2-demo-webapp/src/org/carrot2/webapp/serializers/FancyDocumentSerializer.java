package org.carrot2.webapp.serializers;

import java.io.*;
import java.io.IOException;
import java.io.OutputStream;

import org.carrot2.webapp.Constants;
import org.carrot2.webapp.RawDocumentsSerializer;

import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.impl.RawDocumentEnumerator;
import com.dawidweiss.carrot.util.common.XMLSerializerHelper;

/**
 * A document serializer which produces HTML output similar
 * to that produced by the <code>fancy</code> XSLT stylesheet.
 * 
 * @author Dawid Weiss
 */
final class FancyDocumentSerializer implements RawDocumentsSerializer {
    private final int FLUSH_LIMIT = 10;
    private final String base;
    private final XMLSerializerHelper xml = XMLSerializerHelper.getInstance();

    private Writer writer;
    private int sequence;

    public FancyDocumentSerializer(String contextPath, String stylesheetsBase) {
        this.base = contextPath + stylesheetsBase;
    }

    public String getContentType() {
        return Constants.MIME_HTML_CHARSET_UTF;
    }

    public void startResult(OutputStream os, boolean isProcessing) throws IOException {
        this.writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);
        this.sequence = 1;

        // Write HTML header
        writer.write(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n" + 
                "<html class=\"outside-back-color\" style=\"height: 100%\">\r\n" + 
                "<head>\r\n" + 
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\r\n" + 
                "<title>Carrot Clustering Engine</title>\r\n" + 
                "<link href=\"" + base + "/css/common.css\" type=\"text/css\" rel=\"stylesheet\">\r\n" + 
                "<link href=\"" + base + "/css/documents.css\" rel=\"stylesheet\">\r\n"); 

        if (isProcessing) {
            writer.write(
                    "</head>" +
                    "<body style=\"height: 100%;\" onload=\"javascript:parent.document.getElementById(\'progress\').style.display = \'none\'\">\r\n" + 
                    "<div id=\"documents\">");
            writer.flush();
        } else {
            writer.write(
                    "</head>" +
                    "<body style=\"height: 100%\" onload=\"javascript:parent.document.getElementById(\'progress\').style.display = \'none\'\">" +
                    "<div id=\"documents\">");
        }
    }

    public void write(RawDocument doc) throws IOException {
        final Integer seqId = (Integer) doc.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
        final String url = doc.getUrl();
        if (url == null || url.length() == 0) {
            return;
        }

        final String title = doc.getTitle();
        final String snippet = doc.getSnippet();
        final String hurl = xml.toValidXmlText(url, false);
        
        writer.write(
                "<table id=\"" + seqId.toString() + "\" class=\"d\">\r\n" + 
                "<tr>\r\n" + 
                "<td class=\"r\">" + sequence + "</td><td class=\"c\">\r\n" + 
                "<div class=\"t\">" + 
                "<a target=\"_top\" href=\"" + hurl + "\">" + xml.toValidXmlText(title, false)+ "</a>" + 
                "</div>\r\n" + 
                "<div class=\"s\">" + xml.toValidXmlText(snippet, false) + "</div>\r\n" + 
                "<div class=\"u\">" + hurl + "</div>\r\n" + 
                "\r\n" + 
                "</td>\r\n" + 
                "</tr>\r\n" + 
                "</table>"
                );

        sequence++;

        // Until 30 results we try to flush every FLUSH_LIMIT; beyond that
        // we allow some buffering.
        if (sequence < 30 && (sequence % FLUSH_LIMIT) == 0) {
            writer.flush();
        }
    }

    public void endResult() throws IOException {
        if (sequence == 1) {
            writer.write("<div id='no-documents'>Your query returned no documents.<br/>Please try a more general query.</div>");
        }
        
        writer.write(
                "</div>\r\n" + 
                "</body>\r\n" + 
                "</html>");
        writer.flush();
        this.writer = null;
    }
}
