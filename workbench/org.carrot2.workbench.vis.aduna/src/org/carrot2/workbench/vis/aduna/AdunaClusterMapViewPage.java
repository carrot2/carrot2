
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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.util.Map;

import javax.swing.*;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.Cluster;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.PostponableJob;
import org.carrot2.workbench.core.ui.*;
import org.carrot2.workbench.core.ui.actions.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.UIJob;

import biz.aduna.map.cluster.*;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * A single {@link AdunaClusterMapViewPage} page embeds Aduna's Swing component with
 * visualization of clusters.
 */
final class AdunaClusterMapViewPage extends Page
{
    /** */
    private final int REFRESH_DELAY = 500;

    /**
     * Classification root.
     */
    private DefaultClassification root;

    /**
     * A map of the most recently shown {@link Cluster}s.
     */
    private Map<Integer, DefaultClassification> clusterMap = Maps.newHashMap();

    /**
     * A map of the most recently shown {@link Document}s.
     */
    private Map<String, DefaultObject> documentMap = Maps.newHashMap();

    /**
     * UI job for applying selection to the cluster map component.
     */
    private PostponableJob selectionJob = new PostponableJob(new UIJob(
        "Aduna ClusterMap (selection)...")
    {
        private IStructuredSelection currentlyDisplayed = null;

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor)
        {
            final VisualizationMode mode = VisualizationMode
                .valueOf(AdunaActivator.plugin.getPreferenceStore().getString(
                    PreferenceConstants.VISUALIZATION_MODE));

            if (root != null)
            {
                final IStructuredSelection toBeDisplayed;
                final IStructuredSelection currentSelection = getSelected();
                switch (mode)
                {
                    case SHOW_ALL_CLUSTERS:
                        toBeDisplayed = getAll();
                        break;

                    case SHOW_FIRST_LEVEL_CLUSTERS:
                        toBeDisplayed = getFirstLevel();
                        break;

                    case SHOW_SELECTED_CLUSTERS:
                        toBeDisplayed = currentSelection;
                        break;

                    default:
                        throw new RuntimeException("Unhanded case: " + mode);
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (!toBeDisplayed.equals(currentlyDisplayed))
                        {
                            mapMediator.visualize(selectionToClassification(toBeDisplayed));
                            currentlyDisplayed = toBeDisplayed;
                        }

                        mapMediator.select(selectionToClassification(currentSelection));
                    }
                });
            }

