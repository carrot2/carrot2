package org.carrot2.core;

import java.io.InputStream;
import java.io.Writer;
import java.util.List;

import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.*;
import org.carrot2.util.simplexml.NoClassAttributePersistenceStrategy;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Persister;

/**
 * A set of {@link ProcessingComponent}s used in Carrot2 applications.
 */
@Root(name = "component-suite")
public class ProcessingComponentSuite
{
    @ElementList(name = "sources", entry = "source")
    private List<DocumentSourceDescriptor> sources;

    @ElementList(name = "algorithms", entry = "algorithm")
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
     *
     */
    public static ProcessingComponentSuite deserialize(Resource resource)
        throws Exception
    {
        final InputStream inputStream = resource.open();
        final ProcessingComponentSuite loaded;
        try
        {
            loaded = new Persister().read(ProcessingComponentSuite.class, inputStream);
        }
        finally
        {
            CloseableUtils.close(inputStream);
        }

        return loaded;
    }

    /**
     *
     */
    public static void serialize(ProcessingComponentSuite suite, Writer writer)
        throws Exception
    {
        new Persister(NoClassAttributePersistenceStrategy.INSTANCE).write(suite, writer);
    }
}
