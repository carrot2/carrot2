package org.carrot2.core;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.attribute.Init;
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

    @Element(required = false, name = "icon-path")
    private String iconPath;

    @Element(required = false)
    private String description;

    private AttributeValueSets attributeSets;

    @Attribute(name = "attribute-sets-resource", required = false)
    private String attributeSetsResource;

    @Attribute(name = "attribute-set-id", required = false)
    private String attributeSetId;

    @Attribute(required = false)
    Position position = Position.MIDDLE;

    /**
     * The relative positioning of the component within the suite.
     */
    static enum Position
    {
        /** Component appended at the beginning */
        BEGINNING,

        /** Component appended after those at the beginning but before those at the end */
        MIDDLE,

        /** Component appended at the end */
        END;
    }

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

    /**
     * @return Returns (optional) path to the icon of this component. The interpretation
     *         of this path is up to the application (icon resources may be placed in
     *         various places).
     */
    public String getIconPath()
    {
        return iconPath;
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

    /**
     * Invoked by the XML loading framework when the object is deserialized.
     */
    @Commit
    @SuppressWarnings("unused")
    private void loadAttributeSets() throws Exception
    {
        final ResourceUtils resourceUtils = ResourceUtilsFactory
            .getDefaultResourceUtils();

        final Class<?> clazz = getComponentClass();
        Resource resource = null;

        if (!StringUtils.isBlank(attributeSetsResource))
        {
            // Try to load from the directly provided location
            resource = resourceUtils.getFirst(attributeSetsResource, clazz);
        }

        if (resource == null)
        {
            // Try className.id.attributes.xml
            resource = resourceUtils.getFirst(getComponentClass().getName() + "."
                + getId() + ".attributes.xml", clazz);
        }

        if (resource == null)
        {
            // Try className.attributes.xml
            resource = resourceUtils.getFirst(getComponentClass().getName()
                + ".attributes.xml", clazz);
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

    /**
     * Creates a new initialized instance of the processing component corresponding to
     * this descriptor. The instance will be initialized with the {@link Init} attributes
     * from this descriptor's default attribute set.
     */
    public ProcessingComponent newInitializedInstance() throws InstantiationException,
        IllegalAccessException
    {
        final ProcessingComponent instance = componentClass.newInstance();
        final Map<String, Object> initAttributes = Maps.newHashMap();
        final AttributeValueSet defaultAttributeValueSet = attributeSets
            .getDefaultAttributeValueSet();
        if (defaultAttributeValueSet != null)
        {
            initAttributes.putAll(defaultAttributeValueSet.getAttributeValues());
        }

        ControllerUtils.init(instance, initAttributes);

        return instance;
    }
}