            return Status.OK_STATUS;
        }

        /*
         * 
         */
        private java.util.List<Classification> selectionToClassification(
            IStructuredSelection s)
        {
            final IAdapterManager mgr = Platform.getAdapterManager();

            final java.util.List<Classification> selected = Lists.newArrayList();
            for (Object o : s.toList())
            {
                if (o != null && o instanceof Classification)
                {
                    selected.add((Classification) o);
                }
                else
                {
                    final Cluster c = (Cluster) mgr.getAdapter(o, Cluster.class);
                    if (c != null)
                    {
                        final Classification object = clusterMap.get(c.getId());
                        if (object != null) selected.add(object);
                    }
                }
            }

            return selected;
        }

        /**
         * Return the currently selected clusters.
         */
        private IStructuredSelection getSelected()
        {
            final ISelectionProvider sProvider = editor.getSite().getSelectionProvider();
            final ISelection selection = sProvider.getSelection();
            return (IStructuredSelection) selection;
        }

        /**
         * Return the first level of clusters as the selection.
         */
        private IStructuredSelection getFirstLevel()
        {
            if (root == null)
            {
                return StructuredSelection.EMPTY;
            }
            else
            {
                return new StructuredSelection(root.getChildren().toArray());
            }
        }

        /**
         * Return All clusters as the selection. 
         */
        @SuppressWarnings("unchecked")
        protected IStructuredSelection getAll()
        {
            if (root == null)
            {
                return StructuredSelection.EMPTY;
            }
            else
            {
                final java.util.List<Classification> clusters = Lists.newArrayList();
                final java.util.List<Classification> left = Lists.newLinkedList();

                left.add(root);
                while (!left.isEmpty())
                {
                    final Classification c = left.remove(0);
                    clusters.add(c);

                    left.addAll(c.getChildren());
                }

                return new StructuredSelection(clusters);
            }
        }
    });

    /**
     * Refresh the entire structure of clusters.
     */
    private PostponableJob refreshJob = new PostponableJob(new UIJob(
        "Aduna ClusterMap (full refresh)...")
    {
        public IStatus runInUIThread(IProgressMonitor monitor)
        {
            final ProcessingResult result = editor.getSearchResult()
                .getProcessingResult();

            if (result != null)
            {
                root = new DefaultClassification("All clusters");
                clusterMap = Maps.newHashMap();
                documentMap = Maps.newHashMap();
                toClassification(root, result.getClusters());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mapMediator.setClassificationTree(root);
                    }
                });
                selectionJob.reschedule(0);
            }

            return Status.OK_STATUS;
        }

        private void toClassification(DefaultClassification parent,
            java.util.List<Cluster> clusters)
        {
            for (Cluster cluster : clusters)
            {
                if (clusterMap.containsKey(cluster.getId())) continue;

                final DefaultClassification cc = new DefaultClassification(cluster.getLabel(), parent);
                clusterMap.put(cluster.getId(), cc);

                for (Document d : cluster.getAllDocuments())
                {
                    if (!documentMap.containsKey(d.getStringId()))
                    {
                        String dt = (String) (String) d.getField(Document.TITLE);
                        String title = "[" + d.getStringId() + "]";
                        if (!StringUtils.isEmpty(dt))
                        {
                            title = title + " " + dt;
                        }

                        documentMap.put(d.getStringId(), new DefaultObject(title));
                    }

                    cc.add(documentMap.get(d.getStringId()));
                }

                toClassification(cc, cluster.getSubclusters());
            }
        }
    });

    /*
     * Sync with search result updated event.
     */
    private final SearchResultListenerAdapter editorSyncListener = new SearchResultListenerAdapter()
    {
        public void processingResultUpdated(ProcessingResult result)
        {
            refreshJob.reschedule(REFRESH_DELAY);
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
            selectionJob.reschedule(REFRESH_DELAY);
        }
    };

    /*
     * 
     */
    private SearchEditor editor;

    /**
     * SWT's composite inside which Aduna is embedded (AWT/Swing).
     */
    private Composite scrollable;

    /**
     * Resource disposal.
     */
    private DisposeBin disposeBin = new DisposeBin();

    /**
     * Aduna's GUI mediator component.
     */
    private ClusterMapMediator mapMediator;

    /**
     * @see VisualizationMode
     */
    private IPropertyChangeListener viewModeListener = new PropertyChangeListenerAdapter(
        PreferenceConstants.VISUALIZATION_MODE)
    {
        protected void propertyChangeFiltered(PropertyChangeEvent event)
        {
            selectionJob.reschedule(REFRESH_DELAY);
        }
    };

    /**
     * A composite with embedded AWT stuff. 
     */
    private Composite embedded;

    /*
     *
     */
    public AdunaClusterMapViewPage(SearchEditor editor)
    {
        this.editor = editor;
    }

    @Override
    public void init(IPageSite pageSite)
    {
        super.init(pageSite);

        pageSite.getActionBars().getToolBarManager().add(
            new ExportImageAction(new IImageStreamProvider()
            {
                public void save(OutputStream os) throws IOException
                {
                    mapMediator.getClusterMap().exportPngImage(os);
                }
            }));
    }

    /*
     * 
     */
    @Override
    public void createControl(Composite parent)
    {
        createAdunaControl(parent);
        disposeBin.add(scrollable);

        /*
         * Add listeners.
         */
        disposeBin.registerPropertyChangeListener(AdunaActivator.plugin
            .getPreferenceStore(), viewModeListener);

        /*
         * Add a listener to the editor to update the view after new clusters are
         * available.
         */
        if (editor.getSearchResult().getProcessingResult() != null)
        {
            refreshJob.reschedule(REFRESH_DELAY);
        }

        editor.getSearchResult().addListener(editorSyncListener);
        editor.getSite().getSelectionProvider()
            .addSelectionChangedListener(selectionListener);
    }

    /*
     * 
     */
    private void createAdunaControl(Composite parent)
    {
        /*
         * If <code>true</code>, try some dirty hacks to avoid flicker on Windows.
         */
        final boolean windowsFlickerHack = true;
        if (windowsFlickerHack)
        {
            System.setProperty("sun.awt.noerasebackground", "true");
        }

        this.scrollable = new Composite(parent, 
            SWT.H_SCROLL | SWT.V_SCROLL);
        scrollable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout layout = new GridLayout();
        layout.marginBottom = 0;
        layout.marginLeft = 0;
        layout.marginRight= 0;
        layout.marginTop = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        scrollable.setLayout(layout);
        
        embedded = new Composite(scrollable, SWT.NO_BACKGROUND | SWT.EMBEDDED);
        embedded.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Frame frame = SWT_AWT.new_Frame(embedded);
        frame.setLayout(new java.awt.BorderLayout());
        
        // LINGO-446: flicker fix; see "Creating a Root Pane Container" in http://www.eclipse.org/articles/article.php?file=Article-Swing-SWT-Integration/index.html
        final JApplet applet = new JApplet();
        frame.add(applet);
        applet.setLayout(new java.awt.BorderLayout());

        final JScrollPane scrollPanel = new JScrollPane(
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setDoubleBuffered(true);

        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        applet.getContentPane().add(scrollPanel, java.awt.BorderLayout.CENTER);

        final ClusterMapFactory factory = ClusterMapFactory.createFactory();
        final ClusterMap clusterMap = factory.createClusterMap();
        final ClusterMapMediator mapMediator = factory.createMediator(clusterMap);
        this.mapMediator = mapMediator;

        final ClusterGraphPanel graphPanel = mapMediator.getGraphPanel();
        graphPanel.setDoubleBuffered(true);
        scrollPanel.setViewportView(graphPanel);

        scrollable.addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent e)
            {
                updateScrollBars();
            }
        });

        final SelectionAdapter adapter = new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                ScrollBar hbar = scrollable.getHorizontalBar();
                ScrollBar vbar = scrollable.getVerticalBar();
                final java.awt.Rectangle viewport = new java.awt.Rectangle(
                    hbar.getSelection(),
                    vbar.getSelection(),
                    hbar.getThumb(), 
                    vbar.getThumb());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run()
                    {
                        graphPanel.scrollRectToVisible(viewport);
                    }
                });
            }
        };
        scrollable.getVerticalBar().addSelectionListener(adapter);
        scrollable.getHorizontalBar().addSelectionListener(adapter);

        final Runnable updateScrollBarsAsync = new Runnable() {
            public void run() {
                updateScrollBars();
            }
        };
        
        graphPanel.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentShown(ComponentEvent e)
            {
                graphPanelSize = graphPanel.getPreferredSize();
                Display.getDefault().asyncExec(updateScrollBarsAsync);
            }

            @Override
            public void componentResized(ComponentEvent e)
            {
                graphPanelSize = graphPanel.getPreferredSize();
                Display.getDefault().asyncExec(updateScrollBarsAsync);
            }
        });
    }

    /**
     * The latest size of Aduna's graph panel. Passed between Swing and SWT, so volatile.
     */
    private volatile Dimension graphPanelSize;

    /*
     * 
     */
    protected void updateScrollBars()
    {
        if (Display.findDisplay(Thread.currentThread()) == null)
            throw new IllegalStateException("Not an SWT thread: " + Thread.currentThread());

        if (graphPanelSize == null) 
            return;

        org.eclipse.swt.graphics.Rectangle swtScrollableArea = scrollable.getClientArea();

        int width = Math.max(graphPanelSize.width, 0);
        int viewportWidth = Math.max(swtScrollableArea.width, 0);
        updateScrollBar(scrollable.getHorizontalBar(), width, viewportWidth);

        int height = Math.max(graphPanelSize.height, 0);
        int viewportHeight = Math.max(swtScrollableArea.height, 0);
        updateScrollBar(scrollable.getVerticalBar(), height, viewportHeight);
    }

    private static void updateScrollBar(ScrollBar sbar, int value, int viewportValue)
    {
        int selection = sbar.getSelection();
        int minimum = 0;
        int maximum = value;
        int thumb = Math.min(viewportValue, value);
        int increment = /* SharedScrolledComposite.V_SCROLL_INCREMENT */ 64;
        int pageIncrement = Math.max(thumb - 5 * thumb / 100, 5);

        sbar.setValues(selection, minimum, maximum, thumb, increment, pageIncrement);
    }

    /*
     * 
     */
    @Override
    public Control getControl()
    {
        return scrollable;
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        editor.getSearchResult().removeListener(editorSyncListener);
        editor.getSite().getSelectionProvider().removeSelectionChangedListener(selectionListener);

        disposeBin.dispose();

        super.dispose();
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        // Ignore.
    }
}
