
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

package com.dawidweiss.carrot.input.xml;


import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;

import com.dawidweiss.carrot.core.local.ProcessingException;


import java.io.*;
import java.util.Map;

import javax.servlet.http.*;


/**
 * A remote component for the XML input.
 * 
 * @author Dawid Weiss
 * @author Paul Dlug (identity XSLT patch)
 */
public class XmlRemoteInputComponent
    extends com.dawidweiss.carrot.input.InputRequestProcessor
{
    private final Logger log = Logger.getLogger(this.getClass());

    /**
     * Processes the query and writes the result to the output stream.
     *
     * @param query The query which will be copied to the output.
     * @param output The stream, where the query will be saved to.
     * @param requestedResultsNumber The requested number of results (doesn't matter in this
     *        component).
     * @param request HttpRequest which caused this processing. not used by this component.
     */
    public void processQuery(
        String query, int requestedResultsNumber, Writer output, HttpServletRequest request)
    {
        // get a local component's instance.
        XmlLocalInputComponent local = new XmlLocalInputComponent(); 
        
        try
        {
            log.debug(
                "Received query [" + query.length() + " bytes]: "
                + query.substring(0, Math.min(80, query.length()))
            );

            try {
                local.setQuery(query);
                final Map map = extractParameters(request);

                final String xslt = request.getParameter("xslt");
                // Identify the XSLT file. It is either 'identity' (no transformation)
                // or a named XSLT file.
                if (xslt != null) {
                    if ("identity".equals(xslt)) {
                        map.put("xslt", xslt);
                    } else {
                        String translated = this.getServletConfig().getServletContext().getRealPath(xslt);
                        if (new File(translated).isFile() && new File(translated).canRead()) {
                            map.put("xslt", new File(translated).toURL());
                        }
                    }
                }
                final String source = request.getParameter("source");
                if (source!=null) {
                    String translated = this.getServletConfig().getServletContext().getRealPath(source);
                    if (new File(translated).isFile() && new File(translated).canRead()) {
                        map.put("source", new File(translated).toURL());
                    }
                }

                final Document result = local.performQuery(map);
                new XMLWriter(output).write(result);
            } catch (ProcessingException e) {
                log.warn("Error processing request: " + e.toString(), e);
                output.write("Error processing request: " + e.toString());
            } 
        }
        catch (IOException e)
        {
            log.warn("Error processing request: " + e.toString());
        }
    }
}
