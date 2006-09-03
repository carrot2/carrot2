
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

package org.carrot2.apiexample;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.core.linguistic.Language;
import org.carrot2.filter.lingo.local.LingoLocalFilterComponent;
import org.carrot2.input.yahooapi.YahooApiInputComponent;
import org.carrot2.util.tokenizer.languages.english.English;

import sun.awt.ComponentFactory;

/**
 * This is an example of using the Carrot<sup>2</sup> API and components
 * directly from a Java application. The walk-through starts 
 * in the {@link #main(String[])} method (command-line application's 
 * entry point).
 *
 * @author Dawid Weiss
 */
public final class Example {


    /**
     * <h1>Carrot<sup>2</sup> API example</h1>
     * 
     * <h2>Introduction</h2>
     * 
     * <p>Carrot<sup>2</sup> is composed of <b>components</b> bound together
     * in a <b>processing chain</b>. To start clustering search results
     * we will need at least three components:</p>
     * 
     * <ul>
     *  <li><b>input</b> - anything that produces <b>snippets</b> to be 
     *  clustered. Each snippet consists of a unique URL, a title and
     *  a fragment of text from the content of a document. Example input
     *  components available in Carrot<sup>2</sup> provide bridges to
     *  existing major search engines (Yahoo, Google), open source
     *  search engines (Lucene), but also adapt XMLs (such as RSS or OpenSearch).
     *  As a final resort, you can try to write your own input component
     *  using the examples available in the project.</li>
     *   
     *  <li><b>filters</b> - typically a clustering component and a set
     *  of filters that it requires. Carrot<sup>2</sup> comes with a number
     *  of clustering components; each one implements a different algorithm
     *  and has different requirements concerning configuration and previous
     *  filters in the processing chain. You'll need to take a look at the
     *  demo applications (web application and local application) - there
     *  are full scripts configuring each clustering component there. In this
     *  example we will use <b>Lingo</b> clustering component and configure
     *  it directly from the source code.</li>
     *  
     *  <li><b>output</b> - a clustering component typically produces instances
     *  of {@link RawCluster}. The role of an output component is to do something
     *  with clusters once you receive them from the clusterer. The easiest
     *  way is to save them in an array and wait until all the processing is
     *  finished (all the clusters are available). A more advanced application
     *  could use (display?) clusters as soon as they appear from the clustering
     *  component. In this example we will buffer the output clusters in 
     *  an array. 
     *  </li>
     * </ul>
     * 
     * 
     * <h2>Initialization</h2>
     * 
     * <p>The next step after deciding which components to use is to
     * create an instance of a {@link LocalController} which provides
     * the facility for executing queries and assembling a fully
     * functional processing chain. This is explained in {@link #initLocalController()}
     * method.</p>
     * 
     * 
     * <h2>Running queries</h2>
     * 
     * <p>Once you have a fully configured {@link LocalController}, running
     * queries is quite trivial. The only thing you need to do is to invoke
     * {@link LocalController#query(String, String, java.util.Map)} method,
     * passing it a process identifier, a query and a {@link Map} of 
     * request parameters. These parameters can be used by certain components
     * to alter their behaviour or settings <b>for the duration of a single
     * query</b>. Each component will expose a different set of parameters,
     * so you'll need to take a look at source code to see what is available.
     * </p>
     * 
     * <p>Note that an instance of a {@link LocalController} is thread-safe
     * and may (should!) be reused.</p>
     * 
     * <p>The result of running a query is an instance of a {@link ProcessingResult}
     * interface. The actual output depends a lot on the last (output) component
     * in the chain, so you'll need to see what is available. In our case, the
     * result is a buffered array of snippets and clusters of type
     * {@link ArrayOutputComponent.Result}. We can display
     * documents (snippets) and clusters by fetching data from this
     * object. This procedure is shown in 
     * {@link #displayResults(ArrayOutputComponent.Result)} method.</p>
     *
     *
     * <h2>Cleanup</h2>
     * No special cleanup is necessary when you close your application. Just let
     * the garbage collector consume the resources taken by {@link LocalController}.
     * 
     * 
     * <h2>Other things to look at</h2>
     * This example configures the controller in a very "manual", step-by-step way
     * to explain how things in Carrot<sup>2</sup> work. In the Web and local demo
     * applications, however, you'll see that components and processes can be
     * instantiated semi-automatically using Beanshell scripts and component
     * autodiscovery. These are more advanced topics, refer to the source code
     * of demo applications and JUnit test cases for details.
     * 
     * 
     * @param args Command line arguments are not used in this application.
     */
    public static void main(final String [] args) {
        try {
            /*
             * Initialize local controller. Normally you'd run this only once
             * for an entire application (controller is thread safe).
             */
            final LocalController controller = initLocalController();

            /*
             * Once we have a controller we can run queries. Let's try with
             * "data mining" query. The result object is a generic 
             * ProcessingResult from which we acquire the actual result stored by
             * {@link ArrayOutputComponent}. 
             */
            final ProcessingResult pResult = controller.query("yahoo-lingo", "data mining", new HashMap());
            final ArrayOutputComponent.Result result = (ArrayOutputComponent.Result) pResult.getQueryResult();

            /*
             * Once we have the buffered snippets and clusters, we can display
             * them somehow.
             */
            displayResults(result);

        } catch (Exception e) {
            // There shouldn't be any, but just in case.
            System.err.println("An exception occurred: " + e.toString());
        }
    }

