package org.carrot2.workbench.core.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.*;

import com.google.common.collect.Lists;

/**
 * Search editor serves as a {@link IPostSelectionProvider}, listening on its internal tree
 * panel. If there is any selection on the panel, this selection is forwarded to listeners. If
 * the selection is empty, entire {@link SearchResult} is provided as the default selection.
 */
final class SearchEditorSelectionProxy implements IPostSelectionProvider, ISelectionChangedListener
{
    private final SearchEditor editor;
    private final IPostSelectionProvider delegate;

    private ArrayList<ISelectionChangedListener> listeners = Lists.newArrayList();

    public SearchEditorSelectionProxy(SearchEditor editor, IPostSelectionProvider delegate)
    {
        this.editor = editor;
        this.delegate = delegate;
        
        delegate.addPostSelectionChangedListener(this);
    }

    public void addPostSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.add(listener);
    }

    public void removePostSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.remove(listener);
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.add(listener);
    }

    public ISelection getSelection()
    {
        ISelection selection = this.delegate.getSelection();
        if (selection == null || selection.isEmpty())
        {
            selection = new StructuredSelection(editor.getSearchResult());
        }
        return selection;
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.remove(listener);
    }

    public void setSelection(ISelection selection)
    {
        delegate.setSelection(selection);
    }

    private void fireSelectionChangedEvent()
    {
        final SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
        for (ISelectionChangedListener listener : new ArrayList<ISelectionChangedListener>(listeners))
        {
            listener.selectionChanged(event);
        }
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
        fireSelectionChangedEvent();
    }
}
