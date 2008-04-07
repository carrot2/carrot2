package org.carrot2.workbench.editors.factory;

import java.util.*;

import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.*;

public class AttributeEditorLoader
{
    public static final String EXTENSION_NAME = "attributeEditor";
    public static final String EL_TYPE_EDITOR = "typeEditor";
    public static final String EL_DEDICATED_EDITOR = "dedicatedEditor";

    private List<DedicatedEditorWrapper> dedicatedEditors =
        new ArrayList<DedicatedEditorWrapper>();
    private List<TypeEditorWrapper> typeEditors = new ArrayList<TypeEditorWrapper>();
    public static final AttributeEditorLoader INSTANCE;

    static
    {
        INSTANCE = new AttributeEditorLoader();
    }

    public List<DedicatedEditorWrapper> getDedicatedEditors()
    {
        return Collections.unmodifiableList(dedicatedEditors);
    }

    public List<TypeEditorWrapper> getTypeEditors()
    {
        return Collections.unmodifiableList(typeEditors);
    }

    private AttributeEditorLoader()
    {
        loadExtensions();
    }

    /*
     * 
     */
    private void loadExtensions()
    {
        final IExtension [] extensions =
            Platform.getExtensionRegistry().getExtensionPoint(
                "org.carrot2.workbench.core", EXTENSION_NAME).getExtensions();
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
            try
            {
                if (configurationElement.getName().equals(EL_DEDICATED_EDITOR))
                {

                    DedicatedEditorWrapper wrapper =
                        new DedicatedEditorWrapper(configurationElement);
                    dedicatedEditors.add(wrapper);
                }
                else if (configurationElement.getName().equals(EL_TYPE_EDITOR))
                {
                    TypeEditorWrapper wrapper =
                        new TypeEditorWrapper(configurationElement);
                    typeEditors.add(wrapper);
                }
            }
            catch (IllegalArgumentException ex)
            {
                Utils.logError("Error while parsing extension "
                    + configurationElement.getDeclaringExtension().getUniqueIdentifier(),
                    ex, false);
            }
        }

    }

}
