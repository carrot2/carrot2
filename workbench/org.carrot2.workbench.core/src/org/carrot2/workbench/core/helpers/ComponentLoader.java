package org.carrot2.workbench.core.helpers;

import java.util.*;

import org.carrot2.core.ProcessingComponent;
import org.eclipse.core.runtime.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ComponentLoader
{
    private final String extensionName;
    private final String elementName;
    private final String captionAttName;
    private final String classAttName;
    private final String iconAttName;

    private final Map<String, ComponentWrapper> componentCache = Maps.newHashMap();

    public static final ComponentLoader SOURCE_LOADER =
        new ComponentLoader("source", "source", "label", "class", "icon");

    public static final ComponentLoader ALGORITHM_LOADER =
        new ComponentLoader("algorithm", "algorithm", "label", "class", "icon");

    /**
     * All of parameters should be taken from schema files (*.exsd). Plugin name is
     * assumed to be <code>org.carrot2.core</code>, since this class is dedicated to
     * loading document sources and clustering algorithms, and those both extensions are
     * defined in <code>org.carrot2.core</code> plugin.
     * 
     * @param extensionName extension point ID (without plugin ID as a prefix)
     * @param elementName name of element, that stores info about component
     * @param captionName name of a attribute, that stores label/caption/etc. of a
     *            component
     * @param className name of a attribute, that stores name of a class of a component
     */
    private ComponentLoader(String extensionName, String elementName, String captionName,
        String className, String iconAttName)
    {
        this.extensionName = extensionName;
        this.elementName = elementName;
        this.captionAttName = captionName;
        this.classAttName = className;
        this.iconAttName = iconAttName;
        loadExtensions();
    }

    /**
     * Analyzes extension registry to find all extensions for point defined in
     * constructor. Plugins are not loaded inside this method! List of all components is
     * cached, so only first invocation actually reads the registry.
     * 
     * @return captions for all components of a given type
     * @see ComponentLoader#ComponentLoader(String, String, String, String, String)
     */
    public List<String> getCaptions()
    {
        // loadExtensions();
        return new ArrayList<String>(componentCache.keySet());
    }

    public List<ComponentWrapper> getComponents()
    {
        // loadExtensions();
        return Lists.immutableList(componentCache.values());
    }

    /**
     * @param id
     * @return
     * @throws RuntimeException when component with given id is not found
     */
    public ComponentWrapper getComponent(String id)
    {
        if (!componentCache.containsKey(id))
        {
            throw new RuntimeException("No such component: " + id);
        }
        return componentCache.get(id);
    }

    /**
     * Creates instance of a given component. Plugin, in which component is defined, is
     * loaded while invoking this method! Use when necessary!
     * 
     * @param id
     * @return instance of a component with the given caption.
     */
    public ProcessingComponent getExecutableComponent(String id)
    {
        if (!componentCache.containsKey(id))
        {
            throw new RuntimeException("No such component: " + id);
        }

        return componentCache.get(id).getExecutableComponent();
    }

    /*
     * 
     */
    private void loadExtensions()
    {
        final IExtension [] extensions =
            Platform.getExtensionRegistry().getExtensionPoint("org.carrot2.core",
                extensionName).getExtensions();
        for (IExtension extension : extensions)
        {
            parseExtension(extension.getConfigurationElements());
        }
    }

    /*
     * 
     */
    private void parseExtension(IConfigurationElement [] configurationElements)
    {
        for (int i = 0; i < configurationElements.length; i++)
        {
            IConfigurationElement configurationElement = configurationElements[i];
            if (!configurationElement.getName().equals(elementName))
            {
                return;
            }
            ComponentWrapper wrapper =
                new ComponentWrapper(configurationElement, captionAttName, classAttName,
                    iconAttName);
            componentCache.put(configurationElement.getDeclaringExtension()
                .getUniqueIdentifier(), wrapper);
        }

    }

}
