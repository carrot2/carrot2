
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

package org.carrot2.workbench.core.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.InternalAttributePredicate;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.Input;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DropDownMenuAction;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Event;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import org.carrot2.shaded.guava.common.collect.Maps;

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
            applyAttributes(createAttributeMapToApply(openAttributes()));
        }
    };

    private final Action saveAction = new Action("Save As...")
    {
        public void runWithEvent(Event event)
        {
            AttributeValueSets avs = createAttributeValueSetsToSave(collectAttributes());
            saveAttributes(getFileNameHint(), avs);
        }
    };

    public SaveAttributesAction(String text)
    {
        super(text, IAction.AS_DROP_DOWN_MENU);

        setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/save_e.png"));
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
    protected abstract Map<String, Object> collectAttributes();

    /**
     * Returns the id of the component for which attributes are being loaded/saved.
     */
    protected abstract String getComponentId();

    /**
     * Creates an {@link AttributeValueSets} for saving as XML. The result contains two
     * sets: components defaults and the overriding values the user changed using the
     * editor. Additionally, {@link Internal} non-configuration and a number of special
     * attributes are removed.
     */
    private AttributeValueSets createAttributeValueSetsToSave(
        Map<String, Object> overrides)
    {
        final String componentId = getComponentId();
        assert componentId != null;

        final AttributeValueSet defaults = getDefaultAttributeValueSet(componentId);

        /*
         * Create an AVS for the default values and a based-on AVS with overridden values.
         */
        final AttributeValueSet overridenAvs = new AttributeValueSet(
            "overridden-attributes", defaults);
        removeInternalNonConfigurationAttributes(overrides, componentId);
        removeKeysWithDefaultValues(overrides, defaults);
        overrides.keySet().retainAll(defaults.getAttributeValues().keySet());
        overridenAvs.setAttributeValues(overrides);

        // Flatten and save.
        final AttributeValueSets merged = new AttributeValueSets();
        merged.addAttributeValueSet(overridenAvs.label, overridenAvs);
        merged.addAttributeValueSet(defaults.label, defaults);
        merged.setDefaultAttributeValueSetId(overridenAvs.label);
        return merged;
    }

    /**
     * Apply loaded attributes.
     */
    protected abstract void applyAttributes(Map<String, Object> attrs);

    /**
     * Creates a map of attributes to apply on load. Removes {@link Internal}
     * non-configuration attributes from the map so that we don't overwrite certain
     * Workbench-specific attributes, such as resource lookup.
     */
    private Map<String, Object> createAttributeMapToApply(AttributeValueSets attrs)
    {
        final Map<String, Object> map = Maps.newHashMap(attrs
            .getDefaultAttributeValueSet().getAttributeValues());
        removeInternalNonConfigurationAttributes(map, getComponentId());
        return map;
    }

    /**
     * Get the name hint for the filename.
     */
    protected abstract IPath getFileNameHint();

    /**
     * Save attributes to an XML file.
     */
    static void saveAttributes(IPath filenameHint, AttributeValueSets attributes)
    {
        final IPath pathHint = filenameHint.isAbsolute() ? filenameHint : FileDialogs
            .recallPath(REMEMBER_DIRECTORY).append(filenameHint);

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
            nameHint = FileDialogs.sanitizeFileName(prefix + componentId
                + "-attributes.xml");
        }

        return new Path(nameHint);
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
    private static void removeKeysWithDefaultValues(Map<String, Object> overrides,
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
    static AttributeValueSet getDefaultAttributeValueSet(String componentId)
    {
        BindableDescriptor desc = WorkbenchCorePlugin.getDefault()
            .getComponentDescriptor(componentId);

        final HashMap<String, Object> defaults = Maps.newHashMap();
        for (Map.Entry<String, AttributeDescriptor> e : desc.flatten().only(Input.class).attributeDescriptors
            .entrySet())
        {
            defaults.put(e.getKey(), e.getValue().defaultValue);
        }
        removeInternalNonConfigurationAttributes(defaults, componentId);

        AttributeValueSet result = new AttributeValueSet("defaults");
        result.setAttributeValues(defaults);
        return result;
    }

    /**
     * Removes {@link Internal} non-configuration attributes (such as "resource-lookup")
     * from the provided map.
     */
    private static void removeInternalNonConfigurationAttributes(
        Map<String, Object> attrs, String componentId)
    {
        BindableDescriptor desc = WorkbenchCorePlugin.getDefault()
            .getComponentDescriptor(componentId);

        attrs
            .keySet()
            .removeAll(
                desc.flatten().only(new InternalAttributePredicate(false)).attributeDescriptors
                    .keySet());
    }
}
