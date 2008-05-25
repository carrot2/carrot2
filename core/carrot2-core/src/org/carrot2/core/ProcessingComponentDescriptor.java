package org.carrot2.core;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;
import org.carrot2.util.resource.*;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.load.Commit;

import com.google.common.collect.Maps;

/**
 * Descriptor of a {@link ProcessingComponent} being part of a
 * {@link ProcessingComponentSuite}.
 */
public class ProcessingComponentDescriptor
{
    @Attribute(name = "component-class")
    private Class<? extends ProcessingComponent> componentClass;

    @Attribute
    private String id;

    @Element
    private String label;

    @Element(required = false)
    private String mnemonic;

    @Element
    private String title;

    @Element(required = false)
    private String description;

    private AttributeValueSets attributeSets;

    @Attribute(name = "attribute-sets-resource", required = false)
    private String attributeSetsResource;

    @Attribute(name = "attribute-set-id", required = false)
    private String attributeSetId;

    ProcessingComponentDescriptor()
    {
    }

    public ProcessingComponentConfiguration getComponentConfiguration()
    {
        return new ProcessingComponentConfiguration(componentClass, id, getAttributes());
    }

    private Map<String, Object> getAttributes()
    {
        Map<String, Object> result = AttributeValueSet
            .getAttributeValues(getAttributeSets().getAttributeValueSet(attributeSetId,
                true));

        if (result == null)
        {
            result = Maps.newHashMap();
        }

        return result;
    }

    public Class<? extends ProcessingComponent> getComponentClass()
    {
        return componentClass;
    }

    public String getId()
    {
        return id;
    }

    public String getLabel()
    {
        return label;
    }

    public String getMnemonic()
    {
        return mnemonic;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public AttributeValueSets getAttributeSets()
    {
        return attributeSets;
    }

    public String getAttributeSetId()
    {
        return attributeSetId;
    }

    @Commit
    @SuppressWarnings("unused")
    private void loadAttributeSets() throws Exception
    {
        final ResourceUtils resourceUtils = ResourceUtilsFactory
            .getDefaultResourceUtils();
        Resource resource = null;

        if (!StringUtils.isBlank(attributeSetsResource))
        {
            // Try to load from the directly provided location
            resource = resourceUtils.getFirst(attributeSetsResource);
        }

        if (resource == null)
        {
            // Try className.id.attributes.xml
            resource = resourceUtils.getFirst(getComponentClass().getName() + "."
                + getId() + ".attributes.xml");
        }

        if (resource == null)
        {
            // Try className.attributes.xml
            resource = resourceUtils.getFirst(getComponentClass().getName()
                + ".attributes.xml");
        }

        if (resource != null)
        {
            final InputStream inputStream = resource.open();
            try
            {
                attributeSets = AttributeValueSets.deserialize(inputStream);
            }
            finally
            {
                CloseableUtils.close(inputStream);
            }
        }

        if (getAttributeSets() == null)
        {
            attributeSets = new AttributeValueSets();
        }
    }
}
