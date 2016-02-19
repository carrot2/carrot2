
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

package org.carrot2.workbench.core.ui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Simple SWT composite wrapping a {@link TreeViewer} and displaying a {@link Cluster}
 * and/or sub-clusters.
 */
public final class ClusterTree 
    extends Composite 
    implements IPostSelectionProvider
{
    private TreeViewer treeViewer;
    private List<Cluster> previousClusters;
    
    /**
     * Content provider for the tree.
     */
    @SuppressWarnings("unchecked")
    private final ITreeContentProvider contentProvider = new ITreeContentProvider()
    {
        private Map<Cluster, Cluster> parents = Maps.newIdentityHashMap();

        /*
         * Root tree elements.
         */
        public Object [] getElements(Object treeRoot)
        {
            return ((List<Cluster>) treeRoot).toArray();
        }

        /*
         * Children elements.
         */
        public Object [] getChildren(Object parentElement)
        {
            final Cluster c = (Cluster) parentElement;
            return c.getSubclusters().toArray();
        }

        public Object getParent(Object element)
        {
            return parents.get(element);
        }

        public boolean hasChildren(Object parentElement)
        {
            final Cluster c = (Cluster) parentElement;
            return c.getSubclusters().size() > 0;
        }

        public void dispose()
        {
            // Empty
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
            // Build parents mapping.
            this.parents.clear();

            if (newInput != null)
            {
                recurse((List<Cluster>) newInput, null);
            }
        }

        /** Recursively build parents map. */
        private void recurse(List<Cluster> clusters, Cluster parent)
        {
            for (Cluster c : clusters)
            {
                parents.put(c, parent);
                recurse(c.getSubclusters(), c);
            }
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
    public void show(final List<Cluster> clusters)
    {
        if (clusters == null || clusters.isEmpty())
        {
            clear();
        }
        else
        {
            ClusterLabelPaths clp = null;
            if (previousClusters != null)
                clp = ClusterLabelPaths.from(previousClusters, getExpandedClusters());

            treeViewer.setInput(clusters);

            if (clp != null)
                treeViewer.setExpandedElements(clp.filterMatching(clusters).toArray());

            previousClusters = clusters;
        }
    }

    /** Return a list of expanded clusters in the current tree. */
    private List<Cluster> getExpandedClusters()
    {
        List<Cluster> expanded = Lists.newArrayList(); 
        for (Object o : this.treeViewer.getExpandedElements())
        {
            expanded.add((Cluster) o);
        }
        return expanded;
    }

    /**
     * Resets the display to show all clusters from a {@link ProcessingResult}. 
     */
    public void show(final ProcessingResult result)
    {
        final List<Cluster> clusters = result.getClusters();
        show(clusters);
    }

    /**
     * Clear the display.
     */
    public void clear()
    {
        previousClusters = null;
        treeViewer.setInput(Collections.emptyList());
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
        treeViewer.setInput(Collections.emptyList());
        treeViewer.setAutoExpandLevel(1);
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

        // Expand the paths of a tree selection (all).
        if (selection instanceof TreeSelection)
        {
            for (TreePath tp : ((TreeSelection) selection).getPaths())
            {
                this.treeViewer.reveal(tp);
            }
        }
    }

    public void addPostSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.treeViewer.addPostSelectionChangedListener(listener);
    }

    public void removePostSelectionChangedListener(ISelectionChangedListener listener)
    {
        this.treeViewer.removePostSelectionChangedListener(listener);
    }

    public void expandAll()
    {
        this.treeViewer.expandAll();
    }

    public void collapseAll()
    {
        this.treeViewer.collapseAll();
    }
}
