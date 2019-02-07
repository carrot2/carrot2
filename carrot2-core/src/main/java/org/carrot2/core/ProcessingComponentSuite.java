
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;

/**
 * A set of {@link IProcessingComponent}s used in Carrot2 applications.
 */
public class ProcessingComponentSuite
{
    ArrayList<ProcessingComponentSuiteInclude> includes;

    private ArrayList<DocumentSourceDescriptor> sources;
    private ArrayList<ProcessingComponentDescriptor> algorithms;
    private ArrayList<ProcessingComponentDescriptor> otherComponents;

    public ProcessingComponentSuite(ArrayList<DocumentSourceDescriptor> sources,
                                    ArrayList<ProcessingComponentDescriptor> algorithms)
    {
        this.algorithms = algorithms;
        this.sources = sources;
        this.otherComponents = new ArrayList<>();
    }

    /**
     * Returns the internal list of document sources. Changes to this list will affect the
     * suite.
     */
    public List<DocumentSourceDescriptor> getSources()
    {
        return sources;
    }

    /**
     * Returns the internal list of algorithms. Changes to this list will affect the
     * suite.
     */
    public List<ProcessingComponentDescriptor> getAlgorithms()
    {
        return algorithms;
    }

    /**
     * Return a list of other components (not algorithms, not sources).
     */
    public List<ProcessingComponentDescriptor> getOtherComponents()
    {
        return otherComponents;
    }
    
    /**
     * Returns all components available in this suite, including data sources, algorithms
     * and any other types.
     */
    public List<ProcessingComponentDescriptor> getComponents()
    {
        List<ProcessingComponentDescriptor> out = new ArrayList<>();
        out.addAll(sources);
        out.addAll(algorithms);
        out.addAll(otherComponents);
        return out;
    }

    /**
     * Remove components marked as unavailable from the suite.
     * 
     * @see ProcessingComponentDescriptor#isComponentAvailable()
     */
    public List<ProcessingComponentDescriptor> removeUnavailableComponents()
    {
        List<ProcessingComponentDescriptor> unavailable = Stream.concat(sources.stream(), algorithms.stream())
            .filter(p -> !p.isComponentAvailable())
            .collect(Collectors.toList());

        sources.removeAll(unavailable);
        algorithms.removeAll(unavailable);

        return unavailable;
    }

    /**
     * Returns all processing component configurations available in this suite.
     * 
     * @see Controller#init(Map, ProcessingComponentConfiguration...)
     */
    public ProcessingComponentConfiguration [] getComponentConfigurations()
    {
        final List<ProcessingComponentDescriptor> components = getComponents();
        final ProcessingComponentConfiguration [] result = 
            new ProcessingComponentConfiguration [components.size()];
        int i = 0;
        for (ProcessingComponentDescriptor processingComponentDescriptor : components)
        {
            result[i++] = processingComponentDescriptor.getComponentConfiguration();
        }
        return result;
    }
}
