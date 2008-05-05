package org.carrot2.core;

import java.util.List;

import org.simpleframework.xml.ElementList;

/**
 * Descriptor of a {@link DocumentSource} being part of a {@link ProcessingComponentSuite}.
 */
public class DocumentSourceDescriptor extends ProcessingComponentDescriptor
{
    @ElementList(name = "example-queries", entry = "example-query")
    private List<String> exampleQueries;

    public DocumentSourceDescriptor()
    {
    }

    public DocumentSourceDescriptor(String id,
        Class<? extends ProcessingComponent> componentClass,
        String attributeSetsFileName, String label, String title, String description,
        String mnemonic, List<String> exampleQueries)
    {
        super(id, componentClass, attributeSetsFileName, null, label, title, description,
            mnemonic);
        this.exampleQueries = exampleQueries;
    }

    public List<String> getExampleQueries()
    {
        return exampleQueries;
    }
}
