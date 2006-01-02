
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
package com.dawidweiss.carrot.input.yahoo;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
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
import com.dawidweiss.carrot.core.local.LocalController;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalProcessBase;
import com.dawidweiss.carrot.core.local.MissingProcessException;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.impl.DocumentsConsumerOutputComponent;
import com.dawidweiss.carrot.util.common.XMLSerializerHelper;

public class RemoteYahooApiInputComponent
    extends com.dawidweiss.carrot.input.InputRequestProcessor
{
    private final Logger log = Logger.getLogger(this.getClass());
    
    public final static FileFilter SKIP_CVS_SVN_FILE_FILTER = new FileFilter() {
        public boolean accept(File f) {
            final String name = f.getName();
            if (name.equals(".svn") || name.equals("CVS")) {
                return false;
            }
            return true;
        }
    };

    private LocalController controller;

    private HashMap services = new HashMap();

    public void setServletConfig(ServletConfig servletConfig) {
        super.setServletConfig(servletConfig);
        
        // Initialize controllers
        try {
            this.controller = setUpController();
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize yahoo-api input component.", e);
        }
    }

    private LocalControllerBase setUpController() throws Exception {
        final LocalControllerBase controller = new LocalControllerBase();

        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new DocumentsConsumerOutputComponent();
            }
        };

        // Register with the controller
        controller.addLocalComponentFactory("output", outputFactory);

        // Get the services folder.
        String servicesDir = super.getServletConfig().getInitParameter("yahoo.services.dir");
        if (servicesDir == null) {
            throw new Exception("Set yahoo.services.dir init parameter for the servlet.");
        }
        File servicesDirFile = new File(super.getServletConfig().getServletContext().getRealPath(servicesDir));
        if (!servicesDirFile.exists() || !servicesDirFile.isDirectory()) {
            throw new Exception("Services directory does not exist: " 
                    + servicesDirFile.getAbsolutePath());
            
        }

        // Scan services and add as inputs and processes.
        File [] services = servicesDirFile.listFiles(SKIP_CVS_SVN_FILE_FILTER);
        for (int i = 0; i < services.length; i++) {
            final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
            descriptor.initializeFromXML(new FileInputStream(services[i]));
            final YahooSearchService service = new YahooSearchService(descriptor);
            final LocalComponentFactory inputFactory = new LocalComponentFactoryBase() {
                public LocalComponent getInstance() {
                    return new YahooApiInputComponent(service);
                }
            };

            final String name = services[i].getName().replaceAll(".xml", "");
            final String cname = "input." + name;

            controller.addLocalComponentFactory(cname, inputFactory);
    
            // Create and register the process.
            LocalProcessBase process = new LocalProcessBase();
            process.setInput(cname);
            process.setOutput("output");
            controller.addProcess(name, process);
            log.info("Added Yahoo service process: " + name);
        }
        return controller;
    }

    /**
     * Processes the query and writes the result to the output stream.
     *
     * @param query The query which will be copied to the output.
     * @param output The stream, where the query will be saved to.
     * @param requestedResultsNumber The requested number of results (doesn't matter in this
     *        component).
     * @param request HttpRequest which caused this processing.
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
            
            final String serviceName = request.getParameter("service");
            if (serviceName == null) {
                log.warn("No Yahoo service argument.");
                return;
            }

            try {
                HashMap requestParams = new HashMap();
                requestParams.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, 
                        new Integer(requestedResultsNumber));
                List results = (List) controller.query(serviceName, query, requestParams).getQueryResult();
                
                output.write("<searchresult>\n");
                output.write("<query requested-results=\"" + requestedResultsNumber + "\">");
                output.write(serializer.toValidXmlText(query, false));
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
                throw new RuntimeException("Such Yahoo service is not loaded.");
            } catch (Exception e) {
                throw new RuntimeException("Yahoo API component exception.", e);
            } 
        } catch (IOException e) {
            log.warn("Error processing request: " + e.toString(), e);
        }
    }
}
