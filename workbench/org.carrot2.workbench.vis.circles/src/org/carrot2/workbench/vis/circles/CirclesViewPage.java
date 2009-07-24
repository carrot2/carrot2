
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

package org.carrot2.workbench.vis.circles;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.PostponableJob;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.SearchResultListenerAdapter;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.UIJob;

import com.google.common.collect.Lists;

/**
 * A single {@link CirclesView} page embedding a Web browser and redirecting to an
 * internal HTTP server with flash animation.
 */
final class CirclesViewPage extends Page
{
    /**
     * Delay between the update event and refreshing the browser view.  
     */
    protected static final int BROWSER_REFRESH_DELAY = 750;

    /**
     * Delay between the selection event and refreshing the browser view.
     */
    protected static final int BROWSER_SELECTION_DELAY = 250;

    /**
     * The editor associated with this page. 
     */
    public final SearchEditor editor;

    /**
     * Internal HTML browser.
     */
    private Browser browser;

    /**
     * Browser refresh job. Postponed a bit to make the user interface
     * more responsive.
     */
    private PostponableJob refreshJob = new PostponableJob(new UIJob("Browser (refresh)...") {
        public IStatus runInUIThread(IProgressMonitor monitor)
        {
            if (!browser.isDisposed())
            {
                final CirclesActivator plugin = CirclesActivator.getInstance();
    
                final String refreshURL = plugin.getStartupURL() + "?page=" + getId(); 
                browser.setUrl(refreshURL);
                
                Logger.getLogger("browser").info(refreshURL);
            }
            return Status.OK_STATUS;
        }
    });
    
    /**
     * Selection refresh job.
     */
    private PostponableJob selectionJob = new PostponableJob(new UIJob("Browser (selecting)...") {
        public IStatus runInUIThread(IProgressMonitor monitor)
        {
            if (!browser.isDisposed())
            {
                if (last != null)
                {
                    browser.execute("javascript:selectGroupById(" + last + ");");
                }
            }
            return Status.OK_STATUS;
        }
    });
    
    /*
     * Sync with search result updated event.
     */
    private final SearchResultListenerAdapter editorSyncListener = new SearchResultListenerAdapter()
    {
        public void processingResultUpdated(ProcessingResult result)
        {
            refreshJob.reschedule(BROWSER_REFRESH_DELAY);
        }
    };

    /**
     * Unique ID associated with this page (and this editor).
     */
    private final int id;

    /** 
     * Last selected cluster ID (to avoid repetitions).
     */
    private Integer last;

    /**
     * Editor selection listener.
     */
    private final ISelectionChangedListener selectionListener = 
        new ISelectionChangedListener()
    {
        private IStructuredSelection lastSelection;  

        /* */
        public void selectionChanged(SelectionChangedEvent event)
        {
            final ISelection selection = event.getSelection();
            if (selection != null && selection instanceof IStructuredSelection)
            {
                final IStructuredSelection ss = (IStructuredSelection) selection;
                if (lastSelection == null || !lastSelection.equals(ss))
                {
                    final IAdapterManager mgr = Platform.getAdapterManager();
                    final Cluster cluster = (Cluster)
                        mgr.getAdapter(ss.getFirstElement(), Cluster.class);

                    if (cluster != null)
                    {
                        final int id = cluster.getId();
    
                        if (!ObjectUtils.equals(id, last))
                        {
                            selectionJob.reschedule(BROWSER_SELECTION_DELAY);
                            last = id;
                        }
                    }

                    lastSelection = new StructuredSelection(ss.toArray());
                }
            }
        }
    };

    /*
     * 
     */
    public CirclesViewPage(SearchEditor editor, int id)
    {
        this.editor = editor;
        this.id = id;
    }

    /*
     * 
     */
    @Override
    public void createControl(Composite parent)
    {
        /*
         * Open the browser and redirect it to the internal HTTP server.
         */
        browser = new Browser(parent, SWT.NONE);

        /*
         * Register custom callback functions.
         */

        new BrowserFunction(browser, "swt_selectionCleared") {
            public Object function(Object [] arguments)
            {
                editor.setSelection(StructuredSelection.EMPTY);
                return null;
            }
        };

        new BrowserFunction(browser, "swt_groupClicked") {
            public Object function(Object [] arguments)
            {
                if (arguments.length == 2)
                {
                    final int groupId = (int) Double.parseDouble(arguments[0].toString());
                    doGroupSelection(groupId);
                }

                return null;
            }
        };

        new BrowserFunction(browser, "swt_documentClicked") {
            public Object function(Object [] arguments)
            {
                if (arguments.length == 1)
                {
                    final int documentId = (int) Double.parseDouble(arguments[0].toString());
                    doDocumentSelection(documentId);
                }

                return null;
            }
        };
        
        /*
         * Add a listener to the editor to update the view
         * after new clusters are available.
         */
        if (editor.getSearchResult().getProcessingResult() != null)
        {
            refreshJob.reschedule(BROWSER_REFRESH_DELAY);
        }

        editor.getSearchResult().addListener(editorSyncListener);
        editor.addPostSelectionChangedListener(selectionListener);
    }

    /*
     * 
     */
    @Override
    public Control getControl()
    {
        return browser;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        browser.setFocus();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        editor.getSearchResult().removeListener(editorSyncListener);
        editor.removePostSelectionChangedListener(selectionListener);
        browser.dispose();
        super.dispose();
    }
    
    /*
     * 
     */
    public int getId()
    {
        return id;
    }
    
    private void doGroupSelection(int groupId)
    {
        final ProcessingResult pr = editor.getSearchResult().getProcessingResult();
        if (pr == null) return;

        final List<Cluster> clusters = pr.getClusters();

        if (clusters != null && !clusters.isEmpty())
        {
            ClusterWithParent c = 
                ClusterWithParent.find(groupId, ClusterWithParent.wrap(clusters));

            if (c != null)
            {
                /*
                 * Construct full tree path to allow automatic expansion of 
                 * tree selection observers.
                 */
                final ArrayList<ClusterWithParent> path = Lists.newArrayList();
                while (c != null)
                {
                    path.add(0, c);
                    c = c.parent;
                }

                editor.setSelection(
                    new StructuredSelection(new TreePath(path.toArray())));
            }
        }
    }
    
    private void doDocumentSelection(int documentId)
    {
        final ProcessingResult pr = editor.getSearchResult().getProcessingResult();
        if (pr == null) return;

        for (Document d : pr.getDocuments())
        {
            if (documentId == d.getId())
            {
                final String url = d.getField(Document.CONTENT_URL);
                if (!StringUtils.isEmpty(url))
                {
                    openURL(url);
                }
            }
        }
    }
    
    private static void openURL(String location)
    {
        try
        {
            WorkbenchCorePlugin.getDefault().getWorkbench().getBrowserSupport()
                .createBrowser(
                    IWorkbenchBrowserSupport.AS_EDITOR
                        | IWorkbenchBrowserSupport.LOCATION_BAR
                        | IWorkbenchBrowserSupport.NAVIGATION_BAR
                        | IWorkbenchBrowserSupport.STATUS, null, null, null)
                .openURL(new URL(location));
        }
        catch (Exception e)
        {
            Utils.logError("Couldn't open internal browser", e, false);
        }
    }
}
