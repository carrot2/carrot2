package com.dawidweiss.carrot.util.net.http;


import java.util.Map;


/**
 * This class holds all information about a parameter.
 */
public class Parameter
{
    private String  name;
    private Object  value;
    private boolean mapped;


    /**
     * Constructs a parameter.
     */
    public Parameter( String name, Object value, boolean mapped )
    {
        this.name   = name;
        this.value  = value;
        this.mapped = mapped;
    }


    /**
     * Returns an value associated with a parameter, referring to an external mapping
     * if the parameter is mapped.
     */
    public Object getValue(Map mappings)
    {
        if (isMapped())
            return mappings.get( value );
        else
            return value;
    }

    // accessors

    public boolean isMapped() { return mapped; }
    public String  getName()   { return name; }
    
    public String toString() {
        return getName() + " -> " + (isMapped() ? "{" + value + "}" : value ); 
    }
}
