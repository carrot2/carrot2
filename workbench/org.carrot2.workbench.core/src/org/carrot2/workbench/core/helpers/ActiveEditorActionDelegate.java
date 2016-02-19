
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

package org.carrot2.workbench.core.helpers;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Base class for all <b>global</b> actions that track the active editor, but can be invoked
 * when any other part is active.
 */
public abstract class ActiveEditorActionDelegate
    extends ActionDelegate implements IWorkbenchWindowActionDelegate
{
    /*
     * Currently active editor.
     */
    private IEditorPart editor;

    /*
     * The window we are attached to.
     */
    private IWorkbenchWindow window;
    
    /* 
     * Target action proxy.
     */
    private IAction action;

    /**
     * Tracks active page switches.
     */
    private IPartListener partListener = new IPartListener() {
        public void partActivated(IWorkbenchPart part)
        {
            react(part);
        }

        public void partBroughtToTop(IWorkbenchPart part)
        {
            react(part);
        }

        public void partClosed(IWorkbenchPart part)
        {
            react(part);
        }

        public void partDeactivated(IWorkbenchPart part)
        {
            react(part);
        }

        public void partOpened(IWorkbenchPart part)
        {
            react(part);
        }

        private void react(IWorkbenchPart part)
        {
            if (part instanceof IEditorPart)
            {
                updateActiveEditor();
            }
        }
    };

    /*
     * 
     */
    @Override
    public void init(IAction action)
    {
        this.action = action;

        updateActionState(action, null);
        updateActiveEditor();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        if (window != null)
        {
            window.getPartService().removePartListener(partListener);
        }
    }

    /*
     * Track part changes.
     */
    public void init(IWorkbenchWindow window)
    {
        window.getPartService().addPartListener(partListener);
        this.window = window;
    }

    /**
     * Update reference to the active editor.
     */
    private void updateActiveEditor()
    {
        final IEditorPart previous = this.editor;
        final IEditorPart activeEditor = Utils.getActiveEditor();

        if (previous == activeEditor)
        {
            return;
        }

        if (activeEditor != null && isEditorRelevant(activeEditor))
        {
            this.editor = activeEditor;
        }
        else
        {
            this.editor = null;
        }

        switchingEditors(previous, editor);
        updateActionState(action, editor);
    }

    /**
     * The action is about to switch editors.
     */
    protected void switchingEditors(IEditorPart previous, IEditorPart activeEditor)
    {
        // Empty.
    }

    /**
     * Update action style in response to changed active editor. Overriding methods
     * should call <code>super</code> to ensure enabled state is set properly.
     */
    protected void updateActionState(IAction action, IEditorPart editor)
    {
        action.setEnabled(this.editor != null && isEnabled(this.editor));
    }

    /**
     * Is this action enabled for the given editor? Returns <code>true</code> by default.
     */
    protected boolean isEnabled(IEditorPart activeEditor)
    {
        return true;
    }

    /**
     * Is this action relevant to the given editor?
     */
    protected abstract boolean isEditorRelevant(IEditorPart activeEditor);

    /**
     * Return the associated active editor.
     */
    protected IEditorPart getEditor()
    {
        return editor;
    }
    
    /**
     * Return the associated action.
     */
    protected IAction getAction()
    {
        return action;
    }
}
