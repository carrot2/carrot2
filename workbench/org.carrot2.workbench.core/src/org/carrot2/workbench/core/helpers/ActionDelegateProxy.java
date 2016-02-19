
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * A simple proxy implementing {@link IAction} and forwarding to an
 * {@link IActionDelegate}. This proxy is needed for actions that must be created from
 * within the code (as opposed to adding them via standard extensions), for example in
 * view toolbars. Use standard declarative extensions if possible.
 * <p>
 * Note: {@link IActionDelegate#selectionChanged(IAction, org.eclipse.jface.viewers.ISelection)}
 * is not forwarded.
 */
public final class ActionDelegateProxy extends Action implements IWorkbenchAction
{
    private IActionDelegate delegate;

    /*
     * 
     */
    public ActionDelegateProxy(IActionDelegate delegate, int style) 
    {
        super(null, style);
        this.delegate = delegate;
        
        if (delegate instanceof IActionDelegate2)
        {
            ((IActionDelegate2) delegate).init(this);
        }
        
        if (delegate instanceof IWorkbenchWindowActionDelegate)
        {
            ((IWorkbenchWindowActionDelegate) delegate).init(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow());
        }
    }

    /*
     * 
     */
    public ActionDelegateProxy(IActionDelegate delegate) 
    {
        this(delegate, Action.AS_UNSPECIFIED);
    }

    /*
     * 
     */
    @Override
    public void run()
    {
        checkState();
        delegate.run(this);
    }

    /*
     * 
     */
    @Override
    public void runWithEvent(Event event)
    {
        checkState();
        if (delegate instanceof IActionDelegate2)
        {
            ((IActionDelegate2) delegate).runWithEvent(this, event);
        }
        else
        {
            delegate.run(this);
        }
    }

    /**
     * Dispose of the action.
     */
    public void dispose()
    {
        checkState();
        if (delegate != null)
        {
            if (delegate instanceof IActionDelegate2)
            {
                ((IActionDelegate2) delegate).dispose();
            }
            else
            if (delegate instanceof IWorkbenchWindowActionDelegate)
            {
                ((IWorkbenchWindowActionDelegate) delegate).dispose();
            }
        }

        delegate = null;
    }

    /*
     * 
     */
    private void checkState()
    {
        if (delegate == null)
        {
            throw new RuntimeException("Action already disposed.");
        }
    }
}
