package org.carrot2.webapp.model;

import java.util.*;

import org.carrot2.core.DocumentSource;
import org.simpleframework.xml.ElementList;

public class DocumentSourceModel extends ProcessingComponentModel
{
    @ElementList(required = false, entry = "example")
    public final List<String> examples;

    public DocumentSourceModel(Class<? extends DocumentSource> componentClass, String id,
        String label, String mnemonic, String title, String description,
        List<String> examples)
    {
        this(componentClass, id, label, mnemonic, title, description,
            new HashMap<String, Object>(), examples);
    }

    public DocumentSourceModel(Class<? extends DocumentSource> componentClass, String id,
        String label, String mnemonic, String title, String description,
        Map<String, Object> initAttributes, List<String> examples)
    {
        super(componentClass, id, label, mnemonic, title, description, initAttributes);
        this.examples = examples;
    }
}
