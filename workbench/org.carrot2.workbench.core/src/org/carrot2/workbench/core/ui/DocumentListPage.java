
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;

/**
 * A single page internally bound to a concrete editor.
 */
class DocumentListPage extends Page
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
    DocumentList documentList;

    private DocumentListSelectionAdapter selectionAdapter;

    /*
     * 
     */
    public DocumentListPage(SearchEditor editor)
    {
        this.editor = editor;
    }

    /*
     * 
     */
    @Override
    public void createControl(Composite parent)
    {
        documentList = new DocumentList(parent, SWT.NONE);

        // Register listeners and display the current content.
        registerListeners();
    }

    /*
     * 
     */
    @Override
    public Control getControl()
    {
        return documentList;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        documentList.setFocus();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        unregisterListeners();
        documentList.dispose();
        super.dispose();
    }

    /*
     * 
     */
    private void showProcessingResult()
    {
        final ProcessingResult current = this.editor.getSearchResult()
            .getProcessingResult();

        if (current != null)
        {
            this.documentList.show(current);
        }
        else
        {
            this.documentList.clear();
        }
    }

    /*
     * 
     */
    private void registerListeners()
    {
        /* Update after each change of {@link ProcessingResult} */
        editor.getSearchResult().addListener(editorSyncListener);

        /* Register editor to document list selection propagation. */
        selectionAdapter = new DocumentListSelectionAdapter(
            (SearchEditorSelectionProvider) editor.getSite().getSelectionProvider(), 
            documentList, editor);
    }

    /*
     * 
     */
    private void unregisterListeners()
    {
        editor.getSearchResult().removeListener(editorSyncListener);
        selectionAdapter.unlinkEditorFromTarget();
    }
}
