package org.carrot2.core;

import org.carrot2.core.parameters.ParameterGroup;

public interface Configurable
{
    /**
     * @return Returns configuration parameters of this configurable component.
     */
    public ParameterGroup getParameters();
}
