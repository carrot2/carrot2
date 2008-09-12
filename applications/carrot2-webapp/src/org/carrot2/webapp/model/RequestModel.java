package org.carrot2.webapp.model;

import java.util.List;
import java.util.Map;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.*;
import org.carrot2.util.simplexml.TypeStringValuePair;
import org.carrot2.webapp.QueryProcessorServlet;
import org.simpleframework.xml.ElementList;

/**
 * Represents the data the application received in the HTTP request.
 */
@Bindable
public class RequestModel
{
    @Input
    @Attribute(key = WebappConfig.SKIN_PARAM)
    @org.simpleframework.xml.Attribute
    public String skin;

    @Input
    @Attribute(key = AttributeNames.QUERY)
    @org.simpleframework.xml.Attribute(required = false)
    public String query = "";

    /**
     * Note that this is the number of results user requested, the actual number may be
     * different, in particular 0.
     */
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    @org.simpleframework.xml.Attribute
    public int results;

    @Input
    @Attribute(key = WebappConfig.SOURCE_PARAM)
    @org.simpleframework.xml.Attribute
    public String source = WebappConfig.INSTANCE.components.getSources().get(0).getId();

    @Input
    @Attribute(key = WebappConfig.ALGORITHM_PARAM)
    @org.simpleframework.xml.Attribute
    public String algorithm = WebappConfig.INSTANCE.components.getAlgorithms().get(0)
        .getId();

    @Input
    @Attribute(key = WebappConfig.TYPE_PARAM)
    public RequestType type;

    @Input
    @Attribute(key = WebappConfig.VIEW_PARAM)
    @org.simpleframework.xml.Attribute
    public String view;

    @Input
    @Attribute(key = QueryProcessorServlet.STATS_KEY)
    public String statsKey;

    @org.simpleframework.xml.Attribute
    public boolean modern = true;

    public Map<String, Object> otherParameters;

    @SuppressWarnings("unused")
    @ElementList(entry = "parameter", inline = true, required = false)
    private List<TypeStringValuePair> otherParametersToSerialize;

    public void afterParametersBound(Map<String, Object> remainingHttpParameters)
    {
        if (type == null)
        {
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
