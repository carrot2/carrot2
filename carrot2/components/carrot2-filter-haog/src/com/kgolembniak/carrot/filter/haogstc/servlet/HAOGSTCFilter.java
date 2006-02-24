package com.kgolembniak.carrot.filter.haogstc.servlet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.core.local.LocalComponentFactoryBase;
import com.dawidweiss.carrot.core.local.LocalController;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.core.local.LocalProcessBase;
import com.dawidweiss.carrot.core.local.ProcessingResult;
import com.dawidweiss.carrot.core.local.clustering.RawCluster;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentBase;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.dawidweiss.carrot.core.local.impl.RawDocumentDummyLanguageDetection;
import com.dawidweiss.carrot.core.local.impl.RawDocumentsProducerLocalInputComponent;
import com.dawidweiss.carrot.filter.FilterRequestProcessor;
import com.dawidweiss.carrot.util.tokenizer.SnippetTokenizerLocalFilterComponent;
import com.kgolembniak.carrot.filter.haogstc.local.HAOGSTCLocalFilterComponent;
import com.stachoodev.carrot.filter.normalizer.SmartCaseNormalizer;
import com.stachoodev.carrot.filter.normalizer.local.CaseNormalizerLocalFilterComponent;

/**
 * Remote filter implementing Hierarchical Arrangement of Overlapping
 * Groups.<br/>
 * Implementation of this class is based on 
 * {@link #com.dawidweiss.carrot.filter.stc.servlet.STCFullServlet}
 * @author Karol Gołembniak
 */
public class HAOGSTCFilter extends FilterRequestProcessor {
	
	private final Logger logger = Logger.getLogger(this.getClass());
    /**
     * Name of the local process used for executing remote queries.
     */
    private final static String LOCAL_PROCESS_ID = "haog-stc";

    /**
     * Local controller for handling remote requests.
     */
    private final LocalController controller;
    
    public HAOGSTCFilter() {
        this.controller = setupController();
    }

    public void setServletConfig(ServletConfig servletConfig) {
        super.setServletConfig(servletConfig);
    }

	public void processFilterRequest(InputStream carrotData, 
			HttpServletRequest request, 
			HttpServletResponse response, 
			Map paramsBeforeData) throws Exception
	{
        // Parse the input stream and extract document elements.
		final Element root = parseXmlStream(carrotData, "UTF-8");
        final String query = root.elementText("query");

        List documents = getDocumentsFromXML(root);
        // Check if there's default language coming from HTTP request, if not,
        // replace it with the default.
        Map contextParams = paramsBeforeData;
        if (false == contextParams.containsKey(
        		RawDocumentDummyLanguageDetection.PARAM_LANGUAGE_CODE_TO_SET)) {
            contextParams = new HashMap(paramsBeforeData);
            contextParams.put(RawDocumentDummyLanguageDetection
            		.PARAM_LANGUAGE_CODE_TO_SET, "en");
        }

        // Run local process.
        logger.debug("Running local HAOG-STC clustering process.");
        contextParams.put(RawDocumentsProducerLocalInputComponent
        		.PARAM_SOURCE_RAW_DOCUMENTS, documents);
        final ProcessingResult result = this.controller.query(LOCAL_PROCESS_ID,
        		query, contextParams);
        
        // Now traverse clusters and add them to the previous XML file.
        for (Iterator i = ((ClustersConsumerOutputComponent.Result) 
        		result.getQueryResult()).clusters.iterator(); i.hasNext();) {
            final RawCluster cluster = (RawCluster) i.next();
            createGroupFromRawCluster(cluster, root);
        }

        super.serializeXmlStream(root, response.getOutputStream(), "UTF-8");

	}
	
