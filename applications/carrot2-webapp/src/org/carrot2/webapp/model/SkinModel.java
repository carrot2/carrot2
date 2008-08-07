package org.carrot2.webapp.model;

import org.simpleframework.xml.Attribute;

/**
 *
 */
public class SkinModel
{
    @Attribute
    public final String id;

    public final RequestType resultsRequestType;

    public final boolean sprited;

    public SkinModel(String id, RequestType resultsRequestType)
    {
        this(id, resultsRequestType, true);
    }

    public SkinModel(String id, RequestType resultsRequestType, boolean sprited)
    {
        this.id = id;
        this.resultsRequestType = resultsRequestType;
        this.sprited = sprited;
    }
}
