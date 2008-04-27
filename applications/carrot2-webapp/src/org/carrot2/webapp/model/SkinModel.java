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

    public SkinModel(String id, RequestType resultsRequestType)
    {
        this.id = id;
        this.resultsRequestType = resultsRequestType;
    } 
}
