/**
 * 
 * @author chilang
 * Created 2003-07-25, 23:54:32.
 */
package com.chilang.carrot.filter.cluster;

import com.chilang.carrot.filter.cluster.rough.XClusterWrapper;
import com.chilang.carrot.filter.cluster.rough.clustering.Clusterer;
import com.chilang.carrot.filter.cluster.rough.clustering.KClusterer;
import com.chilang.carrot.filter.cluster.rough.data.IRContext;
import com.chilang.carrot.filter.cluster.rough.data.WebIRContext;
import com.chilang.carrot.filter.cluster.rough.measure.SimilarityFactory;
import com.chilang.carrot.filter.cluster.rough.measure.Similarity;
import com.chilang.util.StringUtils;
import com.chilang.util.Timer;
import org.apache.log4j.Logger;
import org.jdom.Element;
import com.dawidweiss.carrot.util.jdom.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This filter clusters given documents using a baseline K-means algorithm
 */
public class KMeansClusteringRequestProcessor
        extends AbstractClusteringRequestProcessor {
    Timer timer = new Timer();
    /** Logger */
    protected static final Logger log =
            Logger.getLogger(KMeansClusteringRequestProcessor.class);



    /**
     * Filters Carrot2 XML data.
     *
     * @param carrotData A valid InputStream to search results data as specified in the Manual. This filter
     *                   also accepts additional keywords in the input XML data.
     * @param request    Http request which caused this processing (not used in this filter)
     * @param response   Http response for this request
     * @param params     A map of parameters sent before data stream (unused in this filter)
     */
    public void processFilterRequest(InputStream carrotData, HttpServletRequest request,
                                     HttpServletResponse response, Map params)
            throws Exception {
        try {
            // parse input data (must be UTF-8 encoded).
            Element root = parseXmlStream(carrotData, "UTF-8");



            // Snippets
            List documentList = JDOMHelper.getElements("searchresult/document", root);
            if (documentList == null) {
                // save the output.
                serializeXmlStream(root, response.getOutputStream(), "UTF-8");
                return;
            }

            String query = JDOMHelper.getElement("searchresult/query", root).getText();
            log.debug("query : "+query);
            Collection snips = getSnippets(documentList);

            timer.start();
            IRContext context = new WebIRContext(query, snips);

            int numberOfClusters = Integer.parseInt((String)StringUtils.getCarrotParameter(params, "clusters.initial"));
            double membershipThreshold = Double.parseDouble((String)StringUtils.getCarrotParameter(params, "similarity.threshold"));
            Similarity similarity = SimilarityFactory.getCosine();
            Object carrotParameter = StringUtils.getCarrotParameter(params, "phrase.used");
            System.out.println("carrot parameter "+carrotParameter);
            boolean usePhrase = Boolean.valueOf((String)carrotParameter).booleanValue();
            System.out.println("Use phrases "+usePhrase);
            Clusterer clusterer = new KClusterer(numberOfClusters, membershipThreshold, similarity, usePhrase);

            clusterer.setContext(context);

            log.debug("Running K-means baseline clustering ..");

            // Cluster !
            clusterer.cluster();

            // detect any group elements and remove them
            root.removeChildren("group");


            root = new XClusterWrapper(clusterer.getClusters(), root).asElement();

            log.debug("TOTAL TIME : "+timer.elapsedAsString());
//             save the output.
            serializeXmlStream(root, response.getOutputStream(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


}