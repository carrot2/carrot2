
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

package org.carrot2.workbench.core.ui;

import org.carrot2.core.ProcessingResult;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
    private final SearchResultListenerAdapter editorSyncListener = new SearchResultListenerAdapter()
    {
        public void processingResultUpdated(ProcessingResult result)
        {
            showProcessingResult();
        }
    };

    /*
     * 
     */
    ClusterTree clusterTree;

    /*
     * editor->view selection propagation.
     */
    private ClusterTreeSelectionSync selectionSync;

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

        // Register listeners
        registerListeners();
        
        // Display the current content and propagate selection.
        showProcessingResult();
        this.clusterTree.setSelection(editor.getSelection());
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
        /*
         * Update after each change of {@link ProcessingResult}
         */
        editor.getSearchResult().addListener(editorSyncListener);

        /*
         * Subscribe to the editor's selection events, copying its selection
         * to the view's tree (editor's page).
         *
         * Selection propagation direction: editor->view
         */
        selectionSync = new ClusterTreeSelectionSync(clusterTree);
        this.editor.addPostSelectionChangedListener(selectionSync);

        /*
         * Subscribe to the view's tree selection and propagate this selection to the
         * associated editor.
         * 
         * Selection propagation direction: view->editor (be careful about cycles)
         */
        this.clusterTree.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event)
            {
                final ISelection selection = event.getSelection();

                /*
                 * Propagate to the editor, temporarily unregister reverse selection
                 * sync to avoid loops.
                 */
                editor.removePostSelectionChangedListener(selectionSync);
                editor.setSelection(selection);
                editor.addPostSelectionChangedListener(selectionSync);
            }
        });
    }

    /*
     * 
     */
    private void unregisterListeners()
    {
        editor.getSearchResult().removeListener(editorSyncListener);
        editor.removePostSelectionChangedListener(selectionSync);
    }
}
