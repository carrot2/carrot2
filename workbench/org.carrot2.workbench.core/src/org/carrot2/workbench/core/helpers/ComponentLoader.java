package org.carrot2.workbench.core.helpers;

import java.util.*;

import org.carrot2.core.ProcessingComponent;
import org.eclipse.core.runtime.*;

public class ComponentLoader
{

    private String extensionName;
    private String elementName;
    private String captionAttName;
    private String classAttName;
    private Map<String, ComponentWrapper> converterCache;

    public static final ComponentLoader SOURCE_LOADER = new ComponentLoader("source",
        "source", "label", "class");

    public static final ComponentLoader ALGORITHM_LOADER = new ComponentLoader(
        "algorithm", "algorithm", "label", "class");

    /**
     * All of parameters should be taken from schema files (*.exsd). Plugin name is
     * assumed to be <code>"org.carrot2.core"</code>, since this class is dedicated to
     * loading document sources and clustering algorithms, and those both extensions are
     * defined in org.carrot2.core plugin.
     * 
     * @param extensionName extension point ID (without plugin ID as a prefix)
     * @param elementName name of element, that stores info about component
     * @param captionName name of a attribute, that stores label/caption/etc. of a
     *            component
     * @param className name of a attribute, that stores name of a class of a component
     */
    private ComponentLoader(String extensionName, String elementName, String captionName,
        String className)
    {
        this.extensionName = extensionName;
        this.elementName = elementName;
        this.captionAttName = captionName;
        this.classAttName = className;
    }

    /**
     * Analyzes extension registry to find all extensions for point defined in
     * constructor. Plugins are not loaded inside this method! List of all components is
     * cached, so only first invocation actually reads the registry.
     * 
     * @return captions for all components of a given type
     * @see ComponentLoader#ComponentLoader(String, String, String, String)
     */
    public List<String> getCaptions()
    {
        loadExtensions();
        return new ArrayList<String>(converterCache.keySet());
    }

    /**
     * Creates instance of a given component. Plugin, in which component is defined, is
     * loaded while invoking this method! Use when necessary!
     * 
     * @param caption
     * @return instance of a component with the given caption.
     */
    public ProcessingComponent getComponent(String caption)
    {
        return converterCache.get(caption).getExecutableComponent();
    }

    private void loadExtensions()
    {
        if (converterCache == null)
        {
            converterCache = new HashMap<String, ComponentWrapper>();
            IExtension [] extensions = Platform.getExtensionRegistry().getExtensionPoint(
                "org.carrot2.core", extensionName).getExtensions();
            for (int i = 0; i < extensions.length; i++)
            {
                IExtension extension = extensions[i];
                parseExtension(extension.getConfigurationElements());
            }
        }
    }

    private void parseExtension(IConfigurationElement [] configurationElements)
    {
        for (int i = 0; i < configurationElements.length; i++)
        {
            IConfigurationElement configurationElement = configurationElements[i];
            if (!configurationElement.getName().equals(elementName))
            {
                return;
            }
            ComponentWrapper wrapper = new ComponentWrapper(configurationElement,
                captionAttName, classAttName);
            converterCache.put(wrapper.getCaption(), wrapper);
        }

    }

}
