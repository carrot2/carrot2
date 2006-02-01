
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

package com.dawidweiss.carrot.filter.stc.servlet;

import java.io.InputStream;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.impl.*;
import com.dawidweiss.carrot.filter.FilterRequestProcessor;
import com.dawidweiss.carrot.filter.stc.local.STCLocalFilterComponent;
import com.dawidweiss.carrot.util.tokenizer.SnippetTokenizerLocalFilterComponent;
import com.stachoodev.carrot.filter.normalizer.SmartCaseNormalizer;
import com.stachoodev.carrot.filter.normalizer.local.CaseNormalizerLocalFilterComponent;

/**
 * A remote component implementation of the Suffix Tree Clustering
 * algorithm (by Zamir and Etzioni).
 *
 * @author Dawid Weiss
 */
public class STCFullServlet extends FilterRequestProcessor {
    private final Logger log = Logger.getLogger(this.getClass());

    /**
     * Name of the local process used for executing remote queries.
     */
    private final static String LOCAL_PROCESS_ID = "stc";

    /**
     * Local controller for handling remote requests.
     */
    private final LocalController controller;

    public STCFullServlet() {
        this.controller = setupController();
    }

    /**
     * Sets the servlet configuration. This method is invoked by template class
     * instantiating the request processor.
     */
    public void setServletConfig(ServletConfig servletConfig) {
        super.setServletConfig(servletConfig);
    }

    /**
     * Processes a Carrot2 request.
     */
    public void processFilterRequest(InputStream carrotData,
            HttpServletRequest request, HttpServletResponse response,
            Map paramsBeforeData) throws Exception
    {
        // Parse the input stream and extract document elements.
        final Element root = parseXmlStream(carrotData, "UTF-8");
        final String query = root.elementText("query");

        final List documents = root.elements("document");

        // Convert from XML to RawDocuments
        log.debug("Converting XML to RawDocuments.");
        final ArrayList rawDocumentList = new ArrayList(documents.size());
        for (int i = 0; i < documents.size(); i++) {
            final Element docElem = (Element) documents.get(i);
            final String url = docElem.elementText("url");
            final String title = docElem.elementText("title");
            final String snippet = docElem.elementText("snippet");
            final String id = docElem.attributeValue("id");

            if (url == null) {
                log.warn("Input malformed: url element is null.");
                throw new IllegalArgumentException("Document's url must not be null.");
            }
            if (id == null) {
                log.warn("Input malformed: document's id must not be null.");
                throw new IllegalArgumentException("Input malformed: document's id must not be null.");
            }

            rawDocumentList.add(new RawDocumentBase(url, title, snippet) {
                public Object getId() {
                    return id;
                }
            });
        }
        
        // Check if there's default language coming from HTTP request, if not,
        // replace it with the default.
        Map contextParams = paramsBeforeData;
        if (false == contextParams.containsKey(RawDocumentDummyLanguageDetection.PARAM_LANGUAGE_CODE_TO_SET)) {
            contextParams = new HashMap(paramsBeforeData);
            contextParams.put(RawDocumentDummyLanguageDetection.PARAM_LANGUAGE_CODE_TO_SET, "en");
        }

        // Run local process.
        log.debug("Running local STC clustering process.");
        contextParams.put(RawDocumentsProducerLocalInputComponent.PARAM_SOURCE_RAW_DOCUMENTS, rawDocumentList);
        final ProcessingResult result = this.controller.query(LOCAL_PROCESS_ID, query, contextParams);
        
        // Now traverse clusters and add them to the previous XML file.
        for (Iterator i = ((ClustersConsumerOutputComponent.Result) result.getQueryResult()).clusters.iterator(); i.hasNext();) {
            final RawCluster cluster = (RawCluster) i.next();
            final Element group = root.addElement("group");

            // Process title.
            final List labels = cluster.getClusterDescription();
            final Element titleElem = group.addElement("title");
            for (Iterator j = labels.iterator(); j.hasNext();) {
                final Element phraseElem = titleElem.addElement("phrase");
                phraseElem.setText((String) j.next());
            }

            // Process documents and add them to the group.
            final List clusterDocs = cluster.getDocuments();
            for (Iterator j = clusterDocs.iterator(); j.hasNext();) {
                final RawDocument document = (RawDocument) j.next();
                final Element documentElem = group.addElement("document");
                documentElem.addAttribute("refid", (String) document.getId());
            }
        }

        super.serializeXmlStream(root, response.getOutputStream(), "UTF-8");
    }

    /**
     * Set up Local controller configured to cluster with STC. 
     */
    private final static LocalController setupController() {
        final LocalControllerBase controller = new LocalControllerBase();

        // Add component factories
        final LocalComponentFactory rawDocumentsInputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new RawDocumentsProducerLocalInputComponent();
            }
        };
        controller.addLocalComponentFactory("input.rawlist", rawDocumentsInputFactory);

        final LocalComponentFactory languageGuesserFilterFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                // Default language is english.
                return new RawDocumentDummyLanguageDetection("en");
            }
        };
        controller.addLocalComponentFactory(
            "filter.dummy-language-guesser", languageGuesserFilterFactory);

        final LocalComponentFactory clusterConsumerOutputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new ClustersConsumerOutputComponent();
            }
        };
        controller.addLocalComponentFactory("output.cluster-consumer",
            clusterConsumerOutputFactory);

        final LocalComponentFactory snippetTokenizerFilterFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new SnippetTokenizerLocalFilterComponent();
            }
        };
        controller.addLocalComponentFactory("filter.tokenizer",
            snippetTokenizerFilterFactory);

        final LocalComponentFactory caseNormalizerFilterFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new CaseNormalizerLocalFilterComponent(
                    new SmartCaseNormalizer());
            }
        };
        controller.addLocalComponentFactory("filter.case-normalizer",
            caseNormalizerFilterFactory);

        final LocalComponentFactory stcFilter = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new STCLocalFilterComponent();
            }
        };
        controller.addLocalComponentFactory("filter.stc", stcFilter);

        // Add process
        try {
            final LocalProcessBase stcProcess = new LocalProcessBase(
                "input.rawlist",
                "output.cluster-consumer",
                new String []
                {
                    "filter.dummy-language-guesser",
                    "filter.tokenizer",
                    "filter.case-normalizer",
                    "filter.stc"
                },
                "local-stc-process", "Local STC process for the remote component.");
            controller.addProcess(LOCAL_PROCESS_ID, stcProcess);
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize local process", e);
        }

        return controller;
    }
}