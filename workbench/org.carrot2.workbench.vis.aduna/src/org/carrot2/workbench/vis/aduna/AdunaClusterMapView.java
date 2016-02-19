
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

package org.carrot2.workbench.vis.aduna;

import org.carrot2.workbench.core.ui.PageBookViewBase;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.actions.PreferenceStorePropertyHost;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.*;

/**
 * {@link AdunaClusterMapView} displays clusters using Aduna's Cluster View component.
 * 
 * @see "http://www.aduna-software.com/technologies/clustermap/overview.view"
 */
public final class AdunaClusterMapView extends PageBookViewBase
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.views.aduna";

    @Override
    public void init(IViewSite site) throws PartInitException
    {
        super.init(site);

        final IActionBars bars = getViewSite().getActionBars();
        createToolbar(bars.getToolBarManager());
        bars.updateActionBars();
    }

    private void createToolbar(IToolBarManager toolBarManager)
    {
        toolBarManager.add(new VisualizationModeAction(
            PreferenceConstants.VISUALIZATION_MODE, new PreferenceStorePropertyHost(
                AdunaActivator.plugin.getPreferenceStore())));
    }

    /**
     * Create a document list for the given part.
     */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;

        final AdunaClusterMapViewPage page = new AdunaClusterMapViewPage(editor);
        initPage(page);
        page.createControl(getPageBook());

        return new PageRec(part, page);
    }

    /**
     * Only react to {@link SearchEditor} instances.
     */
    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return (part instanceof SearchEditor);
    }
}
