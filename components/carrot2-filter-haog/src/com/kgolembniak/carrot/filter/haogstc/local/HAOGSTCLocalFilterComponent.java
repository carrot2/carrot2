package com.kgolembniak.carrot.filter.haogstc.local;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.filter.stc.algorithm.*;
import com.dawidweiss.carrot.core.local.profiling.ProfiledLocalFilterComponentBase;
import com.dawidweiss.carrot.filter.stc.StcParameters;
import com.dawidweiss.carrot.filter.stc.suffixtree.ExtendedBitSet;
import com.kgolembniak.carrot.filter.haog.algorithm.CombinedVertex;
import com.kgolembniak.carrot.filter.haog.algorithm.GraphProcessor;
import com.kgolembniak.carrot.filter.haog.algorithm.Vertex;

/**
 * Implementation of Hierarchical Arrangement of Overlapping Groups.
 * @author Karol Gołembniak
 */
public class HAOGSTCLocalFilterComponent extends ProfiledLocalFilterComponentBase
    implements TokenizedDocumentsConsumer, RawClustersProducer, LocalFilterComponent
{
	public String getName()
    {
        return "haog-stc";
    }

	/** Documents to be clustered */
	private List documents;
	
    /** STC's document references */
    private List documentReferences;

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsConsumer.class, RawClustersProducer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawClustersConsumer.class }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /**
     * Current request's context.
     */
    private RequestContext requestContext;
    
    /**
     * Parameters for cluster merging and cluster's description creation
     */
    private StcParameters params;

    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
        documents = new ArrayList();
        documentReferences = new ArrayList();
    }

    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawClustersConsumer = (RawClustersConsumer) next;
    }

	public void addDocument(TokenizedDocument doc) throws ProcessingException {
        startTimer();

        documents.add(doc);

        final DocReference documentReference = new DocReference(doc);
        documentReferences.add(documentReference);

        stopTimer();
	}

    public void flushResources()
    {
        super.flushResources();

        documentReferences.clear();
        documents.clear();
        rawClustersConsumer = null;
        this.requestContext = null;
    }

    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        this.requestContext = requestContext;
    }

    public void endProcessing() throws ProcessingException
    {
        startTimer();

        //STC part
        final STCEngine stcEngine = new STCEngine(documentReferences);
        stcEngine.createSuffixTree();
        params = StcParameters.fromMap(
        		this.requestContext.getRequestParameters());

        stcEngine.createBaseClusters(params);
    	final List clusters = stcEngine.getBaseClusters();

        //HAOG part
        connectBaseClusters(clusters);
        GraphProcessor processor = new GraphProcessor();
        List graph = processor.getGraphFromBCList(clusters);
		List cleanGraph = processor.removeCycle(graph);
		List kernel  = processor.findKernel(cleanGraph);
		createRawClusters(kernel);
    
        stopTimer();

        super.endProcessing();
    }
	
    /**
     * This method connects base clusters and creates a graph from them. 
     * A parameter MergeTreshold is used as a condition to connect clusters.
     */
    private void connectBaseClusters(List clusters){
        final StcParameters params = StcParameters.fromMap(
                this.requestContext.getRequestParameters());
    	final float CONNECT_CONDITION = params.getMergeThreshold();
    	
    	for (int i = 1; i < clusters.size(); i++){
            BaseCluster a;
            BaseCluster b;
            
            a = (BaseCluster) clusters.get(i);
            final long a_docCount = a.getNode().getSuffixedDocumentsCount();
            long b_docCount;
            
            for (int j = 0; j < i; j++){
                b = (BaseCluster) clusters.get(j);
                b_docCount = b.getNode().getSuffixedDocumentsCount();
                final double a_and_b_docCount = a.getNode()
                	.getInternalDocumentsRepresentation().numberOfSetBitsAfterAnd(
                        b.getNode().getInternalDocumentsRepresentation()
                    );

                //This processing is bidirectional, not like Zamir's STC -> we
                //have ordered graph
                if ((a_and_b_docCount / b_docCount) >= CONNECT_CONDITION){
                    a.addLink(b);
                }

                if ((a_and_b_docCount / a_docCount) >= CONNECT_CONDITION){
                    b.addLink(a);
                }
            }
        }
    }

    /**
     * This method creates output raw claster from internal results representation.
     * @throws ProcessingException 
     */
    private void createRawClusters(List kernel) throws ProcessingException {
		CombinedVertex kernelVertex;
		RawClusterBase rawCluster;
		//Reverse makes more complex cluster to be first on the list.
    	for (int i1=kernel.size()-1; i1>=0; i1--){
    		kernelVertex = (CombinedVertex) kernel.get(i1);
    		rawCluster = createRCFromCV(kernelVertex);
    		rawClustersConsumer.addCluster(rawCluster);
		}
	}

    /**
     * This method converts internal algorithm representation of vertices
     * to clusters suitalbe for displaying later.
     * @param vertex
     * @return cluster
     */
	private RawClusterBase createRCFromCV(CombinedVertex vertex) {
		RawClusterBase rawCluster = new RawClusterBase();

		RawDocument rawDocument;
		TokenizedDocument tokenizedDoc;		
		
		rawCluster.addLabel(vertex.getVertexDescription(params));
		
		ExtendedBitSet docSet = vertex.getDocuments();
		//for all documents in node(cluster)
		for (Iterator doc = docSet.iterator(); doc.hasNext();){
			final int docIndex = ((Integer) doc.next()).intValue();
			tokenizedDoc = (TokenizedDocument) documents.get(docIndex);
	        rawDocument = (RawDocument) tokenizedDoc.getProperty(
	        		TokenizedDocument.PROPERTY_RAW_DOCUMENT);
        	rawCluster.addDocument(rawDocument);
		}

		List successors = vertex.getSuccList();
		RawClusterBase subCluster;
		CombinedVertex successor;
		CombinedVertex sibling;
		
		boolean belongsToKernel;
		//for all children of this node
		for (int i1=0; i1<successors.size(); i1++){
			belongsToKernel = true;
			successor = (CombinedVertex) successors.get(i1);
			//Check if vertex isn't also grandchild =
			//Check if vertex belongs to kernel of subgraph
			for (int i2=0; i2<successors.size(); i2++) {
				sibling = (CombinedVertex) successors.get(i2); 
				if (sibling.getSuccList().contains(successor)){
					belongsToKernel = false;
				}
			}
			
			if (belongsToKernel){
				subCluster = createRCFromCV(successor);
				rawCluster.addSubcluster(subCluster);
			}
		}
		return rawCluster;
	}

}