	/**
	 * This method creates a group from given cluster and adds this group to
	 * the given node in XML. It also adds all node's children, according to 
	 * HAOG algorithm.
	 * @param cluster - Cluster to add to XML.
	 * @param node - Node to which add this cluster.
	 */
	private void createGroupFromRawCluster(RawCluster cluster, Element node){
        final Element group = node.addElement("group");

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
        
        final List subclusters = cluster.getSubclusters();
        for (Iterator j = subclusters.iterator(); j.hasNext();) {
            final RawCluster rawCluster = (RawCluster) j.next();
            createGroupFromRawCluster(rawCluster, group);
        }
	}
	
	/**
	 * This method parses given XML document and creates a list from it.
	 * @param root - Root node of XML document.
	 * @return list of {@link RawDocumentBase} objects.
	 */
	private List getDocumentsFromXML(Element root){
        // Convert from XML to RawDocuments
        logger.debug("Converting XML to RawDocuments.");
        final List documents = root.elements("document");
        final ArrayList rawDocumentList = new ArrayList(documents.size());
        for (int i = 0; i < documents.size(); i++) {
            final Element docElem = (Element) documents.get(i);
            final String url = docElem.elementText("url");
            final String title = docElem.elementText("title");
            final String snippet = docElem.elementText("snippet");
            final String id = docElem.attributeValue("id");

            if (url == null) {
            	logger.warn("Input malformed: url element is null.");
                throw new IllegalArgumentException("Document's url must not" +
                		" be null.");
            }
            if (id == null) {
            	logger.warn("Input malformed: document's id must not be null.");
                throw new IllegalArgumentException("Input malformed: " +
                		"document's id must not be null.");
            }

            rawDocumentList.add(new RawDocumentBase(url, title, snippet) {
                public Object getId() {
                    return id;
                }
            });
        }
		
        return rawDocumentList;
	}
	
    /**
     * Set up Local controller configured to cluster with STC. 
     */
    private final static LocalController setupController() {
        final LocalControllerBase controller = new LocalControllerBase();

        // Add component factories
        final LocalComponentFactory rawDocumentsInputFactory = 
        	new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new RawDocumentsProducerLocalInputComponent();
            }
        };
        controller.addLocalComponentFactory("input.rawlist", 
        		rawDocumentsInputFactory);

        final LocalComponentFactory languageGuesserFilterFactory = 
        	new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                // Default language is english.
                return new RawDocumentDummyLanguageDetection("en");
            }
        };
        controller.addLocalComponentFactory(
            "filter.dummy-language-guesser", languageGuesserFilterFactory);

        final LocalComponentFactory clusterConsumerOutputFactory = 
        	new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new ClustersConsumerOutputComponent();
            }
        };
        controller.addLocalComponentFactory("output.cluster-consumer",
            clusterConsumerOutputFactory);

        final LocalComponentFactory snippetTokenizerFilterFactory = 
        	new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new SnippetTokenizerLocalFilterComponent();
            }
        };
        controller.addLocalComponentFactory("filter.tokenizer",
            snippetTokenizerFilterFactory);

        final LocalComponentFactory caseNormalizerFilterFactory = 
        	new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new CaseNormalizerLocalFilterComponent(
                    new SmartCaseNormalizer());
            }
        };
        controller.addLocalComponentFactory("filter.case-normalizer",
            caseNormalizerFilterFactory);

        final LocalComponentFactory haogFilter = 
        	new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new HAOGSTCLocalFilterComponent();
            }
        };
        controller.addLocalComponentFactory("filter.haog-stc", haogFilter);

        // Add process
        try {
            final LocalProcessBase hagostcProcess = new LocalProcessBase(
                "input.rawlist",
                "output.cluster-consumer",
                new String []
                {
                    "filter.dummy-language-guesser",
                    "filter.tokenizer",
                    "filter.case-normalizer",
                    "filter.haog-stc"                    
                },
                "local-haog-stc-process", "Local HAOG-STC process " +
                		"for the remote component.");
            controller.addProcess(LOCAL_PROCESS_ID, hagostcProcess);
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize local process", e);
        }

        return controller;
    }
	

}
