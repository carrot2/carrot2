package org.carrot2.workbench.core.helpers;

import java.util.*;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.core.runtime.*;

public class ComponentLoader
{

    private String extensionName;
    private String elementName;
    private String captionName;
    private String className;
    private Map<String, ComponentWrapper> converterCache;

    public static final ComponentLoader SOURCE_LOADER = new ComponentLoader("source",
        "source", "label", "class");

    private ComponentLoader(String extensionName, String elementName, String captionName,
        String className)
    {
        this.extensionName = extensionName;
        this.elementName = elementName;
        this.captionName = captionName;
        this.className = className;
    }

    public List<String> getCaptions()
    {
        loadExtensions();
        return new ArrayList<String>(converterCache.keySet());
    }

    public ProcessingComponent getConverter(String caption)
    {
        return converterCache.get(caption).getExecutableConverter();
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
                captionName, className);
            converterCache.put(wrapper.getCaption(), wrapper);
        }

    }

}
