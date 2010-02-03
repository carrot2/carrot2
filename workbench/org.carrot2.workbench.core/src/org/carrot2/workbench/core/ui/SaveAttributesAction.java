
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import java.util.*;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DropDownMenuAction;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.Event;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import com.google.common.collect.Maps;

/**
 * Superclass for attribute management actions.
 */
abstract class SaveAttributesAction extends Action
{
    private static final String REMEMBER_DIRECTORY = SaveAttributesAction.class.getName()
        + ".lastSaveDir";

    private final Action openAction = new Action("Open")
    {
        public void runWithEvent(Event event)
        {
            applyAttributes(openAttributes());
        }
    };

    private final Action saveAction = new Action("Save As...")
    {
        public void runWithEvent(Event event)
        {
            saveAttributes(getFileNameHint(), collectAttributes());
        }
    };

    public SaveAttributesAction(String text)
    {
        super(text, IAction.AS_DROP_DOWN_MENU);

        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/save_e.gif"));
        setMenuCreator(new MenuManagerCreator()
        {
            protected MenuManager createMenu()
            {
                return SaveAttributesAction.this.createMenu();
            }
        });
    }

    /**
     * Open attributes from an XML file. May return an empty value set, but never null.
     */
    static AttributeValueSets openAttributes()
    {
        final IPath pathHint = FileDialogs.recallPath(REMEMBER_DIRECTORY);
        final IPath readLocation = FileDialogs.openReadXML(pathHint);
        if (readLocation != null)
        {
            FileDialogs.rememberDirectory(REMEMBER_DIRECTORY, readLocation);
            try
            {
                final Persister persister = new Persister();
                final AttributeValueSets avs = persister.read(AttributeValueSets.class,
                    readLocation.toFile());

                return avs;
            }
            catch (Exception e)
            {
                Utils.showError(new Status(IStatus.ERROR, WorkbenchCorePlugin.PLUGIN_ID,
                    "Failed to read attributes from: " + readLocation.toOSString(), e));
            }
        }

        return new AttributeValueSets();
    }

    /**
     * Collect attributes to be saved.
     */
    protected abstract AttributeValueSets collectAttributes();
    
    /**
     * Apply loaded attributes.
     */
    protected abstract void applyAttributes(AttributeValueSets attrs);
    
    /**
     * Get the name hint for the filename.
     */
    protected abstract IPath getFileNameHint();

    /**
     * Save attributes to an XML file.
     */
    static void saveAttributes(IPath filenameHint, AttributeValueSets attributes)
    {
        final IPath pathHint =
            filenameHint.isAbsolute() ? 
                filenameHint : FileDialogs.recallPath(REMEMBER_DIRECTORY).append(filenameHint);

        final Path saveLocation = FileDialogs.openSaveXML(pathHint);
        if (saveLocation != null)
        {
            try
            {
                final Persister persister = new Persister(new Format(2));
                persister.write(attributes, saveLocation.toFile());
            }
            catch (Exception e)
            {
                Utils.showError(new Status(IStatus.ERROR, WorkbenchCorePlugin.PLUGIN_ID,
                    "An error occurred while saving attributes.", e));
            }

            FileDialogs.rememberDirectory(REMEMBER_DIRECTORY, saveLocation);
        }
    }

    /**
     * @return Returns the filename hint for an attribute set. The first take is the
     *         attribute sets resource associated with the algorithm. If this fails, we
     *         try to name the file after the algorithm itself.
     */
    static IPath getDefaultHint(String componentId, String prefix)
    {
        final ProcessingComponentDescriptor component = WorkbenchCorePlugin.getDefault()
            .getComponent(componentId);

        String nameHint = component.getAttributeSetsResource();
        if (StringUtils.isBlank(nameHint))
        {
            // Try a fallback.
            nameHint = FileDialogs.sanitizeFileName(prefix + componentId + "-attributes.xml");
        }

        return new Path(nameHint);
    }

    /**
     * Handle the "special" {@link Input} keys that shouldn't be serialized.
     */
    protected static void removeSpecialKeys(Map<String, Object> keyMap)
    {
        keyMap.remove(AttributeNames.DOCUMENTS);
        keyMap.remove(AttributeNames.QUERY);
        keyMap.remove(AttributeNames.START);
        keyMap.remove(AttributeNames.RESULTS);
    }

    /**
     * @return Create the menu for the action.
     */
    protected MenuManager createMenu()
    {
        final MenuManager menu = new MenuManager();
        menu.add(openAction);
        menu.add(saveAction);
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
     * Remove these keys whose value is identical to the defaults.
     */
    protected static void removeKeysWithDefaultValues(Map<String, Object> overrides,
        AttributeValueSet defaults)
    {
        Iterator<Map.Entry<String, Object>> i = overrides.entrySet().iterator();
        while (i.hasNext())
        {
            final Map.Entry<String, Object> e = i.next();
            final String key = e.getKey();
            final Object value = e.getValue();

            if (ObjectUtils.equals(value, defaults.getAttributeValue(key)))
            {
                i.remove();
            }
        }
    }

    /**
     * Default attribute value set for a given component.
     */
    @SuppressWarnings("unchecked")
    protected static AttributeValueSet getDefaultAttributeValueSet(String componentId)
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
