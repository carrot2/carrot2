package org.carrot2.core;

import java.util.List;

import org.simpleframework.xml.ElementList;

/**
 * Descriptor of a {@link DocumentSource} being part of a {@link ProcessingComponentSuite}.
 */
public class DocumentSourceDescriptor extends ProcessingComponentDescriptor
{
    @ElementList(name = "example-queries", entry = "example-query", required = false)
    private List<String> exampleQueries;

    DocumentSourceDescriptor()
    {
    }

    public List<String> getExampleQueries()
    {
        return exampleQueries;
    }
}
