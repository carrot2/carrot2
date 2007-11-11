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
        final MetadataCollectorContainer container = new MetadataCollectorContainer();
        container.changeMonitor(new MyComponentMonitor());

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
        // let's assume this was read-in from somewhere

        final ClusteringAlgorithm algorithm = container.getComponent(algorithmClass);

        System.out.println(algorithm);
    }
}
