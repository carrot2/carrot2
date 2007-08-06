
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

import javax.servlet.http.HttpServletRequest;

import org.carrot2.webapp.RawClustersSerializer;
import org.carrot2.webapp.RawDocumentsSerializer;

/**
 * A subclass of {@link XMLSerializersFactory} which serializes clusters
 * and documents to JSON format. The main page remains available in HTML.
 *
 * @author Dawid Weiss
 */
public class JSONSerializersFactory extends XMLSerializersFactory {
    /**
     * 
     */
    public RawDocumentsSerializer createRawDocumentSerializer(HttpServletRequest request)
    {
        return new JSONDocumentSerializer();
    }
    
    public RawClustersSerializer createRawClustersSerializer(HttpServletRequest request)
    {
        return new JSONClustersSerializer();
    }
}
