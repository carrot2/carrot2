package org.carrot2.webapp.model;

import org.simpleframework.xml.Attribute;

/**
 * Represents requested number of results.
 */
public class ResultsSizeModel extends ModelWithDefault
{
    @Attribute
    public Integer size;
}
