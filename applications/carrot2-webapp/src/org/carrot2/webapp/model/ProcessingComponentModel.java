package org.carrot2.webapp.model;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.ProcessingComponent;
import org.simpleframework.xml.Attribute;

/**
 *
 */
public class ProcessingComponentModel
{
    public final Class<? extends ProcessingComponent> componentClass;

    @Attribute
    public final String id;

    @Attribute
    public final String label;

    @Attribute
    public final String mnemonic;

    @Attribute
    public final String title;

    @Attribute
    public final String description;

    public final Map<String, Object> initAttributes;

    public ProcessingComponentModel(Class<? extends ProcessingComponent> componentClass, String id, String label,
        String mnemonic, String title, String description)
    {
        this(componentClass, id, label, mnemonic, title, description,
            new HashMap<String, Object>());
    }

    public ProcessingComponentModel(Class<? extends ProcessingComponent> componentClass,
        String id, String label, String mnemonic, String title, String description,
        Map<String, Object> initAttributes)
    {
        this.componentClass = componentClass;
        this.id = id;
        this.label = label;
        this.mnemonic = mnemonic;
        this.title = title;
        this.description = description;
        this.initAttributes = initAttributes;
    }

    // attribute sets file to use
    // attribute set id to use
}