    /**
     * <p>In this method we display documents and clusters received from 
     * an output component of type {@link ArrayOutputComponent.Result}. The
     * 
     */
    private static void displayResults(ArrayOutputComponent.Result result) {
        //
        // let's display a list of snippets recieved from the input
        // component first.
        //
        final List documents = result.documents;
        System.out.println("Collected: " + documents.size() + " snippets.");

        int num = 1;
        for (Iterator i = documents.iterator(); i.hasNext(); num++) {
            // Results of almost all input components in Carrot<sup>2</sup>
            // are of type {@link RawDocument}. Cast it.
            final RawDocument document = (RawDocument) i.next();

            final String url = document.getUrl();
            final String title = document.getTitle();
            // we don't display this here
            // final String snippet = document.getSnippet();

            System.out.print(num + ": " + url
                + "\n\t-> " + (title.length() > 70 ? title.substring(0, 70) : title) + "\n\n");
        }
        
        //
        // Now the clusters. Clustering components will return a list of 
        // top-level clusters, instances of {@link RawCluster} interface. These
        // objects may contain both documents ({@link RawDocument}), but also
        // subgroups - again a list of {@link RawCluster} objects. A recursive
        // routine is probably best to show how to traverse a set of clusters
        // easily.
        //
        System.out.print("\n\nClusters:\n");
        final List clusters = result.clusters;
        num = 1;
        for (Iterator i = clusters.iterator(); i.hasNext(); num++) {
            displayCluster(0, "CL-" + num, (RawCluster) i.next());
        }
    }

    /**
     * Shows the content of a single cluster, descending recursively to
     * subclusters.
     * 
     * @param level current nesting level.
     * @param tag prefix for the current nesting level.
     * @param cluster cluster to display.
     */
    private static void displayCluster(final int level, String tag, RawCluster cluster) {
        // Detect and skip "junk" clusters -- clusters that have no meaning.
        // Also note that clusters have properties. Algorithms may pass additional
        // information about clusters this way.
        if (cluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null) {
            return;
        }

        // Get the label of the current cluster. The description of a cluster
        // is a list of strings, ordered according to the accuracy of their
        // relationship with the cluster's content. Typically you'll just
        // show the first few phrases. We'll limit ourselves to just one.
        final List phrases = cluster.getClusterDescription();
        final String label = (String) phrases.get(0);

        // indent up to level and display this cluster's description phrase
        for (int i = 0; i < level; i++) System.out.print("  ");
        System.out.println("\n" + tag + " " + label + " (" + cluster.getDocuments().size() + " documents)");

        // if this cluster has documents, display three topmost documents.
        int count = 1;
        for (Iterator d = cluster.getDocuments().iterator(); d.hasNext() && count <= 3; count++) {
            final RawDocument document = (RawDocument) d.next();

            for (int i = 0; i < level; i++) System.out.print("  ");
            System.out.print("     " + count + ": " + document.getUrl() + "\n");
        }

        // finally, if this cluster has subclusters, descend into recursion.
        int num = 1;
        for (Iterator c = cluster.getSubclusters().iterator(); c.hasNext(); num++) {
            displayCluster(level + 1, tag + "." + num, (RawCluster) c.next());
        }
    }

