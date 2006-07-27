
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

package org.carrot2.webapp.serializers;

import java.io.*;

import org.carrot2.util.XMLSerializerHelper;
import org.carrot2.webapp.Constants;
import org.carrot2.webapp.RawDocumentsSerializer;

import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.RawDocumentEnumerator;

/**
 * A consumer of {@link RawDocument}s which serializes them to XML.
 *
 * @author Dawid Weiss
 */
class XMLDocumentsSerializer implements RawDocumentsSerializer {
    private final String skinBase;

    private Writer writer;
    private final XMLSerializerHelper xml = XMLSerializerHelper.getInstance();
    private final String contextPath;

    public XMLDocumentsSerializer(String contextPath, String stylesheetsBase) {
        this.skinBase = stylesheetsBase;
        this.contextPath = contextPath;
    }

    public String getContentType() {
        return Constants.MIME_XML_CHARSET_UTF;
    }

    public void startResult(OutputStream os) throws IOException {
        this.writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);

        writer.write("<?xml version=\"1.0\" encoding=\"" + Constants.ENCODING_UTF + "\" ?>\n");

        // We add '@' to inform xslt processor that the stylesheet
        // is webapp-relative (not fs-root relative); this way we can avoid
        // loopback connections from the xslt parser to the webapp container
        writer.write("<?xml-stylesheet type=\"text/xsl\" href=\"@"
                + skinBase + "/documents.xsl\" ?>\n");

        writer.write("<?skin-uri " + contextPath + skinBase + " ?>\n");

        writer.write("<searchresult type=\"documents\">\n");
    }

    public void write(final RawDocument doc) throws IOException {
        final Integer seqId = (Integer) doc.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
        final String url = doc.getUrl();
        if (url == null || url.length() == 0) {
            return;
        }

        final String title = doc.getTitle();
        final String snippet = doc.getSnippet();

        writer.write(
                "<document id=\"" + seqId.toString() + "\">"
                + "<url>" + xml.toValidXmlText(url, false) + "</url>"
                + (title == null 
                        ? ""
                        : "<title>" + xml.toValidXmlText(title, false) + "</title>")
                + (snippet == null 
                        ? ""
                        : "<snippet>" + xml.toValidXmlText(snippet, false) + "</snippet>")
                + "</document>\n");
    }

    public void endResult() {
        try {
            writer.write("</searchresult>");
            writer.flush();
        } catch (IOException e) {
            // ignore.
        }
        writer = null;
    }

    public void processingError(Throwable cause) throws IOException {
        formatProcessingError(writer, cause);
    }
    
    static void formatProcessingError(final Writer writer, final Throwable cause) throws IOException {
        writer.write("<exception>");
            writer.write("<class>" + cause.getClass().getName() + "</class>");
            writer.write("<message>" + cause.getMessage() + "</message>");
            if (cause.getCause() != null) {
                writer.write("<cause>");
                formatProcessingError(writer, cause.getCause());
                writer.write("</cause>");
            }
        writer.write("</exception>");
    }
}
