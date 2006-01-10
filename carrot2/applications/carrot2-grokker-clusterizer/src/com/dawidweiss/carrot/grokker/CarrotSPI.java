
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

package com.dawidweiss.carrot.grokker;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.*;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.groxis.product.grokker.GrokkerApplicationManager;
import com.groxis.support.plugins.AbstractGenerator;
import com.groxis.support.plugins.categorizer.CategorizerEngine;
import com.groxis.support.plugins.facade.CategorizerFacade;


/**
 * A Carrot2 plugin for Grokker.
 * 
 * Integrates Lingo with Grokker Desktop Search.
 * 
 * @author Based on the sample provided with Grokker SDK
 * @author Dawid Weiss
 */
public class CarrotSPI extends AbstractGenerator implements CategorizerEngine {
    private final static Logger logger;

    /**
     * For each {@link RawCluster} returned by Carrot2 we store its path in Grokker's
     * notation (<code>/cluster/subcluster</code>). The path for each cluster is
     * stored as a property of this name. 
     */
    private final static String PROPERTY_GROKKER_CLUSTER_PATH = "grokker.cluster-path";

	static {
		// a static block that initializes log4j sink.
        logger = Logger.getLogger("Carrot2");

		try {
            InputStream resourceConfig;
            
            boolean grokkerDebugInfo = false;
            try {
	            grokkerDebugInfo = Boolean.valueOf(GrokkerApplicationManager.getGrokkerApplicationProperty("carrot2.debug", "false")).booleanValue();
            } catch (Throwable t) {
	            // ignore if anything bad happens.
            }
            
            if (grokkerDebugInfo || Boolean.getBoolean("carrot2.debug") || Boolean.getBoolean("Carrot2.debug")) {
                resourceConfig = CarrotSPI.class.getClassLoader().getResourceAsStream("resources/log4j-debug.properties");
                System.out.println("[Carrot2] Debug mode.");
            } else {
                resourceConfig = CarrotSPI.class.getClassLoader().getResourceAsStream("resources/log4j.properties");
                System.out.println("[Carrot2] Normal mode.");
            }

            if (resourceConfig == null) {
                System.out.println("[Carrot2] Log4j configuration resource not found.");
            } else {
                Properties p = new Properties();
                p.load(resourceConfig);
                PropertyConfigurator.configure(p);
            }

            logger.info("Log4j initialized.");
		} catch (Throwable t) {
			System.out.println("[Carrot2] Could not initialize log4j: " + t);
		}
	}

    private Clusterer clusterer;
    
    /**
     * A comparator used to order cluster categories in the order of their
     * "importance". 
     * 
     * Use any of the predefined comparators:
     * {@link #scorePropertyComparator}.
     * 
     * If <code>null</code>, the categories are ordered in their original order
     * of traversal:
     * <pre> 
     * /first
     * /first/sub1
     * /first/sub2
     * /second
     * /second/sub
     * ...
     * </pre>
     */
    private Comparator comparator;

    /**
     * A comparator used to order cluster categories in the order of their
     * decreasing scores (sub categories from different parents may be interleaved,
     * although it shouldn't b),
     * for example:
     * <pre>
     * /first
     * /first/sub1
     * /second
     * /first/sub2
     * /second/sub
     * </pre>
     * 
     * All clusters must have {@link RawCluster#PROPERTY_SCORE} property set, otherwise
     * a runtime exception is thrown.
     */
    private Comparator scorePropertyComparator = new Comparator() {
		public int compare(Object arg0, Object arg1) {
            final RawCluster a = (RawCluster) arg0;
            final RawCluster b = (RawCluster) arg1;

            final Double aScore = (Double) a.getProperty(RawCluster.PROPERTY_SCORE);
            final Double bScore = (Double) b.getProperty(RawCluster.PROPERTY_SCORE);
            if (aScore == null || bScore == null) { 
                throw new RuntimeException("Cluster score comparator requires PROPERTY_SCORE for each cluster.");
            }

            final double av = aScore.doubleValue();
            final double bv = bScore.doubleValue();
            
            if (av < bv) {
                return 1;
            } else if (av > bv) {
                return -1;
            } else return 0;
		}
    };
    
