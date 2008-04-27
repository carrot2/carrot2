package org.carrot2.webapp.model;


import org.apache.commons.lang.StringUtils;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.*;
import org.carrot2.webapp.attribute.Request;

/**
 * Represents the data the application received in the HTTP request.
 */
@Bindable
public class RequestModel
{
    @Request
    @Input
    @Attribute(key = "skin")
    public String skin = "fancy-large";

    @Request
    @Input
    @Attribute(key = AttributeNames.QUERY)
    @org.simpleframework.xml.Attribute(required = false)
    public String query;

    /**
     * Note that this is the number of results user requested, the actual number may be
     * different, in particular 0.
     */
    @Request
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    @org.simpleframework.xml.Attribute
    public int results = 50;

    @Request
    @Input
    @Attribute(key = WebappConfig.SOURCE_PARAM)
    @org.simpleframework.xml.Attribute
    public String source = "yahoo";

    @Request
    @Input
    @Attribute(key = WebappConfig.ALGORITHM_PARAM)
    @org.simpleframework.xml.Attribute
    public String algorithm = "stc";

    @Request
    @Input
    @Attribute(key = WebappConfig.TYPE_PARAM)
    public RequestType type;

    public void afterParametersBound()
    {
        if (type == null)
        {
            if (StringUtils.isNotBlank(query))
            {
                // TODO: determine based on skin
                type = RequestType.PAGE;
            }
            else
            {
                type = RequestType.PAGE;
            }
        }
    }
}
