

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.input.xml;


import org.apache.log4j.Logger;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.XMLWriter;

import com.dawidweiss.carrot.core.local.ProcessingException;


import java.io.*;
import java.util.Map;

import javax.servlet.http.*;


/**
 * A remote component for the XML input.
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
                // see if the xslt is a named xslt stored in the webapp?
                Map map = extractParameters(request);

                String xslt = (String) request.getParameter("xslt");
                if (xslt!=null) {
                    String translated = this.getServletConfig().getServletContext().getRealPath(xslt);
                    if (new File(translated).isFile() && new File(translated).canRead()) {
                        map.put("xslt", new File(translated).toURL());
                    }
                }
                String source = (String) request.getParameter("source");
                if (source!=null) {
                    String translated = this.getServletConfig().getServletContext().getRealPath(source);
                    if (new File(translated).isFile() && new File(translated).canRead()) {
                        map.put("source", new File(translated).toURL());
                    }
                }

                DocumentResult result = local.performQuery(map);
                new XMLWriter(output).write(result.getDocument());
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
