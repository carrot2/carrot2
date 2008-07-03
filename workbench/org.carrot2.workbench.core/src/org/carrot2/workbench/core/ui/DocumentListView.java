package org.carrot2.workbench.core.ui;

import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

/**
 * A {@link ViewPart} displaying document list from the currently active
 * {@link SearchEditor} or a cluster list from the current selection of {@link Cluster}s.
 */
public final class DocumentListView extends PageBookViewBase
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.core.views.documents";

    /**
     * A single page internally bound to a concrete editor.
     */
    private static class DocumentListPage extends Page
    {
        private final SearchEditor editor;

        /*
         * Sync with search result updated event.
         */
        private final SearchResultListenerAdapter editorSyncListener = 
            new SearchResultListenerAdapter() {
            public void processingResultUpdated(ProcessingResult result)
            {
                showProcessingResult();
            }
        };

        /*
         * Sync with selections.
         */
        private DocumentListSelectionSync selectionSync;

        /*
         * 
         */
        DocumentList documentList;

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

            // Register listeners, but also display the current content.
            registerListeners();
            showProcessingResult();
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
            /*
             * Update after each change of {@link ProcessingResult}
             */
            editor.getSearchResult().addListener(editorSyncListener);

            /*
             * Subscribe to the editor's selection service and listen to selection events
             * occurring on the editor, reflecting them in the corresponding browser page.
             */
            selectionSync = new DocumentListSelectionSync(documentList, editor);
            editor.addPostSelectionChangedListener(selectionSync);
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

    /**
     * Create a document list for the given part.
     */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;

        final DocumentListPage page = new DocumentListPage(editor);
        initPage(page);
        page.createControl(getPageBook());

        return new PageRec(part, page);
    }

    /**
     * Only react to {@link SearchEditor} instances.
     */
    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return part instanceof SearchEditor;
    }
}
