
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

package com.stachoodev.carrot.filter.lingo;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;

import com.dawidweiss.carrot.util.Dom4jUtils;
import com.stachoodev.carrot.filter.lingo.common.Cluster;
import com.stachoodev.carrot.filter.lingo.common.DefaultClusteringContext;


/**
 * This filter clusters given documents using a monolingual version of an
 * LSI-based clustering algorithm.
 */
public class DefaultLsiClustererRequestProcessor
    extends com.dawidweiss.carrot.filter.FilterRequestProcessor {
    /**
     * Filters Carrot2 XML data.
     *
     * @param carrotData A valid InputStream to search results data as
     *        specified in the Manual. This filter also accepts additional
     *        keywords in the input XML data.
     * @param request Http request which caused this processing (not used in
     *        this filter)
     * @param response Http response for this request
     * @param params A map of parameters sent before data stream (unused in
     *        this filter)
     */
    public void processFilterRequest(InputStream carrotData,
        HttpServletRequest request, HttpServletResponse response, Map params)
        throws Exception {
        try {
            // parse input data (must be UTF-8 encoded).
            Element root = parseXmlStream(carrotData, "UTF-8");

            // Prepare data
            DefaultClusteringContext clusteringContext = new DefaultClusteringContext();

            // Parameters
            clusteringContext.setParameters(params);

            // Snippets			
            List documentList = root.selectNodes("document");

            if (documentList == null) {
                // save the output.
                serializeXmlStream(root, response.getOutputStream(), "UTF-8");

                return;
            }

            ClusterStructureHelpers.addSnippets(clusteringContext,
                documentList);

            // Query
            clusteringContext.setQuery(root.elementText("query"));

            // Linguistic data
            List linguisticData = root.selectNodes("l");

            if (linguisticData != null) {
                for (Iterator i = linguisticData.iterator(); i.hasNext();) {
                    Element document = (Element) i.next();

                    clusteringContext.addStem(document.attributeValue("t"),
                        document.attributeValue("s"));

                    if (document.attributeValue("sw") != null) {
                        clusteringContext.addStopWord(document.attributeValue("t"));
                    }
                }
            }

            // Cluster !
            Cluster[] clusters = clusteringContext.cluster();

            // detect any group elements and remove them
            Dom4jUtils.removeChildren(root, "group");

            // Create the output XML
            for (int i = 0; i < clusters.length; i++) {
                ClusterStructureHelpers.addToElement(root, clusters[i]);
            }

            // save the output.
            serializeXmlStream(root, response.getOutputStream(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
