package com.dawidweiss.carrot2.browser;

import java.util.HashMap;

import com.chilang.carrot.filter.cluster.local.RoughKMeansLocalFilterComponent;
import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.core.local.LocalComponentFactoryBase;
import com.dawidweiss.carrot.core.local.LocalController;
import com.dawidweiss.carrot.core.local.LocalProcessBase;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.input.localcache.RemoteCacheAccessLocalInputComponent;
import com.dawidweiss.carrot.input.snippetreader.local.SnippetReaderLocalInputComponent;
import com.dawidweiss.carrot.util.tokenizer.languages.english.English;
import com.stachoodev.carrot.filter.lingo.local.LingoLocalFilterComponent;

/**
 * A facade that creates a few components and processes.
 * 
 * @author Dawid Weiss
 */
public class ProcessesAndComponents {

    /**
     * Adds component factories to the controller.
     */
    public static void addComponentFactories(LocalController controller) {
        // ODP input component factory
        LocalComponentFactory googleFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                SnippetReaderLocalInputComponent sr = new SnippetReaderLocalInputComponent();
        	    try {
                    sr.setConfigurationXml(
                            this.getClass().getResourceAsStream("/res/google.xml"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return sr;
            }
        };
        controller.addLocalComponentFactory("input.google", googleFactory);
        
        // ODP input component factory
        LocalComponentFactory inputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new RemoteCacheAccessLocalInputComponent();
            }
        };
        controller.addLocalComponentFactory("input.localcache", inputFactory);

		// Cluster consumer output component
		LocalComponentFactory clusterConsumerOutputFactory = new LocalComponentFactoryBase() {
			public LocalComponent getInstance() {
				return new ClustersConsumerOutputComponent();
			}
		};
		controller.addLocalComponentFactory("output.cluster-consumer", 
			clusterConsumerOutputFactory);

		// Clustering component here.
		LocalComponentFactory lingoFactory = new LocalComponentFactoryBase() {
			public LocalComponent getInstance()
			{
				HashMap defaults = new HashMap();
				
				defaults.put("lsi.threshold.clusterAssignment", "0.150");
				defaults.put("lsi.threshold.candidateCluster",  "0.775");

				// Lingo uses stemmers and stop words from the languages below.
				return new LingoLocalFilterComponent(
					new Language[]
					{ 
						new English() 
					}, defaults);
			}
		};
		controller.addLocalComponentFactory("filter.lingo-classic", lingoFactory);
		
		// Another clustering compoinent.
		LocalComponentFactory fuzzyFactory = new LocalComponentFactoryBase() {
			public LocalComponent getInstance()
			{
				// Lingo uses stemmers and stop words from the languages below.
				return new RoughKMeansLocalFilterComponent();
			}
		};
		controller.addLocalComponentFactory("filter.trc", fuzzyFactory);
    }

    public static void addProcesses(LocalController controller) {
        try {
            controller.addProcess("Google, cluster with Lingo Classic", 
                    new LocalProcessBase("input.google",
                            "output.cluster-consumer",
                            new String [] {"filter.lingo-classic"}));
            controller.addProcess("Google, cluster with TRC", 
                    new LocalProcessBase("input.google",
                            "output.cluster-consumer",
                            new String [] {"filter.trc"}));
        } catch (Exception e) {
            throw new RuntimeException("Could not add process.", e);
        }
    }
}