	/**
     * Initialization method (grokker) 
     */
	protected void generate() {
        initialize();
        CategorizerFacade.addEngine(getIdentifier(), this);
	}
    
    /**
     * Initialization method (local).
     */
    protected void initialize() {
        clusterer = new Clusterer();
    }
    
    /**
     * This method is not allowed in Carrot categorizer.
     */
	public String categorize(String document) {
		throw new UnsupportedOperationException("Not supported by this categorizer.");
	}

    /**
     * This method is not allowed in Carrot categorizer.
     */
	public String categorize(String document, Object params) {
        throw new UnsupportedOperationException("Not supported by this categorizer.");
	}

    /**
     * Simple delegation to {@link #categorize(String[], Object)} 
     */
	public String[] categorize(String[] documents) {
		return categorize(documents, null);
	}

    /**
     * This method categorizes input documents and additionaly
     * returns the information about the order of categories (sorted
     * according to their score).
     * 
     * @return <code>String[][]</code>, where the zero index contains
     * clustered documents (as in {@link #categorize(String[], Object)}) and
     * the first index contains an array of strings with category paths.
     */
    public String[][] categorizeInOrder(String[] documents, Object params) {
        logger.info("Clustering started.");

        // In the first step, build document wrappers around the provided
        // documents. We also create 'dummy' URLs on the way because these URLs are
        // required by Carrot's API.
        ArrayList docs = new ArrayList(documents.length);
        for (int i=0;i<documents.length;i++) {
            docs.add( new DocumentAdapter(i, "http://dummy.url/", null, documents[i]) );
        }
        
        // now perform the clustering:
        String query = null;
        if (params instanceof String []) {
            StringBuffer b = new StringBuffer();
            for (int i=0;i<((String[])params).length;i++) {
                if (i>0) b.append(" ");
                b.append(((String[])params)[i]);
            }
        } else if (params instanceof String) {
            query = (String) params;
        }
        long start = System.currentTimeMillis();
        List clusters = clusterer.clusterHits(docs, query);
        long stop = System.currentTimeMillis();
        logger.info("Lingo Reloaded clustering time: " + (stop - start) + " ms");

        String [][] paths = convertToPaths(clusters, documents);
        logger.info("Clustering finished, postprocessing " + (System.currentTimeMillis() - stop) + " ms");

        return paths;
    }
    
    /**
     * This is where the clustering takes place.
     */
	public String[] categorize(String[] documents, Object params) {
        return categorizeInOrder(documents, params)[0];
	}
    
    /**
     * Conversion from Carrot data structures to Grokker's paths.
     */
    protected String [][] convertToPaths(List clusters, String [] documents) {
        // retrieve the result and convert it to path-like format
        // used by Grokker.
        String [] paths = new String[documents.length];

        // Create a reusable string buffer.
        StringBuffer tmpbuf = new StringBuffer();
        ArrayList allClusters = new ArrayList();
        constructPaths(clusters, paths, "", tmpbuf, allClusters);

        String [] categories = new String[allClusters.size()];
        if (comparator != null) {
        	Collections.sort(allClusters, comparator);
        }
        for (int i=0; i<allClusters.size(); i++) {
            categories[i] = (String) ((RawCluster) allClusters.get(i)).getProperty(PROPERTY_GROKKER_CLUSTER_PATH);
        }

        // Dump some debugging information
        if (logger.isEnabledFor(Level.DEBUG)) {
        	StringBuffer buf = new StringBuffer();
        	buf.append("Cluster paths (sorted): \n");
	        for (int i=0; i<categories.length; i++) {
	        	buf.append("   ");
	            buf.append(categories[i]);
	            buf.append("\n");
	        }
        	logger.debug(buf.toString());
        }

        return new String[][] {paths, categories};
    }

