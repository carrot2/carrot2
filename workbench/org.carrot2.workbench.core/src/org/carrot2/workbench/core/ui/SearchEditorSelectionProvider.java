
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

import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Display;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Search editor serves as a {@link ISelectionProvider}, providing the selection
 * events (a set of selected clusters or the entire search result).
 */
public final class SearchEditorSelectionProvider implements ISelectionProvider
{
    /**
     * An array of selection listeners.
     */
    private final ArrayList<ISelectionChangedListener> listeners = Lists.newArrayList();
    
    /**
     * Selected clusters by their ID.
     */
    private BitSet selectedClusters = new BitSet();

    /**
     * Mapping from clusters identifiers to cluster instances.
     */
    private final Map<Integer, Cluster> clustersById = Maps.newHashMap();

    /**
     * Mapping from cluster identifiers to {@link TreePath} with
     * {@link Cluster} objects for the tree model.
     */
    private final Map<Integer, TreePath> clusterTreePathsById = Maps.newHashMap();

    /**
     * Last processing result to which this selection applies.
     */
    private ProcessingResult processingResult;

    /**
     * Custom selection for a set of clusters.
     */
    @SuppressWarnings("rawtypes")
    public final class ClusterSelection implements IStructuredSelection
    {
        private final ArrayList<Cluster> selected;
        public final TreeSelection treeSelection;

        ClusterSelection(SearchEditorSelectionProvider provider)
        {
            selected = Lists.newArrayListWithCapacity(
                provider.selectedClusters.cardinality());

            for(int i = provider.selectedClusters.nextSetBit(0); 
                i >= 0; i = provider.selectedClusters.nextSetBit(i + 1))
            {
                selected.add(provider.clustersById.get(i));
            }

            treeSelection = createTreeSelection();
        }

        public Object getFirstElement()
        {
            return selected.get(0);
        }

        public Iterator iterator()
        {
            return selected.iterator();
        }

        public int size()
        {
            return selected.size();
        }

        public Object [] toArray()
        {
            return selected.toArray(new Cluster [selected.size()]);
        }

        public List<Cluster> toList()
        {
            return Lists.newArrayList(selected);
        }

        public boolean isEmpty()
        {
            return selected.isEmpty();
        }
        
        /**
         * Convert a structured selection to a tree selection.  
         */
        private TreeSelection createTreeSelection()
        {
            final TreePath [] paths = new TreePath [selected.size()];
            for (int i = 0; i < selected.size(); i++)
            {
                paths[i] = clusterTreePathsById.get(selected.get(i).getId());
            }

            return new TreeSelection(paths);
        }

        @Override
        public String toString()
        {
            return treeSelection.toString();
        }
    }

    /* */
    SearchEditorSelectionProvider(SearchEditor editor)
    {
        /* Build the representation of selection for each cluster. */
        if (editor.getSearchResult().getProcessingResult() != null)
        {
            buildRepresentation(editor.getSearchResult().getProcessingResult());
        }
        else
        {
            editor.getSearchResult().addListener(new SearchResultListenerAdapter() {
                ClusterLabelPaths paths; 

                @Override
                public void beforeProcessingResultUpdated()
                {
                    paths = null;
                    if (SearchEditorSelectionProvider.this.processingResult != null)
                    {
                        paths = ClusterLabelPaths.from(
                            SearchEditorSelectionProvider.this.processingResult.getClusters(),
                            getSelection().toList());
                    }
                }

                @Override
                public void processingResultUpdated(ProcessingResult result)
                {
                    buildRepresentation(result);
                }

                @Override
                public void afterProcessingResultUpdated()
                {
                    if (paths != null)
                    {
                        setSelected(paths.filterMatching(
                            SearchEditorSelectionProvider.this.processingResult.getClusters()));
                    }
                }
            });
        }
    }

