/**
 * 
 */
package org.carrot2.core;

import java.util.Collections;
import java.util.Map;

import org.carrot2.core.parameters.BindingPolicy;
import org.carrot2.core.parameters.ParameterBinder;

/**
 *
 */
public abstract class ProcessingComponentTest<T extends ProcessingComponent>
{
    public abstract Class<? extends ClusteringAlgorithm> getProcessingComponentClass();

    public Map<String, Object> getInstanceParameters()
    {
        return Collections.<String, Object> emptyMap();
    }

    /**
     * Creates and initializes an instance of the clustering algorighm.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public T createInstance()
    {
        try
        {
            final ClusteringAlgorithm instance = ParameterBinder.createInstance(
                getProcessingComponentClass(), getInstanceParameters());

            instance.init();

            return (T) instance;
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes an instance of a clustering algorithm.
     * 
     * @param instance
     * @return
     */
    @SuppressWarnings("unchecked")
    public T initInstance(ClusteringAlgorithm instance)
    {
        try
        {
            ParameterBinder.bind(instance, getInstanceParameters(),
                BindingPolicy.INSTANTIATION);
            instance.init();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        return (T) instance;
    }

}