    /**
     * This methods recursively descends through all the generated clusters and appends
     * paths to <code>paths</code> table that corresponds to documents. 
     */
    private final void constructPaths(List clusters, String [] paths, String prefix, StringBuffer tmpbuf, ArrayList allClusters) {
        for (Iterator i = clusters.iterator(); i.hasNext();) {
            RawCluster rawCluster = (RawCluster) i.next();

            // is it a 'junk' cluster? Junk clusters group 'other' documents for which the
            // only similarity is the lack of any similarity.
            tmpbuf.setLength(0);
            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null) {
                // Do nothing, this is a junk cluter.
                // tmpbuf.append("(Other Topics)");
            } else {
                // get a description phrase for this cluster
                List phrases = rawCluster.getClusterDescription();
                for (int j=0;j<Math.min(2,phrases.size());j++) {
                    if (j>0) tmpbuf.append(", ");
                    tmpbuf.append((String) phrases.get(j));
                }
            }
            
            // Dump some debugging information
            if (logger.isEnabledFor(Level.DEBUG)) {
                String logInfo = (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) == null ? "[cluster] " : "[junk] ")
                    + "current path: " + prefix + (prefix.length() == 0 ? tmpbuf.toString() : prefix + " / " + tmpbuf.toString());
                logger.debug( logInfo );
            }
            
            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null) {
                // RJ's suggestion: We do not traverse junk groups and do not add any information
                // about them to the result. Just go on to the next cluster.                
            	continue;                
            }
            
            String clusterLabel = tmpbuf.toString();
            
            // make sure cluster label doesn't contain special characters
            if (clusterLabel.indexOf(CategorizerFacade.PATH_SEPARATOR) >= 0
                    || clusterLabel.indexOf(CategorizerFacade.SEPARATOR) >= 0) {
                clusterLabel = clusterLabel.replace(CategorizerFacade.PATH_SEPARATOR, ' ')
                    .replace(CategorizerFacade.SEPARATOR, ' ');
            }
            
            // get documents in this cluster. These might _not_ be the same
            // documents as returned from the input component (in this case, Lucene).
            // but they will have identical identifiers. We simply map them
            // back to the original docs.
            List rawDocuments = rawCluster.getDocuments();
            for (Iterator k = rawDocuments.iterator(); k.hasNext();) {
                tmpbuf.setLength(0);
                RawDocument doc = (RawDocument) k.next();
                int id = ((Integer) doc.getId()).intValue();
                
                // add the cluster label to id-th document.
                if (paths[id] != null) {
                    tmpbuf.append(paths[id]);
                    tmpbuf.append(CategorizerFacade.PATH_SEPARATOR);
                }
                tmpbuf.append(prefix);
                tmpbuf.append(clusterLabel);
                paths[id] = tmpbuf.toString();
            }

            // Set cluster label and add it to a list of all clusters.
            rawCluster.setProperty(PROPERTY_GROKKER_CLUSTER_PATH, prefix + clusterLabel);
            final String currentClusterPath = prefix + clusterLabel + CategorizerFacade.SEPARATOR;
            allClusters.add(rawCluster);

            // Traverse subclusters
            List subclusters = rawCluster.getSubclusters();
            if (subclusters != null && subclusters.size() > 0) {
                constructPaths(subclusters, paths, currentClusterPath, tmpbuf, allClusters);
            }
        }
    }

    /**
     * Enables the use of {@link #scorePropertyComparator} for ordering output
     * category labels. 
     */
    public void setUseScoreComparator() {
        this.comparator = scorePropertyComparator;
    }

    public boolean useInlineTaxonomy() {
        return false;
    }    

	public String getComments() {
		return getPlugin().getShortDescription();
	}

	public String getIdentifier() {
		return getPlugin().getIdentifier();
	}

	public String getDisplayName() {
		return getPlugin().getDisplayName();
	}
    
	public boolean isThreadSafe() {
		return true;
	}
	
	public boolean supportsCategorizeUrls() {
		return false;
	}
}
