
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

import javax.servlet.http.HttpServletRequest;

import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.RawDocumentEnumerator;
import org.carrot2.util.XMLSerializerHelper;
import org.carrot2.webapp.*;
import org.dom4j.Element;

/**
 * A consumer of {@link RawDocument}s which serializes them to XML.
 * 
 * @author Dawid Weiss
 * @author Stanisław Osiński
 */
public class XMLClustersSerializer implements RawClustersSerializer {
    private final static char SEPARATOR = ',';
    private final StringBuffer buffer = new StringBuffer();
    private final XMLSerializerHelper xml = XMLSerializerHelper.getInstance();
    private final ResourceBundle messages;

    private final String contextPath;
    private final String skinBase;

    private Writer writer;
    private List rawDocumentsList;
    
    private TextMarker textMarker;

    public XMLClustersSerializer(String contextPath, String stylesheetsBase, ResourceBundle messages) {
        this.skinBase = stylesheetsBase;
        this.contextPath = contextPath;
        this.messages = messages;
    }

    public final String getContentType() {
        return Constants.MIME_XML_CHARSET_UTF;
    }

    public final void startResult(OutputStream os, List rawDocumentsList, HttpServletRequest request, String query)
	    throws IOException
    {
    	this.writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);
    	this.rawDocumentsList = rawDocumentsList;
    
    	this.textMarker = TextMarkerPool.INSTANCE.borrowTextMarker();

        // We need to process query here as well to maintain id consistency
        prepareQueryWordIds(query);
        
        // In order for cluster label word highlighting to work, we need to 
        // once again tokenize the documents... This kind of sucks, but lets
        // us retain the nice progressive document loading behaviour.
        // TODO: could we somehow cache word ids (essentially, one HashMap),
        // just as we cache the documents? In this case we'd not need to
        // tokenize the documents again.
        generateWordIds();
        
    	writer.write("<?xml version=\"1.0\" encoding=\"" + Constants.ENCODING_UTF + "\" ?>\n");
    
    	// We add '@' to inform xslt processor that the stylesheet
    	// is webapp-relative (not fs-root relative); this way we can avoid
    	// loopback connections from the xslt parser to the webapp container
    	writer.write("<?xml-stylesheet type=\"text/xsl\" href=\"@" + skinBase
    		+ "/clusters.xsl\" ?>\n");
    
    	writer.write("<?skin-uri " + contextPath + skinBase + " ?>\n");
    
    	writer.write("<searchresult type=\"clusters\" totalResultsCount=\""
    		+ rawDocumentsList.size() + "\"");
        
        contributeHeadTagAttributes(writer, request);

        writer.write(">\n");
        
