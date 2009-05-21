/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.*;
import org.carrot2.workbench.core.ui.FileDialogs;
import org.carrot2.workbench.core.ui.SearchInput;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.*;
import org.simpleframework.xml.load.Persister;

import com.google.common.collect.Maps;

/**
 * Save/load attribute values.
 */
public final class SaveAttributesAction extends Action
{
    private final static String REMEMBER_DIRECTORY = SaveAttributesAction.class.getName()
        + ".lastSaveDir";

    private SearchInput searchInput;

    /*
     * 
     */
    public SaveAttributesAction(SearchInput searchInput)
    {
        super("Manage attributes", Action.AS_DROP_DOWN_MENU);

        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/save_e.gif"));
        this.searchInput = searchInput;

        setMenuCreator(new IMenuCreator()
        {
            private DisposeBin bin = new DisposeBin();

            public Menu getMenu(Control parent)
            {
                final Menu m = createMenu().createContextMenu(parent);
                bin.add(m);
                return m;
            }

            public Menu getMenu(Menu parent)
            {
                final Menu m = createMenu().getMenu();
                bin.add(m);
                return m;
            }

            public void dispose()
            {
                bin.dispose();
            }
        });
    }

    /*
     * 
     */
    private MenuManager createMenu()
    {
        final MenuManager menu = new MenuManager();
        menu.add(new Action("Open")
        {
            public void runWithEvent(Event event)
            {
                openAttributes();
            }
        });
        menu.add(new Action("Save As...")
        {
            public void runWithEvent(Event event)
            {
                saveAttributes();
            }
        });
        return menu;
    }

    /*
     * 
     */
    @Override
    public void runWithEvent(Event event)
    {
        DropDownMenuAction.showMenu(this, event);
    }

    /**
     * Save attributes to an XML file.
     */
    public void saveAttributes()
    {
        final IPath pathHint = FileDialogs.recallPath(REMEMBER_DIRECTORY).append(
            FileDialogs.sanitizeFileName(searchInput.getAlgorithmId())
                + "-attributes.xml");

        final Path saveLocation = FileDialogs.openSaveXML(pathHint);
        if (saveLocation != null)
        {
            try
            {
                /*
                 * Extract all @Input defaults for a given algorithm.
                 */
                Map<String, Object> defaults = getDefaultAttributeValues(searchInput
                    .getAlgorithmId());

                // Override customized @Input properties
                AttributeValueSet avs = searchInput.getAttributeValueSet();
                Map<String, Object> current = avs.getAttributeValues();
                current.keySet().retainAll(defaults.keySet());
                defaults.putAll(current);

                // Flatten and save.
                AttributeValueSet flattened = new AttributeValueSet("saved-attribute-set");
                flattened.setAttributeValues(defaults);

                final Persister persister = new Persister();
                persister.write(flattened, saveLocation.toFile());
            }
            catch (Exception e)
            {
                Utils.showError(new Status(Status.ERROR, WorkbenchCorePlugin.PLUGIN_ID,
                    "An error occurred while saving attributes.", e));
            }

            FileDialogs.rememberDirectory(REMEMBER_DIRECTORY, saveLocation);
        }
    }
    
    /**
     * Open attributes from an XML file.
     */
    public void openAttributes()
    {
        final IPath pathHint = FileDialogs.recallPath(REMEMBER_DIRECTORY);
        final IPath readLocation = FileDialogs.openReadXML(pathHint);
        if (readLocation != null)
        {
            FileDialogs.rememberDirectory(REMEMBER_DIRECTORY, readLocation);
            try
            {
                final Persister persister = new Persister();
                final AttributeValueSet avs = 
                    persister.read(AttributeValueSet.class, readLocation.toFile());

                for (Map.Entry<String, Object> e : avs.getAttributeValues().entrySet())
                {
                    searchInput.setAttribute(e.getKey(), e.getValue());
                }
            }
            catch (Exception e)
            {
                Utils.showError(new Status(Status.ERROR, WorkbenchCorePlugin.PLUGIN_ID,
                    "Failed to read attributes from: " + readLocation.toOSString(), e));
            }
        }
    }

    /**
     * Default attribute values for a given component.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getDefaultAttributeValues(String componentId)
    {
        BindableDescriptor desc = WorkbenchCorePlugin.getDefault()
            .getComponentDescriptor(componentId);

        final HashMap<String, Object> defaults = Maps.newHashMap();
        for (Map.Entry<String, AttributeDescriptor> e : desc.flatten().only(Input.class).attributeDescriptors
            .entrySet())
        {
            defaults.put(e.getKey(), e.getValue().defaultValue);
        }

        return defaults;
    }
}
