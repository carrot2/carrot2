
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

package org.carrot2.workbench.vis.foamtree;

import java.util.Map;

import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.vis.Activator;
import org.carrot2.workbench.vis.FlashViewPage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.part.IPageSite;


/**
 * A single {@link FoamTreeView} page embedding a Web browser and redirecting to an
 * internal HTTP server with flash animation.
 */
final class FoamTreeViewPage extends FlashViewPage
{
    /**
     * Entry page for the view.
     */
    private static final String ENTRY_PAGE = "/foamtree/index.vm";

    /**
     * 
     */
    private IPropertyChangeListener listener = new IPropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent event)
        {
            String property = event.getProperty();
            if (property.equals(ToggleRelaxationAction.RELAXATION_ENABLED_KEY))
            {
                doRefresh();
            }
        }
    };

    /**
     * 
     */
    public FoamTreeViewPage(SearchEditor editor)
    {
        super(editor, ENTRY_PAGE);
    }

    /**
     * 
     */
    @Override
    public void init(IPageSite pageSite)
    {
        super.init(pageSite);

        IPreferenceStore store = Activator.getInstance().getPreferenceStore();
        store.addPropertyChangeListener(listener);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    @Override
    protected Map<String, Object> contributeCustomParams()
    {
        Map<String, Object> params = super.contributeCustomParams();
        params.put("performRelaxation", !ToggleRelaxationAction.getCurrent());
        return params;
    }
}