    /* */
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        assert Display.getCurrent() != null;
        listeners.add(listener);
    }

    /* */
    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        assert Display.getCurrent() != null;
        listeners.remove(listener);
    }

    /* */
    public ClusterSelection getSelection()
    {
        return new ClusterSelection(this);
    }

    /* */
    public void setSelection(ISelection selection, ISelectionChangedListener... skipListeners)
    {
        if (selection.isEmpty())
        {
            if (!selectedClusters.isEmpty())
            {
                selectedClusters.clear();
                fireSelectionChanged();
            }
            return;
        }

        if (selection instanceof IStructuredSelection)
        {
            final BitSet newSelection = getClustersFromSelection(
                (IStructuredSelection) selection);

            // Check if the selection has changed.
            final BitSet changes = (BitSet) selectedClusters.clone();
            changes.xor(newSelection);
            if (changes.cardinality() > 0)
            {
                this.selectedClusters = newSelection;
                fireSelectionChanged(skipListeners);
            }
            return;
        }

        // Do nothing (ignore selection we don't understand).
        Utils.logError("Unexpected selection passed to search editor: "
            + selection.getClass(), false);
    }

    /**
     * Get clusters contained in the selection. 
     */
    static BitSet getClustersFromSelection(IStructuredSelection selection)
    {
        final IStructuredSelection ss = (IStructuredSelection) selection;
        final IAdapterManager mgr = Platform.getAdapterManager();

        final BitSet newSelection = new BitSet();
        for (Object selected : ss.toArray())
        {
            final Cluster c = (Cluster) mgr.getAdapter(selected, Cluster.class);             
            if (c == null) continue;
            newSelection.set(c.getId());
        }
        
        return newSelection;
    }

    /* */
    public void setSelection(ISelection selection)
    {
        setSelection(selection, new ISelectionChangedListener [0]);
    }

    /**
     * Toggle cluster's <code>groupId</code> selection to <code>selected</code>. 
     */
    public void toggleSelected(
        int groupId, boolean selected, ISelectionChangedListener... skipListeners)
    {
        assert Display.getCurrent() != null;

        if (this.selectedClusters.get(groupId) == selected)
            return;
        
        this.selectedClusters.set(groupId, selected);
        fireSelectionChanged(skipListeners);
    }

    /**
     * Replace the current selection with the given set of selected groups.
     */
    public void setSelected(int [] selectedGroups,
                            ISelectionChangedListener... skipListeners)
    {
        assert Display.getCurrent() != null;

        this.selectedClusters.clear();
        for (int i : selectedGroups)
            selectedClusters.set(i);

        fireSelectionChanged(skipListeners);
    }

    /**
     * Replace the current selection with the given set of selected groups.
     */
    public void setSelected(List<Cluster> clusters,
                            ISelectionChangedListener... skipListeners)
    {
        assert Display.getCurrent() != null;
        int [] ids = new int [clusters.size()];
        for (int i = 0; i < clusters.size(); i++)
            ids[i] = clusters.get(i).getId();
        setSelected(ids, skipListeners);
    }

    /**
     * Fire selection changed event.
     */
    private void fireSelectionChanged(ISelectionChangedListener... skipListeners)
    {
        assert Display.getCurrent() != null;

        final SelectionChangedEvent event = 
            new SelectionChangedEvent(this, getSelection());

        ArrayList<ISelectionChangedListener> notifyList = Lists.newArrayList(listeners);
        notifyList.removeAll(Arrays.asList(skipListeners));

        for (ISelectionChangedListener listener : notifyList)
        {
            listener.selectionChanged(event);
        }
    }

    /**
     * Build internal data structures required for selections. 
     */
    private void buildRepresentation(ProcessingResult processingResult)
    {
        this.selectedClusters.clear();
        this.clustersById.clear();
        this.clusterTreePathsById.clear();
        this.processingResult = processingResult;

        // Build mappings.
        final ArrayList<Cluster> path = new ArrayList<Cluster>();
        descend(processingResult.getClusters(), path);
    }

    /** Recursive descend on the list of clusters. */
    private void descend(List<Cluster> clusters, List<Cluster> path)
    {
        for (Cluster c : clusters)
        {
            path.add(c);
            this.clusterTreePathsById.put(c.getId(), 
                new TreePath(path.toArray()));
            this.clustersById.put(c.getId(), c);
            
            descend(c.getSubclusters(), path);
            path.remove(path.size() - 1);
        }
    }
}
