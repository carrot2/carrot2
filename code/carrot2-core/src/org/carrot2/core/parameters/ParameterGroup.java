package org.carrot2.core.parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ParameterGroup
{
    private final String name;
    private ArrayList<ParameterGroup> subgroups = new ArrayList<ParameterGroup>();
    private ArrayList<Parameter> parameters = new ArrayList<Parameter>();
    
    public ParameterGroup(String name)
    {
        this.name = name;
    }

    public void addAll(Parameter... parameters)
    {
        Collections.addAll(this.parameters, parameters);
    }

    public void add(ParameterGroup parameters)
    {
        subgroups.add(parameters);
    }

    public List<Parameter> getParameters()
    {
        return parameters;
    }
    
    @Override
    public String toString()
    {
        return "[ParamGroup name=" + name 
        + ", params: " + Arrays.toString(parameters.toArray())
        + ", subgroups: " + Arrays.toString(subgroups.toArray())
        + "]";
    }
}
