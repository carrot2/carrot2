
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.model;

import java.util.*;

import org.carrot2.util.MapUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.simplexml.SimpleXmlWrapperValue;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.simpleframework.xml.*;

import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Models attributes of a document source
 */
@Root(name = "attributes-metadata")
public class AttributeMetadataModel
{
    @ElementMap(entry = "attribute-descriptors", key = "source", value = "attribute-descriptors", attribute = true, inline = true)
    public HashMap<String, AttributeDescriptors> descriptors;

    @ElementMap(entry = "init-values", key = "source", value = "init-values", attribute = true, inline = true)
    public HashMap<String, AttributeInitValues> attributes;

    public AttributeMetadataModel(WebappConfig config)
    {
        attributes = Maps.newHashMap();
        for (Map.Entry<String, Map<String, Object>> entry : 
            config.sourceInitializationAttributes.entrySet())
        {
            attributes.put(entry.getKey(), new AttributeInitValues(MapUtils
                .asHashMap(SimpleXmlWrappers.wrap(entry.getValue()))));
        }

        descriptors = Maps.newHashMap();
        for (Map.Entry<String, List<AttributeDescriptor>> entry : 
            config.sourceAttributeMetadata.entrySet())
        {
            descriptors.put(entry.getKey(), new AttributeDescriptors(entry.getValue()));
        }
    }

    /**
     * We need this class as a workaround for SimpleXML limitation when serializing
     * multiply nested generic types.
     */
    private static class AttributeDescriptors
    {
        @ElementList(name = "attributes", inline = true)
        final List<AttributeDescriptor> descriptors;

        private AttributeDescriptors(List<AttributeDescriptor> descriptors)
        {
            this.descriptors = descriptors;
        }
    }

    /**
     * We need this class as a workaround for SimpleXML limitation when serializing
     * multiply nested generic types.
     */
    private static class AttributeInitValues
    {
        @ElementMap(name = "init-value", key = "key", entry="init-value",  attribute = true, inline = true)
        final HashMap<String, SimpleXmlWrapperValue> initValues;

        private AttributeInitValues(HashMap<String, SimpleXmlWrapperValue> initValues)
        {
            this.initValues = initValues;
        }
    }
}
