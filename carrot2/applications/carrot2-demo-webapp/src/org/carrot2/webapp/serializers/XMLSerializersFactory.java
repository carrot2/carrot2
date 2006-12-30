
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

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.carrot2.webapp.*;

/**
 * A serializer factory returning serializers that produce XML.
 *
 * @author Dawid Weiss
 */
public class XMLSerializersFactory implements SerializersFactory {

    private String stylesheetsBase;

    public void configure(ServletConfig config) {
        final String webappRelative = config.getInitParameter("serializer.xml.stylesheets");
        if (webappRelative == null) {
            throw new RuntimeException("serializer.xml.stylesheets property is required.");
        }
        if (!webappRelative.startsWith("/")) {
            throw new RuntimeException("serializer.xml.stylesheets property must be webapp-relative (start with a '/').");
        }
        this.stylesheetsBase = webappRelative;
    }

    public PageSerializer createPageSerializer(HttpServletRequest request) {
        return new XMLPageSerializer(request.getContextPath(), stylesheetsBase);
    }

    public RawDocumentsSerializer createRawDocumentSerializer(HttpServletRequest request) {
        return new XMLDocumentsSerializer(request.getContextPath(), stylesheetsBase);
    }

    public RawClustersSerializer createRawClustersSerializer(HttpServletRequest request) {
        return new XMLClustersSerializer(request.getContextPath(), stylesheetsBase);
    }

    protected final String getStylesheetsBase() {
        return this.stylesheetsBase;
    }

    /**
     * Accept all requests by default.
     */
    public boolean acceptRequest(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }
}
