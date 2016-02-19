
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

package org.carrot2.workbench.vis;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.PostponableJob;
import org.carrot2.workbench.core.helpers.Utils;
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
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.IntStack;
import org.carrot2.shaded.guava.common.collect.Lists;

public abstract class AbstractBrowserVisualizationViewPage extends Page
{
    /**
     * Delay between the update event and refreshing the browser view.
     */
    protected static final int BROWSER_MODEL_UPDATE_RETRY = 750;
    protected static final int BROWSER_MODEL_UPDATE_INITIAL = 250;

    /**
     * Delay between the selection event and refreshing the browser view.
     */
    protected static final int BROWSER_SELECTION_DELAY = 250;

    /**
     * Delay between the sizing event after the container is initialized.
     */
    protected static final int BROWSER_CLIENTSIZE_DELAY = 250;

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

    private final AtomicInteger view = new AtomicInteger();

    /**
     * This visualization's logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName() + ".view_" + view.incrementAndGet());

    /**
     * Update model XML.
     */
    private class UpdateModelJob extends PostponableJob {
        private AtomicReference<ProcessingResult> currentModel = new AtomicReference<>();
        private ProcessingResult updatedModel;

        public UpdateModelJob()
        {
            setJob(new UIJob("Visualization model update") {
                public IStatus runInUIThread(IProgressMonitor monitor)
                {
                    if (getBrowser().isDisposed())
                    {
                        logger.warn("Browser disposed.");
                        return Status.OK_STATUS;
                    }

                    // If the page has not finished loading, reschedule.
                    if (!browserInitialized)
                    {
                        if (browser.isVisible())
                        {
                            logger.debug("Model update delayed (browser not ready).");
                            reschedule(BROWSER_MODEL_UPDATE_RETRY);
                        }
                        else
                        {
                            logger.debug("Model update delayed (browser invisible).");
                        }

                        return Status.OK_STATUS;
                    }

                    if (updatedModel == currentModel.getAndSet(updatedModel) || updatedModel == null)
                    {
                        // Same model, or no model, ignore.
                        return Status.OK_STATUS;
                    }

                    doUpdateModel(currentModel.get());
                    return Status.OK_STATUS;
                }

                private void doUpdateModel(ProcessingResult pr)
                {
                    try
                    {
                        StringWriter sw = new StringWriter();
                        pr.serializeJson(sw, "updateDataJson", true, false, true, false);

                        String json = sw.toString();
                        String jsonLeader = StringUtils.abbreviate(json, 180);
                        logger.info("Updating view XML: " + jsonLeader);

                        if (!browser.execute("javascript:" + json))
                        {
                            logger.warn("Failed to update the data model: " + jsonLeader);
                        }
                    } 
                    catch (Exception e)
                    {
                        logger.warn("Browser model update error.", e);
                    }
                }
            });
        }

        public void updateModel(ProcessingResult model)
        {
            if (browser.isDisposed())
            {
                // Browser disposed.
                return;
            }

            updatedModel = model;
            reschedule(BROWSER_MODEL_UPDATE_INITIAL);
        }
    };
    private final UpdateModelJob updateModelJob = new UpdateModelJob();

    /**
     * Client size update job.
     */
    private class UpdateClientSizeJob extends PostponableJob
    {
        private volatile Rectangle clientArea;

        public UpdateClientSizeJob()
        {
            setJob(new UIJob("UpdateClientSize")
            {
                public IStatus runInUIThread(IProgressMonitor monitor)
                {
                    if (getBrowser().isDisposed())
                    {
                        logger.warn("Browser disposed.");
                        return Status.OK_STATUS;
                    }

                    Rectangle r = clientArea;
                    if (r == null)
                    {
                        logger.warn("Area is null?");
                        return Status.OK_STATUS;
                    }

                    if (isBrowserInitialized())
                    {
                        logger.info("updateSize(): " + clientArea);
                        getBrowser().execute(
                            "javascript:updateSize(" + clientArea.width + ", "
                                + clientArea.height + ")");
                    }
                    else
                    {
                        reschedule(BROWSER_CLIENTSIZE_DELAY);
                    }

                    return Status.OK_STATUS;
                }
            });
        }

        public void update(Rectangle clientArea)
        {
            this.clientArea = clientArea;
            reschedule(BROWSER_CLIENTSIZE_DELAY);
        }
    };

    private UpdateClientSizeJob updateClientSizeJob = new UpdateClientSizeJob();

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

        /*
         * 
         */
        protected IStatus doSelectionRefresh()
        {
            if (browser.isDisposed() || !browserInitialized)
            {
                return Status.OK_STATUS;
            }

            final IStructuredSelection sel = (IStructuredSelection) editor.getSite()
                .getSelectionProvider().getSelection();

            @SuppressWarnings("unchecked")
            final List<Cluster> selected = (List<Cluster>) sel.toList();

            IntStack ids = new IntStack(selected.size());
            for (Cluster cluster : selected)
            {
                ids.push(cluster.getId());
            }
            browser.execute("javascript:selectGroupsById(" + Arrays.toString(ids.toArray()) + ");");

            return Status.OK_STATUS;
        }        
    });

    /**
     * Sync with search result updated event.
     */
    private final SearchResultListenerAdapter editorSyncListener = new SearchResultListenerAdapter()
    {
        public void processingResultUpdated(ProcessingResult result)
        {
            updateModelJob.updateModel(result);
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
            if (!browserInitialized) return;

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

    /*
     * 
     */
    public AbstractBrowserVisualizationViewPage(SearchEditor editor, String entryPageUri)
    {
        this.entryPageUri = entryPageUri;
        this.editor = editor;
    }


    /**
     * 
     */
    @Override
    public void createControl(final Composite parent)
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
                onBrowserReady();

                updateModelJob.updateModel(getProcessingResult());
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

        browser.addLocationListener(new LocationAdapter()
        {
            @Override
            public void changing(LocationEvent event)
            {
                if (!event.location.startsWith("file:")) {
                    event.doit = false;
                    openURL(event.location);
                }
            }
        });

        browser.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                logger.debug("Browser disposed.");
            }
        });

        browser.addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent e)
            {
                updateClientSize();
            }
        });

        editor.getSearchResult().addListener(editorSyncListener);
        editor.getSite().getSelectionProvider().addSelectionChangedListener(selectionListener);
    }

    /**
     * Invoked when the browser successfully embedded the visualization.
     */
    protected void onBrowserReady() {
        updateClientSize();
    }

    private void updateClientSize() {
        Rectangle clientArea = browser.getClientArea();
        logger.debug("Updating client size: " + clientArea);
        updateClientSizeJob.update(clientArea);
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
    
    public boolean isBrowserInitialized()
    {
        return browser != null && browserInitialized;
    }

    /**
     * 
     */
    private static void openURL(String location)
    {
        try
        {
            WorkbenchCorePlugin
                .getDefault().getWorkbench().getBrowserSupport()
                .createBrowser(
                    IWorkbenchBrowserSupport.AS_EDITOR |
                    IWorkbenchBrowserSupport.LOCATION_BAR |
                    IWorkbenchBrowserSupport.NAVIGATION_BAR |
                    IWorkbenchBrowserSupport.STATUS, null, null, null)
                .openURL(new URL(location));
        }
        catch (Exception e)
        {
            Utils.logError("Couldn't open internal browser", e, false);
        }
    }    
}
