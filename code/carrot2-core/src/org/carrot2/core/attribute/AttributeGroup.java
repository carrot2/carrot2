package org.carrot2.core.attribute;

import java.util.*;

/**
 * A group of configuration parameters and parameter groups. Note that both parameters and
 * sets are kept in {@link Collection}s, hence no order is imposed at this level. Order
 * starts to matter at the level of displaying the corresponding controls or
 * documentation, so the order will be defined by the XML file.
 * 
 * T0DO: It would be sensible to preserve the order of parameters though. If this information is important
 * at runtime (and it is), then we should use a generic List interface instead of Collection. It doesn't
 * change much, really -- the methods are nearly all the same -- and List implies an ordering of elements.  
 */
public class AttributeGroup
{
    /** Name of this parameter group */
    private final String name;

    /** A set of parameter subgroups */
    private Collection<AttributeGroup> attributeGroups;

    /** A set of parameters */
    private Collection<AttributeDescriptor> parameters;

    public AttributeGroup(String name)
    {
        this.name = name;
        this.parameters = new HashSet<AttributeDescriptor>();
        this.attributeGroups = new HashSet<AttributeGroup>();
    }

    public void add(AttributeDescriptor... parametersToAdd)
    {
        parameters.addAll(Arrays.asList(parametersToAdd));
    }

    public void add(Collection<AttributeDescriptor> parametersToAdd)
    {
        parameters.addAll(parametersToAdd);
    }
    
    public void add(AttributeGroup... parameterGroupsToAdd)
    {
        attributeGroups.addAll(Arrays.asList(parameterGroupsToAdd));
    }

    public String getName()
    {
        return name;
    }

    public Collection<AttributeDescriptor> getParameters()
    {
        return Collections.unmodifiableCollection(parameters);
    }

    public Collection<AttributeGroup> getParameterGroups()
    {
        return Collections.unmodifiableCollection(attributeGroups);
    }

    @Override
    public String toString()
    {
        return "[ParamGroup name=" + name + ", params: "
            + Arrays.toString(parameters.toArray()) + ", subgroups: "
            + Arrays.toString(attributeGroups.toArray()) + "]";
    }
}
