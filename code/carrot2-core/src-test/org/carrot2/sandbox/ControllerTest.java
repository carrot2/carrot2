package org.carrot2.sandbox;

import java.util.logging.Logger;

import org.carrot2.core.ClusteringAlgorithm;
import org.carrot2.core.parameters.ParameterGroup;
import org.junit.Test;

/**
 * 
 */
public class ControllerTest
{
    private final static Logger logger = Logger.getAnonymousLogger();

    @Test
    public void testLifecycleWithParameters() {
        final RequestLifecycleStrategy s = new RequestLifecycleStrategy();
        final MetadataCollectorContainer container = 
            new MetadataCollectorContainer(s);

        // We want to get a concrete instance of this class.
        final Class<ExampleClusteringAlgorithm> algorithmClass = 
            ExampleClusteringAlgorithm.class;

        // This is how we could "resolve" declared
        // instantiation-time component dependencies.
        for (ParameterGroup pg : container.resolveInstantiationParameters(algorithmClass))
        {
            logger.info(pg.toString());
        }

        // Then we can use these 'resolved' type descriptors to select
        // and instantiate proper classes, populating the container
        // with their implementations. I guess the parameters
        // could be serialized to an XML file. For the sake of this example,
        // let's assume this was read-in from somewhere and added
        // proper implementations.
        container.addComponent(algorithmClass);
        container.addComponent(ExampleTokenizer.class);

        final ClusteringAlgorithm algorithm =
            (ClusteringAlgorithm) container.getComponent(algorithmClass);

        //
        // Request-time injection of parameters.
        //
        
        // For some reason this doesn't seem to work -- these calls
        // are not propagated properly to the lifecycle object. A look
        // at the implementation reveals some scary gorey details and I'm
        // too tired to investigate now.

        container.start();
        container.stop();
        
        // and again (another request).
        container.start();
        container.stop();
    }
}
