package org.carrot2.core;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.carrot2.core.attribute.Init;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.*;
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
    private String componentClassName;

    /** Cached component class instantiated from {@link #componentClassName}. */
    private Class<? extends ProcessingComponent> componentClass;

    /** If <code>true</code> component class and its instances are available. */
    private boolean componentAvailable;

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
        return new ProcessingComponentConfiguration(getComponentClass(), id,
            getAttributes());
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

    /**
     * @return Returns the {@link Class} object for this component.
     * @throws {@link RuntimeException} if the class cannot be defined for some reason
     *         (class loader issues).
     */
    @SuppressWarnings("unchecked")
    public synchronized Class<? extends ProcessingComponent> getComponentClass()
    {
        if (this.componentClass == null)
        {
            try
            {
                this.componentClass = (Class) Class.forName(componentClassName, true,
                    Thread.currentThread().getContextClassLoader());
            }
            catch (Exception e)
            {
                throw new RuntimeException("Component class cannot be acquired: "
                    + componentClassName, e);
            }
        }
        return this.componentClass;
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
     * Creates a new initialized instance of the processing component corresponding to
     * this descriptor. The instance will be initialized with the {@link Init} attributes
     * from this descriptor's default attribute set. Checking whether all {@link Required}
     * attribute have been provided will not be made, which, when attributes of
     * {@link Bindable} are <code>null</code>, may cause {@link #getBindableDescriptor()}
     * to return incomplete descriptor.
     * <p>
     * The instance may or may not be usable for processing because the
     * {@link ControllerContext} on which it is initialized is disposed before the value
     * is returned.
     * </p>
     */
    private ProcessingComponent newInitializedInstance() throws InstantiationException,
        IllegalAccessException
    {
        final ProcessingComponent instance = getComponentClass().newInstance();
        final Map<String, Object> initAttributes = Maps.newHashMap();
        final AttributeValueSet defaultAttributeValueSet = attributeSets
            .getDefaultAttributeValueSet();
        if (defaultAttributeValueSet != null)
        {
            initAttributes.putAll(defaultAttributeValueSet.getAttributeValues());
        }

        final ControllerContextImpl context = new ControllerContextImpl();
        try
        {
            ControllerUtils.init(instance, initAttributes, false, context);
        }
        finally
        {
            context.dispose();
        }

        return instance;
    }

    /**
     * Builds and returns a {@link BindableDescriptor} for an instance of this
     * descriptor's {@link ProcessingComponent}, with default {@link Init} attributes
     * initialized with the default attribute set. If the default attribute set does provide
     * values for some required {@link Bindable} {@link Init} attributes, the returned
     * descriptor will be incomplete.
     */
    public BindableDescriptor getBindableDescriptor() throws InstantiationException,
        IllegalAccessException
    {
        return BindableDescriptorBuilder.buildDescriptor(newInitializedInstance());
    }

    /**
     * @return Return <code>true</code> if instances of this descriptor are available
     *         (class can be resolved, instances can be created).
     */
    public boolean isComponentAvailable()
    {
        return componentAvailable;
    }

    /**
     * Invoked by the XML loading framework when the object is deserialized.
     */
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
     * On commit, attempt to verify component class and instance availability.
     */
    @Commit
    @SuppressWarnings("unused")
    private void onCommit()
    {
        this.componentAvailable = true;
        try
        {
            loadAttributeSets();
            newInitializedInstance();
        }
        catch (Throwable e)
        {
            Logger.getLogger(this.getClass()).warn(
                "Component availability failure: " + componentClassName, e);
            this.componentAvailable = false;
        }
    }
}
