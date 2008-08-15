package org.carrot2.webapp.model;

import java.util.Map;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.*;
import org.carrot2.util.simplexml.TypeStringValuePair;
import org.carrot2.webapp.attribute.Request;
import org.simpleframework.xml.ElementMap;

/**
 * Represents the data the application received in the HTTP request.
 */
@Bindable
public class RequestModel
{
    @Request
    @Input
    @Attribute(key = WebappConfig.SKIN_PARAM)
    @org.simpleframework.xml.Attribute
    public String skin = "fancy-compact";

    @Request
    @Input
    @Attribute(key = AttributeNames.QUERY)
    @org.simpleframework.xml.Attribute(required = false)
    public String query = "";

    /**
     * Note that this is the number of results user requested, the actual number may be
     * different, in particular 0.
     */
    @Request
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    @org.simpleframework.xml.Attribute
    public int results = 100;

    @Request
    @Input
    @Attribute(key = WebappConfig.SOURCE_PARAM)
    @org.simpleframework.xml.Attribute
    public String source = WebappConfig.INSTANCE.components.getSources().get(0).getId();

    @Request
    @Input
    @Attribute(key = WebappConfig.ALGORITHM_PARAM)
    @org.simpleframework.xml.Attribute
    public String algorithm = WebappConfig.INSTANCE.components.getAlgorithms().get(0).getId();

    @Request
    @Input
    @Attribute(key = WebappConfig.TYPE_PARAM)
    public RequestType type;
    
    @Request
    @Input
    @Attribute(key = WebappConfig.VIEW_PARAM)
    @org.simpleframework.xml.Attribute
    public String view = WebappConfig.INSTANCE.views.get(0).id;

    @Request
    @Input
    @Attribute(key = "modern")
    @org.simpleframework.xml.Attribute
    public boolean modern = true;
    
    public Map<String, Object> otherParameters;

    @SuppressWarnings("unused")
    @ElementMap(name = "parameters", entry = "parameter", key = "name", value="value", inline = true, attribute = true, required = false)
    private Map<String, TypeStringValuePair> otherParametersToSerialize;

    public void afterParametersBound(Map<String, Object> remainingHttpParameters)
    {
        if (type == null)
        {
            // TODO: determine based on skin
            type = RequestType.PAGE;
        }

        if (!modern) 
        {
            skin = "simple";
        }
        
        otherParameters = remainingHttpParameters;
        otherParametersToSerialize = TypeStringValuePair
            .toTypeStringValuePairs(remainingHttpParameters);
    }
}
