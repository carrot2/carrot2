package org.carrot2.core;

import java.io.InputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import org.carrot2.core.ProcessingComponentDescriptor.Position;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.Resource;
import org.carrot2.util.resource.ResourceUtilsFactory;
import org.carrot2.util.simplexml.NoClassAttributePersistenceStrategy;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Persister;

import com.google.common.base.Predicate;
import com.google.common.collect.*;

/**
 * A set of {@link ProcessingComponent}s used in Carrot2 applications.
 */
@Root(name = "component-suite")
public class ProcessingComponentSuite
{
    @ElementList(inline = true, required = false, entry = "include")
    List<ProcessingComponentSuiteInclude> includes;

    @ElementList(name = "sources", entry = "source", required = false)
    private List<DocumentSourceDescriptor> sources;

    @ElementList(name = "algorithms", entry = "algorithm", required = false)
    private List<ProcessingComponentDescriptor> algorithms;

    public ProcessingComponentSuite()
    {
    }

    public ProcessingComponentSuite(List<DocumentSourceDescriptor> sources,
        List<ProcessingComponentDescriptor> algorithms)
    {
        this.algorithms = algorithms;
        this.sources = sources;
    }

    public List<DocumentSourceDescriptor> getSources()
    {
        return sources;
    }

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
            sources = Collections.emptyList();
        }
        if (algorithms == null)
        {
            algorithms = Collections.emptyList();
        }
        if (includes == null)
        {
            includes = Collections.emptyList();
        }

        final List<DocumentSourceDescriptor> mergedSources = Lists.newArrayList();
        final List<ProcessingComponentDescriptor> mergedAlgorithms = Lists.newArrayList();

        // Load included suites. Currently, we don't check for cycles.
        final List<ProcessingComponentSuite> suites = Lists.newArrayList();
        for (ProcessingComponentSuiteInclude include : includes)
        {
            suites.add(deserialize(ResourceUtilsFactory.getDefaultResourceUtils()
                .getFirst(include.suite), false));
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
    public static ProcessingComponentSuite deserialize(Resource resource)
        throws Exception
    {
        return deserialize(resource, true);
    }

    /**
     * Deserializes component suite information from an XML stream and optionally clears
     * the internal implementation information that should not be exposed to the caller.
     */
    private static ProcessingComponentSuite deserialize(Resource resource,
        boolean clearInternals) throws Exception
    {
        final InputStream inputStream = resource.open();
        try
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
        finally
        {
            CloseableUtils.close(inputStream);
        }
    }

    /**
     * Serializes this component suite as XML to the provided writer.
     */
    public void serialize(Writer writer) throws Exception
    {
        new Persister(NoClassAttributePersistenceStrategy.INSTANCE).write(this, writer);
    }
}
