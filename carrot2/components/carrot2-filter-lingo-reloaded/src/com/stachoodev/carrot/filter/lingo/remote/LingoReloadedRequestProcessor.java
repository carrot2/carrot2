/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.remote;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.jdom.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.impl.*;
import com.dawidweiss.carrot.filter.langguesser.*;
import com.dawidweiss.carrot.util.common.*;
import com.dawidweiss.carrot.util.jdom.*;
import com.dawidweiss.carrot.util.tokenizer.*;
import com.stachoodev.carrot.filter.lingo.algorithm.*;
import com.stachoodev.carrot.filter.lingo.local.*;
import com.stachoodev.carrot.filter.normalizer.local.*;
import com.stachoodev.matrix.factorization.*;

/**
 * Remote adapter for the Lingo-Reloaded filter component.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LingoReloadedRequestProcessor extends
    com.dawidweiss.carrot.filter.FilterRequestProcessor
{
    /** Local controller */
    private LocalController localController;

    /**
     * Logger
     */
    protected static final Logger logger = Logger
        .getLogger(LingoReloadedRequestProcessor.class);

    /**
     * Filters Carrot2 XML data.
     * 
     * @param carrotData A valid InputStream to search results data as specified
     *            in the Manual. This filter also accepts additional keywords in
     *            the input XML data.
     * @param request Http request which caused this processing (not used in
     *            this filter)
     * @param response Http response for this request
     * @param params A map of parameters sent before data stream (unused in this
     *            filter)
     */
    public void processFilterRequest(InputStream carrotData,
        HttpServletRequest request, HttpServletResponse response, Map params)
        throws Exception
    {
        // parse input data (must be UTF-8 encoded).
        Element root = parseXmlStream(carrotData, "UTF-8");

        // Snippets
        List documentList = JDOMHelper.getElements("searchresult/document",
            root);

        if (documentList == null)
        {
            // save the output.
            serializeXmlStream(root, response.getOutputStream(), "UTF-8");

            return;
        }

        // Convert snippets from the stream to RawDocument instances
        List rawDocuments = new ArrayList();
        addSnippets(documentList, rawDocuments);

        // Request parameters
        Map requestParameters = new HashMap();
        requestParameters.put(
            RawDocumentsProducerLocalInputComponent.PARAM_SOURCE_RAW_DOCUMENTS,
            rawDocuments);

        // Cluster the data
        ProcessingResult result = localController.query("lingo-nmf-2", root
            .getChildText("query"), requestParameters);

        // Detect any group elements and remove them
        root.removeChildren("group");

        // Add clusters
        addClusters(root, (List) result.getQueryResult());

        // Save the output
        serializeXmlStream(root, response.getOutputStream(), "UTF-8");
    }

    /**
     * @param root
     * @param queryResult
     */
    private void addClusters(Element root, List clusters)
    {
        for (Iterator iter = clusters.iterator(); iter.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) iter.next();

            Element group = new Element("group");

            // Other Topics -like cluster?
            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null)
            {
                group.setAttribute("othertopics", "yes");
            }

            Element title = new Element("title");
            List labels = rawCluster.getClusterDescription();

            if (labels != null)
            {
                for (Iterator labelsIter = labels.iterator(); labelsIter
                    .hasNext();)
                {
                    String label = (String) labelsIter.next();

                    Element phrase = new Element("phrase");
                    phrase.setText(label);
                    title.addContent(phrase);
                }
            }
            else
            {
                Element phrase = new Element("phrase");
                phrase.setText("Group");
                title.addContent(phrase);
            }

            group.addContent(title);

            List clusterDocuments = rawCluster.getDocuments();

            for (Iterator docsIter = clusterDocuments.iterator(); docsIter
                .hasNext();)
            {
                RawDocument rawDocument = (RawDocument) docsIter.next();
                Element doc = new Element("document");
                doc.setAttribute("refid", rawDocument.getId().toString());
                doc.setAttribute("score", StringUtils.toString(
                    (Double) rawDocument
                        .getProperty(LingoWeb.PROPERTY_CLUSTER_MEMBER_SCORE),
                    "#.##"));
                group.addContent(doc);
            }

            addClusters(group, rawCluster.getSubclusters());

            root.addContent(group);
        }
    }

    /**
     * @param documentList
     * @param rawDocuments
     */
    private void addSnippets(List documentList, List rawDocuments)
    {
        for (Iterator iter = documentList.iterator(); iter.hasNext();)
        {
            Element element = (Element) iter.next();
            rawDocuments.add(new RawDocumentSnippet(element
                .getAttributeValue("id"), element.getChildText("title"),
                element.getChildText("snippet"), element.getChildText("url"),
                -1));
        }
    }

    /**
     * Initialize servlet config.
     * 
     * @see com.dawidweiss.carrot.util.AbstractRequestProcessor#setServletConfig(javax.servlet.ServletConfig)
     */
    public void setServletConfig(ServletConfig servletConfig)
    {
        super.setServletConfig(servletConfig);

        // Prepare the controller and clustering algorithms
        localController = new LocalControllerBase();

        // Add components
        LocalComponentFactory remoteInputFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RawDocumentsProducerLocalInputComponent();
            }
        };
        localController.addLocalComponentFactory("input.remote",
            remoteInputFilterFactory);

        LocalComponentFactory languageGuesserFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RawDocumentLanguageDetection(LanguageGuesserFactory
                    .getLanguageGuesser());
            }
        };
        localController.addLocalComponentFactory("filter.language-guesser",
            languageGuesserFilterFactory);

        // Tokenizer filter component
        LocalComponentFactory snippetTokenizerFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new SnippetTokenizerLocalFilterComponent();
            }
        };
        localController.addLocalComponentFactory("filter.tokenizer",
            snippetTokenizerFilterFactory);

        // Case normalizer filter component
        LocalComponentFactory caseNormalizerFilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new CaseNormalizerLocalFilterComponent();
            }
        };
        localController.addLocalComponentFactory("filter.case-normalizer",
            caseNormalizerFilterFactory);

        // Lingo NMF filter component
        LocalComponentFactory lingoNMF3FilterFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                Map parameters = new HashMap();
//                NonnegativeMatrixFactorizationEDFactory matrixFactorizationFactory = new NonnegativeMatrixFactorizationEDFactory();
//                matrixFactorizationFactory.setK(20);
//                parameters.put(LingoWeb.PARAMETER_MATRIX_FACTORIZATION_FACTORY,
//                    matrixFactorizationFactory);
//                parameters.put(LingoWeb.PARAMETER_QUALITY_LEVEL, new Integer(3));
                return new LingoWebLocalFilterComponent(parameters);
            }
        };
        localController.addLocalComponentFactory("filter.lingo-nmf-2",
            lingoNMF3FilterFactory);

        // Cluster consumer output component
        LocalComponentFactory clusterConsumerOutputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RawClustersConsumerLocalOutputComponent();
            }
        };
        localController.addLocalComponentFactory("output.cluster-consumer",
            clusterConsumerOutputFactory);

        // Add the process
        try
        {
            LocalProcessBase lingoNMF2 = new LocalProcessBase(
                "input.remote",
                "output.cluster-consumer",
                new String []
                { "filter.language-guesser", "filter.tokenizer",
                 "filter.case-normalizer", "filter.lingo-nmf-2" },
                "ODP -> Language Guesser -> Tokenizer -> Case Normalizer -> LingoNMF-2",
                "");
            localController.addProcess("lingo-nmf-2", lingoNMF2);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot initialize local process", e);
        }
    }
}