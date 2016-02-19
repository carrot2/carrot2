
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

package org.carrot2.workbench.editors.factory;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.*;

import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.collect.*;

/**
 * 
 */
class AttributeEditorLoader
{
    public static final String EXTENSION_NAME = "attributeEditor";

    public static final String EL_TYPE_EDITOR = "type-editor";
    public static final String EL_DEDICATED_EDITOR = "dedicated-editor";

    public static final AttributeEditorLoader INSTANCE;

    private List<DedicatedEditorWrapper> dedicatedEditorsList =
        new ArrayList<DedicatedEditorWrapper>();
    private List<TypeEditorWrapper> typeEditorsList = new ArrayList<TypeEditorWrapper>();
    public final List<DedicatedEditorWrapper> dedicatedEditors;
    public final List<TypeEditorWrapper> typeEditors;

    static
    {
        INSTANCE = new AttributeEditorLoader();
    }

    /*
     * 
     */
    private AttributeEditorLoader()
    {
        loadExtensions();

        dedicatedEditors = ImmutableList.copyOf(dedicatedEditorsList);
        typeEditors = ImmutableList.copyOf(typeEditorsList);
    }

    /*
     * 
     */
    List<DedicatedEditorWrapper> filterDedicatedEditors(
        Predicate<DedicatedEditorWrapper> predicate)
    {
        return ImmutableList.copyOf(Collections2.filter(dedicatedEditorsList, predicate));
    }

    /*
     * 
     */
    List<TypeEditorWrapper> filterTypeEditors(Predicate<TypeEditorWrapper> predicate)
    {
        return ImmutableList.copyOf(Collections2.filter(typeEditorsList, predicate));
    }

    /*
     * 
     */
    private void loadExtensions()
    {
        final IExtension [] extensions =
            Platform.getExtensionRegistry().getExtensionPoint(
                WorkbenchCorePlugin.PLUGIN_ID, EXTENSION_NAME).getExtensions();
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
                    dedicatedEditorsList.add(wrapper);
                }
                else if (configurationElement.getName().equals(EL_TYPE_EDITOR))
                {
                    TypeEditorWrapper wrapper =
                        new TypeEditorWrapper(configurationElement);
                    typeEditorsList.add(wrapper);
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
