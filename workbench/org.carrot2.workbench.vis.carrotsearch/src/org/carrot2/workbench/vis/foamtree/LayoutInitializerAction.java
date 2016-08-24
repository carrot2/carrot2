
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
public final class LayoutInitializerAction extends Action
{
    /**
     * 
     */
    public final static String LAYOUT_INITIALIZER_KEY = "layout-initializer";

    /*
     * 
     */
    private final IMenuCreator menuCreator;
    
    /**
     * The supported FoamTree layout algorithms.
     */
    public enum LayoutAlgorithm
    {
        FISHEYE("Large groups in the center", "fisheye"),
        BLACKHOLE("Small groups in the center", "blackhole"),
        TREEMAP("TreeMap-like", "treemap"),
        RANDOM("Random group positions", "random");

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
    private LayoutInitializerAction(final String propertyKey, IPropertyHost host)
    {
        super("FoamTree layout initializer", Action.AS_DROP_DOWN_MENU);

        LayoutAlgorithm [] constants = LayoutAlgorithm.values();
        this.menuCreator = DropDownMenuAction.getMenuFor(propertyKey, getText(), constants, host);

        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/enabled/layout.png"));
        setMenuCreator(menuCreator);
    }

    /**
     * 
     */
    public LayoutInitializerAction()
    {
        this(LAYOUT_INITIALIZER_KEY, 
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
                .getString(LAYOUT_INITIALIZER_KEY));
        }
        catch (Exception e)
        {
            return LayoutAlgorithm.FISHEYE;
        }
    }
}
