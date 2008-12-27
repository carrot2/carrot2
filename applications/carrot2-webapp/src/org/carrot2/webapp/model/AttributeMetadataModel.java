package org.carrot2.webapp.model;

import java.util.HashMap;
import java.util.List;

import org.carrot2.util.MapUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.simplexml.SimpleXmlWrapperValue;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.simpleframework.xml.*;

/**
 * Models attributes of a document source
 */
@Root(name = "attribute-metadata")
public class AttributeMetadataModel
{
    @ElementList(entry = "attribute", inline = true, required = false)
    public final List<AttributeDescriptor> descriptors;

    @SuppressWarnings("unused")
    @ElementMap(entry = "init-value", key = "key", attribute = true, inline = true, required = false)
    private HashMap<String, SimpleXmlWrapperValue> attributes;

    public AttributeMetadataModel(RequestModel requestModel)
    {
        this.descriptors = WebappConfig.INSTANCE.sourceAttributeMetadata
            .get(requestModel.source);

        this.attributes = MapUtils.asHashMap(SimpleXmlWrappers
            .wrap(WebappConfig.INSTANCE.sourceInitializationAttributes
                .get(requestModel.source)));
    }
}
