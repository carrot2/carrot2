
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

package org.carrot2.workbench.core.ui.actions;

import org.carrot2.workbench.core.helpers.ActiveEditorActionDelegate;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

/**
 * {@link ActiveEditorActionDelegate} that tracks the active {@link SearchEditor}, registers
 * a property listener to it and updates its state on {@link IEditorPart#PROP_DIRTY} events.
 */
public abstract class ActiveSearchEditorActionDelegate extends ActiveEditorActionDelegate 
{
    /**
     * Reacts to property changes.
     */
    private IPropertyListener listener = new IPropertyListener() {
        public void propertyChanged(Object source, int propId)
        {
            if (propId == SearchEditor.PROP_DIRTY)
            {
                updateActionState(getAction(), getEditor());
            }
        }
    };

    /**
     * Subscribe/ unsubscribe listener on editor switch.
     */
    @Override
    protected void switchingEditors(IEditorPart previous, IEditorPart activeEditor)
    {
        final SearchEditor _previous = (SearchEditor) previous;
        final SearchEditor _active = (SearchEditor) activeEditor;
        
        if (_previous != null) _previous.removePropertyListener(listener);
        if (_active != null) _active.addPropertyListener(listener);
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        if (getEditor() != null) getEditor().removePropertyListener(listener);
        super.dispose();
    }
    
    /*
     * 
     */
    @Override
    public final void run(IAction action)
    {
        final SearchEditor editor = (SearchEditor) getEditor();
        if (editor != null)
        {
            run((SearchEditor) editor);
        }
    }

    /**
     * Run the action on the current (non-<code>null</code>) editor.
     */
    protected abstract void run(SearchEditor editor);

    /**
     * Is this action relevant to the given editor?
     */
    protected final boolean isEditorRelevant(IEditorPart activeEditor)
    {
        return activeEditor instanceof SearchEditor;
    }
}
