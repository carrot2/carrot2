package org.carrot2.core.parameters;

import java.util.*;

/**
 * A group of configuration parameters and parameter groups. Note that both parameters and
 * sets are kept in {@link Collection}s, hence no order is imposed at this level. Order
 * starts to matter at the level of displaying the corresponding controls or
 * documentation, so the order will be defined by the XML file.
 */
public class ParameterGroup
{
    /** Name of this parameter group */
    private final String name;

    /** A set of parameter subgroups */
    private Collection<ParameterGroup> parameterGroups;

    /** A set of parameters */
    private Collection<Parameter> parameters;

    public ParameterGroup(String name)
    {
        this.name = name;
        this.parameters = new HashSet<Parameter>();
        this.parameterGroups = new HashSet<ParameterGroup>();
    }

    public void add(Parameter... parametersToAdd)
    {
        parameters.addAll(Arrays.asList(parametersToAdd));
    }

    public void add(ParameterGroup... parameterGroupsToAdd)
    {
        parameterGroups.addAll(Arrays.asList(parameterGroupsToAdd));
    }

    public String getName()
    {
        return name;
    }

    public Collection<Parameter> getParameters()
    {
        return Collections.unmodifiableCollection(parameters);
    }

    public Collection<ParameterGroup> getParameterGroups()
    {
        return Collections.unmodifiableCollection(parameterGroups);
    }

    @Override
    public String toString()
    {
        return "[ParamGroup name=" + name + ", params: "
            + Arrays.toString(parameters.toArray()) + ", subgroups: "
            + Arrays.toString(parameterGroups.toArray()) + "]";
    }
}
