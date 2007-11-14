package org.carrot2.core;

import org.carrot2.core.parameters.ParameterGroup;

/**
 * TODO: Each configurable element should be able to declare any attributes it would like
 * to put in the global attributes {@link ProcessingResult#getAttributes()}. I can see
 * two solutions here:
 * <ul>
 * <li>Add a relevant method here (which will make the original name of the interface
 * less appropriate)</li>
 * <li>Create a new interface and put the method there, but what should be the name of
 * the interface?</li>
 * </ul>
 */
public interface Configurable
{
    /**
     * @return Returns configuration parameters of this configurable component.
     */
    public ParameterGroup getParameters();
}
