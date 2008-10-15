package org.carrot2.workbench.vis.circles;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.helpers.PostponableJob;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.SearchResultListenerAdapter;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.UIJob;

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
     * Unique ID associated with this page (and this the editor).
     */
    private int id;

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
        browser.addControlListener(new ControlListener()
        {
            public void controlMoved(ControlEvent e)
            {
                System.out.println("moved" + e);
            }
            public void controlResized(ControlEvent e)
            {
                System.out.println("resized: " + e);
            }
        });

        final Listener l = new Listener() {
            public void handleEvent(Event event)
            {
                System.out.println("event: " + event);
            }
        };

        browser.addListener(SWT.PaintItem, l);
        browser.addListener(SWT.EraseItem, l);
        browser.addListener(SWT.Deactivate, l);
        browser.addListener(SWT.Activate, l);
        browser.addListener(SWT.Show, l);
        browser.addListener(SWT.Hide, l);
        

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
}
