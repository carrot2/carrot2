

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.controller.carrot2.process;


import com.dawidweiss.carrot.controller.carrot2.components.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.*;
import org.apache.log4j.Logger;
import java.util.*;


/**
 * An instance of ProcessinChain, where all component references have been resolved into object
 * instances.
 */
public class ResolvedProcessingChain
    implements ProcessDefinition
{
    private static final Logger log = Logger.getLogger(ResolvedProcessingChain.class);
    private final ProcessingChain processingChain;
    private final ProcessDescriptor process;
    private final ComponentDescriptor input;
    private final ComponentDescriptor output;
    private final ComponentDescriptor [] filters;
    private final ArrayList errors = new ArrayList();
    private final ArrayList warnings = new ArrayList();

    /**
     * Resolves the processing chain binding into a concrete instance.
     *
     * @param componentsLoader ComponentsLoader against which references will be resolved.
     * @param processingChain The XML binding object of a processing chain
     */
    public ResolvedProcessingChain(
        ComponentsLoader componentsLoader,
        com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessDescriptor process
    )
        throws ComponentNotAvailableException
    {
        this.process = process;
        this.processingChain = process.getProcessingChain();

        if (processingChain == null)
        {
            throw new IllegalArgumentException(
                "Process must contain a processing chain definition: " + process.getId()
            );
        }

        this.input = componentsLoader.findComponent(processingChain.getInput().getComponentId());

        if (input == null)
        {
            throw new ComponentNotAvailableException(processingChain.getInput().getComponentId());
        }

        this.output = componentsLoader.findComponent(processingChain.getOutput().getComponentId());

        if (output == null)
        {
            throw new ComponentNotAvailableException(processingChain.getOutput().getComponentId());
        }

        Filter [] filters = processingChain.getFilter();
        this.filters = new ComponentDescriptor[filters.length];

        for (int i = 0; i < filters.length; i++)
        {
            this.filters[i] = componentsLoader.findComponent(filters[i].getComponentId());

            if (this.filters[i] == null)
            {
                throw new ComponentNotAvailableException(filters[i].getComponentId());
            }
        }
    }

    /**
     * Return true if this process utilizes a given component.
     */
    public boolean usesComponent(ComponentDescriptor descriptor)
    {
        switch (descriptor.getType().getType())
        {
            case ComponentType.INPUT_TYPE:
                return this.input.getId().equals(descriptor.getId());

            case ComponentType.OUTPUT_TYPE:
                return this.output.getId().equals(descriptor.getId());

            case ComponentType.FILTER_TYPE:

                for (int i = 0; i < this.filters.length; i++)
                {
                    if (this.filters[i].getId().equals(descriptor.getId()))
                    {
                        return true;
                    }
                }

                return false;

            default:
                throw new RuntimeException("Unknown component type: " + descriptor.getType());
        }
    }


    public ComponentDescriptor getInputComponent()
    {
        return this.input;
    }


    public ComponentDescriptor getOutputComponent()
    {
        return this.output;
    }


    public ComponentDescriptor [] getFilterComponents()
    {
        return this.filters;
    }


    public String getId()
    {
        return this.process.getId();
    }


    public String getDefaultDescription()
    {
        return this.process.getDescription();
    }


    public boolean isScripted()
    {
        return false;
    }
}
