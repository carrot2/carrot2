package org.carrot2.webapp.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * Represents search results view
 */
public class ResultsViewModel
{
    @Attribute
    public final String id;

    @Element
    public final String label;

    public ResultsViewModel(String id, String label)
    {
        this.id = id;
        this.label = label;
    }
}
