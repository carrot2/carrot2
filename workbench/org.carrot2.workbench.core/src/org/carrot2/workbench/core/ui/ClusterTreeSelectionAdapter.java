
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

import org.carrot2.workbench.core.ui.SearchEditorSelectionProvider.ClusterSelection;
import org.eclipse.jface.viewers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapt and propagate selection on the {@link ClusterTree} to a selection on the
 * {@link SearchEditorSelectionProvider}.
 */
public final class ClusterTreeSelectionAdapter
{
    private final Logger logger = LoggerFactory.getLogger(ClusterTreeSelectionAdapter.class);

    private final SearchEditorSelectionProvider target;
    private final IPostSelectionProvider provider;

    private final ISelectionChangedListener targetToEditorListener = new ISelectionChangedListener()
    {
        public void selectionChanged(SelectionChangedEvent event)
        {
            logger.debug("tree->editor: " + event.getSelection());
            target.setSelection(event.getSelection(), editorToTargetListener);
        }
    };

    private final ISelectionChangedListener editorToTargetListener = new ISelectionChangedListener()
    {
        public void selectionChanged(SelectionChangedEvent event)
        {
            logger.debug("editor->tree: " + event.getSelection());

            if (event.getSelection() instanceof ClusterSelection)
            {
                unlinkTargetFromEditor();
                provider.setSelection(
                    ((ClusterSelection) event.getSelection()).treeSelection);
                linkTargetToEditor();
            }
        }
    };

    /**
     * 
     */
    public ClusterTreeSelectionAdapter(SearchEditorSelectionProvider target,
        IPostSelectionProvider provider)
    {
        this.target = target;
        this.provider = provider;

        linkEditorToTarget();
        linkTargetToEditor();
    }
    
    /**  */
    private void linkTargetToEditor()
    {
        provider.addPostSelectionChangedListener(targetToEditorListener);
    }

    /** */
    private void unlinkTargetFromEditor()
    {
        provider.removePostSelectionChangedListener(targetToEditorListener);
    }

    /** */
    private void linkEditorToTarget()
    {
        target.addSelectionChangedListener(editorToTargetListener);
    }
}
