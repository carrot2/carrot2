
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.attribute.Init;
import org.carrot2.util.ReflectionUtils;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.attribute.Required;

/**
 * Descriptor of a {@link IProcessingComponent} being part of a
 * {@link ProcessingComponentSuite}.
 */
public class ProcessingComponentDescriptor
{
    private String componentClassName;

    /** Cached component class instantiated from {@link #componentClassName}. */
    private Class<? extends IProcessingComponent> componentClass;

    /** If not <code>null</code>, component initialization ended with an exception. */
    private Throwable initializationException;

    private String id;

    private String label;

    private String mnemonic;

    private String title;

    private String iconPath;

    private String description;

    private AttributeValueSets attributeSets;

    private String attributeSetsResource;

    private String attributeSetId;

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
            result = new HashMap<>();
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
     * attribute have been provided will not be made.
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
        final Map<String, Object> initAttributes = new HashMap<>();
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
}
