package com.dawidweiss.carrot.grokker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.groxis.support.plugins.AbstractGenerator;
import com.groxis.support.plugins.categorizer.CategorizerEngine;
import com.groxis.support.plugins.facade.CategorizerFacade;


/**
 * A test implementation of a categorizer engine for Grokker based
 * on Carrot2 components.
 * 
 * <p>This code is based on the sample provided with Grokker SDK.</p> 
 * 
 * @author Dawid Weiss
 */
public class CarrotSPI extends AbstractGenerator implements CategorizerEngine {

    private Clusterer clusterer;
    
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
     * This is where the clustering takes place.
     */
	public String[] categorize(String[] documents, Object params) {
        // What are the 'params'?
        System.out.println("CARROT2::CATEGORIZE");
        System.out.println("CARROT2::PARAMS: " + params);
        for (int i=0;i<documents.length;i++) {
            System.out.println(i + " > " + documents[i]);
        }
        
        // In the first step, build document wrappers around the provided
        // documents. We also create 'dummy' URLs on the way.
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
        List clusters = clusterer.clusterHits(docs, query);
        
        return convertToPaths( clusters, documents );
	}
    
    protected String [] convertToPaths( List clusters, String [] documents) {
        // retrieve the result and convert it to path-like format
        // used by Grokker.
        String [] paths = new String[ documents.length ];
        StringBuffer tmpbuf = new StringBuffer();
        constructPaths( clusters, paths, "", tmpbuf);
        
        // go through the list of documents and make sure they belong
        // to a category. if not, assign them to other.
        for (int i=0;i<paths.length;i++) {
            if (paths[i] == null) {
                paths[i] = "?";
            }
        }
        
        return paths;
    }
    
    private final void constructPaths(List clusters, String [] paths, String prefix, StringBuffer tmpbuf) {
        for (Iterator i = clusters.iterator(); i.hasNext();) {
            RawCluster rawCluster = (RawCluster) i.next();

            // is it a 'junk' cluster? Junk clusters group 'other' documents for which the
            // only similarity is the lack of any similarity.
            tmpbuf.setLength(0);
            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null) {
                tmpbuf.append("?");
            } else {
                // get a description phrase for this cluster
                List phrases = rawCluster.getClusterDescription();
                for (int j=0;j<Math.min(2,phrases.size());j++) {
                    if (j>0) tmpbuf.append(", ");
                    tmpbuf.append((String) phrases.get(j));
                }
            }
            
            String clusterLabel = tmpbuf.toString();
            
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
                    tmpbuf.append( paths[id] );
                    tmpbuf.append(CategorizerFacade.PATH_SEPARATOR);
                }
                tmpbuf.append( prefix );
                tmpbuf.append(clusterLabel);
                paths[id] = tmpbuf.toString();
            }
            
            // Traverse subclusters
            List subclusters = rawCluster.getSubclusters();
            if (subclusters != null && subclusters.size() > 0) {
                constructPaths(subclusters, paths, prefix + clusterLabel + CategorizerFacade.SEPARATOR, tmpbuf);
            }
        }
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
