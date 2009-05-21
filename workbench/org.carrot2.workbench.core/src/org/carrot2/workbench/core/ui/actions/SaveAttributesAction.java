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

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.core.attribute.AttributeNames;
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
            getFilenameHint());

        final Path saveLocation = FileDialogs.openSaveXML(pathHint);
        if (saveLocation != null)
        {
            try
            {
                /*
                 * Extract all @Input defaults for a given algorithm.
                 */
                final AttributeValueSet defaults 
                    = getDefaultAttributeValueSet(searchInput.getAlgorithmId());

                /*
                 * Create an AVS for the default values and a based-on AVS with
                 * overriden values.
                 */
                final AttributeValueSet avs = searchInput.getAttributeValueSet();
                final Map<String, Object> overrides = avs.getAttributeValues();
                removeSpecialKeys(overrides);
                overrides.keySet().retainAll(defaults.getAttributeValues().keySet());
                
                final AttributeValueSet overridenAvs = new AttributeValueSet(
                    "overriden-attributes", defaults);
                overridenAvs.setAttributeValues(overrides);

                // Flatten and save.
                final AttributeValueSets merged = new AttributeValueSets();
                merged.addAttributeValueSet(overridenAvs.label, overridenAvs);
                merged.addAttributeValueSet(defaults.label, defaults);
                merged.setDefaultAttributeValueSetId(overridenAvs.label);

                final Persister persister = new Persister();
                persister.write(merged, saveLocation.toFile());
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
     * Handle the "special" {@link Input} keys that shouldn't be serialized. 
     */
    private void removeSpecialKeys(Map<String, Object> keyMap)
    {
        keyMap.remove(AttributeNames.DOCUMENTS);
        keyMap.remove(AttributeNames.QUERY);
        keyMap.remove(AttributeNames.START);
    }

    /**
     * @return Returns the filename hint for an attribute set. The first take
     * is the attribute sets resource associated with the algorithm. If this fails,
     * we try to name the file after the algorithm itself.
     */
    private IPath getFilenameHint()
    {        
        final String algorithmId = this.searchInput.getAlgorithmId();
        final ProcessingComponentDescriptor component = 
            WorkbenchCorePlugin.getDefault().getComponent(algorithmId);

        String nameHint = component.getAttributeSetsResource();
        if (StringUtils.isBlank(nameHint))
        {
            // Try a fallback.
            nameHint = FileDialogs.sanitizeFileName(algorithmId + ".attributes.xml");
        }

        return new Path(nameHint);
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
                final AttributeValueSets avs =
                    persister.read(AttributeValueSets.class, readLocation.toFile());

                for (Map.Entry<String, Object> e : 
                    avs.getDefaultAttributeValueSet().getAttributeValues().entrySet())
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
     * Default attribute value set for a given component.
     */
    @SuppressWarnings("unchecked")
    private AttributeValueSet getDefaultAttributeValueSet(String componentId)
    {
        BindableDescriptor desc = WorkbenchCorePlugin.getDefault()
            .getComponentDescriptor(componentId);

        final HashMap<String, Object> defaults = Maps.newHashMap();
        for (Map.Entry<String, AttributeDescriptor> e : desc.flatten().only(Input.class).attributeDescriptors
            .entrySet())
        {
            defaults.put(e.getKey(), e.getValue().defaultValue);
        }
        removeSpecialKeys(defaults);
        
        AttributeValueSet result = new AttributeValueSet("defaults");
        result.setAttributeValues(defaults);
        return result;
    }
}
