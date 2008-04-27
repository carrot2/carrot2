package org.carrot2.webapp.model;

import org.simpleframework.xml.Attribute;

/**
 *
 */
public class ProcessingComponentModel
{
    public final Class<?> componentClass;

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

    public ProcessingComponentModel(Class<?> componentClass, String id, String label,
        String mnemonic, String title, String description)
    {
        this.componentClass = componentClass;
        this.id = id;
        this.label = label;
        this.mnemonic = mnemonic;
        this.title = title;
        this.description = description;
    }

    // attribute sets file to use
    // attribute set id to use
}