        emitMessageStrings(writer);
    }


    private void prepareQueryWordIds(String query)
    {
        String cleanQuery = query.replaceAll("[^a-zA-Z0-9 ]", "");
        textMarker.tokenize(cleanQuery.toCharArray());
    }

    private void generateWordIds()
    {
        for (Iterator it = rawDocumentsList.iterator(); it.hasNext();) {
            RawDocument rawDocument = (RawDocument)it.next();
            if (rawDocument.getTitle() != null) {
                textMarker.tokenize(rawDocument.getTitle().toCharArray());
            }            
            if (rawDocument.getSnippet() != null) {
                textMarker.tokenize(rawDocument.getSnippet().toCharArray());
            }
        }
    }

    public final void write(RawCluster cluster) throws IOException {
    	collect(cluster, null);
    	writer.write(buffer.toString());
    	buffer.setLength(0);
    }

    public final void endResult() throws IOException {
        TextMarkerPool.INSTANCE.returnTextMarker(textMarker);
        
    	try {
    	    writer.write("</searchresult>");
    	    writer.flush();
    	} catch (IOException e) {
    	    // ignore.
    	}
    	writer = null;
    }

    /**
     * Contributes attributes to the head tag of the response. At the moment
     * referer consistency check is performed.
     */
    protected void contributeHeadTagAttributes(Writer writer, HttpServletRequest request)
        throws IOException
    {
        // Check if referer and page URI match. We do it so that people explicitly
        // linking to IFRAMEs with search results get a "powered by" message.
        if (request.getHeader(RefererChecker.HTTP_HEADER_REFERER) == null ||
                !request.getHeader(RefererChecker.HTTP_HEADER_REFERER).startsWith(request.getRequestURL().toString())) {
            writer.write(" insertPoweredBy=\"true\"");
        }
    }
    
    private final void collect(RawCluster cluster, StringBuffer parentWordIds) throws IOException {
    	buffer.append("<group");
    	if (cluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null) {
    	    buffer.append(" junk=\"true\"");
    	}
    	if (cluster.getProperty(RawCluster.PROPERTY_SCORE) != null) {
    	    buffer.append(" score=\""
    		    + cluster.getProperty(RawCluster.PROPERTY_SCORE) + "\"");
    	}
    	buffer.append(" docs=\"");
    	buffer.append(createRefidsString(cluster));
    	buffer.append("\"");
    	buffer.append(" words=\"");
    	StringBuffer wordidsString = createWordidsString(cluster, parentWordIds);
        buffer.append(wordidsString);
    	buffer.append("\"");
    	buffer.append(">");
    
    	buffer.append("<title>");
    	final List phrases = cluster.getClusterDescription();
    	for (Iterator i = phrases.iterator(); i.hasNext();) {
    	    buffer.append("<phrase>");
    	    buffer.append(xml.toValidXmlText((String) i.next(), false));
    	    buffer.append("</phrase>");
    	}
    	buffer.append("</title>");
    
    	final List docs = cluster.getDocuments();
    	for (Iterator i = docs.iterator(); i.hasNext();) {
    	    final RawDocument doc = (RawDocument) i.next();
            final Integer seqId = (Integer) doc.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
    	    buffer.append("<document refid=\"");
    	    buffer.append(xml.toValidXmlText(seqId.toString(), false));
    	    buffer.append("\" />");
    	}
    
    	final List subgroups = cluster.getSubclusters();
    	for (Iterator i = subgroups.iterator(); i.hasNext();) {
    	    final RawCluster subgroup = (RawCluster) i.next();
    	    collect(subgroup, wordidsString);
    	}
    
    	buffer.append("</group>");
    }

    private StringBuffer createWordidsString(RawCluster cluster,
            final StringBuffer parentWordIds)
    {
        final StringBuffer result = new StringBuffer();
        if (parentWordIds != null)
        {
            result.append(parentWordIds);
        }
        
        TextMarkerListener listener = new TextMarkerListener() {
            public void markedTextIdentified(char[] text, int startPosition,
                    int length, String id, boolean newId)
            {
                if (!newId)
                {
                    if (result.length() > 0){
                        result.append(SEPARATOR);
                    }
                    result.append(id);
                }
            }


            public void unmarkedTextIdentified(char[] text, int startPosition,
                    int length)
            {}
        };
        
        List labels = cluster.getClusterDescription();
        for (Iterator it = labels.iterator(); it.hasNext();) {
            String label = (String)it.next();
            textMarker.tokenize(label.toCharArray(), listener);
        }
        
        return result;
    }

    private String createRefidsString(RawCluster cluster) {
    	final Set refids = new HashSet(50);
    	collectRefids(cluster, refids);

    	if (refids.size() == 0) {
    	    return "";
    	}

    	final StringBuffer sb = new StringBuffer();
    	for (Iterator it = rawDocumentsList.iterator(); it.hasNext();) {
    	    final RawDocument doc = (RawDocument) it.next();
            final Integer seqId = (Integer) doc.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
    
    	    if (refids.contains(seqId)) {
        		sb.append(seqId.toString());
        		sb.append(SEPARATOR);
    	    }
    	}
    	sb.deleteCharAt(sb.length() - 1);

    	return sb.toString();
    }

    private void collectRefids(RawCluster cluster, Set set) {
    	final List docs = cluster.getDocuments();
    	for (Iterator it = docs.iterator(); it.hasNext();) {
    	    final RawDocument doc = (RawDocument) it.next();
            final Integer seqId = (Integer) doc.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
    	    set.add(seqId);
    	}

    	final List subclusters = cluster.getSubclusters();
    	for (Iterator it = subclusters.iterator(); it.hasNext();) {
    	    final RawCluster subcluster = (RawCluster) it.next();
    	    collectRefids(subcluster, set);
    	}
    }
    
    private void emitMessageStrings(Writer writer) throws IOException
    {
        writer.write("<strings>");
        emitMessageString(writer, Constants.RB_OTHER_TOPICS);
        emitMessageString(writer, Constants.RB_SHOW_ALL_CLUSTERS);
        emitMessageString(writer, Constants.RB_MORE_CLUSTERS);
        emitMessageString(writer, Constants.RB_NO_CLUSTERS_CREATED);
        writer.write("</strings>");
    }

    /**
     * @param writer
     * @param key
     * @throws IOException
     */
    private void emitMessageString(Writer writer, String key) throws IOException
    {
        writer.write("<");
        writer.write(key);
        writer.write(">");
        writer.write(messages.getString(key));
        writer.write("</");
        writer.write(key);
        writer.write(">");
    }

    public void processingError(Throwable cause) throws IOException {
        XMLDocumentsSerializer.formatProcessingError(this.writer, cause);
    }
}
