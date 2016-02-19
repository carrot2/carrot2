
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

import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.actions.ExportImageAction;
import org.carrot2.workbench.core.ui.actions.IControlProvider;
import org.carrot2.workbench.vis.AbstractBrowserVisualizationViewPage;
import org.carrot2.workbench.vis.Activator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.IPageSite;


/**
 * A single {@link FoamTreeView} page embedding a Web browser and redirecting to an
 * internal HTTP server with flash animation.
 */
final class FoamTreeViewPage extends AbstractBrowserVisualizationViewPage
{
    /**
     * Entry page for the view.
     */
    private static final String ENTRY_PAGE = "/foamtree.html";

    /**
     * 
     */
    private IPropertyChangeListener listener = new IPropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent event)
        {
            String property = event.getProperty();
            if (property.equals(ToggleRelaxationAction.RELAXATION_ENABLED_KEY) ||
                property.equals(LayoutInitializerAction.LAYOUT_INITIALIZER_KEY))
            {
                if (Display.getCurrent() == null)
                    throw new IllegalStateException();

                // Reload the model to flush new settings.
                if (isBrowserInitialized()) {
                    passAttributes();
                    getBrowser().execute(
                        "javascript:vis.set('dataObject', vis.get('dataObject'))");
                }
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

    @Override
    protected void onBrowserReady()
    {
    	super.onBrowserReady();
        passAttributes();
    }
    
    protected void passAttributes()
    {
        if (isBrowserInitialized()) {
            Browser browser = getBrowser();
            browser.execute("javascript:vis.set({"
                + "relaxationVisible: " + ToggleRelaxationAction.getCurrent() + ","
                + "initializer: '" + LayoutInitializerAction.getCurrent().id + "'})");
        }
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

        pageSite.getActionBars().getToolBarManager().add(
            new ExportImageAction(new IControlProvider()
            {
                public Control getControl()
                {
                    return FoamTreeViewPage.this.getControl();
                }
            }));
    }

    @Override
    public void dispose()
    {
        super.dispose();

        IPreferenceStore store = Activator.getInstance().getPreferenceStore();
        store.removePropertyChangeListener(listener);
    }
}
