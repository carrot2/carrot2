
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
import java.util.*;

import org.carrot2.util.XMLSerializerHelper;
import org.carrot2.webapp.Constants;
import org.carrot2.webapp.RawClustersSerializer;

import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.RawDocumentEnumerator;

/**
 * A consumer of {@link RawDocument}s which serializes them to XML.
 * 
 * @author Dawid Weiss
 * @author Stanisław Osiński
 */
public final class XMLClustersSerializer implements RawClustersSerializer {
    private final static char SEPARATOR = ',';
    private final StringBuffer buffer = new StringBuffer();
    private final XMLSerializerHelper xml = XMLSerializerHelper.getInstance();

    private final String contextPath;
    private final String skinBase;

    private Writer writer;
    private List rawDocumentsList;

    public XMLClustersSerializer(String contextPath, String stylesheetsBase) {
        this.skinBase = stylesheetsBase;
        this.contextPath = contextPath;
    }

    public String getContentType() {
        return Constants.MIME_XML_CHARSET_UTF;
    }

    public void startResult(OutputStream os, List rawDocumentsList)
	    throws IOException
    {
    	this.writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);
    	this.rawDocumentsList = rawDocumentsList;
    
    	writer.write("<?xml version=\"1.0\" encoding=\"" + Constants.ENCODING_UTF + "\" ?>\n");
    
    	// We add '@' to inform xslt processor that the stylesheet
    	// is webapp-relative (not fs-root relative); this way we can avoid
    	// loopback connections from the xslt parser to the webapp container
    	writer.write("<?xml-stylesheet type=\"text/xsl\" href=\"@" + skinBase
    		+ "/clusters.xsl\" ?>\n");
    
    	writer.write("<?skin-uri " + contextPath + skinBase + " ?>\n");
    
    	writer.write("<searchresult type=\"clusters\" totalResultsCount=\""
    		+ rawDocumentsList.size() + "\">\n");
    }

    public void write(RawCluster cluster) throws IOException {
    	collect(cluster);
    	writer.write(buffer.toString());
    	buffer.setLength(0);
    }

    public void endResult() throws IOException {
    	try {
    	    writer.write("</searchresult>");
    	    writer.flush();
    	} catch (IOException e) {
    	    // ignore.
    	}
    	writer = null;
    }

    public void collect(RawCluster cluster) throws IOException {
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
    	    collect(subgroup);
    	}
    
    	buffer.append("</group>");
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

    public void processingError(Throwable cause) throws IOException {
        XMLDocumentsSerializer.formatProcessingError(this.writer, cause);
    }
}
