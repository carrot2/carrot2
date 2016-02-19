
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

import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.ui.SearchEditorSelectionProvider.ClusterSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapt and propagate selection from {@link SearchEditorSelectionProvider}
 * to a {@link DocumentList}.
 */
final class DocumentListSelectionAdapter
{
    private final Logger logger = LoggerFactory.getLogger(DocumentListSelectionAdapter.class);

    private final DocumentList target;
    private final SearchEditorSelectionProvider provider;
    private final SearchEditor editor;

    /* */
    private final ISelectionChangedListener editorToTargetListener = new ISelectionChangedListener()
    {
        public void selectionChanged(SelectionChangedEvent event)
        {
            logger.debug("editor->doclist: " + event.getSelection());

            if (event.getSelection() instanceof ClusterSelection)
            {
                final ClusterSelection sel = (ClusterSelection) event.getSelection();
                if (sel.isEmpty())
                {
                    emptySelection();
                }
                else
                {
                    target.show((Cluster []) sel.toArray());
                }
            }
        }
    };

    /* */
    public DocumentListSelectionAdapter(SearchEditorSelectionProvider provider,
        DocumentList target, SearchEditor editor)
    {
        this.target = target;
        this.provider = provider;
        this.editor = editor;

        linkEditorToTarget();

        editorToTargetListener.selectionChanged(
            new SelectionChangedEvent(provider, provider.getSelection()));
    }

    /*
     * 
     */
    private void emptySelection()
    {
        final ProcessingResult processingResult = 
            this.editor.getSearchResult().getProcessingResult();

        if (processingResult == null)
        {
            target.clear();
        }
        else
        {
            target.show(processingResult);
        }
    } 

    /* */
    public void linkEditorToTarget()
    {
        provider.addSelectionChangedListener(editorToTargetListener);
    }

    /* */
    public void unlinkEditorFromTarget()
    {
        provider.removeSelectionChangedListener(editorToTargetListener);
    }
}
