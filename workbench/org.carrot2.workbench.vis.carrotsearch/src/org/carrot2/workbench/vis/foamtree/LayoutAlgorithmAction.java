
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.vis.foamtree;

import org.carrot2.workbench.core.helpers.DropDownMenuAction;
import org.carrot2.workbench.core.ui.actions.IPropertyHost;
import org.carrot2.workbench.core.ui.actions.PreferenceStorePropertyHost;
import org.carrot2.workbench.vis.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Switching foamtree layouts.
 */
public final class LayoutAlgorithmAction extends Action
{
    /**
     * 
     */
    public final static String LAYOUT_ALGORITHM_KEY = "layout-algorithm";

    /*
     * 
     */
    private final IMenuCreator menuCreator;
    
    /**
     * The supported FoamTree layout algorithms.
     */
    public enum LayoutAlgorithm
    {
        STRIP("Strip", "strip"),
        ORDERED("Ordered", "ordered"),
        GREEDY("Greedy", "greedy"),
        SPLIT_WIDEST_ANGLE("Split widest angle", "splitWidestAngle"),
        FIRST_POWER_OF_2("First power of 2", "firstPowerOf2");

        final String label;
        final String id;
        
        private LayoutAlgorithm(String label, String id)
        {
            this.label = label;
            this.id = id;
        }

        public String toString()
        {
            return label;
        }
    }

    /*
     * 
     */
    private LayoutAlgorithmAction(final String propertyKey, IPropertyHost host)
    {
        super("Initial foam layout", Action.AS_DROP_DOWN_MENU);

        LayoutAlgorithm [] constants = LayoutAlgorithm.values();
        this.menuCreator = DropDownMenuAction.getMenuFor(propertyKey, getText(), constants, host);

        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/enabled/layout.gif"));
        setMenuCreator(menuCreator);
    }

    /**
     * 
     */
    public LayoutAlgorithmAction()
    {
        this(LAYOUT_ALGORITHM_KEY, 
            new PreferenceStorePropertyHost(
                Activator.getInstance().getPreferenceStore()));
    }

    /*
     * 
     */
    @Override
    public void runWithEvent(Event event)
    {
        /*
         * Attempt to open the drop-down menu.
         */
        DropDownMenuAction.showMenu(this, event);
    }

    /*
     * 
     */
    static LayoutAlgorithm getCurrent()
    {
        IPreferenceStore preferenceStore = Activator.getInstance().getPreferenceStore();
        try
        {
            return LayoutAlgorithm.valueOf(preferenceStore
                .getString(LAYOUT_ALGORITHM_KEY));
        }
        catch (Exception e)
        {
            return LayoutAlgorithm.STRIP;
        }
    }
}
