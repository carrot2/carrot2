package org.carrot2.webapp.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * Represents search results view
 */
public class ResultsViewModel extends ModelWithDefault
{
    @Attribute
    public String id;

    @Element
    public String label;
}
