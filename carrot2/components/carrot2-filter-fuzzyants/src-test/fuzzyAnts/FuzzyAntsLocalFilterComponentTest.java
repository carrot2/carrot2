package fuzzyAnts;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import junit.framework.*;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.impl.*;
import com.dawidweiss.carrot.input.xml.XmlLocalInputComponent;
import com.dawidweiss.carrot.util.tokenizer.SnippetTokenizerLocalFilterComponent;
import com.stachoodev.carrot.filter.normalizer.SmartCaseNormalizer;
import com.stachoodev.carrot.filter.normalizer.local.CaseNormalizerLocalFilterComponent;

/**
 * A test of the FuzzyAnts clustering component.
 * 
 * @author Dawid Weiss
 */
public class FuzzyAntsLocalFilterComponentTest extends TestCase {
    /** A local process controller instance. */
    private LocalController controller;

    /** XML data stream */
    private InputStream xmlDataStream;

    /** Our process identifier within the controller. */
    private final static String PROCESS_ID = "fuzzyants";

    /**
     * Instantiates the test case.
     * 
     * @param testName Name of the test method to run.
     */
    public FuzzyAntsLocalFilterComponentTest(String testName, InputStream xmlDataStream) {
        super(testName);
        this.xmlDataStream = xmlDataStream;
    }

    /**
     * The setup method instantiates the process "controller". This initialization
     * should be performed only once in the final application - the controller is 
     * thread safe and can be reused in subsequent clustering calls.
     */
    public void setUp() {
        // The controller we initialize.
        final LocalController controller = new LocalControllerBase(); 

        // Now create factories of components integrated in the processing chain.

        // Initialize an input component that takes the input XML file and pushes
        // documents from it to subsequent components.
        final LocalComponentFactory inputFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new XmlLocalInputComponent();
            }
        };
        // Add the factory to the controller, naming it somehow.
        controller.addLocalComponentFactory("input", inputFactory);

        // Add a factory of clustering components.
        final LocalComponentFactory lingo3GFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new FuzzyAntsLocalFilterComponent();
            }
        };
        controller.addLocalComponentFactory("clusterer", lingo3GFactory);

        final LocalComponentFactory languageGuesserFilterFactory = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                // Default language is english.
                return new RawDocumentDummyLanguageDetection("en");
            }
        };
        controller.addLocalComponentFactory(
            "filter.dummy-language-guesser", languageGuesserFilterFactory);

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

        // Add a collector for the output clusters.
        final LocalComponentFactory output = new LocalComponentFactoryBase() {
            public LocalComponent getInstance() {
                return new ClustersConsumerOutputComponent();
            }
        };
        controller.addLocalComponentFactory("output", output);

        //
        // Create processes. Processes bind input, filters and output components together.
        //
        try {
            controller.addProcess(PROCESS_ID,
                    new LocalProcessBase("input", "output", 
                            new String [] {"filter.dummy-language-guesser", "filter.tokenizer", 
                            "filter.case-normalizer", "clusterer"}));
        } catch (Exception e) {
            throw new RuntimeException("Problems when creating a clustering process.", e);
        }

        // Save the controller handle.
        this.controller = controller;
    }

    /**
     * Run the test on the current file.
     */
    public void runTest() {
        // A set of parameters customizing the query (locally).
        final HashMap requestParameters = new HashMap();
        // We configure the input component here.
        requestParameters.put("source", this.xmlDataStream);
        requestParameters.put("xslt", "identity");

        try {
            final String query = "";
            final ProcessingResult result = this.controller.query(PROCESS_ID, query, requestParameters);

            // The processing result contains clusters.
            final ClustersConsumerOutputComponent.Result output =
                (ClustersConsumerOutputComponent.Result) result.getQueryResult();

            final ArrayList flattenedClusters = new ArrayList();
            dump(output.clusters, flattenedClusters);

            // We expect some clusters in the output.
            assertTrue(flattenedClusters.size() > 2);
        } catch (MissingProcessException e) {
            fail("Process not existing?"); // Impossible, but just in case.
        } catch (Exception e) {
            // Some other problem occurred. Unfortunately
            // the exception is quite generic because with multiple
            // clustering components available it isn't possible to
            // predict what they're going to throw.
            throw new RuntimeException("A problem occurred when running clustering.", e);
        }
    }
    
    private void dump(List clusters, ArrayList flattened) {
        if (clusters == null) return;
        for (Iterator i = clusters.iterator(); i.hasNext(); ) {
            final RawCluster rc = (RawCluster) i.next();
            flattened.add(rc);
            dump(rc.getSubclusters(), flattened);
        }
    }

    /**
     * Create a test for each individual cached query.
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();

        final String [] resources = new String [] {
                "data-mining.xml"
        };

        final String methodName = FuzzyAntsLocalFilterComponentTest.class.getName();
        for (int i = 0; i < resources.length; i++) {
            final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resources[i]);
            if (is != null) {
                final TestCase t = new FuzzyAntsLocalFilterComponentTest(methodName, is);
                t.setName(resources[i]);
                suite.addTest(t);
            } else {
                fail("Resource not found: " 
                        + resources[i]);
            }
        }
        suite.setName(methodName);

        return suite;
    }
}