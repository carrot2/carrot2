
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.vis;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.helpers.PostponableJob;
import org.carrot2.workbench.core.ui.BrowserFacade;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.SearchEditorSelectionProvider;
import org.carrot2.workbench.core.ui.SearchResultListenerAdapter;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.IntStack;
import com.google.common.collect.Lists;

public abstract class AbstractBrowserVisualizationViewPage extends Page
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
     * A flag indicating the browser's applet has finished loading.
     */
    private volatile boolean browserInitialized;

    /**
     * Visualization entry page URI.
     */
    private final String entryPageUri;

    /**
     * This visualization's logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Reloading XML data (with cause).
     */
    private class ReloadXMLJob extends PostponableJob {
        public ReloadXMLJob(final String origin)
        {
            super(new UIJob("Browser refresh [" + origin + "]...") {
                public IStatus runInUIThread(IProgressMonitor monitor)
                {
                    logger.debug("Browser refresh [" + origin + "]");
                    return reloadDataXml();
                }
            });
        }
    };

    /**
     * Selection refresh job.
     */
    private PostponableJob selectionJob = new PostponableJob(new UIJob(
        "Browser (selection)...")
    {
        public IStatus runInUIThread(IProgressMonitor monitor)
        {
            return doSelectionRefresh();
        }
    });

    /**
     * Sync with search result updated event.
     */
    private final SearchResultListenerAdapter editorSyncListener = new SearchResultListenerAdapter()
    {
        public void processingResultUpdated(ProcessingResult result)
        {
            new ReloadXMLJob("updated result").reschedule(BROWSER_REFRESH_DELAY);
        }
    };

    /**
     * Editor selection listener.
     */
    private final ISelectionChangedListener selectionListener = new ISelectionChangedListener()
    {
        /* */
        public void selectionChanged(SelectionChangedEvent event)
        {
            final ISelection selection = event.getSelection();
            if (selection != null && selection instanceof IStructuredSelection)
            {
                final IStructuredSelection ss = (IStructuredSelection) selection;
                logger.debug("Selection, editor->visualization: " + ss);

                final IAdapterManager mgr = Platform.getAdapterManager();
                final ArrayList<Cluster> selectedGroups = Lists.newArrayList();

                final Object [] selected = ss.toArray();
                for (Object ob : selected)
                {
                    final Cluster cluster = (Cluster) mgr.getAdapter(ob, Cluster.class);

                    if (cluster != null) selectedGroups.add(cluster);
                }

                selectionJob.reschedule(BROWSER_SELECTION_DELAY);
            }
        }
    };

    /**
     * Most recently serialized processing result. Avoid re-rendering of visualization
     * in case there are delayed update events after the browser has started (race cond.).
     */
    private ProcessingResult lastProcessingResult;

    /*
     * 
     */
    public AbstractBrowserVisualizationViewPage(SearchEditor editor, String entryPageUri)
    {
        this.entryPageUri = entryPageUri;
        this.editor = editor;
    }

    /*
     * 
     */
    protected IStatus doSelectionRefresh()
    {
        final IStructuredSelection sel = (IStructuredSelection) editor.getSite()
            .getSelectionProvider().getSelection();

        @SuppressWarnings("unchecked")
        final List<Cluster> selected = (List<Cluster>) sel.toList();

        if (browser.isDisposed())
        {
            return Status.OK_STATUS;
        }

        IntStack ids = IntStack.newInstanceWithCapacity(selected.size());
        for (Cluster cluster : selected)
        {
            ids.push(cluster.getId());
        }
        browser.execute("javascript:selectGroupsById(" + Arrays.toString(ids.toArray()) + ");");

        return Status.OK_STATUS;
    }

    /**
     * Reloads XML data in the browser. Use {@link ReloadXMLJob} for invoking this.
     */
    private IStatus reloadDataXml()
    {
        // If there is no search result, quit. Search result listener will reschedule.
        if (getProcessingResult() == null)
        {
            logger.debug("Reloading XML aborted: no processing result.");
            // No search result yet.
            return Status.OK_STATUS;
        }

        // If browser disposed, quit.
        if (browser.isDisposed())
        {
            logger.debug("Reloading XML aborted: browser disposed.");
            return Status.OK_STATUS;
        }

        // If the page has not finished loading, reschedule.
        if (!browserInitialized)
        {
            logger.debug("Reloading XML rescheduled: browser not ready.");
            new ReloadXMLJob("delaying").reschedule(BROWSER_REFRESH_DELAY);
            return Status.OK_STATUS;
        }

        ProcessingResult pr = getProcessingResult(); 
        if (pr == lastProcessingResult)
        {
            logger.debug("Reloading XML aborted: identical processing result.");
            return Status.OK_STATUS;
        }

        try
        {
            StringWriter sw = new StringWriter();
            pr.serializeJson(sw, "updateDataJson", true, false, true, false);

            String json = sw.toString();
            logger.info("Updating view XML: " + 
                StringUtils.abbreviate(json, 180));

            if (!browser.execute("javascript:" + json))
            {
                logger.warn("Failed to update the data model (reason unknown): "
                    + StringUtils.abbreviate(json, 200));
            }
            else
            {
                lastProcessingResult = pr;
            }
        }
        catch (Exception e)
        {
            logger.warn("Embedded browser error: ", e);
        }

        return Status.OK_STATUS;
    }

    /**
     * 
     */
    @Override
    public void createControl(Composite parent)
    {
        /*
         * Open the browser and redirect it to the internal HTTP server.
         */
        browser = BrowserFacade.createNew(parent, SWT.NONE);

        final Activator plugin = Activator.getInstance();
        final String refreshURL = plugin.getFullURL(entryPageUri);

        /*
         * Register custom callback functions.
         */
        new BrowserFunction(browser, "swt_onGroupSelectionChanged")
        {
            public Object function(Object [] arguments)
            {
                if (!browserInitialized) return null;

                Object [] ids = (Object[]) arguments[0];
                int [] groupIds = new int [ids.length];
                
                for (int i = 0; i < groupIds.length; i++) {
                  groupIds[i] = (int) Double.parseDouble(ids[i].toString());
                }
                doGroupSelection(groupIds);
                return null;
            }
        };

        new BrowserFunction(browser, "swt_onModelChanged")
        {
            public Object function(Object [] arguments)
            {
                if (!browserInitialized) return null;
                selectionJob.reschedule(BROWSER_SELECTION_DELAY);
                return null;
            }
        };
        
        new BrowserFunction(browser, "swt_onVisualizationLoaded")
        {
            public Object function(Object [] arguments)
            {
                browserInitialized = true;
                new ReloadXMLJob("Browser loaded").reschedule(0);
                selectionJob.reschedule(0);
                return null;
            }
        };

        new BrowserFunction(browser, "swt_log")
        {
            public Object function(Object [] arguments)
            {
                logger.info("JS->SWT log: " + Arrays.toString(arguments));
                return null;
            }
        };

        browserInitialized = false;
        browser.setUrl(refreshURL);

        editor.getSearchResult().addListener(editorSyncListener);
        editor.getSite().getSelectionProvider().addSelectionChangedListener(
            selectionListener);
    }

    @Override
    public Control getControl()
    {
        return browser;
    }

    @Override
    public void setFocus()
    {
        browser.setFocus();
    }

    @Override
    public void dispose()
    {
        editor.getSearchResult().removeListener(editorSyncListener);
        editor.getSite().getSelectionProvider()
            .removeSelectionChangedListener(selectionListener);
        browser.dispose();

        super.dispose();
    }

    private void doGroupSelection(int [] selectedGroups)
    {
        logger.debug("Selection visualization->editor: " + Arrays.toString(selectedGroups));

        SearchEditorSelectionProvider prov = 
          (SearchEditorSelectionProvider) editor.getSite().getSelectionProvider();

        prov.setSelected(selectedGroups, selectionListener);
    }

    /**
     * Returns the current processing result (must be called from the GUI thread).
     */
    private ProcessingResult getProcessingResult()
    {
        assert Display.getCurrent() != null;

        final ProcessingResult pr = editor.getSearchResult().getProcessingResult();
        if (pr == null || pr.getClusters() == null) 
            return null;
        return pr;
    }
    
    /**
     * Make the browser available.
     */
    protected Browser getBrowser()
    {
        return browser;
    }
}
