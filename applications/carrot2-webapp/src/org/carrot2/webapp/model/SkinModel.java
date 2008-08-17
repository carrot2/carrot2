package org.carrot2.webapp.model;

import org.simpleframework.xml.Attribute;

/**
 *
 */
public class SkinModel extends ModelWithDefault
{
    @Attribute
    public String id;

    public final RequestType resultsRequestType = RequestType.PAGE;

    @Attribute
    public boolean sprited;
}
