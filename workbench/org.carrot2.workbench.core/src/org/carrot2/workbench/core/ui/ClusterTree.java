
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Simple SWT composite wrapping a {@link TreeViewer} and displaying a {@link Cluster}
 * and/or sub-clusters.
 */
public final class ClusterTree extends Composite implements IPostSelectionProvider, ISelectionListener
{
    private TreeViewer treeViewer;

    /**
     * Content provider for the tree.
     */
    @SuppressWarnings("unchecked")
    private final static ITreeContentProvider contentProvider = new ITreeContentProvider()
    {
        /*
         * Root tree elements.
         */
        public Object [] getElements(Object treeRoot)
        {
            return ((List<ClusterWithParent>) treeRoot).toArray();
        }

        /*
         * Children elements.
         */
        public Object [] getChildren(Object parentElement)
        {
            final ClusterWithParent c = (ClusterWithParent) parentElement;
            final List<ClusterWithParent> subclusters = c.subclusters;

            return subclusters.toArray();
        }

        public Object getParent(Object element)
        {
            if (!(element instanceof ClusterWithParent))
            {
                return null;
            }

            return ((ClusterWithParent) element).parent;
        }

        public boolean hasChildren(Object parentElement)
        {
            final ClusterWithParent c = (ClusterWithParent) parentElement;
            final List<ClusterWithParent> subclusters = c.subclusters;

            return (subclusters != null && !subclusters.isEmpty());
        }

        public void dispose()
        {
            // Empty
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
            // Empty
        }
    };

    /*
     * 
     */
    public ClusterTree(Composite parent, int style)
    {
        super(parent, style);
        createComponents();
    }

    /*
     * 
     */
    public void dispose()
    {
        treeViewer.getTree().dispose();
        super.dispose();
    }

    /**
     * Resets the display to show a new set of clusters. 
     */
    public void show(final List<Cluster> rawClusters)
    {
        final List<ClusterWithParent> clusters = ClusterWithParent.wrap(rawClusters);

        if (clusters == null || clusters.isEmpty())
        {
            clear();
        }
        else
        {
            treeViewer.setInput(clusters);
        }
    }

    /**
     * Resets the display all clusters from a {@link ProcessingResult}. 
     */
    public void show(final ProcessingResult result)
    {
        Logger.getLogger("SEL: ").debug("Selection changed: " + result);
        final List<Cluster> clusters = result.getClusters();
        show(clusters);
    }

    /**
     * Clear the display.
     */
    public void clear()
    {
        Logger.getLogger("SEL: ").debug("Selection changed: CLEAR");
        
        treeViewer.setInput(Collections.EMPTY_LIST);
    }

    /**
     * Create GUI components and set up listeners.
     */
    private void createComponents()
    {
        this.setLayout(new FillLayout());

        treeViewer = new TreeViewer(this, SWT.MULTI);

        treeViewer.setLabelProvider(new ClusterLabelProvider());
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setInput(new ArrayList<ClusterWithParent>());
        treeViewer.setAutoExpandLevel(2);
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.treeViewer.addSelectionChangedListener(listener);
    }

    public ISelection getSelection()
    {
        return this.treeViewer.getSelection();
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.treeViewer.removeSelectionChangedListener(listener);
    }

    public void setSelection(ISelection selection)
    {
        this.treeViewer.setSelection(selection);
    }

    public void addPostSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.treeViewer.addPostSelectionChangedListener(listener);
    }

    public void removePostSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.treeViewer.removePostSelectionChangedListener(listener);
    }

    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
        this.treeViewer.setSelection(selection);
    }
}
