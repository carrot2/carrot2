
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.input.googleapi;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.core.local.LocalComponentFactoryBase;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalProcessBase;
import com.dawidweiss.carrot.core.local.MissingProcessException;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.impl.DocumentsConsumerOutputComponent;
import com.dawidweiss.carrot.util.common.XMLSerializerHelper;

public class RemoteGoogleApiInputComponent
    extends com.dawidweiss.carrot.input.InputRequestProcessor
{
    private final Logger log = Logger.getLogger(this.getClass());
    private LocalControllerBase controller;

    public void setServletConfig(ServletConfig servletConfig) {
        super.setServletConfig(servletConfig);
        
        // Initialize controller.
        try {
            this.controller = setUpController();
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize google-api input component.", e);
        }
    }

    private LocalControllerBase setUpController() throws Exception {
        LocalControllerBase controller;

        final GoogleKeysPool pool = new GoogleKeysPool();
        
        // Attempt to locate keys.
        if (this.getServletConfig().getInitParameter("keypool") != null) {
            File keyPool = new File(this.getServletConfig().getInitParameter("keypool"));
            if (keyPool.exists() && keyPool.isDirectory()) {
                pool.addKeys(keyPool, ".key");
            }
        }
        if (System.getProperty("googleapi.keypool") != null) {
            File keyPool = new File(System.getProperty("googleapi.keypool"));
            if (keyPool.exists() && keyPool.isDirectory()) {
                pool.addKeys(keyPool, ".key");
            }
        }
        
        if (pool.getKeysTotal() == 0) {
            log.error("No available google api keys.");
            throw new Exception("No available keys. Use 'googleapi.keypool' property to point to the keys folder.");
        }

        LocalComponentFactory inputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new GoogleApiInputComponent(pool);
            }
        };

        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new DocumentsConsumerOutputComponent();
            }
        };

        // Register with the controller
        controller = new LocalControllerBase();
        controller.addLocalComponentFactory("output", outputFactory);
        controller.addLocalComponentFactory("input", inputFactory);

        // Create and register the process
        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        controller.addProcess("googleapi", process);

        return controller;
    }
    
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
        requestedResultsNumber = Math.max(1, requestedResultsNumber);
        XMLSerializerHelper serializer = XMLSerializerHelper.getInstance();
        try
        {
            log.debug(
                "Received query [" + query.length() + " bytes]: "
                + query.substring(0, Math.min(80, query.length()))
            );

            try {
                HashMap requestParams = new HashMap();
                requestParams.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(requestedResultsNumber));
                List results = (List) controller.query("googleapi", query, requestParams).getQueryResult();
                
                output.write("<searchresult>\n");
                output.write("<query requested-results=\"" + requestedResultsNumber + "\">");
                serializer.toValidXmlText(query, false);
                output.write("</query>\n");

                int id = 0;
                for (Iterator i = results.iterator(); i.hasNext(); id++) {
                    RawDocument rd = (RawDocument) i.next();
                    output.write("<document id=\"" + id + "\">");

                    final String url = rd.getUrl();
                    output.write("\t<url>" + serializer.toValidXmlText(url, false) + "</url>\n");
                    
                    final String title = rd.getTitle();
                    if (title != null) {
                        output.write("\t<title>" + serializer.toValidXmlText(title, false) + "</title>\n");
                    }
                    
                    final String snippet = rd.getSnippet();
                    if (snippet != null) {
                        output.write("\t<snippet>" + serializer.toValidXmlText(snippet, false) + "</snippet>\n");
                    }

                    output.write("</document>");
                }
                output.write("</searchresult>");
            } catch (ProcessingException e) {
                log.warn("Error processing request: " + e.toString(), e);
                output.write("Error processing request: " + e.toString());
            } catch (MissingProcessException e) {
                /* Ignore, will never happen. */
                throw new Error("Missing process? Impossible.");
            } catch (Exception e) {
                throw new RuntimeException("Google API component exception.", e);
            } 
        } catch (IOException e) {
            log.warn("Error processing request: " + e.toString(), e);
        }
    }
}
