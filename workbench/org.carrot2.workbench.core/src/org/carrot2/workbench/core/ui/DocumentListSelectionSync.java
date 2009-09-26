
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

import java.util.ArrayList;

import org.carrot2.core.*;
import org.eclipse.jface.viewers.*;

/**
 * Updates {@link DocumentList} with the current selection state originating from a given
 * {@link SearchEditor}. Empty selection causes full {@link ProcessingResult} to be
 * displayed.
 */
final class DocumentListSelectionSync implements ISelectionChangedListener
{
    private final DocumentList documentList;

    /**
     * Only accept selection changes from this source part.
     */
    private final SearchEditor source;

    /**
     * Cache last selection that affected the documents view and do not repeat it, until
     * it changes.
     */
    private ISelection lastSelection;

    /*
     * 
     */
    public DocumentListSelectionSync(DocumentList documentList,
        SearchEditor sourceEditor)
    {
        this.documentList = documentList;
        this.source = sourceEditor;
    }

    /*
     * 
     */
    public void selectionChanged(SelectionChangedEvent event)
    {
        final ISelection selection = event.getSelection();

        // Skip redundant selection events that occur when switching between
        // parts (switching editors, for example).
        if (lastSelection == selection
            || (lastSelection != null && selection.equals(lastSelection)))
        {
            return;
        }

        if (selection instanceof IStructuredSelection)
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;
            final ArrayList<Cluster> clusters = new ArrayList<Cluster>();

            /*
             * TODO: add possibility for adaptable objects in the selection (instead
             * of doing explicit instanceof). This could be done in combination
             * with exposing regular outline view.
             */

            for (Object o : ss.toArray())
            {
                if (o instanceof ClusterWithParent)
                {
                    clusters.add(((ClusterWithParent) o).cluster);
                }
            }

            if (clusters.size() > 0)
            {
                documentList.show(clusters.toArray(new Cluster [clusters.size()]));
            }
            else
            {
                emptySelection();
            }

            lastSelection = selection;
        }
    }

    /*
     * 
     */
    private void emptySelection()
    {
        final ProcessingResult processingResult = 
            this.source.getSearchResult().getProcessingResult();

        if (processingResult == null)
        {
            documentList.clear();
        }
        else
        {
            documentList.show(processingResult);
        }
    }
}
