
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.serializers;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.util.xsltfilter.XSLTFilterConstants;
import org.carrot2.webapp.Constants;
import org.carrot2.webapp.RawDocumentsSerializer;

/**
 * A subclass of {@link XMLSerializersFactory} which serializes clusters
 * to XML (and then via XSLT to HTML), but emits documents directly
 * as a HTML output (with a <code>flush()</code> every ten documents).
 *
 * @author Dawid Weiss
 */
public class FancySerializersFactory extends XMLSerializersFactory {
    public RawDocumentsSerializer createRawDocumentSerializer(HttpServletRequest request) {
        // Disable XSLT processor (if it is enabled).
        request.setAttribute(XSLTFilterConstants.NO_XSLT_PROCESSING, Boolean.TRUE);
        return new FancyDocumentSerializer(request.getContextPath(), super
            .getStylesheetsBase(), (ResourceBundle) request
            .getAttribute(Constants.RESOURCE_BUNDLE_KEY));
    }
}
