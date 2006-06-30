
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

package org.carrot2.webapp;

import java.io.IOException;
import java.io.OutputStream;

import com.dawidweiss.carrot.core.local.clustering.RawDocument;

/**
 * A serializer for {@link RawDocument}s.
 * 
 * @author Dawid Weiss
 */
public interface RawDocumentsSerializer {
    public String getContentType();
    public void startResult(OutputStream os) throws IOException;
    public void write(RawDocument document) throws IOException;
    public void endResult() throws IOException;
}
