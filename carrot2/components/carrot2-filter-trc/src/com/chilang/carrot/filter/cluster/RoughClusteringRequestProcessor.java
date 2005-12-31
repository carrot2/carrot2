/**
 * 
 * @author chilang
 * Created 2003-07-25, 23:54:32.
 */
package com.chilang.carrot.filter.cluster;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.chilang.carrot.filter.cluster.rough.CommonFactory;
import com.chilang.carrot.filter.cluster.rough.clustering.AbstractClusterer;
import com.chilang.carrot.filter.cluster.rough.clustering.Cluster;
import com.chilang.carrot.filter.cluster.rough.clustering.KMeansClusterer;
import com.chilang.carrot.filter.cluster.rough.data.IRContext;
import com.chilang.carrot.filter.cluster.rough.data.WebIRContext;
import com.chilang.carrot.filter.cluster.rough.measure.CosineCoefficient;
import com.chilang.carrot.filter.cluster.rough.measure.Similarity;
import com.chilang.carrot.filter.cluster.rough.trsm.RoughSpace;
import com.chilang.util.StringUtils;
import com.dawidweiss.carrot.util.Dom4jUtils;


/**
 * This filter clusters given documents using a rough-enriched K-Means
 * clustering algorithm.
 */
public class RoughClusteringRequestProcessor
        extends AbstractClusteringRequestProcessor {
    /** Logger */
    protected static final Logger log =
            Logger.getLogger(RoughClusteringRequestProcessor.class);



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
            List documentList = root.elements("document");
            if (documentList == null) {
            // save the output.
                serializeXmlStream(root, response.getOutputStream(), "UTF-8");
                return;
            }

            Collection snips = getSnippets(documentList);

            IRContext corpus = new WebIRContext(snips);

            corpus.buildDocumentTermMatrix();
//            ((WebIRContext)corpus).printTermWeight();

            int toleranceThreshold = Integer.parseInt((String)StringUtils.getCarrotParameter(params, "trc.tolerance.threshold"));
            int initialClusters = Integer.parseInt((String)StringUtils.getCarrotParameter(params, "trc.clusters.initial"));
            int maxIteration = Integer.parseInt((String)StringUtils.getCarrotParameter(params, "trc.iteration.max"));
            double similarityThreshold = Double.parseDouble((String)StringUtils.getCarrotParameter(params, "trc.similarity.threshold"));
            double termSignificanceThreshold = Double.parseDouble((String)StringUtils.getCarrotParameter(params, "trc.termSignificance.threshold"));
            boolean postProcessing = Boolean.getBoolean((String)StringUtils.getCarrotParameter(params, "trc.postProcessing"));


            log.debug("Generating tolerance classes ..");
            RoughSpace roughSetModel = CommonFactory.createRoughSpace(corpus.getTermFrequency(), toleranceThreshold, 0);


            log.debug("Running Rough K-Means algorithm ..");
            Similarity distance = new CosineCoefficient();
            AbstractClusterer clusterer =
                    new KMeansClusterer(initialClusters,
                            corpus,
                            distance,
                            roughSetModel,
                            maxIteration,
                            similarityThreshold,
                            termSignificanceThreshold,
                            postProcessing);

            // Cluster !

            clusterer.doClustering(corpus.getDocuments());
            Cluster[] clusters = clusterer.getClusters();

            // detect any group elements and remove them
            Dom4jUtils.removeChildren(root, "group");

            // Create the output XML
//            for (int i = 0; i < clusters.length; i++) {
//                addToElement(root, clusters[i]);
//            }

            addClusteringResult(root, clusters);
            // save the output.
            serializeXmlStream(root, response.getOutputStream(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


}