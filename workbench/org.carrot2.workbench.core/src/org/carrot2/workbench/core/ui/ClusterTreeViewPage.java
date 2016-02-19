
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

import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.helpers.ActionDelegateProxy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.Page;

/**
 * A single page internally bound to a concrete editor.
 */
final class ClusterTreeViewPage extends Page
{
    private final SearchEditor editor;

    /*
     * Sync with search result updated event.
     */
    private final SearchResultListenerAdapter editorSyncListener = 
        new SearchResultListenerAdapter()
        {
            public void processingResultUpdated(ProcessingResult result)
            {
                showProcessingResult();
            }
        };

    /*
     * 
     */
    private ClusterTree clusterTree;

    /*
     * 
     */
    public ClusterTreeViewPage(SearchEditor editor)
    {
        this.editor = editor;
    }

    /*
     * 
     */
    @Override
    public void createControl(Composite parent)
    {
        clusterTree = new ClusterTree(parent, SWT.NONE);

        // Create toolbar.
        final IActionBars bars = getSite().getActionBars();

        final IToolBarManager toolBarManager = bars.getToolBarManager();
        toolBarManager.add(new ActionDelegateProxy(
            new ClusterTreeExpanderAction(
                ClusterTreeExpanderAction.CollapseAction.EXPAND,
                clusterTree, editor.getSearchResult()),
            IAction.AS_PUSH_BUTTON));
        toolBarManager.add(new ActionDelegateProxy(
            new ClusterTreeExpanderAction(
                ClusterTreeExpanderAction.CollapseAction.COLLAPSE,
                clusterTree, editor.getSearchResult()),
            IAction.AS_PUSH_BUTTON));
        bars.updateActionBars();
        
        // Register listeners
        registerListeners();

        // Display the current content.
        showProcessingResult();
        
        // Link bidirectional selection synchronization.
        final SearchEditorSelectionProvider selectionProvider = 
            (SearchEditorSelectionProvider) editor.getSite().getSelectionProvider();

        new ClusterTreeSelectionAdapter(
            selectionProvider, clusterTree);

        clusterTree.setSelection(selectionProvider.getSelection());
    }

    /*
     * 
     */
    @Override
    public Control getControl()
    {
        return clusterTree;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        clusterTree.setFocus();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        unregisterListeners();
        clusterTree.dispose();
        super.dispose();
    }

    /**
     * Display current processing result associated with our editor.
     */
    private void showProcessingResult()
    {
        final ProcessingResult current = this.editor.getSearchResult()
            .getProcessingResult();

        if (current != null)
        {
            clusterTree.show(current);
        }
        else
        {
            clusterTree.clear();
        }
    }

    /*
     * 
     */
    private void registerListeners()
    {
        /* Update after each change of {@link ProcessingResult} */
        editor.getSearchResult().addListener(editorSyncListener);
    }

    /*
     * 
     */
    private void unregisterListeners()
    {
        editor.getSearchResult().removeListener(editorSyncListener);
    }
}
