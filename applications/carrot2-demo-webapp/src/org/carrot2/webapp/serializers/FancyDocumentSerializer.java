
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

package org.carrot2.webapp.serializers;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.impl.*;
import org.carrot2.util.*;
import org.carrot2.webapp.*;
import org.carrot2.webapp.serializers.TextMarker.*;

/**
 * A document serializer which produces HTML output similar
 * to that produced by the <code>fancy</code> XSLT stylesheet.
 * 
 * @author Dawid Weiss
 */
final class FancyDocumentSerializer implements RawDocumentsSerializer, TextMarkerListener {
    private final static Logger log = Logger.getLogger(FancyDocumentSerializer.class);

    private final int FLUSH_LIMIT = 10;
    private final String base;
    private final XMLSerializerHelper xml = XMLSerializerHelper.getInstance();

    private Writer writer;
    private int sequence;
    
    /** For marking word occurrences */
    private TextMarker textMarker;
    
    /** For permanent marking of query words */
    private Set queryWordIds;

    public FancyDocumentSerializer(String contextPath, String stylesheetsBase) {
        this.base = contextPath + stylesheetsBase;
    }

    public String getContentType() {
        return Constants.MIME_HTML_CHARSET_UTF;
    }

    public void startResult(OutputStream os, String query) throws IOException {
        this.writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);
        this.sequence = 1;
        this.textMarker = TextMarkerPool.INSTANCE.borrowTextMarker();
        
        prepareQueryWordIds(query);

        // Write HTML header
        writer.write(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n" + 
                "<html class=\"outside-back-color\" style=\"height: 100%\">\r\n" + 
                "<head>\r\n" + 
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\r\n" + 
                "<title>Carrot Clustering Engine</title>\r\n" + 
                "<link href=\"" + base + "/css/common.css\" type=\"text/css\" rel=\"stylesheet\">\r\n" + 
                "<link href=\"" + base + "/css/documents.css\" rel=\"stylesheet\">\r\n" + 
                "</head>" +
                "<body style=\"height: 100%;\" onload=\"parent.setProgress('docs-progress', false);\">\r\n" + 
                "<div id=\"documents\">");
    }

    private void prepareQueryWordIds(String query)
    {
        String cleanQuery = query.replaceAll("[^a-zA-Z0-9 ]", "");
        queryWordIds = new HashSet();
        textMarker.tokenize(cleanQuery.toCharArray(), new TextMarkerListener() {
            public void markedTextIdentified(char[] text, int startPosition,
                    int length, String id, boolean newId)
            {
                queryWordIds.add(id);
            }

            public void unmarkedTextIdentified(char[] text, int startPosition,
                    int length)
            {}
        });
    }

    public void write(RawDocument doc) throws IOException {
        final Integer seqId = (Integer) doc.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
        final String url = doc.getUrl();
        if (url == null || url.length() == 0) {
            return;
        }

        final String title = (null == doc.getTitle() ? "" : doc.getTitle());
        final String snippet = (null == doc.getSnippet() ? "" : doc.getSnippet());
        final String hurl = xml.toValidXmlText(url, false);
        String [] sources = (String []) doc.getProperty(RawDocument.PROPERTY_SOURCES);

        writer.write(
                "<table id=\"" + seqId.toString() + "\" class=\"d\">\r\n" + 
                "<tr>\r\n" + 
                "<td class=\"r\">" + sequence + "</td><td class=\"c\">\r\n" + 
                "<div class=\"t\">" + 
                "<a target=\"_top\" href=\"" + hurl + "\">");
                
        textMarker.tokenize(title.toCharArray(), this);
                
        writer.write(
                "</a>" + 
                "</div>\r\n" + 
                "<div class=\"s\">");
        
        textMarker.tokenize(snippet.toCharArray(), this);
        
        writer.write("</div>\r\n" + 
                "<div class=\"u\">" + hurl + (sources != null ? "<div class=\"o\">[" + ArrayUtils.toString(sources) + "]</div>" : "") +"</div>\r\n" +
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

    public void markedTextIdentified(char[] text, int startPosition,
            int length, String id, boolean newId)
    {
        try {
            if (id != null) {
                if (queryWordIds.contains(id)) {
                    writer.write("<b class=\"pm\">");
                }
                else {
                    writer.write("<b class=\"w" + id + "\">");
                }
            }
            writer.write(text, startPosition, length);
            if (id != null) {
                writer.write("</b>");
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Could not write text", e);
        }        
    }

    public void unmarkedTextIdentified(char[] text, int startPosition,
            int length)
    {
        try {
            writer.write(text, startPosition, length);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not write text", e);
        }
    }
    
    public void endResult() throws IOException {
        if (sequence == 1) {
            writer.write("<div id='no-documents'>Your query returned no documents.<br/>Please try a more general query.</div>");
        }
        
        writer.write(
                "</div>\r\n"+
                "<style>\r\n");
        
        // Write dummy CSS rules (we do need this!)
        int all = 0, emitted = 0;
        for (Iterator it = textMarker.getWordInfos(); it.hasNext();) {
            StemInfo stemInfo = (StemInfo)it.next();
            
            if (stemInfo.frequency > 1)
            {
                writer.write(".w");
                writer.write(stemInfo.id);
                writer.write("{}");
                emitted++;
            }
            all++;
        }
        
        log.info("Styles: " + emitted + "/" + all);
        
        writer.write("\r\n" +  
                "</style></body>\r\n" + 
                "</html>");
        writer.flush();
        this.writer = null;
        
        if (textMarker != null)
        {
            TextMarkerPool.INSTANCE.returnTextMarker(textMarker);
        }
    }

    public void processingError(Throwable cause) throws IOException {
        // Ignore processing exceptions.
        writer.write(
                "      <div style=\"margin-top: 5px; border: 1px dotted red; border-left: 5px solid red; padding: 4px; margin-left: 2px;\">\r\n" + 
                "          <div style=\"font-size: 9px; color: gray; background-color: #ffe0e0;\">" + cause.getClass() + "</div>\r\n" + 
                "          <pre style=\"font-size: 11px; color: black; font-weight: bold;\">\r\n" + 
                "              " + cause.getMessage() + 
                "          </pre>\r\n" + 
                "      </div>\r\n"); 

        if (cause.getCause() != null) {
            processingError(cause.getCause());
        }
        
        this.sequence = 0;
    }
}
