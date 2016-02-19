
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

package org.carrot2.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.carrot2.core.attribute.Init;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.ReflectionUtils;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.BindableDescriptorBuilder;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.simplexml.PersisterHelpers;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.core.Commit;

import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Descriptor of a {@link IProcessingComponent} being part of a
 * {@link ProcessingComponentSuite}.
 */
public class ProcessingComponentDescriptor
{
    @Attribute(name = "component-class")
    private String componentClassName;

    /** Cached component class instantiated from {@link #componentClassName}. */
    private Class<? extends IProcessingComponent> componentClass;

    /** If not <code>null</code>, component initialization ended with an exception. */
    private Throwable initializationException;

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

    /**
     * Cached bindable descriptor for this component.
     */
    private BindableDescriptor bindableDescriptor;

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
     * @throws RuntimeException if the class cannot be defined for some reason (class
     *             loader issues).
     */
    @SuppressWarnings("unchecked")
    public synchronized Class<? extends IProcessingComponent> getComponentClass()
    {
        if (this.componentClass == null)
        {
            try
            {
                this.componentClass = (Class<? extends IProcessingComponent>) ReflectionUtils
                    .classForName(componentClassName);
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

    /**
     * @return Return the name of a resource from which {@link #getAttributeSets()} were
     *         read or <code>null</code> if there was no such resource.
     */
    public String getAttributeSetsResource()
    {
        return attributeSetsResource;
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
     * {@link IControllerContext} on which it is initialized is disposed before the value
     * is returned.
     * </p>
     */
    private IProcessingComponent newInitializedInstance()
        throws InstantiationException, IllegalAccessException
    {
        final IProcessingComponent instance = getComponentClass().newInstance();
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
            AttributeBinder
                .set(instance, initAttributes, false, Input.class);

            try
            {
                instance.init(context);
            }
            catch (Throwable t)
            {
                // Ignore if failed to initialize.
            }

            AttributeBinder.get(instance, initAttributes, Output.class,
                Init.class);
        }
        finally
        {
            context.dispose();
        }

        return instance;
    }

    /**
     * Builds and returns a {@link BindableDescriptor} for an instance of this
     * descriptor's {@link IProcessingComponent}, with default {@link Init} attributes
     * initialized with the default attribute set. If the default attribute set does not
     * provide values for some required {@link Bindable} {@link Init} attributes, the
     * returned descriptor may be incomplete.
     */
    public BindableDescriptor getBindableDescriptor()
    {
        if (bindableDescriptor == null)
            throw new RuntimeException(
                "Descriptor not available.", this.initializationException);

        return bindableDescriptor;
    }

    /**
     * @return Return <code>true</code> if instances of this descriptor are available
     *         (class can be resolved, instances can be created).
     */
    public boolean isComponentAvailable()
    {
        return this.initializationException == null;
    }

    /**
     * Returns initialization failure ({@link Throwable}) or <code>null</code>.
     */
    public Throwable getInitializationFailure()
    {
        return this.initializationException;
    }

    /**
     * Invoked by the XML loading framework when the object is deserialized. 
     */
    private void loadAttributeSets(ResourceLookup resourceLookup) throws Exception
    {
        attributeSets = new AttributeValueSets();

        IResource resource = null;
        if (!StringUtils.isBlank(attributeSetsResource))
        {
            // Try to load from the directly provided resource name
            resource = resourceLookup.getFirst(attributeSetsResource);

            if (resource == null)
            {
                throw new IOException("Attribute set resource not found: "
                    + attributeSetsResource);
            }
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
    private void onCommit(Map<Object, Object> session)
    {
        this.initializationException = null;
        try
        {
            ResourceLookup resourceLookup = PersisterHelpers.getResourceLookup(session);
            loadAttributeSets(resourceLookup);
            bindableDescriptor = 
                BindableDescriptorBuilder.buildDescriptor(newInitializedInstance());
        }
        catch (Throwable e)
        {
            org.slf4j.LoggerFactory.getLogger(this.getClass()).warn(
                "Component unavailable: " + componentClassName, e);
            this.initializationException = e;
        }
    }

    /**
     * Transforms a {@link ProcessingComponentDescriptor} to its identifier.
     */
    public static final class ProcessingComponentDescriptorToId implements
        Function<ProcessingComponentDescriptor, String>
    {
        public static final ProcessingComponentDescriptorToId INSTANCE = new ProcessingComponentDescriptorToId();

        private ProcessingComponentDescriptorToId()
        {
        }

        public String apply(ProcessingComponentDescriptor descriptor)
        {
            return descriptor.id;
        }
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