    /**
     * <p>In this method we collect components (or rather
     * instances of {@link ComponentFactory} interface) and put together
     * a {@link LocalController}. A controller assembles processing
     * chains (components) for the execution of each query, controls the process of
     * its execution and in general is the heart of Carrot<sup>2</sup>
     * infrastructure.</p>
     * 
     * <p>In this example, we will create the following processing chain:</p>
     * <ul>
     *  <li><b>input:</b> we will fetch results from Yahoo search engine,</li>
     *  <li><b>clustering:</b> we will cluster these results using Lingo clustering algorithm,</li>
     *  <li><b>output:</b> we will buffer clusters and snippets in an output
     *  component so that we can display them later.</li>
     * </ul>
     * 
     * <p>For each component, we must create a {@link ComponentFactory} and add it
     * to the controller. Each factory, on the other hand, is identifier with a
     * string identifier. This identifier is reused later when you assemble a {@link LocalProcess}.</p>
     * 
     * <p>A {@link Process} contains all the information about input, filters and output components.
     * processes are also identified with a unique string identifier. Note that one controller
     * may contain <b>more than one</b> process. So, for example, you could create a controller
     * that runs two different processing chains, one querying Yahoo and the other one Google.</p>
     * 
     * <p>Code examples and further comments are in the source code of
     * this method.</p>
     *
     * @return This method returns a fully configured, reusable instance
     *      a {@link LocalController}.
     */
    private static LocalController initLocalController() throws DuplicatedKeyException {
        // Carrot<sup>2</sup> comes with a default implementation of a
        // {@link LocalController}. Create its instance here.
        final LocalController controller = new LocalControllerBase();

        //
        // Create an input component first. We will use Yahoo here and
        // utilize an anonymous class to create instances of this 
        // component.
        //
        final LocalComponentFactory input = new LocalComponentFactory() {
            // Note that {@link YahooApiInputComponent} is configurable
            // and accepts an XML descriptor that, among other things, defines
            // the <b>application-id</b> used for querying the Web service. There
            // is a limit on the number of queries for a single application-id 
            // (and source IP), so you may wish to change that.
            //
            // To show how you can customize the input component, we will
            // restrict searches just to subdomains of Wikipedia.
            public LocalComponent getInstance() {
                final InputStream resource = 
                    this.getClass().getResourceAsStream("yahoo-wikipedia.xml");
                try {
                    return new YahooApiInputComponent(resource);
                } catch (IOException e) {
                    throw new RuntimeException("Input component descriptor not found.");
                } finally {
                    try { resource.close(); } catch (IOException e) {/* ignore */}
                }
            }
        };

        // add the input component factory to a controller, name it
        // "yahoo".
        controller.addLocalComponentFactory("yahoo", input);


        //
        // Now it's time to create filters. We will use Lingo clustering
        // component. At the moment it requires no filters to precede
        // it in the processing chain. Let's create its factory then. 
        //
        final LocalComponentFactory lingo = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                //
                // When creating component instances, we can alter their
                // configuration. Here, we will predefine a list of languages
                // the component will work with and set certain algorithm
                // thresholds.
                //
                // Note that you can tune these thresholds with the WebStart
                // demo applications and see which levels suit your needs.
                //
                final Language [] languages = new Language [] {
                        new English(),
                };
                final HashMap parameters = new HashMap();
                parameters.put("lsi.threshold.clusterAssignment", "0.150");
                parameters.put("lsi.threshold.candidateCluster",  "0.775");

                return new LingoLocalFilterComponent(languages, parameters);
            }
        };

        // add the clustering component as "lingo-classic"
        controller.addLocalComponentFactory("lingo-classic", lingo);

        
        //
        // Finally, create a component that will accept and "buffer" snippets
        // and clusters so that we can process them later. This component
        // is part of Carrot<sup>2</sup> framework.
        //
        final LocalComponentFactory output = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new ArrayOutputComponent();
            }
        };

        // add the output component as "buffer"
        controller.addLocalComponentFactory("buffer", output);

        
        //
        // In the final step, assemble a process from the above components
        // and add it to the controller. Name it "yahoo-lingo". Note how
        // we use component factory identifiers.
        //
        try {
            controller.addProcess("yahoo-lingo", 
                    new LocalProcessBase("yahoo", "buffer", new String [] {"lingo-classic"}));
        } catch (InitializationException e) {
            // This exception is thrown during verification of the added component chain,
            // when a component cannot properly initialize for some reason. We don't 
            // expect it here, so rethrow it as runtime exception.
            throw new RuntimeException(e);
        } catch (MissingComponentException e) {
            // If you give an identifier of a component for which factory has not been
            // added to the controller, you'll get this exception. Impossible in our
            // example.
            throw new RuntimeException(e);
        }

        return controller;
    }
}