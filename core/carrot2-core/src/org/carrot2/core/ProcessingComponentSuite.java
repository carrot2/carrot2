/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.io.*;
import java.util.*;

import org.carrot2.core.ProcessingComponentDescriptor.Position;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.*;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persister;

import com.google.common.base.Predicate;
import com.google.common.collect.*;

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

    public ProcessingComponentSuite()
    {
    }

    public ProcessingComponentSuite(ArrayList<DocumentSourceDescriptor> sources,
        ArrayList<ProcessingComponentDescriptor> algorithms)
    {
        this.algorithms = algorithms;
        this.sources = sources;
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
     * Returns all components available in this suite, including data sources, algorithms
     * and any other types.
     */
    public List<ProcessingComponentDescriptor> getComponents()
    {
        return Lists.newArrayList(Iterables.concat(sources, algorithms));
    }

    /**
     * Replace missing attributes with empty lists.
     */
    @Commit
    @SuppressWarnings("unused")
    private void postDeserialize() throws Exception
    {
        if (sources == null)
        {
            sources = Lists.newArrayList();
        }
        if (algorithms == null)
        {
            algorithms = Lists.newArrayList();
        }
        if (includes == null)
        {
            includes = Lists.newArrayList();
        }

        final ArrayList<DocumentSourceDescriptor> mergedSources = Lists.newArrayList();
        final ArrayList<ProcessingComponentDescriptor> mergedAlgorithms = Lists
            .newArrayList();

        // Load included suites. Currently, we don't check for cycles.
        final List<ProcessingComponentSuite> suites = Lists.newArrayList();
        final ResourceUtils ru = ResourceUtilsFactory.getDefaultResourceUtils();
        for (ProcessingComponentSuiteInclude include : includes)
        {
            final IResource resource = ru.getFirst(include.suite);
            if (resource == null)
            {
                throw new Exception("Could not locate resource: " + include.suite);
            }
            suites.add(deserialize(resource, false));
        }

        // Merge sources
        mergedSources.addAll(Collections2.filter(sources, PositionPredicate.BEGINNING));
        for (ProcessingComponentSuite suite : suites)
        {
            mergedSources.addAll(Collections2.filter(suite.getSources(),
                PositionPredicate.BEGINNING));
        }
        mergedSources.addAll(Collections2.filter(sources, PositionPredicate.MIDDLE));
        for (ProcessingComponentSuite suite : suites)
        {
            mergedSources.addAll(Collections2.filter(suite.getSources(),
                PositionPredicate.MIDDLE));
        }
        for (ProcessingComponentSuite suite : suites)
        {
            mergedSources.addAll(Collections2.filter(suite.getSources(),
                PositionPredicate.END));
        }
        mergedSources.addAll(Collections2.filter(sources, PositionPredicate.END));

        // Merge algorithms
        mergedAlgorithms.addAll(Collections2.filter(algorithms,
            PositionPredicate.BEGINNING));
        for (ProcessingComponentSuite suite : suites)
        {
            mergedAlgorithms.addAll(Collections2.filter(suite.getAlgorithms(),
                PositionPredicate.BEGINNING));
        }
        mergedAlgorithms
            .addAll(Collections2.filter(algorithms, PositionPredicate.MIDDLE));
        for (ProcessingComponentSuite suite : suites)
        {
            mergedAlgorithms.addAll(Collections2.filter(suite.getAlgorithms(),
                PositionPredicate.MIDDLE));
        }
        for (ProcessingComponentSuite suite : suites)
        {
            mergedAlgorithms.addAll(Collections2.filter(suite.getAlgorithms(),
                PositionPredicate.END));
        }
        mergedAlgorithms.addAll(Collections2.filter(algorithms, PositionPredicate.END));

        sources = mergedSources;
        algorithms = mergedAlgorithms;
    }

    /**
     * A predicate for filtering {@link ProcessingComponentDescriptor}s by
     * {@link Position}.
     */
    private static class PositionPredicate implements
        Predicate<ProcessingComponentDescriptor>
    {
        public static final PositionPredicate BEGINNING = new PositionPredicate(
            Position.BEGINNING);
        public static final PositionPredicate MIDDLE = new PositionPredicate(
            Position.MIDDLE);
        public static final PositionPredicate END = new PositionPredicate(Position.END);

        private final Position requiredPosition;

        private PositionPredicate(Position requiredPosition)
        {
            this.requiredPosition = requiredPosition;
        }

        public boolean apply(ProcessingComponentDescriptor descriptor)
        {
            return descriptor.position.equals(requiredPosition);
        }
    }

    /**
     * Deserializes component suite information from an XML stream.
     */
    public static ProcessingComponentSuite deserialize(IResource resource)
        throws Exception
    {
        return deserialize(resource, true);
    }

    /**
     * Deserializes component suite information from an XML stream.
     */
    public static ProcessingComponentSuite deserialize(InputStream inputStream)
        throws Exception
    {
        return deserialize(inputStream, true);
    }

    /**
     * Deserializes component suite information from an XML stream and optionally clears
     * the internal implementation information that should not be exposed to the caller.
     */
    private static ProcessingComponentSuite deserialize(IResource resource,
        boolean clearInternals) throws Exception
    {
        if (resource == null) throw new IOException("Resource not found.");

        final InputStream inputStream = resource.open();
        try
        {
            return deserialize(inputStream, clearInternals);
        }
        finally
        {
            CloseableUtils.close(inputStream);
        }
    }

    /**
     * Deserializes component suite information from an XML stream and optionally clears
     * the internal implementation information that should not be exposed to the caller.
     * The provided {@link InputStream} will not be closed.
     */
    private static ProcessingComponentSuite deserialize(final InputStream inputStream,
        boolean clearInternals) throws Exception
    {
        final ProcessingComponentSuite suite = new Persister().read(
            ProcessingComponentSuite.class, inputStream);
        if (clearInternals)
        {
            suite.includes = null;
            for (ProcessingComponentDescriptor processingComponentDescriptor : suite.algorithms)
            {
                processingComponentDescriptor.position = null;
            }
            for (DocumentSourceDescriptor documentSourceDescriptor : suite.sources)
            {
                documentSourceDescriptor.position = null;
            }
        }
        return suite;
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
        final ProcessingComponentConfiguration [] result = new ProcessingComponentConfiguration [components
            .size()];
        int i = 0;
        for (ProcessingComponentDescriptor processingComponentDescriptor : components)
        {
            result[i++] = processingComponentDescriptor.getComponentConfiguration();
        }
        return result;
    }
}
