
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.simplexml.PersisterHelpers;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.TreeStrategy;

import org.carrot2.shaded.guava.common.collect.Iterables;
import org.carrot2.shaded.guava.common.collect.Iterators;
import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * A set of {@link IProcessingComponent}s used in Carrot2 applications.
 */
@Root(name = "component-suite")
public class ProcessingComponentSuite
{
    @ElementList(inline = true, required = false, entry = "include")
    ArrayList<ProcessingComponentSuiteInclude> includes;

    @ElementList(name = "sources", entry = "source", required = false)
    private ArrayList<DocumentSourceDescriptor> sources;

    @ElementList(name = "algorithms", entry = "algorithm", required = false)
    private ArrayList<ProcessingComponentDescriptor> algorithms;

    @ElementList(name = "components", entry = "component", required = false)
    private ArrayList<ProcessingComponentDescriptor> otherComponents;

    public ProcessingComponentSuite()
    {
    }

    public ProcessingComponentSuite(ArrayList<DocumentSourceDescriptor> sources,
                                    ArrayList<ProcessingComponentDescriptor> algorithms)
    {
        this.algorithms = algorithms;
        this.sources = sources;
        this.otherComponents = Lists.newArrayList();
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
        return Lists.newArrayList(Iterables.concat(sources, algorithms, otherComponents));
    }

    /**
     * Replace missing attributes with empty lists.
     */
    @Commit
    private void postDeserialize(Map<Object, Object> session) throws Exception
    {
        if (sources == null) sources = new ArrayList<>();
        if (algorithms == null) algorithms = new ArrayList<>();
        if (includes == null) includes = new ArrayList<>();
        if (otherComponents == null) otherComponents = new ArrayList<>();

        // Acquire contextual resource lookup from the session.
        final ResourceLookup resourceLookup = PersisterHelpers.getResourceLookup(session);

        // Load included suites. Currently, we don't check for cycles.
        final List<ProcessingComponentSuite> suites = Lists.newArrayList();

        for (ProcessingComponentSuiteInclude include : includes)
        {
            final IResource resource = resourceLookup.getFirst(include.suite);
            if (resource == null)
            {
                throw new Exception("Could not locate resource: " + include.suite);
            }
            suites.add(deserialize(resource, resourceLookup));
        }

        // Merge sources
        for (ProcessingComponentSuite suite : suites)
        {
            sources.addAll(suite.getSources());
            algorithms.addAll(suite.getAlgorithms());
            otherComponents.addAll(suite.getOtherComponents());
        }
    }

    /**
     * Deserializes component suite information from an XML stream.
     * 
     * @param resource The resource to be deserialized (must not be null).
     * @param resourceLookup Resource lookup utilities for potential included resources. 
     */
    public static ProcessingComponentSuite deserialize(IResource resource,
                                                        ResourceLookup resourceLookup) 
        throws Exception
    {
        if (resource == null)
        {
            throw new IOException("Resource must not be null.");
        }

        final InputStream inputStream = resource.open();
        try
        {
            if (inputStream == null)
            {
                throw new IOException("Input stream must not be null.");
            }
            
            final Persister persister = PersisterHelpers.createPersister(
                resourceLookup, new TreeStrategy());
            final ProcessingComponentSuite suite = persister.read(ProcessingComponentSuite.class, inputStream);
            
            // Clear internals related do deserialization
            suite.includes = null;
            return suite;
        }
        finally
        {
            CloseableUtils.close(inputStream);
        }
    }

    /**
     * Serializes this component suite as an UTF-8 encoded XML.
     */
    public void serialize(OutputStream stream) throws Exception
    {
        new Persister().write(this, stream);
    }

    /**
     * Remove components marked as unavailable from the suite.
     * 
     * @see ProcessingComponentDescriptor#isComponentAvailable()
     */
    public List<ProcessingComponentDescriptor> removeUnavailableComponents()
    {
        ArrayList<ProcessingComponentDescriptor> failed = Lists.newArrayList();
        ProcessingComponentDescriptor p;
        for (Iterator<? extends ProcessingComponentDescriptor> i = Iterators.concat(
            sources.iterator(), algorithms.iterator()); i.hasNext();)
        {
            p = i.next();
            if (!p.isComponentAvailable())
            {
                failed.add(p);
                i.remove();
            }
        }

        return failed;
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
