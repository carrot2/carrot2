package org.carrot2.webapp.model;

import java.util.List;

import org.simpleframework.xml.ElementList;

public class DocumentSourceModel extends ProcessingComponentModel
{
    @ElementList(required = false, entry = "example")
    public final List<String> examples;

    public DocumentSourceModel(Class<?> componentClass, String id, String label,
        String mnemonic, String title, String description, List<String> examples)
    {
        super(componentClass, id, label, mnemonic, title, description);
        this.examples = examples;
    }
}
