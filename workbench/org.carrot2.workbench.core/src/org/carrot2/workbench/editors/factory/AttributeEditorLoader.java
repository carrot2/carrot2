package org.carrot2.workbench.editors.factory;

import java.util.*;

import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

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

    List<DedicatedEditorWrapper> filterDedicatedEditors(
        Predicate<DedicatedEditorWrapper> predicate)
    {
        List<DedicatedEditorWrapper> result = apply(predicate, dedicatedEditors);
        return Lists.immutableList(result);
    }

    List<TypeEditorWrapper> filterTypeEditors(Predicate<TypeEditorWrapper> predicate)
    {
        List<TypeEditorWrapper> result = apply(predicate, typeEditors);
        return Lists.immutableList(result);
    }

    private <T extends AttributeEditorWrapper> List<T> apply(Predicate<T> predicate,
        List<T> list)
    {
        List<T> result = Lists.newArrayList();
        for (T element : list)
        {
            if (predicate.apply(element))
            {
                result.add(element);
            }
        }
        return Lists.immutableList(result);
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
